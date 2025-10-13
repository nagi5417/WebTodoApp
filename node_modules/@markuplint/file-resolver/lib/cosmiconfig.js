import path from 'node:path';
import { ConfigParserError } from '@markuplint/parser-utils';
import { cosmiconfig, defaultLoaders } from 'cosmiconfig';
import { jsonc } from 'jsonc';
import { ConfigLoadError } from './config-load-error.js';
import { log } from './debug.js';
const searchLog = log.extend('search');
const explorer = cosmiconfig('markuplint', {
    loaders: {
        noExt: ((path, content) => {
            try {
                return jsonc.parse(content);
            }
            catch (error) {
                if (error instanceof Error && error.name === 'JSONError') {
                    return defaultLoaders['noExt'](path, content);
                }
                throw error;
            }
        }),
    },
    searchStrategy: 'project',
});
export async function search(filePath, cacheClear) {
    if (cacheClear) {
        explorer.clearCaches();
    }
    const dir = path.dirname(filePath);
    searchLog('Search dir: %s', dir);
    const result = await explorer.search(dir).catch(cacheConfigError(dir, filePath));
    searchLog('Search result: %O', result);
    if (!result || result.isEmpty) {
        return null;
    }
    const config = result.config;
    return {
        filePath: result.filepath,
        config: config && typeof config === 'object' && 'default' in config && typeof config.default === 'object'
            ? config.default
            : config,
    };
}
export async function load(filePath, cacheClear, referrer) {
    if (cacheClear) {
        explorer.clearCaches();
    }
    const result = await explorer.load(filePath).catch(cacheConfigError(filePath, referrer));
    if (!result || result.isEmpty) {
        return new ConfigLoadError('Config file is empty', filePath, referrer);
    }
    const config = result.config;
    return {
        filePath: result.filepath,
        config: config && typeof config === 'object' && 'default' in config && typeof config.default === 'object'
            ? config.default
            : config,
    };
}
function cacheConfigError(fileOrDirPath, referrer) {
    return (reason) => {
        if (reason instanceof Error) {
            switch (reason.name) {
                case 'YAMLException': {
                    throw new ConfigParserError(reason.message, {
                        // @ts-ignore
                        filePath: reason.filepath ?? fileOrDirPath,
                    });
                }
            }
            throw new ConfigLoadError(reason.message, fileOrDirPath, referrer);
        }
        throw reason;
    };
}

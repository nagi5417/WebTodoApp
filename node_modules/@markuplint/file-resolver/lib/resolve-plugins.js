import { log } from './debug.js';
import { generalImport } from './general-import.js';
const pLog = log.extend('resolve-plugins');
const cache = new Map();
export async function resolvePlugins(pluginPaths) {
    if (!pluginPaths) {
        return {
            plugins: [],
            errors: [],
        };
    }
    const imported = await Promise.all(pluginPaths.map(p => importPlugin(p)));
    const plugins = [];
    const errors = [];
    for (const item of imported) {
        if (item instanceof ReferenceError) {
            errors.push(item);
        }
        else {
            plugins.push(item);
        }
    }
    return {
        plugins,
        errors,
    };
}
export function cacheClear() {
    cache.clear();
}
async function importPlugin(pluginPath) {
    const config = getPluginConfig(pluginPath);
    const cached = cache.get(config.name);
    if (cached) {
        pLog('Return from cache: %s', config.name);
        return cached;
    }
    const pluginCreator = await generalImport(config.name);
    if (!pluginCreator) {
        const error = new ReferenceError(`Plugin not found: ${config.name}`);
        pLog('Error: %s', error.message);
        cache.set(config.name, error);
        return error;
    }
    let name = config.name;
    let plugin = null;
    if (typeof pluginCreator.create === 'function' || pluginCreator?.name) {
        plugin = {
            name: pluginCreator.name,
            ...pluginCreator.create(config.settings),
        };
        name = plugin.name ?? name;
    }
    else {
        pLog('Invalid plugin: %s', config.name);
    }
    name = name
        .toLowerCase()
        .replace(/^(?:markuplint-rule-|@markuplint\/rule-)/i, '')
        .replaceAll(/\s+|[./\\]/g, '-');
    const result = {
        ...plugin,
        name,
    };
    cache.set(name, result);
    return result;
}
function getPluginConfig(pluginPath) {
    if (typeof pluginPath === 'string') {
        return { name: pluginPath, settings: {} };
    }
    return pluginPath;
}

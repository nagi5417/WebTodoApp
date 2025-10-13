import type { PluginConfig } from '@markuplint/ml-config';
import type { Plugin } from '@markuplint/ml-core';
export declare function resolvePlugins(pluginPaths?: readonly (string | PluginConfig)[]): Promise<{
    plugins: Plugin[];
    errors: ReferenceError[];
}>;
export declare function cacheClear(): void;

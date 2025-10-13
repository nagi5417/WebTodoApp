import { forceImportJsonInModule } from './force-import-json-in-module.js';
const cache = new Map();
export async function getPreset(name) {
    if (cache.has(name)) {
        return cache.get(name);
    }
    const json = await forceImportJsonInModule(`@markuplint/config-presets/preset.${name}.json`);
    if (json instanceof Error) {
        throw new ReferenceError(`Preset markuplint:${name} is not found`);
    }
    cache.set(name, json);
    return json;
}

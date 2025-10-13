export function isPluginModuleName(name) {
    return /^plugin:/i.test(name);
}

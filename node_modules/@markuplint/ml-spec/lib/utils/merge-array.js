export function mergeArray(a, b) {
    if (!b) {
        return a;
    }
    const result = [...a];
    for (const bItem of b) {
        const bName = getName(bItem);
        const aIndex = result.findIndex(item => getName(item) === bName);
        if (aIndex === -1) {
            result.push(bItem);
            continue;
        }
        const aItem = result.splice(aIndex, 1)[0];
        if (typeof bItem === 'string') {
            continue;
        }
        if (typeof aItem === 'string') {
            result.push(bItem);
            continue;
        }
        const exItem = {
            ...aItem,
            ...bItem,
        };
        result.push(exItem);
    }
    return result;
}
function getName(def) {
    const result = typeof def === 'string' ? def : def.name;
    return result.toLowerCase().trim();
}

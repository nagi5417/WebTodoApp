import { resolveNameOrAbsPath } from './resolve-name-or-abs-path.js';
export async function relPathToNameOrAbsPath(dir, filePath, resolveProps, resolveKey = false) {
    if (filePath == null) {
        // @ts-ignore
        return undefined;
    }
    if (typeof filePath === 'string') {
        // @ts-ignore
        return resolveNameOrAbsPath(dir, filePath);
    }
    if (Array.isArray(filePath)) {
        // @ts-ignore
        return Promise.all(filePath.map(fp => relPathToNameOrAbsPath(dir, fp, resolveProps)));
    }
    const res = {};
    for (const [key, fp] of Object.entries(filePath)) {
        let _key = key;
        if (resolveKey) {
            _key = await resolveNameOrAbsPath(dir, key);
        }
        if (typeof fp === 'string') {
            if (!resolveProps) {
                res[_key] = await resolveNameOrAbsPath(dir, fp);
            }
            else if (resolveProps.includes(key)) {
                res[_key] = await resolveNameOrAbsPath(dir, fp);
            }
            else {
                res[_key] = fp;
            }
        }
        else {
            res[_key] = fp;
        }
    }
    // @ts-ignore
    return res;
}

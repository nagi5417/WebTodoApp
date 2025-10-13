var __classPrivateFieldSet = (this && this.__classPrivateFieldSet) || function (receiver, state, value, kind, f) {
    if (kind === "m") throw new TypeError("Private method is not writable");
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
    return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
};
var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _MLFile_basename, _MLFile_code, _MLFile_dirname, _MLFile_stat, _MLFile_type;
import { promises as fs } from 'node:fs';
import path from 'node:path';
import ignore from 'ignore';
import { minimatch } from 'minimatch';
export class MLFile {
    constructor(target) {
        _MLFile_basename.set(this, void 0);
        _MLFile_code.set(this, void 0);
        _MLFile_dirname.set(this, void 0);
        /**
         * - `Stats`: Exists
         * - `null`: Not exists
         * - `undefined`: Doesn't read yet
         */
        _MLFile_stat.set(this, undefined);
        _MLFile_type.set(this, void 0);
        if (typeof target === 'string') {
            __classPrivateFieldSet(this, _MLFile_basename, path.basename(target), "f");
            __classPrivateFieldSet(this, _MLFile_dirname, path.dirname(target), "f");
            __classPrivateFieldSet(this, _MLFile_code, null, "f");
            __classPrivateFieldSet(this, _MLFile_type, 'file-base', "f");
            return;
        }
        if (!target.workspace && target.name && path.isAbsolute(target.name)) {
            __classPrivateFieldSet(this, _MLFile_basename, path.basename(target.name), "f");
            __classPrivateFieldSet(this, _MLFile_dirname, path.dirname(target.name), "f");
        }
        else {
            __classPrivateFieldSet(this, _MLFile_basename, target.name ?? '<AnonymousFile>', "f");
            __classPrivateFieldSet(this, _MLFile_dirname, target.workspace ?? process.cwd(), "f");
        }
        __classPrivateFieldSet(this, _MLFile_code, target.sourceCode, "f");
        __classPrivateFieldSet(this, _MLFile_type, 'code-base', "f");
    }
    get dirname() {
        return __classPrivateFieldGet(this, _MLFile_dirname, "f");
    }
    /**
     * Normalized `MLFile.dirname`
     */
    get nDirname() {
        return pathNormalize(this.dirname);
    }
    /**
     * Normalized `MLFile.path`
     */
    get nPath() {
        return pathNormalize(this.path);
    }
    get path() {
        return path.resolve(__classPrivateFieldGet(this, _MLFile_dirname, "f"), __classPrivateFieldGet(this, _MLFile_basename, "f"));
    }
    async dirExists() {
        return !!(await stat(__classPrivateFieldGet(this, _MLFile_dirname, "f")));
    }
    async getCode() {
        if (__classPrivateFieldGet(this, _MLFile_code, "f") != null) {
            return __classPrivateFieldGet(this, _MLFile_code, "f");
        }
        if (__classPrivateFieldGet(this, _MLFile_type, "f") === 'file-base' && (await this.isExist())) {
            return await this._fetch();
        }
        return '';
    }
    ignored(globPath) {
        globPath = typeof globPath === 'string' ? [globPath] : globPath;
        const normalizedPaths = globPath.map(p => pathNormalize(p, true));
        // @ts-ignore
        const ig = ignore().add(normalizedPaths);
        const ignored = ig.ignores(pathNormalize(this.nPath, true));
        return ignored;
    }
    async isExist() {
        if (__classPrivateFieldGet(this, _MLFile_type, "f") === 'code-base') {
            return true;
        }
        const stat = await this._stat();
        return !!stat;
    }
    async isFile() {
        if (__classPrivateFieldGet(this, _MLFile_type, "f") === 'code-base') {
            return true;
        }
        const stat = await this._stat();
        return !!stat && stat.isFile();
    }
    matches(globPath) {
        return minimatch(this.nPath, pathNormalize(globPath));
    }
    setCode(code) {
        if (__classPrivateFieldGet(this, _MLFile_type, "f") === 'file-base') {
            throw new Error(`This file object is readonly (File-base: ${this.path})`);
        }
        __classPrivateFieldSet(this, _MLFile_code, code, "f");
    }
    async _fetch() {
        const code = await fs.readFile(this.path, { encoding: 'utf8' });
        __classPrivateFieldSet(this, _MLFile_code, code, "f");
        return code;
    }
    async _stat() {
        if (__classPrivateFieldGet(this, _MLFile_stat, "f")) {
            return __classPrivateFieldGet(this, _MLFile_stat, "f");
        }
        __classPrivateFieldSet(this, _MLFile_stat, await stat(this.path), "f");
        return __classPrivateFieldGet(this, _MLFile_stat, "f");
    }
}
_MLFile_basename = new WeakMap(), _MLFile_code = new WeakMap(), _MLFile_dirname = new WeakMap(), _MLFile_stat = new WeakMap(), _MLFile_type = new WeakMap();
async function stat(filePath) {
    try {
        return await fs.stat(filePath);
    }
    catch (error) {
        if (
        // @ts-ignore
        'code' in error &&
            // @ts-ignore
            error.code === 'ENOENT') {
            return null;
        }
        throw error;
    }
}
function pathNormalize(filePath, relative = false) {
    const hasBang = filePath.startsWith('!');
    if (hasBang) {
        filePath = filePath.slice(1);
    }
    // Remove the local disk scheme of Windows OS
    if (path.isAbsolute(filePath)) {
        filePath = filePath.replace(/^[a-z]+:/i, '');
        if (relative) {
            filePath = path.relative(path.sep, filePath);
        }
    }
    // Replace the separator of Windows OS
    filePath = filePath.split(path.sep).join('/');
    if (hasBang) {
        filePath = `!${filePath}`;
    }
    return filePath;
}

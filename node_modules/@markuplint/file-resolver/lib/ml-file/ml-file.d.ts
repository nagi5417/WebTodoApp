import type { Target } from '../types.js';
export declare class MLFile {
    #private;
    constructor(target: Target);
    get dirname(): string;
    /**
     * Normalized `MLFile.dirname`
     */
    get nDirname(): string;
    /**
     * Normalized `MLFile.path`
     */
    get nPath(): string;
    get path(): string;
    dirExists(): Promise<boolean>;
    getCode(): Promise<string>;
    ignored(globPath: string | readonly string[]): boolean;
    isExist(): Promise<boolean>;
    isFile(): Promise<boolean>;
    matches(globPath: string): boolean;
    setCode(code: string): void;
    private _fetch;
    private _stat;
}

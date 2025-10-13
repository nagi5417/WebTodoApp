import type { LoaderSync } from 'cosmiconfig';
import { ConfigLoadError } from './config-load-error.js';
type CosmiConfig = ReturnType<LoaderSync>;
export declare function search<T = CosmiConfig>(filePath: string, cacheClear: boolean): Promise<{
    filePath: string;
    config: T;
} | null>;
export declare function load<T = CosmiConfig>(filePath: string, cacheClear: boolean, referrer: string): Promise<ConfigLoadError | {
    filePath: string;
    config: T;
}>;
export {};

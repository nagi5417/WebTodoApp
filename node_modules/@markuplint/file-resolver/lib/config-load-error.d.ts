export declare class ConfigLoadError extends Error {
    filePath: string;
    name: string;
    referrer: string;
    constructor(message: string, filePath: string, referrer: string);
}

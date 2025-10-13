import type { MLFile } from './ml-file.js';
/**
 * Get files
 *
 * Supported glob patterns
 *
 * @param filePathOrGlob
 */
export declare function getFiles(filePathOrGlob: string, ignoreGlob?: string): Promise<MLFile[]>;

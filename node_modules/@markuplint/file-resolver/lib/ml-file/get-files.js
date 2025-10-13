import { glob } from 'glob';
import { minimatch } from 'minimatch';
import { getFile } from './get-file.js';
/**
 * Get files
 *
 * Supported glob patterns
 *
 * @param filePathOrGlob
 */
export async function getFiles(filePathOrGlob, ignoreGlob) {
    const fileList = await glob(filePathOrGlob, {}).catch(() => []);
    const filtered = fileList.filter(fileName => !minimatch(fileName, ignoreGlob ?? ''));
    return filtered.map(fileName => getFile(fileName));
}

import { MLFile } from './ml-file.js';
export function getFile(filePath) {
    const file = new MLFile(filePath);
    return file;
}

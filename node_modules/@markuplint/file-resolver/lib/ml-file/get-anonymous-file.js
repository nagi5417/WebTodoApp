import { MLFile } from './ml-file.js';
export function getAnonymousFile(context, workspace, name) {
    const file = new MLFile({
        sourceCode: context,
        workspace,
        name,
    });
    return file;
}

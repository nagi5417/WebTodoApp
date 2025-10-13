const LINE_BREAK = '\n';
/**
 * @deprecated Use {@link getPosition} instead. Will be removed in v5.0.0.
 */
export function getLine(rawCodeFragment, startOffset) {
    return rawCodeFragment.slice(0, startOffset).split(LINE_BREAK).length;
}
/**
 * @deprecated Use {@link getPosition} instead. Will be removed in v5.0.0.
 */
export function getCol(rawCodeFragment, startOffset) {
    const lines = rawCodeFragment.slice(0, startOffset).split(LINE_BREAK);
    return (lines.at(-1) ?? '').length + 1;
}
export function getPosition(rawCodeFragment, startOffset) {
    const lines = rawCodeFragment.slice(0, startOffset).split(LINE_BREAK);
    const line = lines.length;
    const column = (lines.at(-1) ?? '').length + 1;
    return { line, column };
}
export function getEndLine(rawCodeFragment, startLine) {
    return rawCodeFragment.split(LINE_BREAK).length - 1 + startLine;
}
export function getEndCol(rawCodeFragment, startCol) {
    const lines = rawCodeFragment.split(LINE_BREAK);
    const lineCount = lines.length;
    const lastLine = lines.pop();
    return lineCount > 1 ? lastLine.length + 1 : startCol + rawCodeFragment.length;
}
export function getOffsetsFromCode(rawCode, startLine, startCol, endLine, endCol) {
    const lines = rawCode.split('\n');
    let offset = 0;
    let endOffset = 0;
    for (let i = 0; i < startLine - 1; i++) {
        const line = lines[i];
        if (line == null) {
            continue;
        }
        offset += line.length + 1;
    }
    offset += startCol - 1;
    for (let i = 0; i < endLine - 1; i++) {
        const line = lines[i];
        if (line == null) {
            continue;
        }
        endOffset += line.length + 1;
    }
    endOffset += endCol - 1;
    return { offset, endOffset };
}

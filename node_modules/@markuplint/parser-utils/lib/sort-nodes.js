export function sortNodes(a, b) {
    if (a.startOffset === b.startOffset) {
        return sort(a.endOffset, b.endOffset);
    }
    return sort(a.startOffset, b.startOffset);
}
function sort(a, b) {
    const diff = a - b;
    if (Number.isNaN(diff)) {
        return 0;
    }
    return diff;
}

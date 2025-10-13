export function syncWalk(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
nodeList, walker) {
    for (const node of nodeList) {
        if (node.is(node.ELEMENT_NODE) || node.is(node.MARKUPLINT_PREPROCESSOR_BLOCK)) {
            syncWalk([...node.childNodes], walker);
        }
        walker(node);
    }
}
export function sequentialWalker(list, walker) {
    if (list.length === 0) {
        return Promise.resolve();
    }
    /**
     * The following pattern is used to ensure that all rules run sequentially,
     * no matter it runs asynchronously or synchronously.
     */
    let _resolve;
    let _reject;
    const promise = new Promise((resolve, reject) => {
        _resolve = resolve;
        _reject = reject;
    });
    const loop = (index = 0) => {
        while (true) {
            if (index >= list.length) {
                _resolve();
                return;
            }
            const node = list[index];
            if (node == null) {
                _resolve();
                return;
            }
            const result = walker(node);
            if (result instanceof Promise) {
                result.then(() => loop(index + 1)).catch(_reject);
                return;
            }
            else {
                index = index + 1;
            }
        }
    };
    loop();
    return promise;
}

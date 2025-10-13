class MLNodeList extends Array {
    forEach(callbackfn, thisArg) {
        return super.forEach.bind(this)((v, k) => callbackfn(v, k, thisArg ?? this));
    }
    item(index) {
        const node = this[index];
        if (!node) {
            /**
             * ⚠ TYPE CONTRADICTION
             *
             * It throws an error because it should return null but the `item` method of `NodeListOf` doesn't return null in the type definition of TypeScript.
             *
             * @see https://www.w3.org/TR/DOM-Level-2-Core/core.html#ID-844377136
             *
             * > item
             * > Returns the index-th item in the collection. If index is greater than or equal to the number of nodes in the list, this returns null.
             */
            throw new ReferenceError('Invalid index');
        }
        return node;
    }
}
export function toNodeList(nodes) {
    const nodeList = new MLNodeList(...nodes);
    return nodeList;
}
class MLHTMLCollection extends Array {
    item(index) {
        return this[index] ?? null;
    }
    namedItem(name) {
        return this.find(el => el.getAttribute('name') === name) ?? null;
    }
}
export function toHTMLCollection(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
nodes) {
    const collection = new MLHTMLCollection(...nodes);
    return collection;
}
export function nodeListToHTMLCollection(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
nodeList) {
    const collection = new MLHTMLCollection();
    for (const node of nodeList) {
        if (node.is(node.ELEMENT_NODE)) {
            collection.push(node);
        }
    }
    return collection;
}

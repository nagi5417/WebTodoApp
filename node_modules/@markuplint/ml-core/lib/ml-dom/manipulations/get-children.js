import { toHTMLCollection } from '../node/node-list.js';
export function getChildren(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node) {
    return toHTMLCollection([...node.childNodes].filter((child) => {
        return child.nodeType === child.ELEMENT_NODE;
    }));
}

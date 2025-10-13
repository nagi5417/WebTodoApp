import { UnexpectedCallError } from '../node/unexpected-call-error.js';
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-before%E2%91%A0
 */
export function before(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node, 
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
...additionalNodes) {
    throw new UnexpectedCallError('Not supported "before" method');
}
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-after%E2%91%A0
 */
export function after(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node, 
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
...additionalNodes) {
    throw new UnexpectedCallError('Not supported "after" method');
}
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-replacewith%E2%91%A0
 */
export function replaceWith(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node, 
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
...additionalNodes) {
    throw new UnexpectedCallError('Not supported "replaceWith" method');
}
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-remove%E2%91%A0
 */
export function remove(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node) {
    throw new UnexpectedCallError('Not supported "remove" method');
}
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-previouselementsibling%E2%91%A0
 */
export function previousElementSibling(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node) {
    let prevNode = node.previousSibling;
    while (prevNode) {
        if (prevNode.is(prevNode.ELEMENT_NODE)) {
            return prevNode;
        }
        prevNode = prevNode.previousSibling;
    }
    return null;
}
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-nextelementsibling%E2%91%A0
 */
export function nextElementSibling(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
node) {
    let nextNode = node.nextSibling;
    while (nextNode) {
        if (nextNode.is(nextNode.ELEMENT_NODE)) {
            return nextNode;
        }
        nextNode = nextNode.nextSibling;
    }
    return null;
}

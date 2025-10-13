var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var __classPrivateFieldSet = (this && this.__classPrivateFieldSet) || function (receiver, state, value, kind, f) {
    if (kind === "m") throw new TypeError("Private method is not writable");
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
    return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
};
var _MLParentNode_children, _MLParentNode_selectedElements;
import { syncWalk } from '../helper/walkers.js';
import { getChildren } from '../manipulations/get-children.js';
import { toNodeList } from './node-list.js';
import { MLNode } from './node.js';
import { UnexpectedCallError } from './unexpected-call-error.js';
/**
 *
 * @see https://dom.spec.whatwg.org/#interface-parentnode
 */
export class MLParentNode extends MLNode {
    constructor() {
        super(...arguments);
        /**
         * Cached `children`
         */
        _MLParentNode_children.set(this, null);
        /**
         * Cached elements that created from `querySelectorAll`
         */
        _MLParentNode_selectedElements.set(this, new Map());
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#dom-parentnode-childelementcount
     */
    get childElementCount() {
        return this.children.length;
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-children%E2%91%A0
     */
    get children() {
        if (__classPrivateFieldGet(this, _MLParentNode_children, "f")) {
            return __classPrivateFieldGet(this, _MLParentNode_children, "f");
        }
        const children = getChildren(this);
        __classPrivateFieldSet(this, _MLParentNode_children, children, "f");
        return __classPrivateFieldGet(this, _MLParentNode_children, "f");
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-firstelementchild%E2%91%A0
     */
    get firstElementChild() {
        return this.children[0] ?? null;
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-lastelementchild%E2%91%A0
     */
    get lastElementChild() {
        return this.children[0] ?? null;
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-append%E2%91%A0
     */
    append(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        throw new UnexpectedCallError('Not supported "append" method');
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-prepend%E2%91%A0
     */
    prepend(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        throw new UnexpectedCallError('Not supported "prepend" method');
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-queryselector%E2%91%A0
     */
    querySelector(selectors) {
        const selected = this.querySelectorAll(selectors);
        return selected[0] ?? null;
    }
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-queryselectorall%E2%91%A0
     */
    querySelectorAll(selectors) {
        const selected = __classPrivateFieldGet(this, _MLParentNode_selectedElements, "f").get(selectors);
        if (selected) {
            return selected;
        }
        const elements = toNodeList(this._descendantsToArray(node => {
            if (node.is(node.ELEMENT_NODE) && node.matches(selectors, this)) {
                return node;
            }
        }));
        __classPrivateFieldGet(this, _MLParentNode_selectedElements, "f").set(selectors, elements);
        return elements;
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-replacechildren%E2%91%A0
     */
    replaceChildren(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        throw new UnexpectedCallError('Not supported "replaceChildren" method');
    }
    _descendantsToArray(filter) {
        const nodeList = [];
        syncWalk([...this.childNodes], node => {
            if (filter) {
                const filtered = filter(node);
                if (filtered) {
                    nodeList.push(filtered);
                }
                return;
            }
            return node;
        });
        return Object.freeze(nodeList);
    }
}
_MLParentNode_children = new WeakMap(), _MLParentNode_selectedElements = new WeakMap();

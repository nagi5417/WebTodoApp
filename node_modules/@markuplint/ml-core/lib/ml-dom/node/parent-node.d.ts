import type { MLElement } from './element.js';
import type { MLASTNode } from '@markuplint/ml-ast';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
import { MLNode } from './node.js';
/**
 *
 * @see https://dom.spec.whatwg.org/#interface-parentnode
 */
export declare abstract class MLParentNode<T extends RuleConfigValue, O extends PlainData = undefined, A extends MLASTNode = MLASTNode> extends MLNode<T, O, A> implements ParentNode {
    #private;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#dom-parentnode-childelementcount
     */
    get childElementCount(): number;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-children%E2%91%A0
     */
    get children(): HTMLCollectionOf<MLElement<T, O>>;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-firstelementchild%E2%91%A0
     */
    get firstElementChild(): MLElement<T, O> | null;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-lastelementchild%E2%91%A0
     */
    get lastElementChild(): MLElement<T, O> | null;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-append%E2%91%A0
     */
    append(...nodes: (string | Node)[]): void;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-prepend%E2%91%A0
     */
    prepend(...nodes: (string | Node)[]): void;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-queryselector%E2%91%A0
     */
    querySelector(selectors: string): MLElement<T, O> | null;
    /**
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-queryselectorall%E2%91%A0
     */
    querySelectorAll(selectors: string): NodeListOf<MLElement<T, O>>;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `Element`, `Document`, `DocumentFragment`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-parentnode-replacechildren%E2%91%A0
     */
    replaceChildren(...nodes: (string | Node)[]): void;
    protected _descendantsToArray<N extends MLNode<T, O>>(filter?: (node: MLNode<T, O>) => N | null | void): ReadonlyArray<N>;
}

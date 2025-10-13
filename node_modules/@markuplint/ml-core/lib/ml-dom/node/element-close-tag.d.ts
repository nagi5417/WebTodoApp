import type { MLDocument } from './document.js';
import type { MLElement } from './element.js';
import type { MLASTElementCloseTag } from '@markuplint/ml-ast';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
import { MLNode } from './node.js';
export declare class MLElementCloseTag<T extends RuleConfigValue, O extends PlainData = undefined> extends MLNode<T, O, MLASTElementCloseTag> {
    readonly pair: MLElement<T, O>;
    constructor(astNode: MLASTElementCloseTag, document: MLDocument<T, O>, pair: MLElement<T, O>);
    /**
     * Returns a string appropriate for the type of node as `MLBlock`
     *
     * @implements `@markuplint/ml-core` API: `MLBlock`
     */
    get nodeName(): string;
    /**
     * @implements `@markuplint/ml-core` API: `MLElement`
     */
    get rawName(): string;
    toString(fixed?: boolean): string;
}

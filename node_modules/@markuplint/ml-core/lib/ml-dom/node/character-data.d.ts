import type { MLElement } from './element.js';
import type { MLASTNode } from '@markuplint/ml-ast';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
import { MLNode } from './node.js';
export declare abstract class MLCharacterData<T extends RuleConfigValue, O extends PlainData = undefined, A extends MLASTNode = MLASTNode> extends MLNode<T, O, A> implements CharacterData {
    /**
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-characterdata-data
     */
    get data(): string;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-characterdata-length
     */
    get length(): number;
    /**
     * The element immediately following the specified one in its parent's children list.
     *
     * @readonly
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-nextelementsibling%E2%91%A1
     */
    get nextElementSibling(): MLElement<T, O> | null;
    get nodeValue(): string | null;
    /**
     * The element immediately prior the specified one in its parent's children list.
     *
     * @readonly
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-previouselementsibling%E2%91%A1
     */
    get previousElementSibling(): MLElement<T, O> | null;
    /**
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-node-textcontent
     */
    get textContent(): string;
    /**
     * @implements DOM API: `CharacterData`
     */
    after(...nodes: (string | MLElement<any, any>)[]): void;
    appendData(data: string): void;
    /**
     * @implements DOM API: `CharacterData`
     */
    before(...nodes: (string | MLElement<any, any>)[]): void;
    deleteData(offset: number, count: number): void;
    insertData(offset: number, data: string): void;
    /**
     * @implements DOM API: `CharacterData`
     */
    remove(): void;
    replaceData(offset: number, count: number, data: string): void;
    /**
     * @implements DOM API: `CharacterData`
     */
    replaceWith(...nodes: (string | MLElement<any, any>)[]): void;
    substringData(offset: number, count: number): string;
}

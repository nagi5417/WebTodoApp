import type { MLAttr } from './attr.js';
import type { RuleInfo } from '@markuplint/ml-config';
type Scope = {
    raw: string;
    startOffset: number;
    startLine: number;
    startCol: number;
    rule: RuleInfo<any, any>;
};
export declare class MLDomTokenList extends Array<string> implements DOMTokenList {
    #private;
    constructor(tokens: string, ownerAttrs: MLAttr<any, any>[]);
    get value(): string;
    add(...tokens: readonly string[]): void;
    /**
     * @implements `@markuplint/ml-core` API: `MLDomTokenList`
     */
    allTokens(): Scope[];
    contains(token: string): boolean;
    forEach(callbackfn: (value: string, index: number, parent: any) => void, thisArg?: any): void;
    item(index: number): string | null;
    /**
     * @implements `@markuplint/ml-core` API: `MLDomTokenList`
     */
    pick(token: string): Scope | null;
    remove(...tokens: readonly string[]): void;
    replace(token: string, newToken: string): boolean;
    supports(token: string): boolean;
    toString(): string;
    toggle(token: string, force?: boolean): boolean;
    private _pick;
}
export {};

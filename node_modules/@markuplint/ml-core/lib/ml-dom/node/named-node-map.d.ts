import type { MLAttr } from './attr.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
export declare class MLNamedNodeMap<T extends RuleConfigValue, O extends PlainData = undefined> extends Array<MLAttr<T, O>> implements NamedNodeMap {
    getNamedItem(qualifiedName: string): MLAttr<T, O> | null;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    getNamedItemNS(namespace: string | null, localName: string): Attr | null;
    item(index: number): MLAttr<T, O> | null;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    removeNamedItem(qualifiedName: string): MLAttr<T, O>;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    removeNamedItemNS(namespace: string | null, localName: string): MLAttr<T, O>;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    setNamedItem(attr: MLAttr<T, O>): MLAttr<T, O> | null;
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    setNamedItemNS(attr: MLAttr<T, O>): MLAttr<T, O> | null;
}
export declare function toNamedNodeMap<T extends RuleConfigValue, O extends PlainData = undefined>(nodes: ReadonlyArray<MLAttr<T, O>>): MLNamedNodeMap<T, O>;

import { UnexpectedCallError } from './unexpected-call-error.js';
export class MLNamedNodeMap extends Array {
    getNamedItem(qualifiedName) {
        return this.find(attr => attr.name === qualifiedName) ?? null;
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    getNamedItemNS(namespace, localName) {
        throw new UnexpectedCallError('Not supported "getNamedItemNS" method');
    }
    item(index) {
        return this[index] ?? null;
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    removeNamedItem(qualifiedName) {
        throw new UnexpectedCallError('Not supported "removeNamedItem" method');
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    removeNamedItemNS(namespace, localName) {
        throw new UnexpectedCallError('Not supported "removeNamedItemNS" method');
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    setNamedItem(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    attr) {
        throw new UnexpectedCallError('Not supported "setNamedItem" method');
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `NamedNodeMap`
     */
    setNamedItemNS(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    attr) {
        throw new UnexpectedCallError('Not supported "setNamedItemNS" method');
    }
}
export function toNamedNodeMap(
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
nodes) {
    const namedNodeMap = new MLNamedNodeMap(...nodes);
    return namedNodeMap;
}

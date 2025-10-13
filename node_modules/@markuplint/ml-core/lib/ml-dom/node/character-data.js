import { after, before, nextElementSibling, previousElementSibling, remove, replaceWith, } from '../manipulations/child-node-methods.js';
import { MLNode } from './node.js';
import { UnexpectedCallError } from './unexpected-call-error.js';
export class MLCharacterData extends MLNode {
    /**
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-characterdata-data
     */
    get data() {
        // TODO:
        return this.raw;
    }
    /**
     * **IT THROWS AN ERROR WHEN CALLING THIS.**
     *
     * @unsupported
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-characterdata-length
     */
    get length() {
        throw new UnexpectedCallError('Not supported "length" property');
    }
    /**
     * The element immediately following the specified one in its parent's children list.
     *
     * @readonly
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-nextelementsibling%E2%91%A1
     */
    get nextElementSibling() {
        return nextElementSibling(this);
    }
    get nodeValue() {
        return this.data;
    }
    /**
     * The element immediately prior the specified one in its parent's children list.
     *
     * @readonly
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-previouselementsibling%E2%91%A1
     */
    get previousElementSibling() {
        return previousElementSibling(this);
    }
    /**
     * @implements DOM API: `CharacterData`
     * @see https://dom.spec.whatwg.org/#dom-node-textcontent
     */
    get textContent() {
        return this.data;
    }
    /**
     * @implements DOM API: `CharacterData`
     */
    after(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        after(this, ...nodes);
    }
    // TODO
    appendData(data) { }
    /**
     * @implements DOM API: `CharacterData`
     */
    before(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        before(this, ...nodes);
    }
    // TODO
    deleteData(offset, count) { }
    // TODO
    insertData(offset, data) { }
    /**
     * @implements DOM API: `CharacterData`
     */
    remove() {
        remove(this);
    }
    // TODO
    replaceData(offset, count, data) { }
    /**
     * @implements DOM API: `CharacterData`
     */
    replaceWith(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ...nodes) {
        replaceWith(this, ...nodes);
    }
    // TODO
    substringData(offset, count) {
        return '';
    }
}

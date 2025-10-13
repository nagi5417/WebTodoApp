var __classPrivateFieldSet = (this && this.__classPrivateFieldSet) || function (receiver, state, value, kind, f) {
    if (kind === "m") throw new TypeError("Private method is not writable");
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
    return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
};
var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _MLDomTokenList_origin, _MLDomTokenList_ownerAttrs, _MLDomTokenList_set;
import { getPosition } from '@markuplint/parser-utils/location';
import { UnexpectedCallError } from './unexpected-call-error.js';
export class MLDomTokenList extends Array {
    constructor(tokens, 
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    ownerAttrs) {
        const list = tokens
            .split(/\s+/)
            .map(t => t.trim())
            .filter(t => !!t);
        super(...list);
        _MLDomTokenList_origin.set(this, void 0);
        /**
         * In some cases, an author specifies multiple attributes or directives.
         * The reference is not always one.
         */
        _MLDomTokenList_ownerAttrs.set(this, void 0);
        _MLDomTokenList_set.set(this, void 0);
        __classPrivateFieldSet(this, _MLDomTokenList_origin, tokens, "f");
        __classPrivateFieldSet(this, _MLDomTokenList_ownerAttrs, ownerAttrs, "f");
        __classPrivateFieldSet(this, _MLDomTokenList_set, new Set(list), "f");
    }
    get value() {
        return this.join(' ');
    }
    add(...tokens) {
        for (const token of tokens) {
            if (__classPrivateFieldGet(this, _MLDomTokenList_set, "f").has(token)) {
                continue;
            }
            __classPrivateFieldGet(this, _MLDomTokenList_set, "f").add(token);
            this.push(token);
        }
        __classPrivateFieldSet(this, _MLDomTokenList_origin, __classPrivateFieldGet(this, _MLDomTokenList_origin, "f") + tokens.join(' '), "f");
    }
    /**
     * @implements `@markuplint/ml-core` API: `MLDomTokenList`
     */
    allTokens() {
        let offset = 0;
        const tokens = [...this];
        const locs = [];
        while (tokens.length > 0) {
            const token = tokens.shift();
            if (!token) {
                break;
            }
            const loc = this._pick(token, offset);
            if (!loc) {
                offset = 0;
                continue;
            }
            offset = loc._searchedIndex;
            locs.push({
                raw: loc.raw,
                startOffset: loc.startOffset,
                startLine: loc.startLine,
                startCol: loc.startCol,
                rule: loc.rule,
            });
        }
        return locs;
    }
    contains(token) {
        return __classPrivateFieldGet(this, _MLDomTokenList_set, "f").has(token);
    }
    forEach(callbackfn, thisArg) {
        this.forEach.bind(this)((v, i) => callbackfn(v, i, thisArg ?? this));
    }
    item(index) {
        return this[index] ?? null;
    }
    /**
     * @implements `@markuplint/ml-core` API: `MLDomTokenList`
     */
    pick(token) {
        const r = this._pick(token);
        if (!r) {
            return null;
        }
        return {
            raw: r.raw,
            startOffset: r.startOffset,
            startLine: r.startLine,
            startCol: r.startCol,
            rule: r.rule,
        };
    }
    remove(...tokens) {
        throw new UnexpectedCallError('Not supported "remove" method');
    }
    replace(token, newToken) {
        throw new UnexpectedCallError('Not supported "replace" method');
    }
    supports(token) {
        throw new UnexpectedCallError('Not supported "supports" method');
    }
    toString() {
        return this.value;
    }
    toggle(token, force) {
        throw new UnexpectedCallError('Not supported "toggle" method');
    }
    _pick(token, _offset = 0) {
        token = token.trim().split(/\s+/)[0] ?? '';
        if (!token) {
            return null;
        }
        for (const ownerAttr of __classPrivateFieldGet(this, _MLDomTokenList_ownerAttrs, "f")) {
            if (ownerAttr.isDynamicValue) {
                continue;
            }
            const startOffset = __classPrivateFieldGet(this, _MLDomTokenList_origin, "f").indexOf(token, _offset);
            if (startOffset === -1) {
                continue;
            }
            const position = getPosition(__classPrivateFieldGet(this, _MLDomTokenList_origin, "f"), startOffset);
            const startLine = position.line;
            const startCol = position.column;
            return {
                raw: token,
                startOffset: (ownerAttr.valueNode?.startOffset ?? 0) + startOffset,
                startLine: (ownerAttr.valueNode?.startLine ?? 0) + (startLine - 1),
                startCol: (ownerAttr.valueNode?.startCol ?? 0) + (startCol - 1),
                rule: ownerAttr.rule,
                _searchedIndex: startOffset,
            };
        }
        return null;
    }
}
_MLDomTokenList_origin = new WeakMap(), _MLDomTokenList_ownerAttrs = new WeakMap(), _MLDomTokenList_set = new WeakMap();

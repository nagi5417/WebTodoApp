import type { QuoteSet, ValueType } from './types.js';
import { AttrState } from './enums.js';
/**
 * @see https://html.spec.whatwg.org/multipage/parsing.html#tag-name-state
 * @see https://html.spec.whatwg.org/multipage/parsing.html#before-attribute-name-state
 * @see https://html.spec.whatwg.org/multipage/parsing.html#attribute-name-state
 */
export declare function attrTokenizer(raw: string, quoteSet?: readonly QuoteSet[], startState?: AttrState, noQuoteValueType?: ValueType, endOfUnquotedValueChars?: ReadonlyArray<string>): {
    spacesBeforeAttrName: string;
    attrName: string;
    spacesBeforeEqual: string;
    equal: string;
    spacesAfterEqual: string;
    quoteStart: string;
    attrValue: string;
    quoteEnd: string;
    leftover: string;
};

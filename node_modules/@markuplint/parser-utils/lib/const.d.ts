export declare const MASK_CHAR = "\uE000";
/**
 * SVG Element list
 *
 * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Element
 */
export declare const svgElementList: string[];
export declare const reTagName: RegExp;
export declare const reSplitterTag: RegExp;
/**
 * - U+0009 CHARACTER TABULATION (tab) => `\t`
 * - U+000A LINE FEED (LF) => `\n`
 * - U+000C FORM FEED (FF) => `\f`
 * - U+000D CARRIAGE RETURN (CR) => `\r`
 * - U+0020 SPACE => ` `
 */
export declare const defaultSpaces: readonly ["\t", "\n", "\f", "\r", " "];

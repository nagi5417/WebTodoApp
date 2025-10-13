import type { HtmlParser } from './parser.js';
import type { MLASTNodeTreeItem } from '@markuplint/ml-ast';
export type Replacements = {
    readonly code: string;
    readonly heads: readonly string[];
    readonly bodies: readonly string[];
};
export declare function isStartsHeadTagOrBodyTag(rawCode: string): boolean;
export declare function optimizeStartsHeadTagOrBodyTagSetup(rawCode: string): Replacements;
export declare function optimizeStartsHeadTagOrBodyTagResume(parser: HtmlParser, nodeList: readonly MLASTNodeTreeItem[], replacements: Replacements): readonly MLASTNodeTreeItem[];

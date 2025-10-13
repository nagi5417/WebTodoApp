import type { Parser } from './parser.js';
import type { IgnoreBlock, IgnoreTag } from './types.js';
import type { MLASTNodeTreeItem } from '@markuplint/ml-ast';
export declare function ignoreBlock(source: string, tags: readonly IgnoreTag[], maskChar?: string): IgnoreBlock;
export declare function restoreNode(parser: Parser<any, any>, nodeList: readonly MLASTNodeTreeItem[], ignoreBlock: IgnoreBlock, throwErrorWhenTagHasUnresolved?: boolean): MLASTNodeTreeItem[];

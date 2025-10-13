import type { MLDocument } from '../node/document.js';
import type { MappedNode } from '../node/types.js';
import type { MLASTNode } from '@markuplint/ml-ast';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
export declare function createNode<N extends MLASTNode, T extends RuleConfigValue, O extends PlainData = undefined>(astNode: N, document: MLDocument<T, O>): MappedNode<N, T, O>;

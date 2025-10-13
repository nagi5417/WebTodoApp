import type { MLChildNode } from './child-node.js';
import type { MLElement } from './element.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
export declare function toNodeList<T extends RuleConfigValue, O extends PlainData, N extends MLChildNode<T, O>>(nodes: ReadonlyArray<N>): NodeListOf<N>;
export declare function toHTMLCollection<T extends RuleConfigValue, O extends PlainData = undefined>(nodes: ReadonlyArray<MLElement<T, O>>): HTMLCollectionOf<MLElement<T, O>>;
export declare function nodeListToHTMLCollection<T extends RuleConfigValue, O extends PlainData = undefined>(nodeList: NodeListOf<MLChildNode<T, O>>): HTMLCollectionOf<MLElement<T, O>>;

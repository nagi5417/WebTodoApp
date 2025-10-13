import type { MLElement } from '../node/element.js';
import type { MLNode } from '../node/node.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
export declare function getChildren<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>): HTMLCollectionOf<MLElement<T, O>>;

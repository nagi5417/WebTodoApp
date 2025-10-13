import type { MLNode } from './node.js';
import type { MappedNode } from './types.js';
import type { MLASTNode } from '@markuplint/ml-ast';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
declare class NodeStore {
    #private;
    getNode<N extends MLASTNode, T extends RuleConfigValue, O extends PlainData = undefined>(astNode: N): MappedNode<N, T, O>;
    setNode<A extends MLASTNode, T extends RuleConfigValue, O extends PlainData = undefined>(astNode: A, node: MLNode<T, O, A>): void;
}
/**
 * `NodeStore` Singleton
 */
export declare const nodeStore: NodeStore;
export {};

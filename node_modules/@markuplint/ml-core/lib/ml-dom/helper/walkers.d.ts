import type { MLNode } from '../node/node.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
export type Walker<T extends RuleConfigValue, O extends PlainData = undefined, N = MLNode<T, O>> = (node: N) => void | Promise<void> | Promise<void>[];
export type SyncWalker<T extends RuleConfigValue, O extends PlainData = undefined, N = MLNode<T, O>> = (node: N) => void;
export declare function syncWalk<T extends RuleConfigValue, O extends PlainData = undefined>(nodeList: ReadonlyArray<MLNode<T, O>>, walker: SyncWalker<T, O>): void;
export declare function sequentialWalker<T extends RuleConfigValue, O extends PlainData = undefined, N = MLNode<T, O>>(list: ReadonlyArray<N>, walker: Walker<T, O, N>): Promise<void>;

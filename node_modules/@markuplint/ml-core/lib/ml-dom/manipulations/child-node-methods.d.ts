import type { MLElement } from '../node/element.js';
import type { MLNode } from '../node/node.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-before%E2%91%A0
 */
export declare function before<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>, ...additionalNodes: ReadonlyArray<MLNode<T, O> | string>): void;
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-after%E2%91%A0
 */
export declare function after<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>, ...additionalNodes: ReadonlyArray<MLNode<T, O> | string>): void;
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-replacewith%E2%91%A0
 */
export declare function replaceWith<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>, ...additionalNodes: ReadonlyArray<MLNode<T, O> | string>): void;
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-childnode-remove%E2%91%A0
 */
export declare function remove<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>): void;
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-previouselementsibling%E2%91%A0
 */
export declare function previousElementSibling<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>): MLElement<T, O> | null;
/**
 *
 * @see https://dom.spec.whatwg.org/#ref-for-dom-nondocumenttypechildnode-nextelementsibling%E2%91%A0
 */
export declare function nextElementSibling<T extends RuleConfigValue, O extends PlainData = undefined>(node: MLNode<T, O>): MLElement<T, O> | null;

import type { RuleSeed } from './types.js';
import type { PlainData, RuleConfigValue } from '@markuplint/ml-config';
import { MLRule } from './ml-rule.js';
export declare function createRule<T extends RuleConfigValue, O extends PlainData = undefined>(seed: Readonly<RuleSeed<T, O>> & {
    readonly name: string;
}): MLRule<T, O>;

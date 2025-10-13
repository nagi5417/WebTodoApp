import type { MLDocument } from './document.js';
import type { MLNode } from './node.js';
import type { AnyRule } from '@markuplint/ml-config';
import type { Specificity } from '@markuplint/selector';
type RuleType = 'rules' | 'nodeRules' | 'childNodeRules';
type MappingLayer = {
    readonly from: RuleType;
    readonly specificity: Specificity;
    readonly rule: AnyRule;
};
export declare class RuleMapper {
    #private;
    constructor(document: MLDocument<any, any>);
    apply(): void;
    set(node: MLNode<any, any>, ruleName: string, rule: MappingLayer): void;
}
export {};

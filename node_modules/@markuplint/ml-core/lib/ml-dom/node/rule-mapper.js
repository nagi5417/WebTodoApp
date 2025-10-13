var __classPrivateFieldSet = (this && this.__classPrivateFieldSet) || function (receiver, state, value, kind, f) {
    if (kind === "m") throw new TypeError("Private method is not writable");
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
    return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
};
var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _RuleMapper_nodeList, _RuleMapper_ruleMap;
import { compareSpecificity } from '@markuplint/selector';
import { log as coreLog } from '../../debug.js';
const ruleMapperLog = coreLog.extend('rule-mapper');
const ruleMapperNodeLog = ruleMapperLog.extend('node');
const ruleMapperNodeRuleLog = ruleMapperNodeLog.extend('rule');
export class RuleMapper {
    constructor(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    document) {
        _RuleMapper_nodeList.set(this, void 0);
        _RuleMapper_ruleMap.set(this, new Map());
        __classPrivateFieldSet(this, _RuleMapper_nodeList, Object.freeze([document, ...document.nodeList]), "f");
    }
    apply() {
        ruleMapperLog('ruleTree:');
        for (const node of __classPrivateFieldGet(this, _RuleMapper_nodeList, "f")) {
            const rules = __classPrivateFieldGet(this, _RuleMapper_ruleMap, "f").get(node.uuid);
            if (!rules) {
                continue;
            }
            ruleMapperNodeLog('<%s>', node.nodeName);
            for (const ruleName of Object.keys(rules)) {
                const rule = rules[ruleName];
                if (!rule) {
                    continue;
                }
                node.rules[ruleName] = rule.rule;
                ruleMapperNodeRuleLog('[from: %s(%s)] %s: %o', rule.from, rule.specificity, ruleName, rule.rule);
            }
        }
    }
    set(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    node, ruleName, rule) {
        const rules = __classPrivateFieldGet(this, _RuleMapper_ruleMap, "f").get(node.uuid) ?? {};
        const currentRule = rules[ruleName];
        if (currentRule) {
            const order = compareSpecificity(currentRule.specificity, rule.specificity);
            if (order === 1) {
                ruleMapperLog("Don't set %o ([%s] vs [%s])", rule, currentRule.specificity, rule.specificity);
                return;
            }
            ruleMapperLog('Unset %o from %s', currentRule, node);
        }
        rules[ruleName] = rule;
        __classPrivateFieldGet(this, _RuleMapper_ruleMap, "f").set(node.uuid, rules);
        ruleMapperLog('Set to %s from %s (%o): %O', node.nodeName, rule.from, rule.specificity, rule.rule);
    }
}
_RuleMapper_nodeList = new WeakMap(), _RuleMapper_ruleMap = new WeakMap();

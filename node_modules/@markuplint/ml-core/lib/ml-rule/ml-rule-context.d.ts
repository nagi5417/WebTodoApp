import type { CheckerReport } from './types.js';
import type { MLDocument } from '../ml-dom/node/document.js';
import type { LocaleSet, Translator } from '@markuplint/i18n';
import type { PlainData, Report, RuleConfigValue } from '@markuplint/ml-config';
export declare class MLRuleContext<T extends RuleConfigValue, O extends PlainData = undefined> {
    #private;
    readonly document: MLDocument<T, O>;
    readonly locale: string;
    readonly translate: Translator;
    constructor(document: MLDocument<T, O>, locale: LocaleSet);
    get reports(): ({
        message: string;
        line: number;
        col: number;
        raw: string;
    } | {
        message: string;
        scope: import("@markuplint/ml-config").Scope<T, O>;
    } | {
        message: string;
        scope: import("@markuplint/ml-config").Scope<T, O>;
        line: number;
        col: number;
        raw: string;
    })[];
    provide(): {
        document: MLDocument<T, O>;
        translate: Translator;
        t: Translator;
        reports: ({
            message: string;
            line: number;
            col: number;
            raw: string;
        } | {
            message: string;
            scope: import("@markuplint/ml-config").Scope<T, O>;
        } | {
            message: string;
            scope: import("@markuplint/ml-config").Scope<T, O>;
            line: number;
            col: number;
            raw: string;
        })[];
        report: {
            (report: Report<T, O>): undefined;
            (report: CheckerReport<T, O>): boolean;
        };
    };
    report(report: Report<T, O>): undefined;
    report(report: CheckerReport<T, O>): boolean;
    private _push;
}

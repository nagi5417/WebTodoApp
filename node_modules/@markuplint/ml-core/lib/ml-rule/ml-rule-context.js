var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _MLRuleContext_reports;
import { translator } from '@markuplint/i18n';
export class MLRuleContext {
    constructor(
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    document, locale) {
        _MLRuleContext_reports.set(this, []);
        this.document = document;
        this.translate = translator(locale);
        this.locale = locale.locale;
    }
    get reports() {
        return __classPrivateFieldGet(this, _MLRuleContext_reports, "f").map(report => ({
            ...report,
            message: finish(report.message, this.locale),
        }));
    }
    provide() {
        return {
            document: this.document,
            translate: this.translate,
            t: this.translate,
            reports: this.reports,
            report: this.report.bind(this),
        };
    }
    report(report) {
        if (typeof report === 'function') {
            const r = report(this.translate);
            if (r) {
                this._push(r);
                return true;
            }
            return false;
        }
        this._push(report);
    }
    _push(report) {
        if (!__classPrivateFieldGet(this, _MLRuleContext_reports, "f").some(r => is(r, report))) {
            __classPrivateFieldGet(this, _MLRuleContext_reports, "f").push(report);
        }
    }
}
_MLRuleContext_reports = new WeakMap();
function finish(message, locale = 'en') {
    switch (locale) {
        case 'en': {
            return message.replace(/^[a-z]/, $0 => $0.toUpperCase());
        }
    }
    return message;
}
function is(r1, r2) {
    if ('col' in r1 && 'col' in r2) {
        return r1.col === r2.col && r1.line === r2.line && r1.message === r2.message && r1.raw === r2.raw;
    }
    if ('scope' in r1) {
        if (!('scope' in r2)) {
            return false;
        }
        return r1.scope === r2.scope && r1.message === r2.message;
    }
    return false;
}

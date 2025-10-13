var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var __classPrivateFieldSet = (this && this.__classPrivateFieldSet) || function (receiver, state, value, kind, f) {
    if (kind === "m") throw new TypeError("Private method is not writable");
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a setter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot write private member to an object whose class did not declare it");
    return (kind === "a" ? f.call(receiver, value) : f ? f.value = value : state.set(receiver, value)), value;
};
var _PerformanceTimer_logs, _PerformanceTimer_counter;
import debug from 'debug';
import { nodeListToDebugMaps } from './debugger.js';
export const log = debug('ml-parser');
export function domLog(nodeList) {
    log('Parse result: %O', nodeListToDebugMaps(nodeList, true));
}
export class PerformanceTimer {
    constructor() {
        _PerformanceTimer_logs.set(this, []);
        _PerformanceTimer_counter.set(this, -1);
    }
    push(name) {
        var _a;
        if (!log.enabled) {
            return '';
        }
        __classPrivateFieldSet(this, _PerformanceTimer_counter, (_a = __classPrivateFieldGet(this, _PerformanceTimer_counter, "f"), _a++, _a), "f");
        const now = performance.now();
        const last = __classPrivateFieldGet(this, _PerformanceTimer_logs, "f").at(-1);
        if (last && Number.isNaN(last[2])) {
            last[2] = now;
        }
        name = name || `#${__classPrivateFieldGet(this, _PerformanceTimer_counter, "f")}`;
        __classPrivateFieldGet(this, _PerformanceTimer_logs, "f").push([name, now, Number.NaN]);
    }
    log() {
        if (!log.enabled) {
            return;
        }
        this.push('end');
        __classPrivateFieldGet(this, _PerformanceTimer_logs, "f").pop();
        const map = new Map();
        for (const content of __classPrivateFieldGet(this, _PerformanceTimer_logs, "f")) {
            const diff = content[2] - content[1];
            const name = content[0];
            if (map.has(name)) {
                const [total, count] = map.get(name);
                map.set(name, [total + diff, count + 1]);
            }
            else {
                map.set(name, [diff, 1]);
            }
        }
        for (const [name, [total, count]] of map) {
            const avg = total / count;
            log.extend(name)('%dms (avg: %dms, count: %d)', total.toExponential(3), avg.toExponential(3), count);
        }
    }
}
_PerformanceTimer_logs = new WeakMap(), _PerformanceTimer_counter = new WeakMap();

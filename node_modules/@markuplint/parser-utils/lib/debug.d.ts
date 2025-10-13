import type { MLASTNode } from '@markuplint/ml-ast';
import debug from 'debug';
export declare const log: debug.Debugger;
export declare function domLog(nodeList: readonly (MLASTNode | null)[]): void;
export declare class PerformanceTimer {
    #private;
    push(name?: string): "" | undefined;
    log(): void;
}

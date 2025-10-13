/**
 * @deprecated Use {@link getPosition} instead. Will be removed in v5.0.0.
 */
export declare function getLine(rawCodeFragment: string, startOffset: number): number;
/**
 * @deprecated Use {@link getPosition} instead. Will be removed in v5.0.0.
 */
export declare function getCol(rawCodeFragment: string, startOffset: number): number;
export declare function getPosition(rawCodeFragment: string, startOffset: number): {
    readonly line: number;
    readonly column: number;
};
export declare function getEndLine(rawCodeFragment: string, startLine: number): number;
export declare function getEndCol(rawCodeFragment: string, startCol: number): number;
export declare function getOffsetsFromCode(rawCode: string, startLine: number, startCol: number, endLine: number, endCol: number): {
    offset: number;
    endOffset: number;
};

interface Location {
    line: number;
    col: number;
    raw: string;
}
export declare function getLocationFromChars(searches: readonly string[], text: string, currentLine: number, currentCol: number): Location[];
export {};

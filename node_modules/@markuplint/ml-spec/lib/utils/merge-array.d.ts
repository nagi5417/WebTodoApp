type NamedDefinition = string | {
    readonly name: string;
};
export declare function mergeArray<T extends NamedDefinition>(a: readonly T[], b: readonly T[] | null | undefined): readonly T[];
export {};

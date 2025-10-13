import type { Nullable } from './types.js';
/**
 * Converts a given value of string, string array, null, or undefined
 * into an array of non-empty strings.
 *
 * If a string is provided, it wraps it into an array. If null or undefined
 * is provided, an empty array is returned.
 *
 * @param value The input value to be converted to a non-empty string array.
 * @returns The resulting array of non-empty strings derived from the input value.
 */
export declare function toNoEmptyStringArrayFromStringOrArray(value: string | readonly string[] | null | undefined): readonly string[];
/**
 * Converts a given value of an item or an array of items into
 * an array of non-nullable items.
 *
 * @template T The type of the items in the input value.
 * @param value The input value to be converted to a non-nullable item array.
 * @returns The resulting array of non-nullable items derived from the input value.
 */
export declare function toNonNullableArrayFromItemOrArray<T>(value: T | readonly T[]): readonly NonNullable<T>[];
/**
 * A filter function for use with the `Array.filter` method,
 * which determines if the given string item is non-empty.
 *
 * @param item The string item to be checked for non-emptiness.
 * @returns Returns true if the item is a non-empty string, otherwise false.
 */
export declare function noEmptyFilter(item: string): item is string;
/**
 * A filter function for use with the Array.filter method,
 * which determines if the given item is non-nullable.
 *
 * @template T The type of the items in the array.
 * @param item The item to be checked for non-nullability.
 * @returns Returns true if the item is non-nullable, otherwise false.
 */
export declare function nonNullableFilter<T>(item: Nullable<T>): item is T;
/**
 * Decodes the provided text by replacing HTML entities
 * with their corresponding characters.
 *
 * The decoding process uses the 'html5' (HTML Standard) level.
 *
 * Unknown entities are left as they are.
 *
 * @param text The input text containing HTML entities to be decoded.
 * @returns The decoded text with HTML entities replaced by their corresponding characters.
 */
export declare function decodeEntities(text: string): string;
/**
 * Decodes the provided URL string (href) using
 * the `decodeURIComponent` function.
 *
 * If a `URIError` is encountered,
 * the original href is returned. Any other errors are propagated.
 *
 * @param href The URL string to be decoded.
 * @returns The decoded URL string or the original href if a `URIError` occurs.
 */
export declare function decodeHref(href: string): string;
/**
 * Converts an array of branches into an array of patterns.
 *
 * @example
 * ```ts
 * branchesToPatterns([1, [2, 3]]) // [[1, 2], [1, 3]]
 * branchesToPatterns([1, [2, 3], 4]) // [[1, 2, 4], [1, 3, 4]]
 * branchesToPatterns([1, [2, undefined], 3]) // [[1, 2, 3], [1, 3]]
 * branchesToPatterns([1, [2, 3], [4, 5], 6]) // [[1, 2, 4, 6], [1, 3, 4, 6], [1, 2, 5, 6], [1, 3, 5, 6]]
 * branchesToPatterns([1, [], 2]) // [[1, 2]]
 * ```
 *
 * @template T The type of elements in the branches array.
 * @param branches The array of branches to convert.
 * @returns The array of patterns generated from the branches.
 */
export declare function branchesToPatterns<T>(branches: ReadonlyArray<Nullable<T> | ReadonlyArray<Nullable<T>>>): T[][];
/**
 * Parses a regular expression pattern in the form of `/pattern/flags`.
 * The pattern is parsed using the `RegExp` constructor.
 * If the pattern is invalid, `null` is returned.
 * The pattern must be a valid regular expression pattern.
 * Throws an error if the pattern or flags are invalid.
 *
 * @param regexpLikeString - The regular expression pattern to parse.
 * @returns - The parsed regular expression or null if the pattern is invalid.
 */
export declare function regexParser(regexpLikeString: string): RegExp | null;

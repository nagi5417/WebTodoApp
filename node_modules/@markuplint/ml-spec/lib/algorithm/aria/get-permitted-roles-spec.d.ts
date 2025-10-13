import type { ARIAVersion, Matches, MLMLSpec } from '../../types/index.js';
/**
 * Getting permitted ARIA roles.
 *
 * - If an array, it is role list.
 * - If `true`, this mean is "Any".
 * - If `false`, this mean is "No".
 */
export declare function getPermittedRoles(specs: MLMLSpec, localName: string, namespace: string | null, version: ARIAVersion, matches: Matches): readonly {
    readonly name: string;
    readonly deprecated?: boolean;
}[];

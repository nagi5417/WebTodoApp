// @ts-ignore
import { isPlainObject } from 'is-plain-object';
import { mergeArray } from '../../utils/merge-array.js';
import { ariaSpecs } from './aria-specs.js';
import { getARIA } from './get-aria.js';
/**
 * Getting permitted ARIA roles.
 *
 * - If an array, it is role list.
 * - If `true`, this mean is "Any".
 * - If `false`, this mean is "No".
 */
export function getPermittedRoles(specs, localName, namespace, version, matches) {
    const aria = getARIA(specs, localName, namespace, version, matches);
    if (!aria) {
        return [];
    }
    const { implicitRole, permittedRoles } = aria;
    const { roles, graphicsRoles } = ariaSpecs(specs, version);
    let permittedRoleList = [];
    if (permittedRoles === true) {
        permittedRoleList = mergeArray(permittedRoleList, roles
            .filter(role => !role.isAbstract)
            .map(role => ({
            name: role.name,
        })));
    }
    if (isAAMInfo(permittedRoles)) {
        if (permittedRoles['core-aam']) {
            permittedRoleList = mergeArray(permittedRoleList, roles
                .filter(role => !role.isAbstract)
                .map(role => ({
                name: role.name,
            })));
        }
        if (permittedRoles['graphics-aam']) {
            permittedRoleList = mergeArray(permittedRoleList, graphicsRoles
                .filter(role => !role.isAbstract)
                .map(role => ({
                name: role.name,
            })));
        }
    }
    else if (typeof permittedRoles !== 'boolean') {
        permittedRoleList = mergeArray(permittedRoleList, permittedRoles.map(role => {
            if (typeof role === 'string') {
                return {
                    name: role,
                };
            }
            return role;
        }));
    }
    if (implicitRole === false) {
        return permittedRoleList;
    }
    const implicitRoles = implicitRole === 'presentation' || implicitRole === 'none' ? ['none', 'presentation'] : [implicitRole];
    return mergeArray(implicitRoles.map(r => ({ name: r })), permittedRoleList);
}
function isAAMInfo(permittedRoles) {
    return isPlainObject(permittedRoles);
}

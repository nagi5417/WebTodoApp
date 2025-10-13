import { getARIA } from './get-aria.js';
export function getImplicitRole(specs, localName, namespace, version, matches) {
    const aria = getARIA(specs, localName, namespace, version, matches);
    if (!aria) {
        return false;
    }
    return aria.implicitRole;
}

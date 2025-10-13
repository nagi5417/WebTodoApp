import { isCustomElementName } from '@markuplint/types';
import { svgElementList } from './const.js';
/**
 *
 *
 * @param nodeName
 * @returns
 */
export function isSVGElement(nodeName) {
    return svgElementList.includes(nodeName);
}
const isCEN = isCustomElementName();
export function isPotentialCustomElementName(tagName) {
    return isCEN(tagName);
}

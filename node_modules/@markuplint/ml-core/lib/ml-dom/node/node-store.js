var __classPrivateFieldGet = (this && this.__classPrivateFieldGet) || function (receiver, state, kind, f) {
    if (kind === "a" && !f) throw new TypeError("Private accessor was defined without a getter");
    if (typeof state === "function" ? receiver !== state || !f : !state.has(receiver)) throw new TypeError("Cannot read private member from an object whose class did not declare it");
    return kind === "m" ? f : kind === "a" ? f.call(receiver) : f ? f.value : state.get(receiver);
};
var _NodeStore_store;
import { TargetParserError } from '@markuplint/parser-utils';
import { log } from '../../debug.js';
const nodeStoreLog = log.extend('node-store');
const nodeStoreError = nodeStoreLog.extend('error');
class NodeStore {
    constructor() {
        _NodeStore_store.set(this, new Map());
    }
    getNode(astNode) {
        // console.log(`Get: ${astNode.uuid} -> ${astNode.raw.trim()}(${astNode.type})`);
        const node = __classPrivateFieldGet(this, _NodeStore_store, "f").get(astNode.uuid);
        if (!node) {
            nodeStoreError('Ref ID: %s (%s: "%s")', astNode.uuid, astNode.nodeName, astNode.raw);
            nodeStoreError('Map: %O', [...__classPrivateFieldGet(this, _NodeStore_store, "f").entries()].map(([id, node]) => ({
                id,
                name: node.nodeName,
            })));
            throw new TargetParserError('Broke mapping nodes.', {
                line: astNode.startLine,
                col: astNode.startCol,
                raw: astNode.raw,
                nodeName: astNode.nodeName,
            });
        }
        return node;
    }
    setNode(astNode, 
    // eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
    node) {
        if (node.is(node.DOCUMENT_NODE)) {
            return;
        }
        if (!astNode.uuid) {
            nodeStoreError('UUID is invalid: %s (%s: "%s")', astNode.uuid, astNode.nodeName, astNode.raw);
            nodeStoreError('Invalid node: %O', node);
        }
        nodeStoreLog('Mapped: %s (%s: "%s")', astNode.uuid, astNode.nodeName, astNode.raw.replaceAll('\n', '⏎').replaceAll('\t', '→'));
        __classPrivateFieldGet(this, _NodeStore_store, "f").set(astNode.uuid, node);
    }
}
_NodeStore_store = new WeakMap();
/**
 * `NodeStore` Singleton
 */
export const nodeStore = new NodeStore();

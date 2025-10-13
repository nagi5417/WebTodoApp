import { MLBlock } from '../node/block.js';
import { MLComment } from '../node/comment.js';
import { MLDocumentType } from '../node/document-type.js';
import { MLElement } from '../node/element.js';
import { MLText } from '../node/text.js';
export function createNode(astNode, 
// eslint-disable-next-line @typescript-eslint/prefer-readonly-parameter-types
document) {
    switch (astNode.type) {
        case 'doctype': {
            return new MLDocumentType(astNode, document);
        }
        case 'starttag': {
            return new MLElement(astNode, document);
        }
        case 'psblock': {
            return new MLBlock(astNode, document);
        }
        case 'comment': {
            return new MLComment(astNode, document);
        }
        case 'text': {
            return new MLText(astNode, document);
        }
        case 'invalid': {
            switch (astNode.kind) {
                case 'starttag': {
                    return new MLElement({
                        ...astNode,
                        type: 'starttag',
                        nodeName: 'x-invalid',
                        namespace: 'http://www.w3.org/1999/xhtml',
                        elementType: 'web-component',
                        attributes: [],
                        childNodes: [],
                        pairNode: null,
                        tagOpenChar: '',
                        tagCloseChar: '',
                        isGhost: false,
                        isFragment: false,
                    }, document);
                }
                default: {
                    return new MLText({
                        ...astNode,
                        type: 'text',
                        nodeName: '#text',
                    }, document);
                }
            }
        }
    }
    throw new TypeError(`Invalid AST node types "${astNode.type}"`);
}

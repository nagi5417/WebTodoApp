export function getNS(namespaceURI) {
    switch (namespaceURI) {
        case 'http://www.w3.org/2000/svg': {
            return 'svg';
        }
        case 'http://www.w3.org/1998/Math/MathML': {
            return 'mml';
        }
        case 'http://www.w3.org/1999/xlink': {
            return 'xlink';
        }
        default: {
            return 'html';
        }
    }
}

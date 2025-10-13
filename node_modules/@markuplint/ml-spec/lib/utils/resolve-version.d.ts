import type { ARIA } from '../types/aria.js';
import type { ARIAVersion } from '../types/index.js';
import type { ReadonlyDeep } from 'type-fest';
export declare function resolveVersion(aria: ReadonlyDeep<ARIA>, version: ARIAVersion): Omit<ReadonlyDeep<ARIA>, ARIAVersion>;

export class ConfigLoadError extends Error {
    constructor(message, filePath, referrer) {
        super(message + ` in ${referrer}`);
        this.name = 'ConfigLoadError';
        this.filePath = filePath;
        this.referrer = referrer;
    }
}

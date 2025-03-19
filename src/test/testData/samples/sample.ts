interface ValidatorOptions {
    minLength?: number,
}

type ValidatorDescriptor = {
    validatorOptions: ValidatorOptions,
    describe(): void,
};



function globalFunction({ validatorOptions = {} }: ValidatorDescriptor) {
    const { minLength } = validatorOptions;

interface ButtonProps {
    click(): void;
}

function defineElement(tagName: string) {
    return function <T extends { new (...args: any[]): {} }>(constructor: T) {
        return class extends constructor {
            static tagName = tagName;
        };
    };
}

@defineElement("download-button")
class DownloadButton<T extends Record<string, string>> extends HTMLButtonElement {
    static STATIC_FIELD = `<span title="HTML injection">${globalVariable}</span>`;

    static get observedAttributes(): string[] {
        return ['data-test'];
    }

    #field = { prop: 1 };

    public method(props: T) {
        this.click();

        label:
            while (true) {
                break label;
            }
    }
}

enum EnumName {
    EnumMember,
}

module Test {
    declare function run(): void;
}

export const EXPORTED_VARIABLE = 1;
export function exportedFunction() {}
export class ExportedClass {}

const globalVariable = "chars\n\u11";
const templateLiteral: `Template ${string | number} type` = `Template ${globalVariable} type`;

#
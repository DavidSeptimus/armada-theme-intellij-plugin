type ValidatorDescriptor = {
    validatorOptions: ValidatorOptions,
    describe(): void,
};

const globalVariable = "\tdata\n";
const templateLiteral: `Template ${string | number} type` = `Template ${globalVariable} type`;

export function globalFunction({validatorOptions = {}}: ValidatorDescriptor) {
    const {minLength} = validatorOptions;
    console.log(minLength);
}

interface ButtonProps {
    click(): void;
}

function defineElement(tagName: string) {
    return function <T extends { new(...args: any[]): {} }>(constructor: T) {
        return class extends constructor {
            static tagName = tagName;
        };
    };
}

@defineElement("download-button")
class DownloadButton<T extends Record<string, string>> extends HTMLButtonElement {
    static STATIC_FIELD = `<span title="HTML injection">${globalVariable}</span>`;

    // @ts-ignorexx
    #field = {prop: 1};

    public method(props: T) {
        this.click();

        label:
            while (true) {
                break label;
            }
    }

}

new DownloadButton().method({key: "value"});

enum EnumName {
    EnumMember,
}

module Test {
    declare function run(): void;
}

export const EXPORTED_VARIABLE = 1;

globalFunction({} as any)

const x: ButtonProps = {} as ButtonProps;
console.log(templateLiteral)

export class ExportedClass {
}

interface ValidatorOptions {
    minLength?: number,
}
console.log(DownloadButton.STATIC_FIELD)
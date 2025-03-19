// ASCII Color Generator - Simple Version
// This script prints a single line of text in each available color using ANSI escape codes

// Define color codes for foreground colors (30-37 for standard, 90-97 for bright)
type ColorMap = {
    [key: string]: number;
};

const foregroundColors: ColorMap = {
    black: 30,
    red: 31,
    green: 32,
    yellow: 33,
    blue: 34,
    magenta: 35,
    cyan: 36,
    white: 37,
    brightBlack: 90,
    brightRed: 91,
    brightGreen: 92,
    brightYellow: 93,
    brightBlue: 94,
    brightMagenta: 95,
    brightCyan: 96,
    brightWhite: 97,
};

const backgroundColors: ColorMap = {
    bgBlack: 40,
    bgRed: 41,
    bgGreen: 42,
    bgYellow: 43,
    bgBlue: 44,
    bgMagenta: 45,
    bgCyan: 46,
    bgWhite: 47,
    bgBrightBlack: 100,
    bgBrightRed: 101,
    bgBrightGreen: 102,
    bgBrightYellow: 103,
    bgBrightBlue: 104,
    bgBrightMagenta: 105,
    bgBrightCyan: 106,
    bgBrightWhite: 107,
};

// Function to color text with ANSI escape codes
function colorize(
    text: string,
    fgColor?: keyof typeof foregroundColors,
    bgColor?: keyof typeof backgroundColors,
    bold: boolean = false
): string {
    let colorCode = '\x1b[';
    const codes: number[] = [];

    if (bold) {
        codes.push(1);
    }

    if (fgColor && foregroundColors[fgColor] !== undefined) {
        codes.push(foregroundColors[fgColor]);
    }

    if (bgColor && backgroundColors[bgColor] !== undefined) {
        codes.push(backgroundColors[bgColor]);
    }

    colorCode += codes.join(';') + 'm';

    return colorCode + text + '\x1b[0m'; // Reset formatting at the end
}

// Function to print one line in each color
function printAllColors(text: string = "Hello, Colorful World!"): void {
    console.log("=== FOREGROUND COLORS ===");
    Object.keys(foregroundColors).forEach((color) => {
        console.log(`${color.padEnd(15)}: ${colorize(text, color as keyof typeof foregroundColors)}`);
    });

    console.log("\n=== BACKGROUND COLORS (with contrasting text) ===");
    Object.keys(backgroundColors).forEach((bgColor) => {
        // Choose contrasting text color for better visibility
        const textColor = bgColor.includes("Black") ||
        bgColor.includes("Blue") ||
        bgColor.includes("Green") ||
        bgColor.includes("Magenta") ?
            "white" : "black";

        console.log(`${bgColor.padEnd(15)}: ${colorize(text, textColor as keyof typeof foregroundColors, bgColor as keyof typeof backgroundColors)}`);
    });
}

// Get command line argument or use default text
const textToDisplay = process.argv[2] || "Hello, Colorful World!";
console.log(`Printing "${textToDisplay}" in all available colors:\n`);
printAllColors(textToDisplay);

// Usage instructions
// console.log("\n=== USAGE ===");
// console.log("Run this script with your own text:");
// console.log("ts-node ascii-colors.ts \"Your Text Here\"");
// console.log("\nOr compile it first with tsc and then run:");
// console.log("node ascii-colors.js \"Your Text Here\"");
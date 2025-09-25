#!/bin/bash

echo "=== Basic 8 Colors (30-37) ==="
echo -e "\033[30mBlack text\033[0m"
echo -e "\033[31mRed text\033[0m"
echo -e "\033[32mGreen text\033[0m"
echo -e "\033[33mYellow text\033[0m"
echo -e "\033[34mBlue text\033[0m"
echo -e "\033[35mMagenta text\033[0m"
echo -e "\033[36mCyan text\033[0m"
echo -e "\033[37mWhite text\033[0m"

echo -e "\n=== Bright/High Intensity Colors (90-97) ==="
echo -e "\033[90mBright Black (Gray)\033[0m"
echo -e "\033[91mBright Red\033[0m"
echo -e "\033[92mBright Green\033[0m"
echo -e "\033[93mBright Yellow\033[0m"
echo -e "\033[94mBright Blue\033[0m"
echo -e "\033[95mBright Magenta\033[0m"
echo -e "\033[96mBright Cyan\033[0m"
echo -e "\033[97mBright White\033[0m"

echo -e "\n=== Background Colors (40-47) ==="
echo -e "\033[40mBlack background\033[0m"
echo -e "\033[41mRed background\033[0m"
echo -e "\033[42mGreen background\033[0m"
echo -e "\033[43mYellow background\033[0m"
echo -e "\033[44mBlue background\033[0m"
echo -e "\033[45mMagenta background\033[0m"
echo -e "\033[46mCyan background\033[0m"
echo -e "\033[47mWhite background\033[0m"

echo -e "\n=== Bright Background Colors (100-107) ==="
echo -e "\033[100mBright Black background\033[0m"
echo -e "\033[101mBright Red background\033[0m"
echo -e "\033[102mBright Green background\033[0m"
echo -e "\033[103mBright Yellow background\033[0m"
echo -e "\033[104mBright Blue background\033[0m"
echo -e "\033[105mBright Magenta background\033[0m"
echo -e "\033[106mBright Cyan background\033[0m"
echo -e "\033[107mBright White background\033[0m"

echo -e "\n=== Text Styles ==="
echo -e "\033[1mBold text\033[0m"
echo -e "\033[2mDim/Faint text\033[0m"
echo -e "\033[3mItalic text\033[0m"
echo -e "\033[4mUnderlined text\033[0m"
echo -e "\033[5mBlinking text\033[0m"
echo -e "\033[7mReversed (inverted) text\033[0m"
echo -e "\033[8mHidden text\033[0m"
echo -e "\033[9mStrikethrough text\033[0m"

echo -e "\n=== Combined Styles ==="
echo -e "\033[1;31mBold Red\033[0m"
echo -e "\033[4;32mUnderlined Green\033[0m"
echo -e "\033[1;4;33mBold Underlined Yellow\033[0m"
echo -e "\033[7;35mReversed Magenta\033[0m"
echo -e "\033[1;97;41mBold White on Red\033[0m"

echo -e "\n=== 256-Color Mode (Sample) ==="
# Display some 256-color examples
echo -e "\033[38;5;196mColor 196 (Bright Red)\033[0m"
echo -e "\033[38;5;46mColor 46 (Bright Green)\033[0m"
echo -e "\033[38;5;21mColor 21 (Bright Blue)\033[0m"
echo -e "\033[38;5;201mColor 201 (Bright Magenta)\033[0m"
echo -e "\033[38;5;208mColor 208 (Orange)\033[0m"

echo -e "\n=== 256-Color Palette ==="
# Display the full 256-color palette
echo "Standard colors (0-15):"
for i in {0..15}; do
    printf "\033[48;5;${i}m  \033[0m"
    if [ $((($i + 1) % 8)) -eq 0 ]; then echo; fi
done

echo -e "\n216-color cube (16-231):"
for i in {16..231}; do
    printf "\033[48;5;${i}m  \033[0m"
    if [ $((($i - 15) % 36)) -eq 0 ]; then echo; fi
done

echo -e "\nGrayscale (232-255):"
for i in {232..255}; do
    printf "\033[48;5;${i}m  \033[0m"
done
echo

echo -e "\n=== True Color (24-bit RGB) Examples ==="
echo -e "\033[38;2;255;0;0mTrue Color Red (RGB 255,0,0)\033[0m"
echo -e "\033[38;2;0;255;0mTrue Color Green (RGB 0,255,0)\033[0m"
echo -e "\033[38;2;0;0;255mTrue Color Blue (RGB 0,0,255)\033[0m"
echo -e "\033[38;2;255;165;0mTrue Color Orange (RGB 255,165,0)\033[0m"
echo -e "\033[38;2;128;0;128mTrue Color Purple (RGB 128,0,128)\033[0m"

echo -e "\n=== Rainbow Gradient ==="
for i in {0..255}; do
    r=$((255 - i))
    g=$i
    b=$((i / 2))
    printf "\033[48;2;${r};${g};${b}m \033[0m"
done
echo

echo -e "\n=== One-liners for quick testing ==="
echo "# Basic colors test:"
echo 'for i in {30..37}; do echo -e "\033[${i}mColor $i\033[0m"; done'

echo -e "\n# Background colors test:"
echo 'for i in {40..47}; do echo -e "\033[${i}mBackground $i\033[0m"; done'

echo -e "\n# 256-color test:"
echo 'for i in {0..255}; do printf "\033[38;5;${i}mColor$i \033[0m"; done; echo'

echo -e "\n# True color gradient:"
echo 'for i in {0..255}; do printf "\033[38;2;${i};$((255-i));128mX\033[0m"; done; echo'




T='gYw' # The test text

echo -e "\n                 40m     41m     42m     43m\
     44m     45m     46m     47m";

for FGs in '    m' '   1m' '  30m' '1;30m' '  31m' '1;31m' '  32m' \
           '1;32m' '  33m' '1;33m' '  34m' '1;34m' '  35m' '1;35m' \
           '  36m' '1;36m' '  37m' '1;37m';
  do FG=${FGs// /}
  echo -en " $FGs \033[$FG  $T  "
  for BG in 40m 41m 42m 43m 44m 45m 46m 47m;
    do echo -en "$EINS \033[$FG\033[$BG  $T  \033[0m";
  done
  echo;
done
echo

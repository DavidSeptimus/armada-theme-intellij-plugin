# Armada IntelliJ Theme

![Build](https://github.com/DavidSeptimus/armada-theme-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26844-armada-theme.svg)](https://plugins.jetbrains.com/plugin/26844-armada-theme)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26844-armada-theme.svg)](https://plugins.jetbrains.com/plugin/26844-armada-theme)

## Description

<!-- Plugin description -->
Armada is a plugin that aims to faithfully recreate the Fleet's builtin themes for IntelliJ-based IDEs.

### Features

- Fleet-inspired Dark and Light UI themes/Editor Color Schemes
- Includes UI theme variants for all UI versions:
  - [Islands](https://blog.jetbrains.com/platform/2025/09/islands-theme-the-new-look-coming-to-jetbrains-ides/) (2025.2.3+)
  - New UI (default since 2024.2)
  - [Classic UI](https://plugins.jetbrains.com/plugin/24468-classic-ui)
- Custom highlighting tokens for improved highlighting accuracy in various languages
- Explicit support for over 25 programming languages with specially tailored colors

### Coming Soon

**Armada Pro** - A paid version of the plugin that will include additional themes and color schemes including:

- **Dark Purple** - Adapted from Fleet's Purple theme; an excellent choice for those looking for a more modern [Dark Purple Theme](https://plugins.jetbrains.com/plugin/12100-dark-purple-theme).
- **Deep** - A new deep ocean blue theme with a vibrant high-contrast color scheme.

### Supported Languages

|            | Armada Dark | Armada Light |
|------------|-------------|--------------|
| C/C++      | ✅           | ✅            |
| C#         | ✅           | ✅            |
| CSS        | ✅           | ✅            |
| Dockerfile | ✅           | ✅            |
| Go         | ✅           | ✅            |
| Java       | ✅           | ✅            |
| JavaScript | ✅           | ✅            |
| JSON       | ✅           | ✅            |
| Kotlin     | ✅           | ✅            |
| Lua        | ✅           | ✅            |
| HTML       | ✅           | ✅            |
| Markdown   | ✅           | ✅            |
| NGINX Conf | ✅           | ✅            |
| Perl       | ✅           | ❌            |
| PHP        | ✅           | ✅            |
| PowerShell | ✅           | ❌            |
| Properties | ✅           | ✅            |
| Python     | ✅           | ✅            |
| R          | ✅           | ❌            |
| Rust       | ✅           | ✅            |
| SASS/SCSS  | ✅           | ✅            |
| Shell      | ✅           | ✅            |
| SQL        | ✅           | ✅            |
| Typescript | ✅           | ✅            |
| TOML       | ✅           | ✅            |
| XML        | ✅           | ✅            |
| YAML       | ✅           | ✅            |
| Zig        | ✅           | ✅            |

<!-- Plugin description end -->

IntelliJ's tokens don't perfectly match Fleet's, so some colors may not be 100% accurate. However, the goal is to get as
close as possible.
Where there are differences, I intend to add custom annotators to make the colors more accurate over time.

This project is not affiliated with or endorsed by JetBrains.

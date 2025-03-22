# Armada IntelliJ Theme

![Build](https://github.com/DavidSeptimus/armada-theme-intellij-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/26844-armada-theme.svg)](https://plugins.jetbrains.com/plugin/26844-armada-theme)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/26844-armada-theme.svg)](https://plugins.jetbrains.com/plugin/26844-armada-theme)

## Description

<!-- Plugin description -->
Armada is a plugin that aims to faithfully recreate the Fleet's builtin themes for IntelliJ-based IDEs.

### Features

- Fleet Dark UI and Editor Color Scheme
- Fleet Light UI and Editor Color Scheme
- Custom tokens for improved highlighting accuracy

### Language Support

Language-specific colors have been added for the following languages:

|            | Fleet Light | Fleet Dark |
|------------|-------------|------------|
| CSS        | ✅           | ✅          |
| Go         | ❌           | ✅          |
| Java       | ✅           | ✅          |
| JavaScript | ✅           | ✅          |
| JSON       | ✅           | ✅          |
| HTML       | ✅           | ✅          |
| Python     | ✅           | ✅          |
| Properties | ✅           | ✅          |
| Ruby       | ❌           | ✅          |
| Rust       | ❌           | ✅          |
| SASS/SCSS  | ✅           | ✅          |
| Typescript | ✅           | ✅          |
| TOML       | ❌           | ✅          |
| XML        | ✅           | ✅          |
| YAML       | ✅           | ✅          |
| Zig        | ❌           | ✅          |

<!-- Plugin description end -->

IntelliJ's tokens don't perfectly match Fleet's, so some colors may not be 100% accurate. However, the goal is to get as
close as possible.
Where there are differences, I intend to add custom annotators to make the colors more accurate over time.

This project is not affiliated with or endorsed by JetBrains.

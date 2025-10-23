<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Armada Theme Changelog

## [Unreleased]

### Added 

- Added dependency on [Armada Syntax Highlighter](https://plugins.jetbrains.com/plugin/28723-armada-syntax-highlighter)
  plugin to handle enhanced syntax highlighting
  - This plugin contains the annotators and settings pages previously included in this plugin to to allow for
    reuse as well as separation of theme and syntax highlighting updates (no more restarts needed when updating themes).

### Changed

- Updated icon colors (with a focus on classic UI)
- Added highlighting for YAML 1.2 `true`, `false` and `null` literal variants

### Fixed

- Fixed broken highlighting of breakpoint lines: breakpoints other than the execution point are now unhighlighted and the correct color is used to highlight the execution point line
- Fixed tree collapsed/expanded selected state icon colors in Armada Light themes
- Fixed dual-color selection overlay in run configuration dropdown for Armada Light (Classic UI) theme
- Fixed white error stripe marker for identifier under caret in Armada Dark color scheme
- Fixed missing borders in non-Islands themes
- Adjusted colors for various tokens in the Armada Dark color scheme that were previously set incorrectly or were using IntelliJ default colors

### Removed

- Removed syntax highlighting annotators and settings pages (now in the Armada Syntax Highlighter plugin)

## [0.8.0] - 2025-10-05

### Added

- Added Classic UI theme variants for both light and dark themes

### Changed

- Adjusted panel, tab, and toolbar colors for better consistency

### Changed

- The addition of `(Classic UI)` variants necessitated another variant rename of the original Armada themes from `(Classic)` to `(New UI)` to clarify which UI version they are designed for
- Added `targetUI` attribute to theme definitions to ensure that `(Classic UI)` and `(Islands)` themes are only available when the corresponding UI is available in the IDE

## [0.7.1] - 2025-10-02

### Added

- Added Islands-based UI theme variants for both light and dark themes
- Added colors for SQL, including custom annotations for various SQL syntax elements to improve highlighting accuracy and better match Fleet's colors
- Added colors for Markdown, NGINX configuration files, and Lua
- Added colors to `Armada Dark` for Perl, PowerShell, and R

### Changed

- Adjusted dark theme selection background color for better visibility
- Updated label and tag colors in the plugins menu
- Updated in-editor banner-syle notification colors for better visibility
- Reworked color settings page for "Armada Additions" to nest attributes under their respective languages for better organization and added highlighting to the demo text to make it easier to see the effects of changes
- Removed RGBa colors from `Armada Light` to prevent issues with losing transparency when exporting/importing user customizations
- Adjusted light theme diff colors for better visibility

## [0.6.0] - 2025-09-25

### Added

- Added colors for Shell Script, including annotations for let commands and subshell parentheses
- Added old terminal ANSI background colors as well as both foreground and background colors for the new terminal color
- Added an annotator to support highlighting of Rust attributes

### Changed

- Updated terminal search and selection colors
- Updated Rust colors to reflect Rust Rover's improved syntax highlighting capabilities for the language since initial support was added

### Fixed

- Fixed terminal background color in dark themes
- Replaced transparent colors Armada Dark color scheme with solid blended colors to resolve issues with losing transparency when exporting/importing user customizations
- Removed all non-color settings from color schemes to prevent overriding user font and other preferences when applying the color scheme

## [0.5.2] - 2025-09-18

### Changed

- Made minor color adjustments to `Armada Dark (Classic)` UI theme
- Renamed existing themes to `Armada Dark (Classic)` and `Armada Light (Classic)`
- Bumped maximum supported platform version to 253.*

## [0.5.1] - 2025-08-15

### Fixed

- Restored default editor font size to 13pt

## [0.5.0] - 2025-07-19

### Added

- Added C#, C++,Kotlin, PHP, Rust, and Zig colors to `Fleet Light (Armada)`

### Changed

- Refined light theme colors
- Refined `Fleet Dark (Armada)` theme and updated Zig colors to reflect IntelliJ's better syntax highlighting capabilities for the language (compared to Fleet)

### Fixed

- Light theme folded text showing as a dark-gray block

## [0.4.0] - 2025-05-13

### Added

- Added C/C++, C#, and PHP colors to `Fleet Dark (Armada)`

### Changed

- Removed background color from default identifier in `Fleet Dark (Armada)`
- Bumped maximum supported platform version to 252.*

## [0.3.2] - 2025-04-03

### Fixed

- Action button highlighting in the Fleet Dark UI
- JS property reference colors incorrectly applyinh to type references

### Changed

- Darkened documentation popup background color in Fleet Dark UI

## [0.3.1] - 2025-03-25

### Added

- Added Kotlin colors to `Fleet Dark (Armada)`
- Added Docker, Go, and TOML colors to `Fleet Light (Armada)`

### Changed

- Dark warning decoration colors
- Reworked dark CSS/SCSS and Go colors
- Dark border colors

## [0.3.0] - 2025-03-23

### Added

- Fleet Light UI and Editor Color Scheme
- Additional custom highlighting tokens for various languages

### Changed

- Dark UI improvements focusing on tooltips and search options
- Reworked fleet Dark colors for Java and Python
- Reworked Default colors for Fleet Dark

### Removed

- Custom colors for languages that are not yet fully supported

## [0.2.0] - 2025-03-19

### Added

- YAML support
- Zig support
- Color settings page for text attribute keys added by the plugin's annotators
- Completion popup colors
- File colors
- Drag-and-Drop colors
- Added spotlight border color

### Fixed

- Selection transparency causing non-uniformity in multiple selection
- Caret row transparency causing flickering when typing

### Changed

- Toolbar header tab colors
- Default border color
- Error underline style
- Button focus style

## [0.1.0] - 2025-03-18

### Added

- JavaScript annotator for property reference highlighting
- Colors for Properties including Annotator support for string literals
- Editor scrollbar colors
- Diff and Commit log colors
- Icon colors
- Checkbox and Radio button icons
- VCS Annotation colors
- Debugger colors

### Changed

- caret, caret row, and selection colors

### Fixed

- list background color

## [0.0.2] - 2025-03-17

### Added

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- Fleet Dark UI and Editor Color Scheme
- Plugin Icon

[Unreleased]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.5.2...HEAD
[0.5.3-eap]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.5.1...v0.5.3-eap
[0.5.2]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.5.1...v0.5.2
[0.5.1]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.5.0...v0.5.1
[0.5.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.2...v0.4.0
[0.3.2]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.1...v0.3.2
[0.3.1]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.0.2...v0.1.0
[0.0.2]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/commits/v0.0.2

<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Armada Theme Changelog

## [Unreleased]

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

[Unreleased]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.5.0...HEAD
[0.5.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.4.0...v0.5.0
[0.4.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.2...v0.4.0
[0.3.2]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.1...v0.3.2
[0.3.1]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/compare/v0.0.2...v0.1.0
[0.0.2]: https://github.com/DavidSeptimus/armada-theme-intellij-plugin/commits/v0.0.2

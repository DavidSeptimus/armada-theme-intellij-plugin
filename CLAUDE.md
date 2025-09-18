# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IntelliJ Platform plugin that provides Armada themes for IntelliJ-based IDEs. The plugin recreates Fleet's builtin themes (dark and light variants) with custom language-specific syntax highlighting.

## Development Commands

### Build and Test
- **Build plugin**: `./gradlew build`
- **Run tests**: `./gradlew test`
- **Run IDE with plugin for testing**: `./gradlew runIde`
- **Run UI tests**: `./gradlew runIdeForUiTests`
- **Verify plugin compatibility**: `./gradlew verifyPlugin`
- **Code quality check**: `./gradlew qodana`
- **Code coverage**: `./gradlew koverXmlReport`

### Publishing
- **Build plugin distribution**: `./gradlew buildPlugin`
- **Publish to marketplace**: `./gradlew publishPlugin`
- **Patch changelog**: `./gradlew patchChangelog`

### EAP (Early Access Program) Builds
- **Build EAP version**: `./gradlew buildEAP`
- **Publish to EAP channel**: `./gradlew publishEAP`
- **Manual EAP flag**: Use `-Peap=true` with other tasks to enable EAP mode

## Architecture

### Core Components

#### Theme System
- **Theme JSON files**: Located in `src/main/resources/themes/` with separate folders for `fleet-dark` and `fleet-light`
- **Editor color schemes**: XML files (e.g., `fleet-dark.xml`) defining syntax highlighting colors
- **UI theme definitions**: JSON files (e.g., `fleet-dark.theme.json`) defining IDE UI colors and icons
- **Theme variants**: Both "classic" and "islands" variants available for each theme

#### Language-Specific Annotators
Located in `src/main/kotlin/com/github/davidseptimus/armada/annotators/`:
- Each language has its own annotator class (e.g., `JavaScriptAnnotator.kt`, `PythonAnnotator.kt`)
- `AnnotationUtils.kt` provides common utility functions for PSI element traversal
- Annotators enhance syntax highlighting beyond IntelliJ's default tokens to match Fleet's appearance

#### Plugin Configuration
- **Main plugin descriptor**: `src/main/resources/META-INF/plugin.xml`
- **Language-specific configs**: Separate XML files for each supported language (e.g., `plugin-javascript.xml`)
- **Optional dependencies**: Each language support is optional and conditionally loaded
- **Color settings**: `ArmadaColorSettingsPage.java` provides color configuration UI

### Supported Languages
The plugin provides enhanced syntax highlighting for: C/C++, C#, CSS, Dockerfile, Go, Java, JavaScript, JSON, Kotlin, HTML, Python, PHP, Properties, Rust, SASS/SCSS, TypeScript, TOML, XML, YAML, and Zig.

### Project Structure
```
src/main/
├── java/com/github/davidseptimus/armada/
│   └── settings/          # Color settings UI
├── kotlin/com/github/davidseptimus/armada/
│   └── annotators/        # Language-specific syntax highlighting
└── resources/
    ├── META-INF/          # Plugin configuration
    ├── themes/            # Theme definitions and icons
    └── messages/          # Internationalization
```

## Development Guidelines

### Theme Development
- Theme JSON files must include both UI colors and icon mappings
- Editor XML files should be comprehensive for all syntax elements
- Use Fleet's original color palettes as reference (available in root directory as CSV files)
- Test themes with sample files located in `src/test/testData/samples/`

### Adding Language Support
1. Create annotator class in `annotators/` package
2. Add corresponding `plugin-[language].xml` configuration file
3. Register the language dependency in main `plugin.xml`
4. Add syntax highlighting tests using sample files

### Code Standards
- Use Kotlin for new annotator implementations
- Follow existing patterns in `AnnotationUtils.kt` for PSI manipulation
- Ensure optional dependencies don't break plugin when languages aren't available
- JVM target: Java 21
- Follow IntelliJ Platform plugin development best practices

### Testing
- Sample files for testing are in `src/test/testData/samples/`
- Use `runIde` task to manually test theme appearance
- UI tests can be run with `runIdeForUiTests` task
- Plugin verification ensures compatibility across IntelliJ versions (build 233-253.*)
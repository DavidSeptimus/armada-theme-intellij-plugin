// A code-free theme plugin: ships the theme folders it lists in `armadaThemes` from the root
// `themes/` directory, and runs with the in-repo syntax highlighter it depends on.
plugins {
    id("armada.intellij-plugin")
}

val armadaThemes = extensions.create<ArmadaThemesExtension>("armadaThemes")

dependencies {
    intellijPlatform {
        // Every theme plugin declares <depends> on the highlighter: install the working-tree build
        // in the sandbox and verify against it rather than the published one. A public mirror
        // has no highlighter project, so it uses the published build instead.
        if (findProject(":plugins:syntax-highlighter") != null) {
            localPlugin(project(":plugins:syntax-highlighter"))
        } else {
            compatiblePlugin("com.github.davidseptimus.armada-syntax-highlighter")
        }
    }
}

intellijPlatform {
    // No code to instrument. Left on, instrumentCode's cached output kept shipping classes whose
    // sources had been deleted.
    instrumentCode = false
}

tasks {
    processResources {
        dependsOn(":generateAllThemes")

        val themes = armadaThemes.themes
        inputs.property("armadaThemes", themes)
        from(rootProject.layout.projectDirectory.dir("themes")) {
            // The generated variants, scheme XML, and icons; not the merge inputs.
            include { element -> element.relativePath.segments.first() in themes.get() }
            exclude("**/*-base.theme.json", "**/*.overrides.json")
            into("themes")
        }
    }

    // No unit tests, but the IntelliJ Platform plugin wires generated test resources, which
    // Gradle 9 treats as "test sources present".
    test {
        failOnNoDiscoveredTests = false
    }
}

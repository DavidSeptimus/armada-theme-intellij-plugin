plugins {
    id("ThemeMergerPlugin")
}

// Every theme's variants are merged from its base theme and one overrides file per UI flavor:
// themes/<theme>/<theme>-base.theme.json + <theme>[-islands|-classic-ui].overrides.json
// -> <theme>[-islands|-classic-ui].theme.json. The generated files are committed.
val themeVariants = mapOf("" to "New UI", "-islands" to "Islands", "-classic-ui" to "Classic UI")
// Every folder under themes/ (a public mirror carries only its own plugin's themes).
val themeNames = layout.projectDirectory.dir("themes").asFile.listFiles { f -> f.isDirectory }.orEmpty()
    .map { it.name }.sorted()

themeMerger {
    variants {
        themeNames.forEach { theme ->
            themeVariants.forEach { (suffix, flavor) ->
                val taskSuffix = theme.split('-').joinToString("") { it.replaceFirstChar(Char::uppercase) } +
                    suffix.split('-').joinToString("") { it.replaceFirstChar(Char::uppercase) }
                register(taskSuffix) {
                    baseTheme.set("themes/$theme/$theme-base.theme.json")
                    overrides("themes/$theme/$theme$suffix.overrides.json")
                    output.set("themes/$theme/$theme$suffix.theme.json")
                    description.set("Generates the $theme $flavor theme by merging the base theme with its overrides")
                }
            }
        }
    }
}

data class CachedIde(val dir: File, val type: String, val branch: Int, val isRelease: Boolean, val parts: List<Int>)

fun pruneIdeCache(cacheDir: File, sinceBranch: Int, keepNames: Set<String>, confirm: Boolean) {
    val releaseVersion = Regex("""^(\d{4})\.(\d)(\.\d+)*$""")
    val buildNumber = Regex("""^(\d{3})(\.\d+)+$""")
    val ides = cacheDir.listFiles { f -> f.isDirectory }.orEmpty().mapNotNull { dir ->
        val type = dir.name.substringBefore('-')
        val version = dir.name.substringAfter('-')
        val parts = version.split('.').mapNotNull { it.toIntOrNull() }
        when {
            releaseVersion.matches(version) -> CachedIde(dir, type, (parts[0] - 2000) * 10 + parts[1], true, parts)
            buildNumber.matches(version) -> CachedIde(dir, type, parts[0], false, parts)
            else -> null // unrecognized layout: never delete
        }
    }
    val byNewest = compareBy<CachedIde> { it.isRelease }.thenComparator { a, b ->
        a.parts.zip(b.parts).map { (x, y) -> x.compareTo(y) }.firstOrNull { it != 0 } ?: a.parts.size.compareTo(b.parts.size)
    }
    val newestPerLine = ides.groupBy { it.type to it.branch }.values.map { it.maxWith(byNewest) }.toSet()
    val doomed = ides.filter { it.dir.name !in keepNames && (it.branch < sinceBranch || it !in newestPerLine) }
        .sortedBy { it.dir.name }

    if (doomed.isEmpty()) println("Nothing to prune in $cacheDir")
    doomed.forEach { ide ->
        if (confirm) {
            ide.dir.deleteRecursively()
            println("Deleted ${ide.dir}")
        } else {
            println("Would delete ${ide.dir}")
        }
    }
    if (!confirm && doomed.isNotEmpty()) println("Dry run: re-run with -Pconfirm to delete.")
}

tasks {
    // Deletes cached IDEs that are below the since-build, or superseded by a newer build of the
    // same release line (a release supersedes that line's EAPs). The build target and the Pro
    // sandbox IDEs (current release, next EAP) are always kept, and anything newer is never
    // touched, since other projects share the cache. Dry run unless -Pconfirm is passed.
    register("pruneIdeCache") {
        group = "intellij platform"
        description = "Removes superseded and pre-since-build IDEs from the shared IDE cache (-Pconfirm to delete)"
        notCompatibleWithConfigurationCache("Deletes outside the build directory")

        val cacheDir = ArmadaBuild.ideCacheDir(providers)
        val sinceBranch = providers.gradleProperty("pluginSinceBuild").map { it.substringBefore('.').toInt() }
        val platformType = providers.gradleProperty("platformType")
        val keep = listOf("platformVersion", "platformVersionCurrent", "platformVersionEap").map { version ->
            platformType.zip(providers.gradleProperty(version)) { t, v -> "$t-$v" }
        }
        val confirm = providers.gradleProperty("confirm").isPresent

        doLast {
            pruneIdeCache(cacheDir.get(), sinceBranch.get(), keep.map { it.get() }.toSet(), confirm)
        }
    }

    // Converts dotted keys to nested objects: ./gradlew normalizeTheme -Pinput=<in.json> -Poutput=<out.json>
    register("normalizeTheme") {
        group = "theme"
        description = "Normalizes a theme JSON file by converting dotted keys to nested objects"
        val input = providers.gradleProperty("input")
        val output = providers.gradleProperty("output")
        doLast {
            if (!input.isPresent || !output.isPresent) {
                throw GradleException("Usage: ./gradlew normalizeTheme -Pinput=<input.json> -Poutput=<output.json>")
            }
            val theme = buildscripts.ThemeMerger.normalize(file(input.get()))
            buildscripts.ThemeMerger.writeTheme(theme, file(output.get()))
        }
    }

    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}

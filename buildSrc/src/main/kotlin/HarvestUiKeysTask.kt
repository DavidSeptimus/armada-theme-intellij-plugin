import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.tachilabs.armada.uitheme.UiKeyRules
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.TreeMap
import java.util.TreeSet
import java.util.zip.ZipFile

/**
 * Writes the catalog of UI color keys that the UI theme review reads: every key an IDE release can
 * paint, which branches have it, and what the built-in themes set it to.
 *
 * Three sources, because each misses keys the others have: the themeMetadata registry, the built-in
 * themes, and key names found in compiled code. The toggle added in 2026.3 reads named colors that
 * only the themes declare. Run it when a version in `uiCatalogVersions` changes and commit the diff.
 */
abstract class HarvestUiKeysTask : DefaultTask() {
    @get:Input
    abstract val versions: ListProperty<String>

    /** Holds an extracted `IU-<version>` per version. Multi-GB, so it isn't fingerprinted. */
    @get:Internal
    abstract val ideCache: DirectoryProperty

    /** Keys known to be dead, one per line, `#` for comments. */
    @get:InputFile
    abstract val excludedKeys: RegularFileProperty

    @get:OutputFile
    abstract val catalog: RegularFileProperty

    private class Entry {
        var named = false
        val branches = TreeSet<Int>()
        val sources = TreeSet<String>()
        var description: String? = null
        var deprecated = false
    }

    /** [stars] names the `*.suffix` key that last wrote each key, once a theme is [merge]d. */
    private class Theme(
        val parent: String?,
        val ui: Map<String, String>,
        val colors: Map<String, String>,
        val stars: Map<String, String> = emptyMap(),
    )

    private class Distribution(
        val version: String,
        val branch: Int,
        val code: Set<String>,
        val metadata: Map<String, JsonObject>,
        val themes: Map<String, Theme>,
    )

    @TaskAction
    fun harvest() {
        val distributions = versions.get().map { version -> read(version) }.sortedBy { it.branch }
        val entries = TreeMap<String, Entry>()
        // Distributions are sorted, so this ends with the newest IDE's themes.
        var merged = emptyMap<String, Theme>()
        for (distribution in distributions) merged = collect(distribution, entries)
        val excluded = excludedKeys.get().asFile.readLines().map(String::trim).filter { it.isNotEmpty() && !it.startsWith("#") }
        for (key in excluded) if (entries.remove(key) == null) logger.warn("Excluded key $key isn't in any IDE. Remove it from the list.")

        val out = catalog.get().asFile
        out.parentFile.mkdirs()
        out.writeText(render(entries, merged, distributions))
        logger.lifecycle("UI key catalog: ${entries.size} keys from ${distributions.size} IDEs -> $out")
    }

    private fun read(version: String): Distribution {
        val root = ideCache.get().asFile.resolve("IU-$version")
        val home = sequenceOf(root, root.resolve("Contents")).firstOrNull { it.resolve("lib").isDirectory }
            ?: throw GradleException("No IDE at $root. Download it with a sandbox task, or remove $version from uiCatalogVersions.")
        val jars = sequenceOf("lib", "plugins").map(home::resolve).filter(File::isDirectory)
            .flatMap { dir -> dir.walkTopDown().filter { it.isFile && it.extension == "jar" } }.sorted().toList()

        val code = TreeSet<String>()
        val metadata = TreeMap<String, JsonObject>()
        val themeFiles = HashMap<String, Theme>()
        var candidates = 0
        var dropped = 0
        for (jar in jars) {
            val zip = runCatching { ZipFile(jar) }.getOrNull() ?: continue
            zip.use {
                for (entry in zip.entries()) {
                    val name = entry.name
                    when {
                        name.endsWith(".class") && !name.endsWith("module-info.class") -> {
                            val bytes = zip.getInputStream(entry).readBytes()
                            if (!LafKeyHarvester.mayContainKeys(bytes)) continue
                            candidates++
                            if (scan(bytes, code, version, name) > 0) dropped++
                        }
                        name.lowercase().endsWith(".thememetadata.json") -> {
                            val text = zip.getInputStream(entry).readBytes().decodeToString()
                            JsonParser.parseString(text).asJsonObject.getAsJsonArray("ui")?.forEach { element ->
                                val item = element.asJsonObject
                                metadata[item.get("key").asString] = item
                            }
                        }
                        name in BUILT_IN_THEMES -> {
                            val text = zip.getInputStream(entry).readBytes().decodeToString()
                            themeFiles[BUILT_IN_THEMES.getValue(name)] = parseTheme(text)
                        }
                    }
                }
            }
        }
        // A scan that silently lost its classes would read as "this IDE added no keys".
        if (dropped > candidates / 100) throw GradleException("Dropped $dropped of $candidates classes in $version")
        // A theme that moved would otherwise just vanish from the catalog, changing every fingerprint.
        val missing = BUILT_IN_THEMES.values - themeFiles.keys
        if (missing.isNotEmpty()) throw GradleException("No ${missing.joinToString()} theme in $version. Update BUILT_IN_THEMES if it moved.")
        logger.lifecycle("  $version: ${code.size} code keys, ${metadata.size} metadata keys, ${themeFiles.size} themes")
        return Distribution(version, branchOf(home), code, metadata, themeFiles)
    }

    private fun scan(bytes: ByteArray, code: MutableSet<String>, version: String, name: String): Int =
        try {
            LafKeyHarvester.scanClass(bytes, code)
        }
        catch (e: IllegalArgumentException) {
            if (e.message?.contains("Unsupported class file major version") == true) {
                throw GradleException("ASM can't read $version's bytecode ($name). Bump asm in libs.versions.toml.", e)
            }
            1
        }
        catch (_: RuntimeException) {
            1
        }

    private fun branchOf(home: File): Int {
        val file = sequenceOf(home.resolve("build.txt"), home.resolve("Resources/build.txt")).firstOrNull(File::isFile)
            ?: throw GradleException("No build.txt in $home")
        val text = file.readText().trim()
        return text.substringAfter('-', text).substringBefore('.').toIntOrNull()
            ?: throw GradleException("Unexpected build number '$text' in $file")
    }

    private fun parseTheme(text: String): Theme {
        val json = JsonParser.parseString(text).asJsonObject
        return Theme(json.get("parentTheme")?.asString, UiKeyRules.flatten(json.getAsJsonObject("ui")), UiKeyRules.flatten(json.getAsJsonObject("colors")))
    }

    /** Adds the distribution's keys to [entries] and returns its built-in themes, merged. */
    private fun collect(distribution: Distribution, entries: MutableMap<String, Entry>): Map<String, Theme> {
        fun entry(key: String) = entries.getOrPut(key) { Entry() }.also { it.branches += distribution.branch }
        val themes = distribution.themes.keys.associateWith { merge(it, distribution.themes) }

        for (key in distribution.code) {
            val name = key.removePrefix(PALETTE_PREFIX)
            entry(name).apply {
                if (name != key) named = true
                sources += "code"
            }
        }
        for (theme in themes.values) {
            for ((key, value) in theme.ui) {
                if ('*' !in key && UiKeyRules.resolve(value, theme.colors) != null) entry(key).sources += "theme"
            }
        }
        for ((key, item) in distribution.metadata) {
            if ('*' in key || !isColorMetadata(key, themes.values)) continue
            entry(key).apply {
                sources += "metadata"
                description = item.get("description")?.asString?.takeIf(String::isNotBlank) ?: description
                deprecated = item.get("deprecated")?.asBoolean == true
            }
        }
        // Darcula has no palette, so every named color it declares is one the platform reads.
        distribution.themes["Darcula"]?.colors?.keys?.forEach { name ->
            entry(name).apply {
                named = true
                sources += "named"
            }
        }
        return themes
    }

    /** Metadata also lists sizes, insets and UI delegates. */
    private fun isColorMetadata(key: String, themes: Collection<Theme>): Boolean {
        val set = themes.filter { key in it.ui }
        return if (set.isEmpty()) LafKeyHarvester.isColorName(key) else set.any { UiKeyRules.resolve(it.ui.getValue(key), it.colors) != null }
    }

    /**
     * A theme with its parents applied, the way the platform does it: a child's entries replace its
     * parent's and come after them, and walking them in order, a star overwrites what's set so far.
     */
    private fun merge(id: String, themes: Map<String, Theme>): Theme {
        val chain = generateSequence(id) { themes[it]?.parent }.takeWhile { it in themes }.take(8).toList().asReversed()
        var entries: Map<String, String> = emptyMap()
        val colors = LinkedHashMap<String, String>()
        for (name in chain) {
            val child = themes.getValue(name).ui
            entries = entries.filterKeys { it !in child } + child
            colors += themes.getValue(name).colors
        }
        val ui = LinkedHashMap<String, String>()
        val stars = HashMap<String, String>()
        for ((key, value) in entries) {
            if (key.startsWith("*.")) {
                val tail = key.substring(1)
                for (written in ui.keys.filter { !it.startsWith("*.") && it.endsWith(tail) }) {
                    ui[written] = value
                    stars[written] = key
                }
            }
            else {
                stars.remove(key)
            }
            ui[key] = value
        }
        return Theme(null, ui, colors, stars)
    }

    private fun render(
        entries: Map<String, Entry>,
        themes: Map<String, Theme>,
        distributions: List<Distribution>,
    ): String = buildString {
        val line = Gson()
        appendLine("{")
        appendLine("""  "schema": "ui-key-catalog/1",""")
        appendLine("""  "platforms": ${line.toJson(distributions.associate { it.branch.toString() to it.version })},""")
        appendLine("""  "keys": {""")
        val last = entries.keys.lastOrNull()
        val starLists = themes.mapValues { UiKeyRules.stars(it.value.ui.keys) }
        for ((key, entry) in entries) {
            val values = TreeMap<String, String>()
            val tokens = TreeMap<String, String>()
            for ((id, theme) in themes) {
                val raw = if (entry.named) theme.colors[key] else theme.ui[key]
                val star = when {
                    entry.named -> null
                    raw != null -> theme.stars[key]
                    else -> UiKeyRules.starFor(key, starLists.getValue(id), defined = false)
                }
                val hex = (raw ?: star?.let { theme.ui.getValue(it) })?.let { UiKeyRules.resolve(it, theme.colors) } ?: continue
                values[id] = hex
                val via = star ?: raw?.takeUnless(UiKeyRules::isHex)
                if (via != null) tokens[id] = via
            }
            val row = LinkedHashMap<String, Any>()
            if (entry.named) row["named"] = true
            row["branches"] = entry.branches
            row["sources"] = entry.sources
            entry.description?.let { row["description"] = it }
            if (entry.deprecated) row["deprecated"] = true
            if (values.isNotEmpty()) row["values"] = values
            if (tokens.isNotEmpty()) row["tokens"] = tokens
            appendLine("    ${line.toJson(key)}: ${line.toJson(row)}${if (key == last) "" else ","}")
        }
        appendLine("  }")
        appendLine("}")
    }

    private companion object {
        /** How the platform publishes a theme's `colors` block, so these are set as named colors. */
        const val PALETTE_PREFIX = "ColorPalette."

        /** The built-in themes a theme can descend from, by the id `parentTheme` uses. */
        val BUILT_IN_THEMES = mapOf(
            "themes/darcula.theme.json" to "Darcula",
            "themes/intellijlaf.theme.json" to "IntelliJ",
            "themes/expUI/expUI_dark.theme.json" to "ExperimentalDark",
            "themes/expUI/expUI_light.theme.json" to "ExperimentalLight",
            "themes/islands/ManyIslandsDark.theme.json" to "Islands Dark",
            "themes/islands/ManyIslandsLight.theme.json" to "Islands Light",
        )
    }
}

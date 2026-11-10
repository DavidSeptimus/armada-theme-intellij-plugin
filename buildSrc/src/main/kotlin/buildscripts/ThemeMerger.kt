package buildscripts

import com.google.gson.*
import dev.tachilabs.armada.uitheme.UiKeyRules
import java.io.File

/**
 * Utilities for merging and normalizing IntelliJ theme JSON files
 */
object ThemeMerger {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Paths that should not have their dotted keys flattened into nested structures.
     * Add paths here to preserve dotted notation (e.g., "panel.button", "notification")
     */
    private val pathsNotToFlatten = setOf(
        "icons"  // Never flatten any paths under the icons property
    )

    /**
     * Normalizes a theme JSON by converting dotted keys to nested objects.
     * For example: {"ui.*.background": "#000"} becomes {"ui": {"*": {"background": "#000"}}}
     * If keys already exist in the hierarchy, values are deep merged.
     */
    fun normalize(themeFile: File): JsonObject {
        val content = themeFile.readText()
        val root = JsonParser.parseString(content).asJsonObject
        return normalizeObject(root, "")
    }

    /**
     * Deep merges multiple theme JSON files in order.
     * Later themes override earlier themes for conflicting keys.
     */
    fun mergeThemes(vararg themeFiles: File): JsonObject {
        if (themeFiles.isEmpty()) {
            return JsonObject()
        }

        var result = normalize(themeFiles[0])
        for (i in 1 until themeFiles.size) {
            val next = normalize(themeFiles[i])
            result = deepMerge(result, next)
        }

        return result
    }

    /**
     * Writes a JsonObject to a file with pretty printing
     */
    fun writeTheme(theme: JsonObject, outputFile: File) {
        outputFile.parentFile?.mkdirs()
        outputFile.writeText(gson.toJson(theme))
    }

    private fun normalizeObject(obj: JsonObject, currentPath: String): JsonObject {
        val result = JsonObject()

        for ((key, value) in obj.entrySet()) {
            // Check if we're in a path that shouldn't be flattened
            val shouldNotFlatten = pathsNotToFlatten.any { path ->
                currentPath == path || currentPath.startsWith("$path.")
            }

            val newPath = if (currentPath.isEmpty()) key else "$currentPath.$key"
            val normalizedValue = if (value.isJsonObject && isValue(value)) {
                value
            } else if (value.isJsonObject) {
                normalizeObject(value.asJsonObject, newPath)
            } else if (value.isJsonArray) {
                normalizeArray(value.asJsonArray, newPath)
            } else {
                value
            }

            putPath(result, if (shouldNotFlatten) listOf(key) else key.split('.'), normalizedValue, currentPath)
        }

        return result
    }

    /** A leaf: anything but an object, or a per-OS object, which holds one value for each OS. */
    private fun isValue(element: JsonElement): Boolean = !element.isJsonObject || UiKeyRules.isPerOs(element.asJsonObject)

    /** Only `ui` keys are independent of each other, so only there can one be a prefix of another. */
    private fun inUi(path: String) = path == "ui" || path.startsWith("ui.")

    private fun child(path: String, key: String) = if (path.isEmpty()) key else "$path.$key"

    /** Sets [value] at a dotted path under the object at [path], nesting until it reaches a value in `ui`. */
    private fun putPath(target: JsonObject, parts: List<String>, value: JsonElement, path: String) {
        val head = parts.first()
        val existing = target.get(head)
        when {
            parts.size == 1 -> put(target, head, value, path)
            existing != null && isValue(existing) && inUi(path) -> putLeaves(target, parts.joinToString("."), value, path)
            else -> {
                if (existing == null || isValue(existing)) target.add(head, JsonObject())
                putPath(target.getAsJsonObject(head), parts.drop(1), value, child(path, head))
            }
        }
    }

    /**
     * Sets [value] at [key] in the object at [path], merging objects. A `ui` key can be both a value
     * and a prefix of other keys, like `Color.Blue` and `Color.Blue.selection`. Nesting can't hold
     * both, so the children stay dotted. Anywhere else, the later value replaces the earlier one.
     */
    private fun put(target: JsonObject, key: String, value: JsonElement, path: String) {
        val existing = target.get(key)
        when {
            existing == null || (isValue(existing) && isValue(value)) || !inUi(path) && (isValue(existing) || isValue(value)) ->
                target.add(key, value.deepCopy())
            !isValue(existing) && !isValue(value) ->
                target.add(key, deepMerge(existing.asJsonObject, value.asJsonObject, child(path, key)))
            isValue(existing) -> putLeaves(target, key, value, path)
            else -> {
                putLeaves(target, key, existing, path)
                target.add(key, value.deepCopy())
            }
        }
    }

    /**
     * Sets [value] under the dotted [key], an object as one dotted key per leaf. An object at a dotted
     * key could define a key a second way, next to the same key written out in full.
     */
    private fun putLeaves(target: JsonObject, key: String, value: JsonElement, path: String) {
        if (isValue(value)) put(target, key, value, path)
        else value.asJsonObject.entrySet().toList().forEach { (name, v) -> putLeaves(target, "$key.$name", v, path) }
    }

    private fun normalizeArray(arr: JsonArray, currentPath: String): JsonArray {
        val result = JsonArray()
        for (element in arr) {
            if (element.isJsonObject) {
                result.add(normalizeObject(element.asJsonObject, currentPath))
            } else if (element.isJsonArray) {
                result.add(normalizeArray(element.asJsonArray, currentPath))
            } else {
                result.add(element)
            }
        }
        return result
    }

    private fun deepMerge(base: JsonObject, overlay: JsonObject, path: String = ""): JsonObject {
        val result = base.deepCopy()
        for ((key, value) in overlay.entrySet()) put(result, key, value, path)
        return result
    }
}

// Example usage when run as a script
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: ThemeMerger <command> [args...]")
        println("Commands:")
        println("  normalize <input.json> <output.json>")
        println("  merge <output.json> <input1.json> <input2.json> [input3.json...]")
        return
    }

    when (args[0]) {
        "normalize" -> {
            if (args.size < 3) {
                println("Usage: normalize <input.json> <output.json>")
                return
            }
            val input = File(args[1])
            val output = File(args[2])
            val normalized = ThemeMerger.normalize(input)
            ThemeMerger.writeTheme(normalized, output)
            println("Normalized ${input.name} -> ${output.name}")
        }
        "merge" -> {
            if (args.size < 4) {
                println("Usage: merge <output.json> <input1.json> <input2.json> [input3.json...]")
                return
            }
            val output = File(args[1])
            val inputs = args.slice(2 until args.size).map { File(it) }.toTypedArray()
            val merged = ThemeMerger.mergeThemes(*inputs)
            ThemeMerger.writeTheme(merged, output)
            println("Merged ${inputs.size} themes -> ${output.name}")
        }
        else -> {
            println("Unknown command: ${args[0]}")
        }
    }
}
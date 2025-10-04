package buildscripts

import com.google.gson.*
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

            if (key.contains('.') && !shouldNotFlatten) {
                // Split the key and create nested structure
                val parts = key.split('.')
                var current = result

                for (i in 0 until parts.size - 1) {
                    val part = parts[i]
                    if (!current.has(part)) {
                        current.add(part, JsonObject())
                    } else if (!current.get(part).isJsonObject) {
                        // If the path exists but isn't an object, we need to convert it
                        current.add(part, JsonObject())
                    }
                    current = current.getAsJsonObject(part)
                }

                // Set the final value
                val finalKey = parts.last()
                val newPath = if (currentPath.isEmpty()) parts.joinToString(".") else "$currentPath.${parts.joinToString(".")}"
                val normalizedValue = if (value.isJsonObject) {
                    normalizeObject(value.asJsonObject, newPath)
                } else if (value.isJsonArray) {
                    normalizeArray(value.asJsonArray, newPath)
                } else {
                    value
                }

                // If the key already exists, merge the values
                if (current.has(finalKey)) {
                    val existing = current.get(finalKey)
                    if (existing.isJsonObject && normalizedValue.isJsonObject) {
                        current.add(finalKey, deepMerge(existing.asJsonObject, normalizedValue.asJsonObject))
                    } else {
                        current.add(finalKey, normalizedValue)
                    }
                } else {
                    current.add(finalKey, normalizedValue)
                }
            } else {
                // No dots in key or path should not be flattened
                val newPath = if (currentPath.isEmpty()) key else "$currentPath.$key"
                val normalizedValue = if (value.isJsonObject) {
                    normalizeObject(value.asJsonObject, newPath)
                } else if (value.isJsonArray) {
                    normalizeArray(value.asJsonArray, newPath)
                } else {
                    value
                }

                // Merge if key already exists
                if (result.has(key)) {
                    val existing = result.get(key)
                    if (existing.isJsonObject && normalizedValue.isJsonObject) {
                        result.add(key, deepMerge(existing.asJsonObject, normalizedValue.asJsonObject))
                    } else {
                        result.add(key, normalizedValue)
                    }
                } else {
                    result.add(key, normalizedValue)
                }
            }
        }

        return result
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

    private fun deepMerge(base: JsonObject, overlay: JsonObject): JsonObject {
        val result = JsonObject()

        // First, add all entries from base
        for ((key, value) in base.entrySet()) {
            result.add(key, value.deepCopy())
        }

        // Then, merge in entries from overlay
        for ((key, value) in overlay.entrySet()) {
            if (result.has(key)) {
                val existing = result.get(key)
                if (existing.isJsonObject && value.isJsonObject) {
                    // Recursively merge objects
                    result.add(key, deepMerge(existing.asJsonObject, value.asJsonObject))
                } else {
                    // Override with overlay value
                    result.add(key, value.deepCopy())
                }
            } else {
                // Add new key from overlay
                result.add(key, value.deepCopy())
            }
        }

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
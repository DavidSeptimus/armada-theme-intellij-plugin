package buildscripts

import com.google.gson.*
import java.io.File

/**
 * Utilities for merging and normalizing IntelliJ theme JSON files
 */
object ThemeMerger {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val parser = JsonParser()

    /**
     * Normalizes a theme JSON by converting dotted keys to nested objects.
     * For example: {"ui.*.background": "#000"} becomes {"ui": {"*": {"background": "#000"}}}
     * If keys already exist in the hierarchy, values are deep merged.
     */
    fun normalize(themeFile: File): JsonObject {
        val content = themeFile.readText()
        val root = parser.parse(content).asJsonObject
        return normalizeObject(root)
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

    private fun normalizeObject(obj: JsonObject): JsonObject {
        val result = JsonObject()
        val imageExtensions = listOf(".svg", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".webp")

        for ((key, value) in obj.entrySet()) {
            // Don't split keys that end with image extensions (they're icon paths)
            val isImagePath = imageExtensions.any { key.endsWith(it, ignoreCase = true) }

            if (key.contains('.') && !isImagePath) {
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
                val normalizedValue = if (value.isJsonObject) {
                    normalizeObject(value.asJsonObject)
                } else if (value.isJsonArray) {
                    normalizeArray(value.asJsonArray)
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
                // No dots in key, just normalize the value if needed
                val normalizedValue = if (value.isJsonObject) {
                    normalizeObject(value.asJsonObject)
                } else if (value.isJsonArray) {
                    normalizeArray(value.asJsonArray)
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

    private fun normalizeArray(arr: JsonArray): JsonArray {
        val result = JsonArray()
        for (element in arr) {
            if (element.isJsonObject) {
                result.add(normalizeObject(element.asJsonObject))
            } else if (element.isJsonArray) {
                result.add(normalizeArray(element.asJsonArray))
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
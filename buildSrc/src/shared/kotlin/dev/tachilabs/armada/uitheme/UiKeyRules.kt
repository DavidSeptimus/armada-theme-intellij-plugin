package dev.tachilabs.armada.uitheme

import com.google.gson.JsonElement
import com.google.gson.JsonObject

/** How the platform reads a theme's keys. Shared by the catalog harvest (buildSrc) and the UI theme review (Pro's devtools). */
object UiKeyRules {
    private val HEX = Regex("^#[0-9a-fA-F]{6}([0-9a-fA-F]{2})?$")

    fun isHex(value: String): Boolean = HEX.matches(value)

    /** Follows named colors down to a hex value, `rrggbb` or `rrggbbaa` in lower case. */
    fun resolve(value: String, colors: Map<String, String>, depth: Int = 0): String? = when {
        isHex(value) -> value.drop(1).lowercase()
        depth > 12 -> null
        else -> colors[value]?.let { resolve(it, colors, depth + 1) }
    }

    /** An object that holds one value per OS, like `{"os.mac": ..., "os.default": ...}`, rather than nested keys. */
    fun isPerOs(node: JsonObject): Boolean = node.size() > 0 && node.keySet().all { it.startsWith("os.") }

    /** The `*.suffix` keys among [keys], most specific first, as [starFor] expects them. */
    fun stars(keys: Collection<String>): List<String> = keys.filter { it.startsWith("*.") }.sortedByDescending { it.length }

    /**
     * The star from [stars] that reaches [key]. A key nothing [defined] also matches on the bare
     * suffix, through JBColor's pattern map.
     */
    fun starFor(key: String, stars: List<String>, defined: Boolean): String? =
        stars.firstOrNull { key.endsWith(it.substring(1)) }
            ?: stars.takeUnless { defined }?.firstOrNull { key.endsWith(it.substring(2)) }

    /** Dotted keys to string values. A per-OS object counts as a single value. */
    fun flatten(node: JsonObject?): Map<String, String> = LinkedHashMap<String, String>().also { flattenInto(node, "", it) }

    private fun flattenInto(node: JsonObject?, prefix: String, into: MutableMap<String, String>) {
        for ((key, value) in node?.entrySet().orEmpty()) {
            val path = if (prefix.isEmpty()) key else "$prefix.$key"
            if (value.isJsonObject && !isPerOs(value.asJsonObject)) {
                flattenInto(value.asJsonObject, path, into)
            }
            else {
                scalar(value)?.let { into[path] = it }
            }
        }
    }

    private fun scalar(value: JsonElement): String? = when {
        value.isJsonPrimitive -> value.asString
        value.isJsonObject -> value.asJsonObject.run { get("os.default") ?: entrySet().firstOrNull()?.value }?.takeIf { it.isJsonPrimitive }?.asString
        else -> null
    }
}

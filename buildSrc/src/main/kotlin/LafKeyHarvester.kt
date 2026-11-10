import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.Frame
import org.objectweb.asm.tree.analysis.SourceInterpreter
import org.objectweb.asm.tree.analysis.SourceValue

/**
 * Finds UI color key names in IDE bytecode: string literals passed to `JBColor.namedColor`,
 * `JBColor.namedColorOrNull`, the `JBColor(String, ...)` constructor and `UIManager.getColor`.
 *
 * Follows the dataflow to each call's first argument, so nested defaults and keys picked by a
 * conditional all count. Names built at runtime have no literal to find: the 2026.3 toggle's
 * `ColorPalette.toggle-*` is one, which is why the catalog also reads the themes' named colors.
 */
object LafKeyHarvester {
    private const val JBCOLOR = "com/intellij/ui/JBColor"
    private const val UIMANAGER = "javax/swing/UIManager"
    private val PACKAGE_PREFIXES = setOf(
        "com", "org", "net", "io", "dev", "java", "javax", "jdk",
        "kotlin", "kotlinx", "idea", "intellij", "jetbrains", "androidx",
    )
    private val COLOR_SUFFIXES = listOf(
        "background", "foreground", "color", "border", "hover",
        "focus", "shadow", "selection", "separator", "underline",
    )

    /** A cheap constant-pool check that skips most of a distribution. */
    fun mayContainKeys(bytes: ByteArray): Boolean = contains(bytes, JBCOLOR) || contains(bytes, UIMANAGER)

    /** Adds the class's key names to [keys] and returns how many of its methods couldn't be analyzed. */
    fun scanClass(bytes: ByteArray, keys: MutableSet<String>): Int {
        val node = ClassNode()
        ClassReader(bytes).accept(node, ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG)
        var unanalyzable = 0
        for (method in node.methods) {
            val targets = method.instructions.filterIsInstance<MethodInsnNode>().filter(::takesKey)
            if (targets.isEmpty()) continue
            val frames = runCatching { Analyzer(SourceInterpreter()).analyze(node.name, method) }.getOrNull()
            if (frames == null) {
                unanalyzable++
                continue
            }
            for (target in targets) {
                val frame = frames[method.instructions.indexOf(target)] ?: continue
                val argument = frame.getStack(frame.stackSize - Type.getArgumentTypes(target.desc).size)
                collectLiterals(method, frames, argument, keys, HashSet(), 0)
            }
        }
        return unanalyzable
    }

    /** Rejects package-like strings, and undotted strings that don't end in a color word. */
    fun isKeyShaped(text: String): Boolean {
        if (text.isEmpty() || text.length > 100 || !text[0].isLetter()) return false
        if (text.any { !it.isLetterOrDigit() && it != '.' && it != '_' && it != '-' }) return false
        val segments = text.split('.')
        if (segments.any(String::isEmpty) || segments.first() in PACKAGE_PREFIXES) return false
        return segments.size > 1 || isColorName(text)
    }

    fun isColorName(text: String): Boolean = text.lowercase().let { lower -> COLOR_SUFFIXES.any(lower::endsWith) }

    private fun contains(hay: ByteArray, needle: String): Boolean {
        outer@ for (i in 0..hay.size - needle.length) {
            for (j in needle.indices) if (hay[i + j].toInt() != needle[j].code) continue@outer
            return true
        }
        return false
    }

    private fun takesKey(call: MethodInsnNode): Boolean =
        (call.owner == JBCOLOR && (call.name == "namedColor" || call.name == "namedColorOrNull" ||
            (call.name == "<init>" && call.desc.startsWith("(Ljava/lang/String;")))) ||
            (call.owner == UIMANAGER && call.name == "getColor")

    /** Walks back through loads, stores and stack copies to the literals that fed [value]. */
    private fun collectLiterals(
        method: MethodNode,
        frames: Array<Frame<SourceValue>?>,
        value: SourceValue,
        keys: MutableSet<String>,
        seen: MutableSet<AbstractInsnNode>,
        depth: Int,
    ) {
        if (depth > 16) return
        for (source in value.insns) {
            if (!seen.add(source)) continue
            val frame = frames[method.instructions.indexOf(source)]
            fun follow(next: SourceValue) = collectLiterals(method, frames, next, keys, seen, depth + 1)
            when {
                source is LdcInsnNode -> (source.cst as? String)?.takeIf(::isKeyShaped)?.let(keys::add)
                frame == null -> Unit
                source is VarInsnNode && source.opcode == Opcodes.ALOAD -> follow(frame.getLocal(source.`var`))
                source is VarInsnNode && source.opcode == Opcodes.ASTORE -> follow(frame.getStack(frame.stackSize - 1))
                source.opcode == Opcodes.DUP || source.opcode == Opcodes.CHECKCAST -> follow(frame.getStack(frame.stackSize - 1))
            }
        }
    }
}

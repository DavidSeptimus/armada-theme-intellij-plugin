import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import java.io.File

/** Shared values for the Armada plugin builds. */
object ArmadaBuild {
    /** JVM flags for the interactive sandboxes. */
    val sandboxJvmArgs = listOf(
        "-Deditor.color.scheme.mark.colors=true",
        "-Dide.color.mixture.mark.colors=true",
        // Enables the platform's LaF Defaults / UI Inspector diagnostics.
        "-Didea.is.internal=true",
    )

    /** Downloaded IDEs, shared across projects on this machine; see the root pruneIdeCache task. */
    fun ideCacheDir(providers: ProviderFactory): Provider<File> =
        providers.environmentVariable("INTELLIJ_PLATFORM_IDES_CACHE")
            .orElse(providers.gradleProperty("org.jetbrains.intellij.platform.intellijPlatformIdesCache"))
            .orElse("${System.getProperty("user.home")}/idea-sandbox/downloads")
            .map { File(it).absoluteFile }
}

/**
 * Reads a property from the project's own gradle.properties, falling back to the root one.
 * `providers.gradleProperty` only sees the root file, so per-plugin values need this.
 */
fun Project.armadaProperty(name: String): String =
    findProperty(name)?.toString() ?: throw GradleException("Property '$name' is not set for $path")

/** The theme folders under the root `themes/` directory that a theme plugin ships. */
abstract class ArmadaThemesExtension {
    abstract val themes: ListProperty<String>
}

/**
 * Held by every verifyPlugin task so only one Plugin Verifier runs at a time: concurrent runs share
 * ~/.pluginVerifier and delete each other's extracted plugins.
 */
abstract class PluginVerifierLock : org.gradle.api.services.BuildService<org.gradle.api.services.BuildServiceParameters.None>

import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformDependenciesExtension
import org.jetbrains.intellij.pluginRepository.PluginRepositoryFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.com.github.gundy.semver4j.model.Version
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.collections.sortedWith

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
    id("ThemeMergerPlugin") // Theme merging plugin
}

abstract class GitTagValueSource : ValueSource<String, GitTagValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val pattern: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val output = ByteArrayOutputStream()
        return try {
            execOperations.exec {
                commandLine("git", "tag", "--list", "--sort=-version:refname", parameters.pattern.get())
                standardOutput = output
                isIgnoreExitValue = true
            }
            output.toString().trim()
        } catch (ignored: Exception) {
            ""
        }
    }
}

fun generateEAPVersion(baseVersion: String, gitTagOutput: String): String {
    val baseSemVer = Version.fromString(baseVersion)
    val tags = gitTagOutput.lines().filter { it.isNotBlank() }
        .map { Version.fromString(if (it.startsWith("v")) it.substring(1) else it) }
    val eapTags = tags.filter { it.preReleaseIdentifiers.any { id -> id.toString().startsWith("eap") } }
        .sortedWith { a, b -> Version.reverseComparator().compare(a, b) }
    val stableTags = tags.filter { it.preReleaseIdentifiers.isEmpty() }
        .sortedWith { a, b -> Version.reverseComparator().compare(a, b) }
    val isBaseReleased = stableTags.any { it.equals(baseSemVer) }
    val eapBaseVersion = if (isBaseReleased) baseSemVer.incrementMinor() else baseSemVer
    val eapsForBase =
        eapTags.filter { it.major == eapBaseVersion.major && it.minor == eapBaseVersion.minor && it.patch == eapBaseVersion.patch }
    if (eapsForBase.size == 0) {
        return "$eapBaseVersion-eap"
    } else {
        return eapsForBase.first().let {
            val currentEapNumber =
                if (it.preReleaseIdentifiers.size > 1 && it.preReleaseIdentifiers.get(1).isNumeric) it.preReleaseIdentifiers.get(
                    1
                ).toString().toInt() else 0
            "$eapBaseVersion-eap.${currentEapNumber + 1}"
        }
    }
}

group = providers.gradleProperty("pluginGroup").get()

// Handle EAP version
val baseVersion = providers.gradleProperty("pluginVersion").get()
val isEAPBuild = project.hasProperty("eap") ||
        providers.environmentVariable("IS_EAP_BUILD").isPresent ||
        gradle.startParameter.taskNames.any { it.contains("EAP") }

version = if (isEAPBuild) {
    val gitTagOutput = providers.of(GitTagValueSource::class.java) {
        parameters.pattern.set("v*")
    }.get()
    val eapVersion = generateEAPVersion(baseVersion, gitTagOutput)
    println("Generated EAP version: $eapVersion")
    eapVersion
} else {
    baseVersion
}

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(21)
}

// Configure project's dependencies
repositories {
    mavenCentral()

    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}


val IntelliJPlatformDependenciesExtension.pluginRepository by lazy {
    PluginRepositoryFactory.create("https://plugins.jetbrains.com")
}

fun IntelliJPlatformDependenciesExtension.pluginsInLatestCompatibleVersion(pluginIdProvider: Provider<List<String>>) =
    plugins(provider {
        pluginIdProvider.get().map { pluginId ->
            val platformType = intellijPlatform.productInfo.productCode
            val platformVersion = intellijPlatform.productInfo.buildNumber

            val plugin = pluginRepository.pluginManager.searchCompatibleUpdates(
                build = "$platformType-$platformVersion",
                xmlIds = listOf(pluginId),
            ).firstOrNull()
                ?: throw GradleException("No plugin update with id='$pluginId' compatible with '$platformType-$platformVersion' found in JetBrains Marketplace")

            "${plugin.pluginXmlId}:${plugin.version}"
        }
    })

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    implementation(libs.gson)
    implementation(kotlin("stdlib"))
    testImplementation(libs.junit)
    testImplementation(libs.opentest4j)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPluginsLatestCompatibleVersion` property from the gradle.properties file for plugin from JetBrains Marketplace.
        pluginsInLatestCompatibleVersion(
            providers.gradleProperty("platformPluginsLatestCompatibleVersion").map { it.split(',') })

        testFramework(TestFrameworkType.Platform)
    }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = if (isEAPBuild) project.version as String else providers.gradleProperty("pluginVersion").get()

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = provider {
            listOf(
                project.version.toString().substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

// Configure theme merging plugin
themeMerger {
    variants {
        register("darkClassicUITheme") {
            baseTheme.set("src/main/resources/themes/armada-dark/armada-dark.theme.json")
            overrides("src/main/resources/themes/armada-dark/armada-dark-classic-ui.overrides.json")
            output.set("src/main/resources/themes/armada-dark/armada-dark-classic-ui.theme.json")
            description.set("Generates the Armada Dark Classic UI theme by merging base theme with overrides")
        }

        register("lightClassicUITheme") {
            baseTheme.set("src/main/resources/themes/armada-light/armada-light.theme.json")
            overrides("src/main/resources/themes/armada-light/armada-light-classic-ui.overrides.json")
            output.set("src/main/resources/themes/armada-light/armada-light-classic-ui.theme.json")
            description.set("Generates the Armada Light Classic UI theme by merging base theme with overrides")
        }

        register("darkPurpleTheme") {
            baseTheme.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple-base.theme.json")
            overrides("src/main/resources/themes/armada-dark-purple/armada-dark-purple.overrides.json")
            output.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple.theme.json")
            description.set("Generates the Armada Dark Purple New UI theme by merging base and overrides")
        }

        register("darkPurpleIslandsTheme") {
            baseTheme.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple-base.theme.json")
            overrides("src/main/resources/themes/armada-dark-purple/armada-dark-purple-islands.overrides.json")
            output.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple-islands.theme.json")
            description.set("Generates the Armada Dark Purple Islands theme by merging base and Islands overrides")
        }

        register("darkPurpleClassicUITheme") {
            baseTheme.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple-base.theme.json")
            overrides("src/main/resources/themes/armada-dark-purple/armada-dark-purple-classic-ui.overrides.json")
            output.set("src/main/resources/themes/armada-dark-purple/armada-dark-purple-classic-ui.theme.json")
            description.set("Generates the Armada Dark Purple Classic UI theme by merging base and Classic UI overrides")
        }
    }
}

tasks {
    // Task to generate plugin.xml from plugin-main.xml with conditional EAP injection
    register<Task>("generatePluginXml") {
        group = "build"
        description = "Generates plugin.xml from plugin-main.xml with conditional EAP dependencies"

        val pluginMainXml = layout.projectDirectory.file("src/main/resources/META-INF/plugin-main.xml")
        val pluginEapXml = layout.projectDirectory.file("src/main/resources/META-INF/plugin-eap.xml")
        val outputPluginXml = layout.projectDirectory.file("src/main/resources/META-INF/plugin.xml")

        // Use providers to capture all EAP detection logic at configuration time
        val eapPropertyProvider = providers.gradleProperty("eap").orElse("false")
        val eapEnvProvider = providers.environmentVariable("IS_EAP_BUILD").orElse("false")
        val taskNamesProvider = provider { gradle.startParameter.taskNames.joinToString(",") }

        inputs.file(pluginMainXml)
        inputs.file(pluginEapXml)
        inputs.property("eapProperty", eapPropertyProvider)
        inputs.property("eapEnv", eapEnvProvider)
        inputs.property("taskNames", taskNamesProvider)
        outputs.file(outputPluginXml)

        val mainContent = pluginMainXml.asFile.readText()

        // Determine if this is an EAP build using only the captured inputs
        val hasEapProperty = eapPropertyProvider.get() != "false"
        val hasEapEnv = eapEnvProvider.get() != "false"
        val hasEapTask = taskNamesProvider.get().contains("EAP")
        val isEAP = hasEapProperty || hasEapEnv || hasEapTask

        val finalContent = if (isEAP) {
            // Inject EAP dependencies into the designated section
            val beginMarker = "<!-- BEGIN Build-time Dependencies -->"
            val endMarker = "<!-- END Build-time Dependencies -->"

            val indent =
                mainContent.lineSequence().firstOrNull { it.contains(beginMarker) }?.takeWhile { it.isWhitespace() }
                    ?: "    "

            if (mainContent.contains(beginMarker)) {
                // Inject dependencies
                val dependenciesText =
                    """<depends optional="true" config-file="plugin-eap.xml">com.intellij.modules.platform</depends>"""
                mainContent.replace(
                    "^\\s*$beginMarker\n\\s*$endMarker".toRegex(RegexOption.MULTILINE),
                    "$indent$beginMarker\n$indent$dependenciesText\n$indent$endMarker"
                )
            } else {
                throw GradleException("Build-time dependency markers not found in plugin-main.xml")
            }
        } else {
            // For non-EAP builds, just copy the main file as-is
            mainContent
        }

        outputPluginXml.asFile.writeText(finalContent)
        println("Generated plugin.xml for ${if (isEAP) "EAP" else "stable"} build")
    }

    build {
        dependsOn("generateAllThemes")
    }

    runIde {
        dependsOn("generateAllThemes")
    }

    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }

    initializeIntellijPlatformPlugin {
        dependsOn("generatePluginXml")
    }

    register<Task>("buildEAP") {
        group = "eap"
        description = "Builds plugin with EAP version and configuration"

        dependsOn("buildPlugin")

        val buildVersion = version.toString()
        doFirst {
            println("Building EAP with version $buildVersion")
        }
    }

    register<Exec>("publishEAP") {
        group = "eap"
        description = "Publishes plugin to EAP channel"

        commandLine("./gradlew", "publishPlugin", "-Peap=true")
        doLast {
            println("EAP plugin published successfully")
        }
    }

    register<Exec>("checkEAP") {
        group = "eap"
        description = "Runs tests with EAP configuration"

        commandLine("./gradlew", "check", "-Peap=true")
    }

    register<Exec>("runIdeEAP") {
        group = "eap"
        description = "Runs IDE with EAP dependencies enabled"

        commandLine("./gradlew", "runIde", "-Peap=true")
    }

    register("verifyPluginEAP") {
        group = "eap"
        description = "Verifies plugin with EAP dependencies enabled"

        finalizedBy("verifyPlugin")

        doLast {
            println("EAP plugin built successfully, running verification...")
        }
    }

    // Manual theme manipulation tasks (for ad-hoc use)
    register<JavaExec>("normalizeTheme") {
        group = "theme"
        description = "Normalizes a theme JSON file by converting dotted keys to nested objects"

        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("buildscripts.ThemeMergerKt")

        args = listOfNotNull(
            "normalize",
            project.findProperty("input")?.toString(),
            project.findProperty("output")?.toString()
        )

        doFirst {
            if (project.findProperty("input") == null || project.findProperty("output") == null) {
                throw GradleException("Usage: ./gradlew normalizeTheme -Pinput=<input.json> -Poutput=<output.json>")
            }
        }
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins {
                robotServerPlugin()
            }
        }

        register("runIdeClassicUI") {
            task {
                description = "Runs IDE with the classic UI plugin for testing theme compatibility"
            }

            plugins {
                plugin(provider {
                    val platformType = intellijPlatform.productInfo.productCode
                    val platformVersion = intellijPlatform.productInfo.buildNumber

                    val repo = PluginRepositoryFactory.create("https://plugins.jetbrains.com")
                    val plugin = repo.pluginManager.searchCompatibleUpdates(
                        build = "$platformType-$platformVersion",
                        xmlIds = listOf("com.intellij.classic.ui"),
                    ).firstOrNull()
                        ?: throw GradleException("No plugin update with id='com.intellij.classic.ui' compatible with '$platformType-$platformVersion' found in JetBrains Marketplace")

                    "${plugin.pluginXmlId}:${plugin.version}"
                })
            }
        }
    }
}

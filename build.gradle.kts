import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformDependenciesExtension
import org.jetbrains.intellij.pluginRepository.PluginRepositoryFactory
import java.io.ByteArrayOutputStream

plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = providers.gradleProperty("pluginGroup").get()

// Handle EAP version
val baseVersion = providers.gradleProperty("pluginVersion").get()
val isEAPBuild = project.hasProperty("eap") ||
                 providers.environmentVariable("IS_EAP_BUILD").isPresent ||
                 gradle.startParameter.taskNames.any { it.contains("EAP") }

version = if (isEAPBuild) {
    // Generate EAP version with point release support
    val eapVersion = generateEAPVersion(baseVersion)
    println("Generated EAP version: $eapVersion")
    eapVersion
} else {
    baseVersion
}

fun generateEAPVersion(baseVersion: String): String {
    // Get existing EAP tags to determine next version
    val result = ByteArrayOutputStream()
    try {
        providers.exec {
            commandLine("git", "tag", "--list", "--sort=-version:refname", "v*-eap*")
            standardOutput = result
            isIgnoreExitValue = true
        }
    } catch (e: Exception) {
        // If git command fails, fall back to simple increment
        val parts = baseVersion.split(".")
        val patch = parts[2].toInt() + 1
        return "${parts[0]}.${parts[1]}.$patch-eap"
    }

    val eapTags = result.toString().trim().lines().filter { it.isNotBlank() }
    val parts = baseVersion.split(".")
    val baseVersionPattern = "${parts[0]}\\.${parts[1]}\\.${parts[2]}"

    // Find existing EAP versions for this base version
    val existingEapVersions = eapTags
        .filter { it.matches(Regex("^v?$baseVersionPattern-eap(?:\\.\\d+)?$")) }
        .mapNotNull { tag ->
            val version = tag.removePrefix("v")
            val eapPart = version.substringAfter("-eap")
            if (eapPart.isEmpty()) 1 else eapPart.removePrefix(".").toIntOrNull() ?: 1
        }
        .sorted()

    return if (existingEapVersions.isNotEmpty()) {
        // Increment the highest existing EAP point release
        val nextPointRelease = existingEapVersions.last() + 1
        "$baseVersion-eap.$nextPointRelease"
    } else {
        // First EAP for this base version
        "$baseVersion-eap"
    }
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
        channels = provider { listOf(project.version.toString().substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
        print("Publishing to channel(s): ${channels.get()}\n")
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

tasks {
    // Task to generate plugin.xml from plugin-main.xml with conditional EAP injection
    register("generatePluginXml") {
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
            val beginMarker = "  <!-- BEGIN Build-time Dependencies -->"
            val endMarker = "  <!-- END Build-time Dependencies -->"

            if (mainContent.contains(beginMarker)) {
                // Inject dependencies
                val dependenciesText = """  <depends optional="true" config-file="plugin-eap.xml">com.intellij.modules.platform</depends>"""
                mainContent.replace(
                    "$beginMarker\n$endMarker",
                    "$beginMarker\n$dependenciesText\n$endMarker"
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

    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn(patchChangelog)
    }

    initializeIntellijPlatformPlugin {
        dependsOn("generatePluginXml")
    }

    register("buildEAP") {
        group = "eap"
        description = "Builds plugin with EAP version and configuration"

        dependsOn("buildPlugin")

        doFirst {
            println("Building EAP version with incremented patch number")
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
    }
}

// Shared setup for every Armada plugin: IntelliJ Platform target, plugin.xml patching from the
// plugin's own gradle.properties / README / CHANGELOG, signing, publishing, and verification.
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

group = armadaProperty("pluginGroup")
// The version being built: pluginVersion, or the release tag's version when the Release workflow
// passes -PreleaseVersion (EAP releases like 1.3.0-eap.1 only exist as tags).
val armadaVersion = findProperty("releaseVersion")?.toString() ?: armadaProperty("pluginVersion")

version = armadaVersion

// The jar and Kotlin module keep the names each plugin's standalone repo used (its root project
// name, now pluginArchiveName) rather than the Gradle subproject's name.
base {
    archivesName = armadaProperty("pluginArchiveName")
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        moduleName = armadaProperty("pluginArchiveName")
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(armadaProperty("platformType"), armadaProperty("platformVersion"))
    }
}

intellijPlatform {
    projectName = armadaProperty("pluginArchiveName")

    caching {
        ides {
            enabled = true
            path = layout.dir(ArmadaBuild.ideCacheDir(providers))
        }
    }

    pluginConfiguration {
        name = armadaProperty("pluginName")
        version = armadaVersion

        // The <!-- Plugin description --> section of the plugin's README.md
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
        val pluginVersion = armadaVersion
        changeNotes = provider {
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
            sinceBuild = armadaProperty("pluginSinceBuild")
            untilBuild = provider { null }
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        // A pre-release label (2.1.7-alpha.3) publishes to that release channel.
        val pluginVersion = armadaVersion
        channels = provider {
            listOf(pluginVersion.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }

    pluginVerification {
        ides {
            // Newest release of each line from the since-build on. Not recommended(): it also
            // pulls EAPs, and every new patch or EAP is another ~4 GB download that nothing
            // cleans up. Superseded downloads are removed by the root pruneIdeCache task.
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaUltimate)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = armadaProperty("pluginSinceBuild")
            }
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = armadaProperty("pluginRepositoryUrl")
}

tasks {
    // The jar in the plugin's lib/ folder; IPGP names it after the Gradle project by default.
    named<org.jetbrains.intellij.platform.gradle.tasks.ComposedJarTask>("composedJar") {
        archiveBaseName = armadaProperty("pluginArchiveName")
    }

    runIde {
        val jvmArgs = ArmadaBuild.sandboxJvmArgs
        jvmArgumentProviders += CommandLineArgumentProvider { jvmArgs }

        // Pro's sandbox runs every plugin together, so a plugin's own runIde hands off to it: a
        // root-level `./gradlew runIde` then opens one IDE instead of one per plugin. A standalone
        // mirror build has no Pro project and keeps its own runIde.
        val devEnvironment = ":plugins:armada-pro"
        if (project.path != devEnvironment && findProject(devEnvironment) != null) {
            enabled = false
            dependsOn("$devEnvironment:runIde")
        }
    }

    // verifyPluginSignature reads signPlugin's output without declaring it.
    verifyPluginSignature {
        dependsOn(signPlugin)
    }

    // A paid plugin's version must start with its <product-descriptor release-version> (20261 ->
    // 2026.1.x); the Plugin Verifier rejects anything else. Catch it before building.
    val checkReleaseVersion = register("checkReleaseVersion") {
        group = "verification"
        description = "Checks that a paid plugin's version matches its product-descriptor release-version"
        val descriptor = layout.projectDirectory.file("src/main/resources/META-INF/plugin.xml").asFile
        val version = armadaVersion
        inputs.file(descriptor)
        inputs.property("version", version)
        doLast {
            val releaseVersion = Regex("""<product-descriptor[^>]*release-version="(\d{4})(\d+)"""")
                .find(descriptor.readText())?.destructured ?: return@doLast
            val (year, release) = releaseVersion
            if (!version.startsWith("$year.$release.") && version != "$year.$release") {
                throw GradleException(
                    "Version $version doesn't match release-version $year$release in plugin.xml: paid plugins " +
                        "must be versioned $year.$release.x, or raise release-version (and release-date) " +
                        "for a new major release"
                )
            }
        }
    }
    buildPlugin {
        dependsOn(checkReleaseVersion)
    }

    publishPlugin {
        // Without signing configured, signPlugin is silently skipped and the unsigned zip would be
        // published; verifyPluginSignature fails instead ("Certificate chain not found").
        dependsOn(patchChangelog, verifyPluginSignature)
    }

    val verifierLock = gradle.sharedServices.registerIfAbsent("pluginVerifierLock", PluginVerifierLock::class) {
        maxParallelUsages = 1
    }
    verifyPlugin {
        usesService(verifierLock)
    }
}

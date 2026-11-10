plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// Also compiled into Pro's devtools, so the catalog and the review read theme keys the same way.
kotlin.sourceSets.main {
    kotlin.srcDir("src/shared/kotlin")
}

dependencies {
    implementation(libs.asm.analysis)
    implementation(libs.gson)
    implementation(libs.changelog.gradlePlugin)
    implementation(libs.intelliJPlatform.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kover.gradlePlugin)
}

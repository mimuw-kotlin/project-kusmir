import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm") version "2.0.20"
    alias(libs.plugins.ktlint)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose.plugin)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinxSerialization)
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.sqldelight.desktop)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.runtime)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.navigation.compose)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.uuid.sqldelight)
    implementation(libs.kotlinx.uuid.core.jvm)
    implementation(libs.gson)
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    @OptIn(ExperimentalComposeLibrary::class)
    testImplementation(compose.uiTest)
    implementation(libs.kotlinx.coroutines.test)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("data.local.database")
        }
    }
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
    compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "mtgo-tracker"
            packageVersion = "1.0.0"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

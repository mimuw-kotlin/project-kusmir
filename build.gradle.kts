import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.compose") version "1.7.0-beta01"
    id("org.jetbrains.kotlin.plugin.compose")
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
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.runtime)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.coroutines.test)
    implementation("app.softwork:kotlinx-uuid-sqldelight:0.1.2")
    implementation("app.softwork:kotlinx-uuid-core-jvm:0.1.2")

    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
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

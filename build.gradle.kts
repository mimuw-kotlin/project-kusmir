import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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
    implementation(libs.kotlinx.coroutines.test)
    implementation("app.softwork:kotlinx-uuid-sqldelight:0.1.2")
    implementation("app.softwork:kotlinx-uuid-core-jvm:0.1.2")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-api:2.0.7") // Ensure you use the latest version
    implementation("ch.qos.logback:logback-classic:1.4.11") // Ensure you use the latest version
    implementation(compose.materialIconsExtended)
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc01")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc01")
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

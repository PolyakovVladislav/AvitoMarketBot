import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

var versionName = "1.4.0"

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "ua.dualbot"
version = versionName

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven  ("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.seleniumhq.selenium:selenium-java:4.6.0")
                implementation("io.github.bonigarcia:webdrivermanager:5.3.1")
                implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.7")
                implementation("org.junit.jupiter:junit-jupiter:5.8.1")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AvitoBot"
            packageVersion = versionName
        }
    }
}

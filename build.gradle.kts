import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("js") version "1.7.22"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "net.ruffpug.suckerreimu"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport { enabled = true }
            }
        }
        dependencies {
            //  https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core-js
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.4")
        }
    }
}

configure<KtlintExtension> {
    debug.set(false)
    verbose.set(false)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    disabledRules.set(setOf())
    reporters { reporter(ReporterType.CHECKSTYLE) }
}

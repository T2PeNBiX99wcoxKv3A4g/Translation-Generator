plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    `java-gradle-plugin`
    `maven-publish`
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
}

dependencies {
    implementation(gradleApi())
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("translationGenerator") {
            id = "io.github.ykysnk.translation-generator"
            implementationClass = "io.github.ykysnk.translationGenerator.TranslationGeneratorPlugin"
            displayName = "Translation Generator"
            description = "Generate translation resources"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPages"
            url = layout.buildDirectory.dir("repo").get().asFile.toURI()
        }
    }
}
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    `java-gradle-plugin`
    `maven-publish`
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
}

dependencies {
    implementation(gradleApi())
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(providers.gradleProperty("jdk_version").get().toInt())
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
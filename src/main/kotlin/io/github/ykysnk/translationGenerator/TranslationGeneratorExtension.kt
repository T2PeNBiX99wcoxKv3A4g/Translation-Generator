package io.github.ykysnk.translationGenerator

import org.gradle.api.provider.Property

abstract class TranslationGeneratorExtension {
    abstract val modId: Property<String>
    abstract val langDirectory: Property<String>

    init {
        langDirectory.convention("src/main/resources/assets")
    }
}
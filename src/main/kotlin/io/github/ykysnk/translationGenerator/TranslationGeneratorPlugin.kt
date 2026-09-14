package io.github.ykysnk.translationGenerator

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class TranslationGeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("translationGenerator", TranslationGeneratorExtension::class.java)

        project.tasks.register("generateFallbackTranslations", FallbackTranslationsTask::class.java) {
            it.modId.set(extension.modId)
            it.langDirectory.set(extension.langDirectory)
            it.packageName.set(extension.packageName)
            it.inputFile.set(
                extension.langDirectory.zip(extension.modId) { langDirectory, modId ->
                    project.layout.projectDirectory.file(
                        "$langDirectory/$modId/lang/en_us.json"
                    )
                }
            )

            it.group = "translation"
            it.description = "Generate fallback translations"
        }

        project.tasks.register("generateUpsideDownTranslation", UpsideDownTranslationTask::class.java) {
            it.modId.set(extension.modId)
            it.langDirectory.set(extension.langDirectory)
            it.inputFile.set(
                extension.langDirectory.zip(extension.modId) { langDirectory, modId ->
                    project.layout.projectDirectory.file(
                        "$langDirectory/$modId/lang/en_us.json"
                    )
                }
            )

            it.group = "translation"
            it.description = "Generate the en_ud translation"
        }
    }
}
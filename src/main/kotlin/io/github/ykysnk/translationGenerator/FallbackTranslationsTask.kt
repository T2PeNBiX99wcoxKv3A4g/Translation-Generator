package io.github.ykysnk.translationGenerator

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Translation files are cheap to generate.")
abstract class FallbackTranslationsTask : DefaultTask() {
    @get:Input
    abstract val modId: Property<String>

    @get:Input
    abstract val langDirectory: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        outputDirectory.convention(project.layout.buildDirectory.dir("generated/sources/fallbackTranslations"))

        onlyIf {
            val file = inputFile.get().asFile

            logger.lifecycle("Fallback translation input: $file")
            logger.lifecycle("Exists: ${file.exists()}")

            file.exists()
        }
    }

    @TaskAction
    fun generate() {
        val translations = loadTranslations()
        val packageName = packageName.getOrElse("io.github.ykysnk.generated")

        require(packageName.isNotBlank()) {
            "Gradle project group must not be empty."
        }

        val outputDir = outputDirectory.get().asFile

        val output = buildString {
            appendLine("// Generated file. DO NOT EDIT.")
            appendLine("// Generated from en_us.json")
            appendLine()
            appendLine("package $packageName")
            appendLine()
            appendLine("object FallbackTranslations {")

            translations.forEach { (key, value) ->
                val constantName = toConstantName(key)
                val text = escapeKotlinString(value)

                appendLine("    const val $constantName = \"$text\"")
            }

            appendLine("}")
        }

        val outputFile = outputDir.resolve(
            "${packageName.replace('.', '/')}/FallbackTranslations.kt"
        )

        outputFile.parentFile.mkdirs()
        outputFile.writeText(output)

        logger.lifecycle(
            "Generated fallback translations: ${outputFile.relativeTo(project.projectDir)}"
        )
    }

    private fun loadTranslations(): Map<String, String> {
        val parsed = JsonSlurper().parse(inputFile.get().asFile)

        require(parsed is Map<*, *>) {
            "Translation file must contain a JSON object: ${inputFile.get().asFile}"
        }

        return parsed.entries.associate { (key, value) ->
            require(key is String) {
                "Translation key must be a string: $key"
            }

            require(value is String) {
                "Translation value must be a string: $key"
            }

            key to value
        }
    }

    private fun toConstantName(key: String): String =
        key.replace(Regex("(?<!^)([A-Z])")) { "_${it.value.lowercase()}" }.uppercase()
            .replace(Regex("[^A-Za-z0-9]+"), "_").trim('_')

    private fun escapeKotlinString(value: String): String =
        value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t")
}
package io.github.ykysnk.translationGenerator

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Translation files are cheap to generate.")
abstract class UpsideDownTranslationTask : DefaultTask() {
    @get:Input
    abstract val modId: Property<String>

    @get:Input
    abstract val langDirectory: Property<String>

    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        inputFile.convention(
            project.layout.projectDirectory.file(
                "${langDirectory.getOrElse("src/main/resources/assets")}/${
                    modId.getOrElse(
                        "null"
                    )
                }/lang/en_us.json"
            )
        )

        outputDirectory.convention(
            project.layout.buildDirectory.dir(
                "generated/resources/upsideDownTranslations"
            )
        )

        onlyIf {
            inputFile.get().asFile.exists()
        }
    }

    @TaskAction
    fun generate() {
        val translations = loadTranslations()

        val outputDir = outputDirectory.get().asFile
        outputDir.mkdirs()

        val output = buildString {
            appendLine("{")

            translations.entries.forEachIndexed { index, (key, value) ->
                val translated = transformText(value)
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")

                append("  ")
                append("\"")
                append(key)
                append("\": \"")
                append(translated)
                append("\"")

                if (index != translations.size - 1) {
                    append(",")
                }

                appendLine()
            }

            appendLine("}")
        }

        val outputFile = outputDir.resolve(
            "assets/${modId.get()}/lang/en_ud.json"
        )

        outputFile.parentFile.mkdirs()
        outputFile.writeText(output)

        logger.lifecycle(
            "Generated upside-down translation: " +
                    outputFile.relativeTo(project.projectDir)
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

    private val placeholderRegex =
        Regex("%(?:\\d+\\$)?(?:\\d+)?(?:\\.\\d+)?[a-zA-Z]")

    private val upsideDownMap = mapOf(
        // Lowercase
        'a' to 'ɐ',
        'b' to 'q',
        'c' to 'ɔ',
        'd' to 'p',
        'e' to 'ǝ',
        'f' to 'ɟ',
        'g' to 'ƃ',
        'h' to 'ɥ',
        'i' to 'ᴉ',
        'j' to 'ɾ',
        'k' to 'ʞ',
        'l' to 'ן',
        'm' to 'ɯ',
        'n' to 'u',
        'o' to 'o',
        'p' to 'd',
        'q' to 'b',
        'r' to 'ɹ',
        's' to 's',
        't' to 'ʇ',
        'u' to 'n',
        'v' to 'ʌ',
        'w' to 'ʍ',
        'x' to 'x',
        'y' to 'ʎ',
        'z' to 'z',

        // Uppercase
        'A' to '∀',
        'B' to 'B',
        'C' to 'Ɔ',
        'D' to '◖',
        'E' to 'Ǝ',
        'F' to 'Ⅎ',
        'G' to 'פ',
        'H' to 'H',
        'I' to 'I',
        'J' to 'ſ',
        'K' to 'ʞ',
        'L' to '˥',
        'M' to 'M',
        'N' to 'N',
        'O' to 'O',
        'P' to 'Ԁ',
        'Q' to 'Ό',
        'R' to 'ᴚ',
        'S' to 'S',
        'T' to '┴',
        'U' to '∩',
        'V' to 'Λ',
        'W' to 'M',
        'X' to 'X',
        'Y' to '⅄',
        'Z' to 'Z',

        // Punctuation
        '.' to '˙',
        ',' to '\'',
        '\'' to ',',
        '?' to '¿',
        '!' to '¡',
        '[' to ']',
        ']' to '[',
        '(' to ')',
        ')' to '(',
        '<' to '>',
        '>' to '<'
    )

    private fun transformText(text: String): String {
        val matches = placeholderRegex.findAll(text).toList()

        if (matches.isEmpty()) {
            return text
                .reversed()
                .map { upsideDownMap[it] ?: it }
                .joinToString("")
        }

        val result = StringBuilder()

        var end = text.length

        for (match in matches.asReversed()) {
            result.append(
                text.substring(match.range.last + 1, end)
                    .reversed()
                    .map { upsideDownMap[it] ?: it }
                    .joinToString("")
            )

            result.append(match.value)

            end = match.range.first
        }

        result.append(
            text.substring(0, end)
                .reversed()
                .map { upsideDownMap[it] ?: it }
                .joinToString("")
        )

        return result.toString()
    }
}
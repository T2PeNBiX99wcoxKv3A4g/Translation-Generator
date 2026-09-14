# Translation Generator Plugin

This is a Gradle plugin for generates translations for mod.

`build.gradle.kts`

```kotlin
plugins {
    id("io.github.ykysnk.translation-generator")
}

translationGenerator {
    modId.set("your_mod_id")
    packageName.set("the_package_group_path") // Not the same as the Maven group, this is the package path used in the code.
}
```

`settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        maven("https://t2penbix99wcoxkv3a4g.github.io/Translation-Generator/") {
            name = "TranslationGenerator"
        }
    }

    plugins {
        id("io.github.ykysnk.translation-generator") version "0.0.25"
    }
}
```

`build.gradle`

```groovy
plugins {
    id 'io.github.ykysnk.translation-generator'
}

translationGenerator {
    modId = 'your_mod_id'
    packageName = 'the_package_group_path' // Not the same as the Maven group, this is the package path used in the code.
}
```

`settings.gradle`

```groovy
pluginManagement {
    repositories {
        maven {
            url = 'https://t2penbix99wcoxkv3a4g.github.io/Translation-Generator/'
            name = 'TranslationGenerator'
        }
    }

    plugins {
        id 'io.github.ykysnk.translation-generator' version '0.0.25'
    }
}
```
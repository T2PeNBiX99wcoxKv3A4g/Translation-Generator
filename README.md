# Translation Generator Plugin

This is a Gradle plugin for generates translations for mod.

`build.gradle.kts`

```kotlin
plugins {
    id("io.github.ykysnk.translation-generator")
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
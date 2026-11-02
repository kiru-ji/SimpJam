package com.maxrave.simpmusic.utils

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the trap that shipped twice (v0.1.1 as 'Serializer for class s87', v0.1.6 as
 * 'Serializer for class Companion'): a @Serializable destination that is a data CLASS still
 * COMPILES when passed as a bare name — `navigate(ListenTogetherDestination)` resolves to the
 * generated Companion object, navigation serializes THAT, and every tap crashes at runtime with
 * "Serializer for class 'Companion' is not found". R8 keep rules cannot fix it; the crash is in
 * the call, not the minifier. Only constructor calls are safe for classes; a bare name is only
 * correct for a data object (where it IS the instance).
 */
class NavigationRouteSafetyTest {
    private val srcRoot = File("src/commonMain/kotlin/com/maxrave/simpmusic")

    @Test
    fun `data-class destinations are never navigated to by bare name`() {
        assertTrue(srcRoot.isDirectory, "run from the composeApp module dir; ${srcRoot.absolutePath} is missing")

        val destRoot = File(srcRoot, "ui/navigation/destination")
        assertTrue(destRoot.isDirectory, "destination package moved? ${destRoot.absolutePath} is missing")

        // Every route type in the destination package declared as a data class.
        val dataClassNames =
            destRoot
                .walkTopDown()
                .filter { it.extension == "kt" }
                .flatMap { file -> Regex("""data class (\w+)""").findAll(file.readText()).map { it.groupValues[1] } }
                .toSet()
        assertTrue(dataClassNames.isNotEmpty(), "found no data-class destinations — did the scan drift?")

        val violations =
            srcRoot
                .walkTopDown()
                .filter { it.extension == "kt" }
                .flatMap { file ->
                    val text = file.readText()
                    dataClassNames.flatMap { name ->
                        Regex("""navigate\(\s*$name\s*\)""")
                            .findAll(text)
                            .map {
                                "${file.path}: navigate($name) — '$name' is a data CLASS; the bare name is its " +
                                    "Companion, which has no serializer and crashes every tap. Call navigate($name(...))."
                            }
                    }
                }.toList()

        assertTrue(
            violations.isEmpty(),
            "Bare-name navigation to a data-class destination (runtime SerializationException):\n" +
                violations.joinToString("\n"),
        )
    }
}

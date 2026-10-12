package com.nullpointer.nullsiteadmin.models.personalInfo

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StringResourcesTest {
    private fun strings(locale: String): Map<String, String> {
        val root = listOf(File("src/main/res"), File("app/src/main/res")).first { it.isDirectory }
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File(root, "$locale/strings.xml")).getElementsByTagName("string")
        return (0 until nodes.length).map { nodes.item(it) }
            .filter { it.attributes.getNamedItem("translatable")?.nodeValue != "false" }
            .associate { it.attributes.getNamedItem("name").nodeValue to it.textContent }
    }

    @Test fun englishAndSpanishHaveTheSameTranslatableKeysAndPlaceholders() {
        val english = strings("values")
        val spanish = strings("values-es")
        assertEquals(english.keys, spanish.keys)
        val placeholders = Regex("%(?:[0-9]+\\$)?[sdif]")
        english.forEach { (key, value) ->
            assertEquals(key, placeholders.findAll(value).map { it.value }.toList(),
                placeholders.findAll(spanish.getValue(key)).map { it.value }.toList())
            assertTrue("Blank translation: $key", spanish.getValue(key).isNotBlank())
        }
    }
}

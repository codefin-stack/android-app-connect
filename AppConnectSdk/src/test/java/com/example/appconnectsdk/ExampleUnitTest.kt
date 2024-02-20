package com.example.appconnectsdk

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getGroupIdTest() {
        val source = "A.source"
        val destination = "B.destination"
        val groupId = getGroupId(source, destination)
        assertEquals("A.source_B.destination", groupId)
    }

    @Test
    fun getGroupIdTestMustSort() {
        val source = "B.source"
        val destination = "A.destination"
        val groupId = getGroupId(source, destination)
        assertEquals("A.destination_B.source", groupId)
    }


}
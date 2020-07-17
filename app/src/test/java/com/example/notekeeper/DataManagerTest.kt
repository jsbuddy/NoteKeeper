package com.example.notekeeper

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DataManagerTest {

    @Before
    fun setUp() {
        DataManager.initializeNotes()
    }

    @After
    fun tearDown() {
        DataManager.notes.clear()
    }

    @Test
    fun addNote() {
        val course = DataManager.courses["android_async"]!!
        val title = "Note Title"
        val text = "Note Text"
        val index = DataManager.addNote(course, title, text)
        val note = DataManager.notes[index]
        assertEquals(course, note.course)
        assertEquals(title, note.title)
        assertEquals(text, note.text)
    }

    @Test
    fun findNote() {
        val course = DataManager.courses["android_async"]!!
        val title = "Note Title"
        val text1 = "Note Text"
        val text2 = "Note Text 2"

        val index1 = DataManager.addNote(course, title, text1)
        val index2 = DataManager.addNote(course, title, text2)

        val note1 = DataManager.findNote(course, title, text1)
        val foundIndex1 = DataManager.notes.indexOf(note1)
        assertEquals(index1, foundIndex1)

        val note2 = DataManager.findNote(course, title, text2)
        val foundIndex2 = DataManager.notes.indexOf(note2)
        assertEquals(index2, foundIndex2)
    }
}
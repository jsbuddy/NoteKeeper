package com.example.notekeeper

import android.database.Cursor
import android.util.Log
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry

object DataManager {
    val courses = HashMap<String, CourseInfo>()
    val notes = ArrayList<NoteInfo>()

//    fun addNote(course: CourseInfo, title: String, text: String): Int {
//        val note = NoteInfo(course, title, text)
//        notes.add(note)
//        return notes.lastIndex
//    }

    fun findNote(course: CourseInfo, title: String, text: String): NoteInfo? {
        for (note in notes) {
            if (note.course == course && note.title == title && note.text == text)
                return note
        }
        return null
    }

    fun loadFromDatabase(openHelper: NoteKeeperOpenHelper) {
        val db = openHelper.readableDatabase

        val courseColumns: Array<String> = arrayOf(
            CourseInfoEntry.COLUMN_COURSE_ID,
            CourseInfoEntry.COLUMN_COURSE_TITLE
        )
        val courseCursor =
            db.query(
                CourseInfoEntry.TABLE_NAME,
                courseColumns,
                null,
                null,
                null,
                null,
                CourseInfoEntry.COLUMN_COURSE_TITLE
            )
        loadCoursesFromDatabase(courseCursor)

        val noteColumns: Array<String> = arrayOf(
            NoteInfoEntry.COLUMN_NOTE_TITLE,
            NoteInfoEntry.COLUMN_NOTE_TEXT,
            NoteInfoEntry.COLUMN_COURSE_ID,
            NoteInfoEntry.COLUMN_ID
        )
        val noteCursor =
            db.query(
                NoteInfoEntry.TABLE_NAME,
                noteColumns,
                null,
                null,
                null,
                null,
                NoteInfoEntry.COLUMN_COURSE_ID
            )
        loadNotesFromDatabase(noteCursor)

        db.close()
    }

    private fun loadNotesFromDatabase(cursor: Cursor) {
        notes.clear()
        val noteTitlePos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
        val noteTextPos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)
        val courseIdPos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
        val noteIdPos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_ID)

        while (cursor.moveToNext()) {
            val title = cursor.getString(noteTitlePos)
            val text = cursor.getString(noteTextPos)
            val courseId = cursor.getString(courseIdPos)
            val id = cursor.getInt(noteIdPos)

            val course = courses[courseId]
            val note = NoteInfo(id, course, title, text)
            notes.add(note)
        }

        cursor.close()
    }

    private fun loadCoursesFromDatabase(cursor: Cursor) {
        courses.clear()
        val courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        val courseTitlePos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE)

        while (cursor.moveToNext()) {
            val id = cursor.getString(courseIdPos)
            val title = cursor.getString(courseTitlePos)
            val course = CourseInfo(id, title)
            courses[id] = course
        }

        Log.d("DataManager", courses.toString())

        cursor.close()
    }
}
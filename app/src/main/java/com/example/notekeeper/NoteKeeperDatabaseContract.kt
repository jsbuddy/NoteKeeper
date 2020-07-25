package com.example.notekeeper

import android.provider.BaseColumns
import android.provider.BaseColumns._ID

object NoteKeeperDatabaseContract {
    object CourseInfoEntry : BaseColumns {
        const val TABLE_NAME = "course_info"
        const val COLUMN_COURSE_ID = "course_id"
        const val COLUMN_COURSE_TITLE = "course_title"
        const val COLUMN_ID = _ID

        const val SQL_CREATE_TABLE =
            """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COURSE_ID TEXT UNIQUE NOT NULL, 
                $COLUMN_COURSE_TITLE TEXT NOT NULL
            )
            """
    }

    object NoteInfoEntry : BaseColumns {
        const val TABLE_NAME = "note_info"
        const val COLUMN_NOTE_TITLE = "note_title"
        const val COLUMN_NOTE_TEXT = "note_text"
        const val COLUMN_COURSE_ID = "course_id"
        const val COLUMN_ID = _ID

        const val SQL_CREATE_TABLE =
            """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_TITLE TEXT NOT NULL, 
                $COLUMN_NOTE_TEXT TEXT, 
                $COLUMN_COURSE_ID TEXT NOT NULL
            )
            """
    }
}
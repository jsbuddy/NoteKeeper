package com.example.notekeeper

import android.net.Uri
import android.provider.BaseColumns

object NoteKeeperProviderContract {
    const val AUTHORITY = "com.example.notekeeper.provider"
    val AUTHORITY_URI: Uri = Uri.parse("content://$AUTHORITY")

    interface CoursesIdColumns : BaseColumns {
        val COLUMN_COURSE_ID get() = "course_id"
        val COLUMN_ID get() = BaseColumns._ID
    }

    interface CoursesColumns {
        val COLUMN_COURSE_TITLE get() = "course_title"
    }

    interface NotesColumns {
        val COLUMN_NOTE_TITLE get() = "note_title"
        val COLUMN_NOTE_TEXT get() = "note_text"
    }

    object Courses : BaseColumns, CoursesColumns, CoursesIdColumns {
        const val PATH = "courses"
        val CONTENT_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, PATH)
    }

    object Notes : BaseColumns, NotesColumns, CoursesIdColumns, CoursesColumns {
        const val PATH = "notes"
        val CONTENT_URI: Uri = Uri.withAppendedPath(AUTHORITY_URI, PATH)
        const val PATH_EXPANDED = "notes_expanded"
        val CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED)
    }
}
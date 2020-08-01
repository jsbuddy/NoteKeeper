package com.example.notekeeper

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.provider.BaseColumns
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry
import com.example.notekeeper.NoteKeeperProviderContract.Courses
import com.example.notekeeper.NoteKeeperProviderContract.Notes

class NoteKeeperProvider : ContentProvider() {
    private lateinit var openHelper: NoteKeeperOpenHelper
    private val matcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        matcher.addURI(NoteKeeperProviderContract.AUTHORITY, Courses.PATH, COURSES)
        matcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH, NOTES)
        matcher.addURI(NoteKeeperProviderContract.AUTHORITY, Notes.PATH_EXPANDED, NOTES_EXPANDED)
        matcher.addURI(NoteKeeperProviderContract.AUTHORITY, "${Notes.PATH}/#", NOTES_ROW)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = openHelper.writableDatabase
        return when (matcher.match(uri)) {
            NOTES_ROW -> {
                val id = ContentUris.parseId(uri)
                val rowSelection = "${NoteInfoEntry.COLUMN_ID} = ?"
                val rowSelectionArgs = arrayOf(id.toString())
                db.delete(NoteInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs)
            }
            else -> -1
        }
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = openHelper.writableDatabase

        return when (matcher.match(uri)) {
            NOTES -> {
                val id = db.insert(NoteInfoEntry.TABLE_NAME, null, values)
                ContentUris.withAppendedId(Notes.CONTENT_URI, id)
            }
            COURSES -> {
                val id = db.insert(CourseInfoEntry.TABLE_NAME, null, values)
                ContentUris.withAppendedId(Courses.CONTENT_URI, id)
            }
            else -> null
        }
    }

    override fun onCreate(): Boolean {
        openHelper = NoteKeeperOpenHelper(context)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = openHelper.readableDatabase

        return when (matcher.match(uri)) {
            COURSES -> {
                db.query(
                    CourseInfoEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            NOTES -> {
                db.query(
                    NoteInfoEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            NOTES_EXPANDED -> {
                notesExpandedQuery(
                    db,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            }
            NOTES_ROW -> {
                val id = ContentUris.parseId(uri)
                val rowSelection = "${NoteInfoEntry.COLUMN_ID} = ?"
                val rowSelectionArgs = arrayOf(id.toString())

                db.query(
                    NoteInfoEntry.TABLE_NAME,
                    projection,
                    rowSelection,
                    rowSelectionArgs,
                    null,
                    null,
                    null
                )
            }
            else -> null
        }
    }

    private fun notesExpandedQuery(
        db: SQLiteDatabase,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val columns = Array(projection?.size!!) { i ->
            if (projection[i] == BaseColumns._ID || projection[i] == Notes.COLUMN_COURSE_ID)
                NoteInfoEntry.getQName(projection[i])
            else projection[i]
        }

        val tableWithJoin = """
                    ${NoteInfoEntry.TABLE_NAME} JOIN ${CourseInfoEntry.TABLE_NAME} ON
                    ${NoteInfoEntry.getQName(NoteInfoEntry.COLUMN_COURSE_ID)} = 
                    ${CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID)}
                """.trimIndent()

        return db.query(
            tableWithJoin,
            columns, selection, selectionArgs, null, null,
            sortOrder
        )
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = openHelper.writableDatabase

        return when (matcher.match(uri)) {
            NOTES_ROW -> {
                val id = ContentUris.parseId(uri)
                val rowSelection = "${NoteInfoEntry.COLUMN_ID} = ?"
                val rowSelectionArgs = arrayOf(id.toString())
                db.update(NoteInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs)
            }
            else -> -1
        }
    }

    companion object {
        const val COURSES = 0
        const val NOTES = 1
        const val NOTES_EXPANDED = 2
        const val NOTES_ROW = 3
    }
}

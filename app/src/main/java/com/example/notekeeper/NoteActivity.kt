package com.example.notekeeper

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SimpleCursorAdapter
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry
import kotlinx.android.synthetic.main.content_note.*

class NoteActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private var noteId = NOTE_ID_NOT_SET
    private lateinit var openHelper: NoteKeeperOpenHelper
    private lateinit var adapterCourses: SimpleCursorAdapter
    private lateinit var noteCursor: Cursor
    private var courseQueryFinished = false
    private var notesQueryFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        openHelper = NoteKeeperOpenHelper(this)

        adapterCourses = SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item,
            null,
            arrayOf(CourseInfoEntry.COLUMN_COURSE_TITLE),
            intArrayOf(android.R.id.text1),
            0
        )
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses

        LoaderManager.getInstance(this).initLoader(LOADER_COURSES, null, this)

        initializeNote(savedInstanceState)
    }

    private fun initializeNote(savedInstanceState: Bundle?) {
        noteId = savedInstanceState?.getInt(NOTE_ID, NOTE_ID_NOT_SET) ?: intent.getIntExtra(
            NOTE_ID,
            NOTE_ID_NOT_SET
        )

        if (noteId != NOTE_ID_NOT_SET) {
            LoaderManager.getInstance(this).initLoader(LOADER_NOTES, null, this)
        } else {
            createNewNote()
        }
    }

    private fun createNewNote() {
        val values = ContentValues()
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, "")
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, "")
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, "")

        val db = openHelper.writableDatabase
        noteId = db.insert(NoteInfoEntry.TABLE_NAME, null, values).toInt()
    }

    private fun displayNote() {
        val courseIdPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
        val titlePos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
        val textPos = noteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)
        noteCursor.moveToNext()

        textNoteTitle.setText(noteCursor.getString(titlePos))
        textNoteText.setText(noteCursor.getString(textPos))

        val courseIndex = getIndexOfCourse(noteCursor.getString(courseIdPos))
        spinnerCourses.setSelection(courseIndex)
    }

    private fun getIndexOfCourse(courseId: String): Int {
        val cursor = adapterCourses.cursor
        val courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        var courseRowIndex = 0

        var more = cursor.moveToFirst()
        while (more) {
            val courseCursorId = cursor.getString(courseIdPos)
            if (courseCursorId == courseId) break
            courseRowIndex++
            more = cursor.moveToNext()
        }
        return courseRowIndex
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NOTE_ID, noteId)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_next -> {
                moveNext()
                true
            }
            R.id.action_reminder -> {
                NoteReminderNotification.notify(
                    this,
                    DataManager.notes[noteId],
                    noteId
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveNext() {
//        ++noteId
//        loadNoteData()
//        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (noteId >= DataManager.notes.lastIndex) {
            val menuItem = menu?.findItem(R.id.action_next)
            menuItem?.isEnabled = false
            menuItem?.icon = getDrawable(R.drawable.ic_baseline_arrow_forward_24_transparent)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        saveNote()
        super.onPause()
    }

    private fun saveNote() {
        val text = textNoteText.text.toString()
        val title = textNoteTitle.text.toString()
        val courseId = selectedCourseId()
        saveNoteToDatabase(courseId, title, text)
        NoteKeeperAppWidget.sendRefreshBroadcast(this)
    }

    private fun selectedCourseId(): String {
        val position = spinnerCourses.selectedItemPosition
        val cursor = adapterCourses.cursor
        cursor.moveToPosition(position)
        val idPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID)
        return cursor.getString(idPos)
    }

    private fun saveNoteToDatabase(courseId: String, title: String, text: String) {
        val selection = "${NoteInfoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(noteId.toString())

        val values = ContentValues()
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId)
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, title)
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, text)

        val db = openHelper.writableDatabase
        db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    override fun onDestroy() {
        openHelper.close()
        super.onDestroy()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        var loader: CursorLoader? = null
        when (id) {
            LOADER_NOTES -> loader = createLoaderNotes()
            LOADER_COURSES -> loader = createLoaderCourses()
        }
        return loader as Loader<Cursor>
    }

    private fun createLoaderCourses(): CursorLoader? {
        courseQueryFinished = false
        return object : CursorLoader(this) {
            override fun loadInBackground(): Cursor? {
                val db = openHelper.readableDatabase
                val columns = arrayOf(
                    CourseInfoEntry.COLUMN_ID,
                    CourseInfoEntry.COLUMN_COURSE_ID,
                    CourseInfoEntry.COLUMN_COURSE_TITLE
                )
                return db.query(
                    CourseInfoEntry.TABLE_NAME,
                    columns, null, null, null, null,
                    CourseInfoEntry.COLUMN_COURSE_TITLE
                )
            }
        }
    }

    private fun createLoaderNotes(): CursorLoader {
        notesQueryFinished = false
        return object : CursorLoader(this) {
            override fun loadInBackground(): Cursor? {
                val db = openHelper.readableDatabase

                val selection = "${NoteInfoEntry.COLUMN_ID} = ?"
                val selectionArgs = arrayOf(noteId.toString())
                val columns = arrayOf(
                    NoteInfoEntry.COLUMN_COURSE_ID,
                    NoteInfoEntry.COLUMN_NOTE_TITLE,
                    NoteInfoEntry.COLUMN_NOTE_TEXT
                )
                return db.query(
                    NoteInfoEntry.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
                )
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            LOADER_NOTES -> loadFinishedNotes(data)
            LOADER_COURSES -> {
                adapterCourses.changeCursor(data)
                courseQueryFinished = true
                displayNoteWhenQueriesFinished()
            }
        }
    }

    private fun loadFinishedNotes(cursor: Cursor?) {
        this.noteCursor = cursor!!
        notesQueryFinished = true
        displayNoteWhenQueriesFinished()
    }

    private fun displayNoteWhenQueriesFinished() {
        if (courseQueryFinished && notesQueryFinished) displayNote()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_NOTES -> noteCursor.close()
            LOADER_COURSES -> adapterCourses.changeCursor(null)
        }
    }

    companion object {
        private const val LOADER_NOTES = 0
        private const val LOADER_COURSES = 1
    }
}

package com.example.notekeeper

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.SimpleCursorAdapter
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry
import kotlinx.android.synthetic.main.content_note.*

class NoteActivity : AppCompatActivity() {
    private var id = NOTE_ID_NOT_SET
    private lateinit var openHelper: NoteKeeperOpenHelper
    private lateinit var adapterCourses: SimpleCursorAdapter

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

        loadCourseData()

        initializeNote(savedInstanceState)
    }

    private fun loadCourseData() {
        val db = openHelper.readableDatabase
        val columns = arrayOf(
            CourseInfoEntry.COLUMN_ID,
            CourseInfoEntry.COLUMN_COURSE_ID,
            CourseInfoEntry.COLUMN_COURSE_TITLE
        )
        val cursor = db.query(
            CourseInfoEntry.TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            CourseInfoEntry.COLUMN_COURSE_TITLE
        )
        adapterCourses.changeCursor(cursor)
        db.close()
    }

    private fun initializeNote(savedInstanceState: Bundle?) {
        id = savedInstanceState?.getInt(NOTE_ID, NOTE_ID_NOT_SET) ?: intent.getIntExtra(
            NOTE_ID,
            NOTE_ID_NOT_SET
        )

        if (id != NOTE_ID_NOT_SET) {
            loadNoteData()
        } else {
            DataManager.notes.add(NoteInfo())
            id = DataManager.notes.lastIndex
        }
    }

    private fun loadNoteData() {
        val db = openHelper.readableDatabase

        val selection = "${NoteInfoEntry.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val columns = arrayOf(
            NoteInfoEntry.COLUMN_COURSE_ID,
            NoteInfoEntry.COLUMN_NOTE_TITLE,
            NoteInfoEntry.COLUMN_NOTE_TEXT
        )
        val cursor = db.query(
            NoteInfoEntry.TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null, null, null
        )
        val courseIdPos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
        val titlePos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
        val textPos = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)
        cursor.moveToNext()

        val courseId = cursor.getString(courseIdPos)
        val title = cursor.getString(titlePos)
        val text = cursor.getString(textPos)
        displayNote(courseId, title, text)
        cursor.close()
    }

    private fun displayNote(courseId: String, title: String, text: String) {
        textNoteTitle.setText(title)
        textNoteText.setText(text)

        val courseIndex = getIndexOfCourse(courseId)
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
        outState.putInt(NOTE_ID, id)
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
                    DataManager.notes[id],
                    id
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun moveNext() {
        ++id
        loadNoteData()
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (id >= DataManager.notes.lastIndex) {
            val menuItem = menu?.findItem(R.id.action_next)
            menuItem?.isEnabled = false
            menuItem?.icon = getDrawable(R.drawable.ic_baseline_arrow_forward_24_transparent)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        Log.d("NoteActivity", "onPause called")
//        saveNote()
    }

    private fun saveNote() {
        val note = DataManager.notes.find { note -> note.id == id }
        note?.text = textNoteText.text.toString()
        note?.title = textNoteTitle.text.toString()
        note?.course = spinnerCourses.selectedItem as CourseInfo
        NoteKeeperAppWidget.sendRefreshBroadcast(this)
    }

    override fun onDestroy() {
        openHelper.close()
        super.onDestroy()
    }
}
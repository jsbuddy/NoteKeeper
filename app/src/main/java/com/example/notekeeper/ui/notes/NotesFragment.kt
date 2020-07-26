package com.example.notekeeper.ui.notes

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.*
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry

class NotesFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var openHelper: NoteKeeperOpenHelper
    private lateinit var noteRecyclerAdapter: NoteRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notesViewModel =
            ViewModelProviders.of(this).get(NotesViewModel::class.java)

        openHelper = NoteKeeperOpenHelper(requireContext())

        val root = inflater.inflate(R.layout.fragment_notes, container, false)
        initialize(root)

        return root
    }

    private fun initialize(root: View) {
        noteRecyclerAdapter = NoteRecyclerAdapter(requireContext(), null)
        displayNotes(root)
    }

    private fun displayNotes(root: View) {
        val listItems = root.findViewById<RecyclerView>(R.id.listItems)
        listItems.layoutManager = LinearLayoutManager(requireContext())
        listItems.adapter = noteRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        LoaderManager.getInstance(this).restartLoader(LOADER_NOTES, null, this)
    }

    override fun onDestroy() {
        openHelper.close()
        super.onDestroy()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        var loader: CursorLoader? = null
        when (id) {
            LOADER_NOTES -> loader = createLoaderNotes()
        }
        return loader as Loader<Cursor>
    }

    private fun createLoaderNotes(): CursorLoader {
        return object : CursorLoader(requireContext()) {
            override fun loadInBackground(): Cursor? {
                val db = openHelper.readableDatabase
                val noteColumns: Array<String> = arrayOf(
                    NoteInfoEntry.COLUMN_NOTE_TITLE,
                    NoteInfoEntry.COLUMN_COURSE_ID,
                    NoteInfoEntry.COLUMN_ID
                )
                return db.query(
                    NoteInfoEntry.TABLE_NAME,
                    noteColumns,
                    null,
                    null,
                    null,
                    null,
                    "${NoteInfoEntry.COLUMN_COURSE_ID}, ${NoteInfoEntry.COLUMN_NOTE_TITLE}"
                )
            }
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            LOADER_NOTES -> {
                noteRecyclerAdapter.changeCursor(data!!)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_NOTES -> noteRecyclerAdapter.changeCursor(null)
        }
    }

    companion object {
        private const val LOADER_NOTES = 0
    }
}
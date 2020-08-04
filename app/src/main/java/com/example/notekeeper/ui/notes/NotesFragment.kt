package com.example.notekeeper.ui.notes

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.NoteKeeperProviderContract.Notes
import com.example.notekeeper.NoteRecyclerAdapter
import com.example.notekeeper.R

class NotesFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var noteRecyclerAdapter: NoteRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            LOADER_NOTES -> createLoaderNotes()
            else -> null
        } as Loader<Cursor>
    }

    private fun createLoaderNotes(): CursorLoader {
        val noteColumns: Array<String> = arrayOf(
            Notes.COLUMN_ID,
            Notes.COLUMN_NOTE_TITLE,
            Notes.COLUMN_COURSE_TITLE
        )
        val noteOrderBy =
            "${Notes.COLUMN_COURSE_TITLE}, ${Notes.COLUMN_NOTE_TITLE}"

        return CursorLoader(
            requireContext(),
            Notes.CONTENT_EXPANDED_URI,
            noteColumns,
            null,
            null,
            noteOrderBy
        )
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
package com.example.notekeeper.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.*
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {
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
        loadNotes()
        listItems.adapter?.notifyDataSetChanged()
    }

    private fun loadNotes() {
        val db = openHelper.readableDatabase
        val noteColumns: Array<String> = arrayOf(
            NoteInfoEntry.COLUMN_NOTE_TITLE,
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
                "${NoteInfoEntry.COLUMN_COURSE_ID}, ${NoteInfoEntry.COLUMN_NOTE_TITLE}"
            )
        noteRecyclerAdapter.changeCursor(noteCursor)
    }

    override fun onDestroy() {
        openHelper.close()
        super.onDestroy()
    }
}
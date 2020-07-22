package com.example.notekeeper.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.DataManager
import com.example.notekeeper.NoteRecyclerAdapter
import com.example.notekeeper.R
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment() {
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notesViewModel =
            ViewModelProviders.of(this).get(NotesViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_notes, container, false)
        displayNotes(root)

        return root
    }

    private fun displayNotes(root: View) {
        val listItems = root.findViewById<RecyclerView>(R.id.listItems)
        listItems.layoutManager = LinearLayoutManager(requireContext())
        listItems.adapter = NoteRecyclerAdapter(requireContext(), DataManager.notes)
    }

    override fun onResume() {
        super.onResume()
        listItems.adapter?.notifyDataSetChanged()
    }
}
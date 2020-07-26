package com.example.notekeeper

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry

class NoteRecyclerAdapter(private val context: Context, private var cursor: Cursor?) :
    RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var courseIdPos: Int = 0
    private var titlePos: Int = 0
    private var textPos: Int = 0
    private var idPos: Int = 0

    init {
        populateColumnPositions()
    }

    private fun populateColumnPositions() {
        if (cursor == null) return
        courseIdPos = cursor!!.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID)
        titlePos = cursor!!.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE)
        textPos = cursor!!.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT)
        idPos = cursor!!.getColumnIndex(NoteInfoEntry.COLUMN_ID)
    }

    fun changeCursor(_cursor: Cursor) {
        if (cursor !== null) cursor!!.close()
        cursor = _cursor
        populateColumnPositions()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCourse = itemView.findViewById<TextView?>(R.id.textCourse)
        val textTitle = itemView.findViewById<TextView?>(R.id.textTitle)
        var id: Int? = null

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra(NOTE_ID, id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.item_note_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = cursor?.count ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        val course = cursor?.getString(courseIdPos)
        val title = cursor?.getString(titlePos)
        val id = cursor?.getInt(idPos)

        holder.textCourse?.text = course
        holder.textTitle?.text = title
        holder.id = id
    }
}
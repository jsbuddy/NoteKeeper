package com.example.notekeeper

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry

class CourseRecyclerAdapter(context: Context, private var cursor: Cursor?) :
    RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private var titlePos: Int = 0
    private var idPos: Int = 0

    init {
        populateColumnPositions()
    }

    private fun populateColumnPositions() {
        if (cursor == null) return
        titlePos = cursor!!.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE)
        idPos = cursor!!.getColumnIndex(CourseInfoEntry.COLUMN_ID)
    }

    fun changeCursor(_cursor: Cursor?) {
        cursor = _cursor
        populateColumnPositions()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textCourse)
        var id: Int? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.item_course_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = cursor?.count ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        val title = cursor?.getString(titlePos)
        val id = cursor?.getInt(idPos)

        holder.textTitle.text = title
        holder.id = id
    }
}
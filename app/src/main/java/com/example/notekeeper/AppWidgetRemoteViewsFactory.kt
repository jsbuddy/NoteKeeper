package com.example.notekeeper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class AppWidgetRemoteViewsFactory(val context: Context) : RemoteViewsService.RemoteViewsFactory {
    override fun onCreate() = Unit

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun onDataSetChanged() = Unit

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.item_note_widget)
        rv.setTextViewText(R.id.note_title, DataManager.notes[p0].title)

        val extras = Bundle()
        extras.putInt(NOTE_POSITION, p0)
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent)

        return rv
    }

    override fun getCount(): Int = DataManager.notes.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() = Unit

}
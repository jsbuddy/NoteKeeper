package com.example.notekeeper

import android.content.Intent
import android.widget.RemoteViewsService

class AppWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return AppWidgetRemoteViewsFactory(applicationContext)
    }
}
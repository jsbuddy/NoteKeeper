package com.example.notekeeper

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NoteReminderNotification {
    const val CHANNEL_ID = "reminder"
    const val CHANNEL_NAME = "Note Reminders"
    const val CHANNEL_DESCRIPTION = "N/A"

    fun notify(context: Context, noteId: Int, noteText: String, noteTitle: String) {
        val intent = Intent(context, NoteActivity::class.java)
        intent.putExtra(NOTE_ID, noteId)

        val pendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val shareIntent = PendingIntent.getActivity(
            context, 0,
            Intent.createChooser(
                Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, noteText), "Share Note Reminder"
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_baseline_assignment_24)
            .setContentTitle(noteTitle)
            .setContentText(noteText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(context, R.color.colorAccent))
            .setColorized(true)
            .setAutoCancel(true)
            .addAction(
                0, "View all notes", PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, ItemsActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(R.drawable.ic_baseline_share_24, "Share", shareIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(noteText)
                    .setBigContentTitle(noteTitle)
                    .setSummaryText("Review note")
            )

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }
}
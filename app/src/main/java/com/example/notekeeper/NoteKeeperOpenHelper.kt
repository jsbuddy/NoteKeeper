package com.example.notekeeper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry
import com.example.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry
import com.jwhh.jim.notekeeper.DatabaseDataWorker

class NoteKeeperOpenHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE)
        db.execSQL(NoteInfoEntry.SQL_CREATE_TABLE)
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1)
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1)

        val worker = DatabaseDataWorker(db)
        worker.insertCourses()
        worker.insertSampleNotes()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1)
            db?.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1)
        }
    }

    companion object {
        const val DATABASE_NAME = "notekeeper.db"
        const val DATABASE_VERSION = 2
    }
}
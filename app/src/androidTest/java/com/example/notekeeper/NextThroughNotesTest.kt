package com.example.notekeeper

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NextThroughNotesTest {
    @get: Rule
    val itemsActivity = ActivityTestRule(ItemsActivity::class.java)

    @Test
    fun nextThroughNotes() {
        onView(withId(R.id.listItems)).perform(
            RecyclerViewActions.actionOnItemAtPosition<NoteRecyclerAdapter.ViewHolder>(0, click())
        )

        for (i in 0..DataManager.notes.lastIndex) {
            val note = DataManager.notes[i]

            onView(withId(R.id.spinnerCourses)).check(matches(withSpinnerText(note.course?.title)))
            onView(withId(R.id.textNoteTitle)).check(matches(withText(note.title)))
            onView(withId(R.id.textNoteText)).check(matches(withText(note.text)))

            if (i != DataManager.notes.lastIndex)
                onView(withId(R.id.action_next)).perform(click())
        }

        onView(withId(R.id.action_next)).check(matches(not(isEnabled())))
    }
}
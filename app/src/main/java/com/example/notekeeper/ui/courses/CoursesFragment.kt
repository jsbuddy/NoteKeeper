package com.example.notekeeper.ui.courses

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.CourseRecyclerAdapter
import com.example.notekeeper.NoteKeeperProviderContract.Courses
import com.example.notekeeper.R

class CoursesFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var galleryViewModel: CoursesViewModel
    private lateinit var courseRecycleAdapter: CourseRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_courses, container, false)

        initialize(root)

        return root
    }

    private fun initialize(root: View) {
        courseRecycleAdapter = CourseRecyclerAdapter(requireContext(), null)
        displayCourses(root)
    }

    private fun displayCourses(root: View) {
        val listCourses = root.findViewById<RecyclerView>(R.id.listCourses)
        listCourses.layoutManager = GridLayoutManager(requireContext(), 2)
        listCourses.adapter = courseRecycleAdapter
    }

    override fun onResume() {
        super.onResume()
        LoaderManager.getInstance(this)
            .restartLoader(LOADER_COURSES, null, this)
    }

    companion object {
        private const val LOADER_COURSES: Int = 0
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            LOADER_COURSES -> createLoaderCourses()
            else -> null
        } as Loader<Cursor>
    }

    private fun createLoaderCourses(): CursorLoader {
        val columns = arrayOf(
            Courses.COLUMN_ID,
            Courses.COLUMN_COURSE_TITLE
        )

        val orderBy = Courses.COLUMN_COURSE_TITLE

        return CursorLoader(
            requireContext(),
            Courses.CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            LOADER_COURSES -> courseRecycleAdapter.changeCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        when (loader.id) {
            LOADER_COURSES -> courseRecycleAdapter.changeCursor(null)
        }
    }
}
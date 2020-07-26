package com.example.notekeeper.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notekeeper.CourseRecyclerAdapter
import com.example.notekeeper.DataManager
import com.example.notekeeper.R

class CoursesFragment : Fragment() {

    private lateinit var galleryViewModel: CoursesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel = ViewModelProvider(this).get(CoursesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_courses, container, false)

        val listCourses = root.findViewById<RecyclerView>(R.id.listCourses)
        listCourses.layoutManager = GridLayoutManager(requireContext(), 2)
        listCourses.adapter = CourseRecyclerAdapter(
            requireContext(), DataManager.courses.values.toList()
        )

        return root
    }
}
package com.raji.todoapp.ui.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.raji.todoapp.R
import com.raji.todoapp.databinding.FragmentTasksBinding
import com.raji.todoapp.onQuerySubmit
import com.raji.todoapp.ui.SortOrder
import com.raji.todoapp.ui.TaskListAdapter
import com.raji.todoapp.ui.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_tasks) {

    private val taskListViewModel by viewModels<TasksViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskListAdapter()

        binding.apply {
            rvTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        taskListViewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        setHasOptionsMenu(true)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.onQuerySubmit {
            taskListViewModel.searchQuery.value = it

        }

        viewLifecycleOwner.lifecycleScope.launch{
            menu.findItem(R.id.action_hide_completed_tasks).isChecked = taskListViewModel.preferenceFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                taskListViewModel.onSortSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                taskListViewModel.onSortSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                taskListViewModel.onHideCompleteClicked(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
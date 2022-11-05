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
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.raji.todoapp.R
import com.raji.todoapp.data.Task
import com.raji.todoapp.databinding.FragmentTasksBinding
import com.raji.todoapp.onQuerySubmit
import com.raji.todoapp.ui.TaskListAdapter
import com.raji.todoapp.ui.viewmodel.SortOrder
import com.raji.todoapp.ui.viewmodel.TaskEvent
import com.raji.todoapp.ui.viewmodel.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_tasks), TaskListAdapter.OnItemClickListener {

    private val taskListViewModel by viewModels<TasksViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskListAdapter(this)

        binding.apply {
            rvTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    taskListViewModel.onTaskSwiped(task)
                }

            }).attachToRecyclerView(rvTasks)

            fabAddTask.setOnClickListener {
                taskListViewModel.onAddNewTaskClick()
            }
        }

        taskListViewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            taskListViewModel.taskEvent.collect { taskevent ->
                when (taskevent) {

                    is TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task Deleted", Snackbar.LENGTH_SHORT)
                            .setAction(
                                "Undo"
                            ) {
                                taskListViewModel.undoDeletedTask(taskevent.task)
                            }.show()
                    }
                    TaskEvent.NavigateToAddTaskScreen -> {

                        val action =
                            TaskListFragmentDirections.actionTaskListFragmentToAddEditFragment(
                                title = "Add New Task",
                                null
                            )
                        findNavController().navigate(action)

                    }
                    is TaskEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TaskListFragmentDirections.actionTaskListFragmentToAddEditFragment(
                                task = taskevent.task,
                                title = "Edit Task"

                            )
                        findNavController().navigate(action)
                    }
                }
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                taskListViewModel.preferenceFlow.first().hideCompleted
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

    override fun onItemClick(task: Task) {
        taskListViewModel.onTaskSelected(task)

    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        taskListViewModel.onTaskCheckedChanged(task, isChecked)
    }

}
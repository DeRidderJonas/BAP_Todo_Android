package be.nextapps.jonas.bap_todo_android

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tasks_fragment, container, false);

        val button: Button = view.findViewById(R.id.create_task_button);

        button.setOnClickListener(View.OnClickListener {
            if(it is Button) {
                val task: Task = Task(0, "test", false, "none", "none");
                uiScope.launch {
                    val newRowId = taskViewModel.insert(task);

                    val activity: FragmentActivity? = getActivity();
                    val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    with(sharedPref!!.edit()){
                        putString(getString(R.string.current_task_id), newRowId.toString());
                        apply();
                    }
                }
            }
        })

        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java);
    }
}

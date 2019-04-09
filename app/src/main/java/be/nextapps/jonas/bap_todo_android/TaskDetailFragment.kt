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
import android.widget.CheckBox
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskDetailFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    var task: Task = Task(-1, "placeholder", false, "placeholder", "placeholder")
    val uiScope = CoroutineScope(Dispatchers.Main);

    suspend fun loadData() = uiScope.launch {
        val activity: FragmentActivity? = getActivity();
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        val currentTaskId : String = sharedPref!!.getString(getString(R.string.current_task_id), "0");

        val result = taskViewModel.getById(currentTaskId.toInt());
        if(result != null){
            task = result as Task;
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiScope.launch {
            val result = loadData()
        }
        val view = inflater.inflate(R.layout.task_detail_fragment, container, false);

        val editText: EditText = view.findViewById(R.id.detail_task_title);
        val checkBox: CheckBox = view.findViewById(R.id.detail_task_done);
        val save: Button = view.findViewById(R.id.detail_task_save);
        val deadlineButton: Button = view.findViewById(R.id.detail_task_deadline);
        val extraButton: Button = view.findViewById(R.id.detail_task_extra);

        checkBox.setOnClickListener(View.OnClickListener {
            if(it is CheckBox) {
                val checked : Boolean = it.isChecked;
            }
        })

        save.setOnClickListener(View.OnClickListener {
            if(it is Button) {

            }
        })

        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java);
    }
}


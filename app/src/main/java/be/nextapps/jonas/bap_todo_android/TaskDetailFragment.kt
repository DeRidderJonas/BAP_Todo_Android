package be.nextapps.jonas.bap_todo_android

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class TaskDetailFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    var task: Task = Task(-1, "placeholder", false, "placeholder", "placeholder")
    val uiScope = CoroutineScope(Dispatchers.Main);

    lateinit var editText: EditText;
    lateinit var checkBox: CheckBox;
    lateinit var deadlineButton: Button;
    lateinit var extraButton: Button;
    lateinit var extraDropdown: Spinner;
    lateinit var extraTextView: TextView;

    suspend fun loadData() = uiScope.launch {
        val activity: FragmentActivity? = getActivity();
        val sharedPref = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        val currentTaskId : String = sharedPref!!.getString(getString(R.string.current_task_id), "0");
        println("CurrentTaskId:"+currentTaskId)

        val result = taskViewModel.getById(currentTaskId.toInt());
        if(result != null) {
            task = result;
            editText.setHint(task.title)
            checkBox.isChecked = task.done
            deadlineButton.setText(task.deadline)
            extraTextView.setText(task.extra)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        uiScope.launch {
            val result = loadData()
        }
        val view = inflater.inflate(R.layout.task_detail_fragment, container, false);

        editText= view.findViewById(R.id.detail_task_title);
        checkBox= view.findViewById(R.id.detail_task_done);
        val save: Button = view.findViewById(R.id.detail_task_save);
        val refresh: Button = view.findViewById(R.id.detail_task_refresh);
        deadlineButton = view.findViewById(R.id.detail_task_deadline);
        extraButton = view.findViewById(R.id.detail_task_extra_button);
        extraDropdown = view.findViewById(R.id.detail_task_extra_dropdown);
        extraTextView = view.findViewById(R.id.detail_task_extra);

        checkBox.setOnClickListener(View.OnClickListener {
            if(it is CheckBox) {
                val checked : Boolean = it.isChecked;
                task.done = checked;
            }
        })

        save.setOnClickListener(View.OnClickListener {
            if(it is Button) {
                task.title = editText.text.toString();
                uiScope.launch {
                    taskViewModel.update(task)
                }
            }
        })

        refresh.setOnClickListener(View.OnClickListener {
            if(it is Button){
                uiScope.launch {
                    loadData()
                }
            }
        })

        deadlineButton.setOnClickListener(View.OnClickListener {
            if(it is Button) {
                val c = Calendar.getInstance();
                val year = c.get(Calendar.YEAR);
                val month = c.get(Calendar.MONTH);
                val day = c.get(Calendar.DAY_OF_MONTH);

                val dpd = DatePickerDialog(
                    activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val deadlineString = "$dayOfMonth/${monthOfYear + 1}/$year"
                        task.deadline = deadlineString;
                        deadlineButton.setText(deadlineString)
                    }, year, month, day);
                dpd.show()
            }
        })

        val extraDropdownAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, listOf("None", "Not important"))
        extraDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        extraDropdown.adapter = extraDropdownAdapter;
        extraDropdown.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                task.extra = "empty"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.selectedItem as String
                task.extra = item
                extraTextView.setText(task.extra)
            }

        }

        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java);
    }
}


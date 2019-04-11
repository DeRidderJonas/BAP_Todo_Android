package be.nextapps.jonas.bap_todo_android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {
    //Database variables
    private lateinit var taskViewModel: TaskViewModel
    val uiScope = CoroutineScope(Dispatchers.Main)

    //Recyclerview variables
    private lateinit var recyclerView: RecyclerView;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tasks_fragment, container, false);

        val create: Button = view.findViewById(R.id.create_task_button);

        create.setOnClickListener(View.OnClickListener {
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

        val recyclerManager: RecyclerView.LayoutManager = LinearLayoutManager(context);
        val recyclerAdapter = TasksAdapter()
        recyclerView = view.findViewById<RecyclerView>(R.id.tasks_recycler_view).apply {
            layoutManager = recyclerManager;
            adapter = recyclerAdapter
        }

        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { recyclerAdapter.setTasks(it) }
        })

        return view;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java);
    }
}

class TasksAdapter():
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    private var tasks = emptyList<Task>();
    class TaskViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(task: Task){
            println("binding view for task ${task}")
            val displayString = "${task.title} \t ${task.done} \t ${task.id}"
            view.findViewById<TextView>(R.id.item_display_text).text = displayString;
            view.findViewById<Button>(R.id.item_button_set_active).setOnClickListener(View.OnClickListener {
                val sharedPref = view.context?.getSharedPreferences(view.context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                with(sharedPref!!.edit()){
                    putString(view.context?.getString(be.nextapps.jonas.bap_todo_android.R.string.current_task_id), task.id.toString());
                    apply();
                }
            })
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_recycler_item, parent, false)
                return TaskViewHolder(view);
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TaskViewHolder {
        return TaskViewHolder.create(p0)
    }

    override fun onBindViewHolder(p0: TaskViewHolder, p1: Int) {
        println("onBindBiewHolder ${tasks[p1]}")
        p0.bind(tasks[p1]);
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    internal fun setTasks(tasks: List<Task>){
        this.tasks = tasks;
        notifyDataSetChanged()
    }
}
package be.nextapps.jonas.bap_todo_android

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class TasksFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tasks_fragment, container, false);

        val button: Button = view.findViewById(R.id.create_task_button);
        val textView : TextView = view.findViewById(R.id.list_task_title) as TextView


        button.setOnClickListener(View.OnClickListener {
            if(it is Button) {

            }
        })

        return view;
    }

}

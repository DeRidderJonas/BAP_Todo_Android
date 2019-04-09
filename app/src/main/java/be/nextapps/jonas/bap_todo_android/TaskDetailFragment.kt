package be.nextapps.jonas.bap_todo_android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

class TaskDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.task_detail_fragment, container, false);

        val editText: EditText = view.findViewById(R.id.detail_task_title);
        val checkBox: CheckBox = view.findViewById(R.id.detail_task_done);
        val save: Button = view.findViewById(R.id.detail_task_save);

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
    }
}


package be.nextapps.jonas.bap_todo_android

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MyPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment {
        return when(p0){
            0 -> {
                TasksFragment()
            }
            1 -> {
                TaskDetailFragment()
            }
            else -> {
                return AlarmFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3;
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Tasks"
            1 -> "Task Detail"
            else -> {
                return "Alarm"
            }
        }
    }
}
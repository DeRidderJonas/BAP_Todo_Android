package be.nextapps.jonas.bap_todo_android

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread

class TaskRepository(private val taskDao: TaskDao){
    val allTasks: List<Task> = taskDao.getAll();

    @WorkerThread
    suspend fun insert(task: Task): Long{
        return taskDao.insert(task);
    }

    @WorkerThread
    suspend fun update(task: Task){
        taskDao.update(task);
    }

    @WorkerThread
    suspend fun getById(id: Int): Task {
        return taskDao.getById(id);
    }
}
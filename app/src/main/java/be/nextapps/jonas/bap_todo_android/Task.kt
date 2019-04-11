package be.nextapps.jonas.bap_todo_android

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@Entity(tableName = "task_table")
data class Task(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
        @ColumnInfo(name = "title") var title: String,
        @ColumnInfo(name = "done") var done: Boolean,
        @ColumnInfo(name = "deadline") var deadline: String,
        @ColumnInfo(name = "extra") var extra: String) {

    override fun toString(): String {
        return "{'id': $id, 'title': '$title', 'done': $done, 'deadline': '$deadline', 'extra': '$extra'}";
    }

}

@Dao
interface TaskDao {

    @Query("SELECT * from task_table")
    fun getAll(): LiveData<List<Task>>

    @Query("select * from task_table where id = :id")
    fun getById(id: Int) : Task

    @Insert
    fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Query("DELETE from task_table")
    fun deleteAll()
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    init {
        val taskDao = TaskDatabase.getDatabase(application, scope).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    suspend fun insert(task: Task): Long {
        var id: Long? = null;
        val job = scope.launch {
            val result = repository.insert(task)
            id = result
        }
        delay(500)
        return id as Long
    }

    suspend fun getById(id: Int): Task? {
        var task: Task? = null;
        val job = scope.launch {
            val result = repository.getById(id)
            task = result;
        }
        delay(500);
        return task;
    }

    suspend fun update(task: Task) {
        scope.launch {
            repository.update(task);
        }
    }
}
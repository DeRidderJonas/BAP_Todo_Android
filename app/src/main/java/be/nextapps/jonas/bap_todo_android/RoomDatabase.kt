package be.nextapps.jonas.bap_todo_android

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Task::class], version = 2)
public abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null;

        fun getDatabase(context: Context, scope: CoroutineScope): TaskDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val migration_1_2 = object: Migration(1,2){
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("""
                            create table tasks (id integer primary key autoincrement, title text, done integer, deadline text, extra text);
                            insert into tasks (title, done, deadline, extra)
                            select title, done, deadline, extra from task_table;
                            drop table task_table;
                            alter table tasks rename to task_table
                        """.trimIndent())
                    }

                }
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TaskDatabase::class.java,
                        "Task_database"
                )
                .addMigrations(migration_1_2)
                .allowMainThreadQueries()
                .addCallback(TaskDatabaseCallback(scope))
                .build()
                INSTANCE = instance;
                return instance
            }
        }
    }

    private class TaskDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDb(database.taskDao())
                }
            }
        }
        fun populateDb(taskDao: TaskDao){
//            val task = Task(1, "test", false, "none", "none");
//            taskDao.insert(task)
        }
    }
}


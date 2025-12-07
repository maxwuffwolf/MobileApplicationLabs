package com.lab4.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lab4.data.dao.SubjectDao
import com.lab4.data.dao.SubjectLabsDao
import com.lab4.data.entity.LabStatus
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lab4Database - the main database class
 * - extends on RoomDatabase()
 * - marked with @Database annotation for generating communication interfaces
 * - in annotation are added all your entities (tables)
 * - includes abstract properties of all DAO interfaces for each entity (table)
 */
@Database(entities = [SubjectEntity::class, SubjectLabEntity::class], version = 2)
abstract class Lab4Database : RoomDatabase() {
    //DAO properties for each entity (table)
    // must be abstract (because Room will generate instances by itself)
    abstract val subjectsDao: SubjectDao
    abstract val subjectLabsDao: SubjectLabsDao
}

/**
 * DatabaseStorage - custom class where you initialize and store Lab4Database single instance
 *
 */
object DatabaseStorage {
    // ! Important - all operations with DB must be done from non-UI thread!
    // coroutineScope: CoroutineScope - is the scope which allows to run asynchronous operations
    // > we will learn it soon! For now just put it here
    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        },
    )

    // single instance of Lab4Database
    private var _database: Lab4Database? = null

    /**
        Function of initializing and getting Lab4Database instance
        - is invoked from place where DB should be used (from Compose screens)
        [context] - context from Compose screen to init DB
    */
    fun getDatabase(context: Context): Lab4Database {
        // if _database already contains Lab4Database instance, return this instance
        if (_database != null) return _database as Lab4Database
        // if not, create instance, preload some data and return this instance
        else {
            // creating Lab4Database instance by builder
            _database = Room.databaseBuilder(
                context,
                Lab4Database::class.java, "lab4Database"
            )
                .fallbackToDestructiveMigration() // Allow destructive migration for schema changes
                .build()

            // preloading some data to DB
            preloadData()

            return _database as Lab4Database
        }
    }

    /**
        Function for preloading some initial data to DB
     */
    private fun preloadData() {
        // List of subjects - 5 subjects from the semester
        val listOfSubject = listOf(
            SubjectEntity(title = "Мережева безпека"),
            SubjectEntity(title = "Розгортання інформаційно-комунікаційних систем"),
            SubjectEntity(title = "Економіка та підприємництво"),
            SubjectEntity(title = "Проєктування мультисервісних систем"),
            SubjectEntity(title = "Програмування мобільних додатків"),
        )
        
        // List of labs for each subject
        val listOfSubjectLabs = mutableListOf<SubjectLabEntity>()
        
        // Мережева безпека - 12 labs
        for (i in 1..12) {
            listOfSubjectLabs.add(
                SubjectLabEntity(
                    subjectId = 1,
                    title = "Лабораторна робота №$i",
                    description = "Опис лабораторної роботи №$i з мережевої безпеки",
                    status = LabStatus.NOT_STARTED.name,
                    comment = ""
                )
            )
        }
        
        // Розгортання інформаційно-комунікаційних систем - 8 labs
        for (i in 1..8) {
            listOfSubjectLabs.add(
                SubjectLabEntity(
                    subjectId = 2,
                    title = "Лабораторна робота №$i",
                    description = "Опис лабораторної роботи №$i з розгортання ІКС",
                    status = if (i <= 3) LabStatus.COMPLETED.name else LabStatus.NOT_STARTED.name,
                    comment = if (i == 1) "Перша робота виконана успішно" else ""
                )
            )
        }
        
        // Економіка та підприємництво - 1 lab
        listOfSubjectLabs.add(
            SubjectLabEntity(
                subjectId = 3,
                title = "Лабораторна робота №1",
                description = "Опис лабораторної роботи з економіки та підприємництва",
                status = LabStatus.IN_PROGRESS.name,
                comment = "Робота в процесі виконання"
            )
        )
        
        // Проєктування мультисервісних систем - 8 labs
        for (i in 1..8) {
            listOfSubjectLabs.add(
                SubjectLabEntity(
                    subjectId = 4,
                    title = "Лабораторна робота №$i",
                    description = "Опис лабораторної роботи №$i з проєктування МСС",
                    status = when {
                        i <= 2 -> LabStatus.COMPLETED.name
                        i == 3 -> LabStatus.IN_PROGRESS.name
                        i == 4 -> LabStatus.POSTPONED.name
                        else -> LabStatus.NOT_STARTED.name
                    },
                    comment = when (i) {
                        3 -> "Потрібно доробити діаграми"
                        4 -> "Відкладено через інші роботи"
                        else -> ""
                    }
                )
            )
        }
        
        // Програмування мобільних додатків - 7 labs
        for (i in 1..7) {
            listOfSubjectLabs.add(
                SubjectLabEntity(
                    subjectId = 5,
                    title = "Лабораторна робота №$i",
                    description = "Опис лабораторної роботи №$i з програмування мобільних додатків",
                    status = when {
                        i <= 4 -> LabStatus.COMPLETED.name
                        i == 5 -> LabStatus.IN_PROGRESS.name
                        else -> LabStatus.NOT_STARTED.name
                    },
                    comment = if (i == 5) "Зараз виконується Lab4" else ""
                )
            )
        }

        // Request to add all Subjects from the list to DB
        listOfSubject.forEach { subject ->
            // coroutineScope.launch{...} - start small thread where you can make query to DB
            coroutineScope.launch {
                // INSERT query to add Subject (subjectsDao is used)
                _database?.subjectsDao?.addSubject(subject)
            }
        }
        // Request to add all Labs from the list to DB
        listOfSubjectLabs.forEach { lab ->
            coroutineScope.launch {
                // INSERT query to add Lab (subjectLabsDao is used)
                _database?.subjectLabsDao?.addSubjectLab(lab)
            }
        }
    }
}
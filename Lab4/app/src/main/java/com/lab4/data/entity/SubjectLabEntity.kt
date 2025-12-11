package com.lab4.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * SubjectLabEntity - the data class which represents the `subjectsLabs` table
 * - marked with annotation @Entity - for SQL table
 * - contains @PrimaryKey field id - all objects in tables has unique primary keys
 * - contains subjectId field - which represents dependency to `subjects` data
 * (there are more configurations of tables relations, which you can learn by yourself)
 * - contains other fields with primitive values
 */
@Entity(
    tableName = "subjectsLabs",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subject_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )]
)
data class SubjectLabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "subject_id") val subjectId: Int,
    val title: String,
    val description: String,
    val comment: String? = null,
    val status: LabStatus = LabStatus.NOT_STARTED
)
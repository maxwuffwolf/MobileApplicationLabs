package com.lab4.data.db

import androidx.room.TypeConverter
import com.lab4.data.entity.LabStatus

/**
 * Converters for Room database to handle custom types
 */
class Converters {
    @TypeConverter
    fun fromLabStatus(status: LabStatus): String {
        return status.name
    }

    @TypeConverter
    fun toLabStatus(value: String): LabStatus {
        return LabStatus.valueOf(value)
    }
}

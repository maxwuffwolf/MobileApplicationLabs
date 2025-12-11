package com.lab4.data.entity

/**
 * Extension function to get Ukrainian text representation of LabStatus
 */
fun LabStatus.toUkrainianText(): String {
    return when (this) {
        LabStatus.NOT_STARTED -> "Не розпочато"
        LabStatus.IN_PROGRESS -> "В прогресі"
        LabStatus.POSTPONED -> "Відкладено"
        LabStatus.COMPLETED -> "Виконано"
    }
}

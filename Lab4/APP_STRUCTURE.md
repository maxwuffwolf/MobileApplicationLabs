# Lab4 App Structure

## Navigation Flow
```
┌──────────────────────────┐
│  SubjectsListScreen      │
│  (List all subjects)     │
│                          │
│  • Мережева безпека      │
│  • Розгортання ІС        │
│  • Економіка             │
│  • Проектування КС       │
│  • Програмування МД      │
└────────────┬─────────────┘
             │ Click subject
             ▼
┌──────────────────────────┐
│ SubjectDetailsScreen     │
│ (List labs for subject)  │
│                          │
│  Лабораторна робота 1    │
│  Статус: Виконано        │
│  ─────────────────────   │
│  Лабораторна робота 2    │
│  Статус: В прогресі      │
│  ─────────────────────   │
│  Лабораторна робота 3    │
│  Статус: Не розпочато    │
└────────────┬─────────────┘
             │ Click lab
             ▼
┌──────────────────────────┐
│  LabDetailsScreen        │
│  (Edit lab details)      │
│                          │
│  ← Назад                 │
│                          │
│  Лабораторна робота 1    │
│  Опис: ...               │
│                          │
│  Статус: [Dropdown]      │
│  ├─ Не розпочато         │
│  ├─ В прогресі           │
│  ├─ Відкладено           │
│  └─ Виконано             │
│                          │
│  Коментар:               │
│  ┌─────────────────────┐ │
│  │                     │ │
│  │  [Text Area]        │ │
│  │                     │ │
│  └─────────────────────┘ │
│                          │
│  [Зберегти]              │
└──────────────────────────┘
```

## Data Model

### Database Tables

```
┌─────────────────────────┐
│      subjects           │
├─────────────────────────┤
│ id (PK)                 │
│ title                   │
└──────────┬──────────────┘
           │
           │ 1:N relationship
           │
┌──────────▼──────────────┐
│    subjectsLabs         │
├─────────────────────────┤
│ id (PK)                 │
│ subject_id (FK)         │
│ title                   │
│ description             │
│ comment                 │
│ status (enum)           │
└─────────────────────────┘
```

### LabStatus Enum
```kotlin
enum class LabStatus {
    NOT_STARTED,    // Не розпочато
    IN_PROGRESS,    // В прогресі
    POSTPONED,      // Відкладено
    COMPLETED       // Виконано
}
```

## File Structure

```
com.lab4/
├── MainActivity.kt
├── data/
│   ├── dao/
│   │   ├── SubjectDao.kt
│   │   └── SubjectLabsDao.kt
│   ├── db/
│   │   ├── Lab4Database.kt
│   │   └── Converters.kt
│   └── entity/
│       ├── SubjectEntity.kt
│       ├── SubjectLabEntity.kt
│       └── LabStatus.kt
└── ui/
    ├── navigation/
    │   └── NavigationGraph.kt
    ├── screens/
    │   ├── subjectsList/
    │   │   └── SubjectsListScreen.kt
    │   ├── subjectDetails/
    │   │   └── SubjectDetailsScreen.kt
    │   └── labDetails/
    │       └── LabDetailsScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Key Features

### 1. Persistent Storage
- All data stored in Room database
- Changes persist across app restarts
- Foreign key constraints for data integrity

### 2. Status Management
- Four distinct statuses for each lab
- Visual feedback with Ukrainian labels
- Easy-to-use dropdown selector

### 3. Comment System
- Multi-line text input for detailed notes
- Optional field (can be null)
- Persisted with each lab

### 4. Navigation
- Three-level navigation hierarchy
- Back button support
- Serializable route parameters

## Data Flow

```
User Action → UI Event → Coroutine Launch → DAO Method → Room Database
                                                              ↓
User Interface ← State Update ← LaunchedEffect ← Query Result ←
```

## Example Data

After initialization, the database contains:

1. **Мережева безпека** (12 labs)
   - Лабораторна робота 1-12
   
2. **Розгортання інформаційних систем** (8 labs)
   - Лабораторна робота 1-8
   
3. **Економіка та підприємництво** (1 lab)
   - Лабораторна робота 1
   
4. **Проектування комунікаційних систем** (8 labs)
   - Лабораторна робота 1-8
   
5. **Програмування мобільних додатків** (7 labs)
   - Лабораторна робота 1-7

**Total: 36 laboratory works**

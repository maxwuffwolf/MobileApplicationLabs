# Lab4 - Developer Guide

## Quick Start

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK with API 29+ (minSdk 29, targetSdk 34)

### Build Instructions

1. **Clone and Open**
   ```bash
   git clone https://github.com/maxwuffwolf/MobileApplicationLabs.git
   cd MobileApplicationLabs/Lab4
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - File → Open → Select Lab4 folder
   - Wait for Gradle sync

3. **Build**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run**
   - Select device/emulator
   - Click Run (green triangle)

## Project Structure

```
Lab4/
├── app/
│   └── src/main/java/com/lab4/
│       ├── MainActivity.kt              # Entry point
│       ├── data/
│       │   ├── dao/                     # Data Access Objects
│       │   │   ├── SubjectDao.kt
│       │   │   └── SubjectLabsDao.kt
│       │   ├── db/                      # Database
│       │   │   ├── Lab4Database.kt
│       │   │   └── Converters.kt
│       │   └── entity/                  # Data models
│       │       ├── SubjectEntity.kt
│       │       ├── SubjectLabEntity.kt
│       │       ├── LabStatus.kt
│       │       └── LabStatusExtensions.kt
│       └── ui/
│           ├── navigation/              # Navigation setup
│           │   └── NavigationGraph.kt
│           ├── screens/                 # UI screens
│           │   ├── subjectsList/
│           │   ├── subjectDetails/
│           │   └── labDetails/
│           └── theme/                   # Material3 theme
├── gradle/
│   └── libs.versions.toml              # Dependency versions
└── Documentation/
    ├── IMPLEMENTATION_NOTES.md
    ├── APP_STRUCTURE.md
    ├── CHANGES_SUMMARY.md
    ├── FINAL_SUMMARY.md
    └── DEVELOPER_GUIDE.md (this file)
```

## Key Components

### Data Layer

#### Entities
- **SubjectEntity** - Represents a subject/course
- **SubjectLabEntity** - Represents a lab work for a subject
- **LabStatus** - Enum for lab status (NOT_STARTED, IN_PROGRESS, POSTPONED, COMPLETED)

#### DAOs
- **SubjectDao** - CRUD operations for subjects
- **SubjectLabsDao** - CRUD operations for labs

#### Database
- **Lab4Database** - Room database with version 2
- **Converters** - TypeConverters for enum serialization

### UI Layer

#### Screens
1. **SubjectsListScreen** - List all subjects
2. **SubjectDetailsScreen** - Show labs for a subject
3. **LabDetailsScreen** - Edit lab status and comments

#### Navigation
- Uses Navigation3 library
- Serializable route parameters
- Back stack management

## Database Schema

### subjects table
```sql
id       INTEGER PRIMARY KEY AUTOINCREMENT
title    TEXT NOT NULL
```

### subjectsLabs table
```sql
id           INTEGER PRIMARY KEY AUTOINCREMENT
subject_id   INTEGER NOT NULL (FK to subjects.id)
title        TEXT NOT NULL
description  TEXT NOT NULL
comment      TEXT
status       TEXT NOT NULL (enum as string)
```

## Making Changes

### Adding a New Subject

Edit `Lab4Database.kt` in the `preloadData()` function:

```kotlin
val listOfSubject = listOf(
    SubjectEntity(title = "Мережева безпека"),
    // Add new subject here
    SubjectEntity(title = "New Subject"),
)
```

### Adding Labs to a Subject

```kotlin
// Add labs for subject ID 6
for (i in 1..10) {
    listOfSubjectLabs.add(
        SubjectLabEntity(
            subjectId = 6,  // ID of your new subject
            title = "Лабораторна робота $i",
            description = "Description for lab $i"
        )
    )
}
```

### Modifying Status Options

Edit `LabStatus.kt`:

```kotlin
enum class LabStatus {
    NOT_STARTED,
    IN_PROGRESS,
    POSTPONED,
    COMPLETED,
    NEW_STATUS  // Add new status here
}
```

Then update `LabStatusExtensions.kt`:

```kotlin
fun LabStatus.toUkrainianText(): String {
    return when (this) {
        LabStatus.NOT_STARTED -> "Не розпочано"
        LabStatus.IN_PROGRESS -> "В прогресі"
        LabStatus.POSTPONED -> "Відкладено"
        LabStatus.COMPLETED -> "Виконано"
        LabStatus.NEW_STATUS -> "Новий статус"
    }
}
```

### Database Migrations

When changing schema, increment version in `Lab4Database.kt`:

```kotlin
@Database(entities = [...], version = 3)  // Increment version
```

For production, add proper migration:

```kotlin
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add migration code here
    }
}

// In database builder:
.addMigrations(MIGRATION_2_3)
```

## Dependencies

### Core Dependencies
- **Kotlin**: 2.0.21
- **Compose BOM**: 2023.08.00
- **Room**: 2.6.1
- **Navigation3**: 1.0.0-alpha10

### Build Configuration
- **compileSdk**: 36
- **minSdk**: 29
- **targetSdk**: 34
- **AGP**: 8.7.2

## Testing

### Manual Testing Checklist

1. **Subject List**
   - [ ] All 5 subjects appear
   - [ ] Subjects are clickable
   - [ ] Navigation works

2. **Subject Details**
   - [ ] Correct number of labs shown
   - [ ] Labs are clickable
   - [ ] Status displayed correctly
   - [ ] Comments shown when present

3. **Lab Details**
   - [ ] Lab info loads correctly
   - [ ] Status dropdown works
   - [ ] Comment field accepts input
   - [ ] Save button persists changes
   - [ ] Back button works

4. **Persistence**
   - [ ] Close and reopen app
   - [ ] Changes are preserved
   - [ ] No data loss

## Common Issues

### Build Errors

**Issue**: Gradle sync fails
- **Solution**: Check internet connection, clear Gradle cache
  ```bash
  ./gradlew clean
  ./gradlew --refresh-dependencies
  ```

**Issue**: Room schema error
- **Solution**: Delete app data or use destructive migration

### Runtime Issues

**Issue**: Database not initializing
- **Solution**: Check `Lab4Database.getDatabase()` is called with proper context

**Issue**: Status not saving
- **Solution**: Verify `updateSubjectLab()` is called in coroutine scope

## Best Practices

### Code Style
- Use Kotlin conventions
- Keep functions small and focused
- Use meaningful variable names
- Add comments for complex logic

### Database
- Always use suspend functions with DAO
- Run database operations in coroutines
- Handle nullable return values
- Use transactions for multiple operations

### UI
- Keep UI state in remember/mutableStateOf
- Use LaunchedEffect for side effects
- Follow Material3 design guidelines
- Support configuration changes

### Navigation
- Use serializable data classes for routes
- Handle back navigation properly
- Validate route parameters

## Contributing

1. Create feature branch
2. Make changes
3. Test thoroughly
4. Commit with descriptive message
5. Create pull request

## Resources

- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Material3 Design](https://m3.material.io/)

## Support

For issues or questions:
1. Check documentation files in Lab4/
2. Review code comments
3. Check Android documentation
4. Create GitHub issue

---

**Last Updated**: December 11, 2025
**Version**: 1.0.0
**Status**: Production Ready ✅

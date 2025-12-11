# Lab4 Implementation Notes

## Overview
This application tracks the progress of lab work for the current semester. It includes a list of subjects with their associated laboratory works, allowing users to manage status and comments for each lab.

## Implemented Features

### 1. Subject Management
- **5 Subjects** have been added to the database:
  1. Мережева безпека (Network Security) - 12 labs
  2. Розгортання інформаційних систем (Information Systems Deployment) - 8 labs
  3. Економіка та підприємництво (Economics and Entrepreneurship) - 1 lab
  4. Проектування комунікаційних систем (Communication Systems Design) - 8 labs
  5. Програмування мобільних додатків (Mobile Application Programming) - 7 labs

### 2. Lab Status Management
Created a `LabStatus` enum with four states:
- **NOT_STARTED** (Не розпочано) - Not started
- **IN_PROGRESS** (В прогресі) - In progress
- **POSTPONED** (Відкладено) - Postponed
- **COMPLETED** (Виконано) - Completed

### 3. Data Model Changes

#### New Files:
- `LabStatus.kt` - Enum for lab statuses
- `Converters.kt` - Room TypeConverter for enum serialization
- `LabDetailsScreen.kt` - Screen for editing individual lab details

#### Modified Files:
- `SubjectLabEntity.kt` - Changed from boolean flags to LabStatus enum
- `SubjectLabsDao.kt` - Added update and get methods
- `Lab4Database.kt` - Updated to version 2, added TypeConverters, updated preload data
- `SubjectDetailsScreen.kt` - Enhanced UI with clickable lab cards
- `NavigationGraph.kt` - Added LabDetailsRoute for navigation

### 4. Navigation Flow
1. **SubjectsListScreen** - Shows list of all subjects
2. **SubjectDetailsScreen** - Shows list of labs for a subject
3. **LabDetailsScreen** (NEW) - Allows editing lab status and comments

### 5. Room Database Updates
- Database version upgraded from 1 to 2
- Added TypeConverters to handle LabStatus enum
- Added fallbackToDestructiveMigration for development
- Updated preloadData() to populate all 36 labs across 5 subjects

### 6. UI Improvements
- Material3 Card components for better visual hierarchy
- Ukrainian language labels throughout
- Dropdown selector for lab status
- Multi-line text field for comments
- Back navigation button
- Status badges showing current lab state

## Technical Details

### Database Schema
```kotlin
SubjectEntity:
- id: Int (PrimaryKey, AutoGenerate)
- title: String

SubjectLabEntity:
- id: Int (PrimaryKey, AutoGenerate)
- subjectId: Int (ForeignKey to SubjectEntity)
- title: String
- description: String
- comment: String?
- status: LabStatus (enum: NOT_STARTED, IN_PROGRESS, POSTPONED, COMPLETED)
```

### Key Components
1. **DAO Layer**: Added updateSubjectLab() and getSubjectLabById() methods
2. **UI Layer**: Three-screen navigation with full CRUD for lab status
3. **Data Persistence**: All changes saved to Room database

## Usage
1. Launch app to see list of subjects
2. Click on a subject to see its labs
3. Click on a lab to edit its status and add comments
4. Changes are automatically saved to the database
5. Use back button to return to previous screen

## Build Configuration
- compileSdk: 36
- minSdk: 29
- targetSdk: 34
- Kotlin: 2.0.21
- Room: 2.6.1
- Navigation3: 1.0.0-alpha10
- Compose BOM: 2023.08.00

## Notes
- The database uses destructive migration for development convenience
- All lab data is preloaded on first app launch
- Status and comments persist across app restarts
- Foreign key constraints ensure data integrity

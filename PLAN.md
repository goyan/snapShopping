# Development Plan - Fridge Scan App

## Overview

This document outlines the development plan for the Fridge Scan Android application, an AI-powered refrigerator inventory management tool.

## Architecture Decisions

### MVVM Pattern
- **View**: Jetpack Compose UI components
- **ViewModel**: State management with StateFlow
- **Model**: Room entities and data classes

### Repository Pattern
- Single source of truth for data operations
- Abstracts data sources (local DB + remote API)
- Easy to test and swap implementations

### Dependency Injection
- Hilt for compile-time DI
- Module-based configuration
- Scoped dependencies (Singleton for DB/API)

## Component Breakdown

### Data Layer

```
FoodItem (Entity)
├── id: String (UUID)
├── name: String
├── category: FoodCategory
├── confidence: Float
├── quantity: Int
└── addedAt: Long

FoodCategory (Enum)
├── DAIRY, MEAT, VEGETABLES, FRUITS
├── BEVERAGES, CONDIMENTS, LEFTOVERS
└── SNACKS, FROZEN, OTHER
```

### Database Schema

**Table: food_items**
| Column | Type | Notes |
|--------|------|-------|
| id | TEXT | Primary Key, UUID |
| name | TEXT | Food item name |
| category | TEXT | Enum stored as string |
| confidence | REAL | 0.0 - 1.0 |
| quantity | INTEGER | Default 1 |
| addedAt | INTEGER | Unix timestamp |

### API Integration

**Gemini Vision API**
- Model: `gemini-1.5-flash` (fast, cost-effective)
- Input: Compressed JPEG images (max 1024px)
- Output: Structured JSON with food items

**Request Flow**
```
Camera → ImageUtils.compress() → VisionApiService.analyzeImages()
    → Parse JSON → Repository.processDetectedFoods()
    → Filter/Merge/Normalize → Save to Room
```

### UI Screens

**1. Camera Screen**
- CameraX preview
- Multi-photo capture
- Photo thumbnails with remove option
- Gallery import support
- Analyze button

**2. Inventory Screen**
- Grouped by category
- Swipe to delete
- Quantity +/- controls
- Edit dialog
- Manual add dialog
- FAB to camera

### Navigation

```
Camera Screen ←→ Inventory Screen
     ↓                  ↓
  (on success)     (scan button)
```

## Implementation Phases

### Phase 1: Core Infrastructure ✅
- [x] Project setup with Gradle
- [x] Hilt dependency injection
- [x] Room database configuration
- [x] Data models and entities

### Phase 2: Camera Integration ✅
- [x] CameraX setup
- [x] Image capture
- [x] Image compression utility
- [x] Gallery import support

### Phase 3: AI Vision ✅
- [x] Gemini API integration
- [x] Vision prompt engineering
- [x] JSON response parsing
- [x] Error handling

### Phase 4: Data Processing ✅
- [x] Confidence filtering (< 0.6)
- [x] Name normalization
- [x] Duplicate merging
- [x] Category mapping

### Phase 5: UI Implementation ✅
- [x] Camera screen with preview
- [x] Inventory screen with list
- [x] Material 3 theming
- [x] Navigation setup

### Phase 6: Polish (Current)
- [x] Error states and loading
- [x] Empty state UI
- [ ] Unit tests
- [ ] UI tests

## Data Flow Diagram

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Camera    │────▶│  ViewModel   │────▶│  Repository │
│   Screen    │◀────│              │◀────│             │
└─────────────┘     └──────────────┘     └─────────────┘
                                               │
                          ┌────────────────────┼────────────────────┐
                          ▼                    ▼                    ▼
                    ┌──────────┐        ┌────────────┐       ┌──────────┐
                    │   Room   │        │  Vision    │       │  Image   │
                    │    DB    │        │    API     │       │  Utils   │
                    └──────────┘        └────────────┘       └──────────┘
```

## Security Considerations

1. **API Key Storage**: Using `local.properties` + BuildConfig
2. **Image Compression**: Reduce data sent over network
3. **Network Security**: HTTPS only via OkHttp
4. **Permission Handling**: Runtime permission requests

## Performance Optimizations

1. **Image Compression**: Max 1024px, 85% JPEG quality
2. **Lazy Loading**: Room Flow for reactive updates
3. **Background Processing**: Coroutines for API calls
4. **Memory Management**: Bitmap recycling

## Testing Strategy

### Unit Tests ✅
- `FoodInventoryRepositoryTest` - Repository methods, filtering, normalization, merging
- `FoodCategoryTest` - Category parsing from strings
- `CameraViewModelTest` - Photo capture state, analysis flow
- `InventoryViewModelTest` - CRUD operations, quantity updates

### Integration Tests ✅
- `FoodItemDaoTest` - Room database CRUD operations with in-memory DB

### UI Tests ✅
- `InventoryScreenTest` - Compose UI tests for inventory screen

### Test Libraries
- **MockK** - Kotlin-first mocking
- **Turbine** - Flow testing
- **Truth** - Fluent assertions
- **Coroutines-test** - Async testing
- **Room-testing** - In-memory database

## Dependencies

```kotlin
// Core
- Kotlin 1.9.21
- Compose BOM 2023.10.01
- Material 3

// Camera
- CameraX 1.3.1

// Data
- Room 2.6.1
- Gson 2.10.1

// DI
- Hilt 2.48.1

// AI
- Google Generative AI 0.1.2

// Network
- Retrofit 2.9.0
- OkHttp 4.12.0
```

## Build Configuration

- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34
- Kotlin JVM Target: 17

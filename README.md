# Fridge Scan (SnapShopping)

An Android application that uses AI vision to scan your refrigerator and automatically create a structured inventory of food items.

## Features

- **Camera Integration**: Take multiple photos of your fridge (shelves, door, drawers)
- **AI-Powered Detection**: Uses Google Gemini Vision API to detect food items
- **Smart Processing**:
  - Filters low-confidence detections
  - Normalizes names (plural → singular)
  - Merges duplicates
- **Inventory Management**:
  - Editable list with quantity controls
  - Swipe to delete
  - Manual item addition
  - Persistent storage with Room database

## Tech Stack

| Feature | Technology |
|---------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Camera | CameraX |
| AI Vision | Google Gemini Vision API |
| Local Storage | Room Database |
| DI | Hilt |
| Architecture | MVVM + Repository Pattern |
| Async | Coroutines + Flow |

## Project Structure

```
app/src/main/java/com/snapshopping/
├── data/
│   ├── local/           # Room database, DAO
│   ├── model/           # Data models (FoodItem, etc.)
│   ├── remote/          # Vision API service
│   └── repository/      # Repository pattern implementation
├── di/                  # Hilt dependency injection modules
├── ui/
│   ├── camera/          # Camera screen & ViewModel
│   ├── inventory/       # Inventory screen & ViewModel
│   ├── navigation/      # Navigation graph
│   └── theme/           # Material 3 theme
├── util/                # Utility classes (ImageUtils)
├── MainActivity.kt
└── SnapShoppingApp.kt
```

## Prerequisites

### Hardware
- Android device with camera (Android 8.0+ / API 26+)
- Computer with Android Studio

### Software
- Android Studio (latest stable)
- Android SDK API 26+
- Kotlin 1.9+

### API Keys
- Google Gemini API key ([Get one here](https://ai.google.dev/))

## Setup Instructions

### 1. Clone the repository
```bash
git clone <repository-url>
cd snapShopping
```

### 2. Configure API Key

Create or edit `local.properties` in the project root:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

> ⚠️ Never commit `local.properties` to version control

### 3. Open in Android Studio

1. Open Android Studio
2. File → Open → Select the `snapShopping` folder
3. Wait for Gradle sync to complete

### 4. Run on Device

1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Click Run (▶) in Android Studio

## Vision API Prompt

The following prompt is sent to Gemini Vision for food detection:

```
Analyze the image of a refrigerator.
List only visible food items.
Ignore containers, plates, and non-food objects.
Use generic food names in singular form.
Return a JSON object with an "items" array containing objects with:
- name: string (generic food name)
- category: string (dairy, meat, vegetables, fruits, beverages, condiments, leftovers, snacks, frozen, other)
- confidence: number (0-1)

Return ONLY valid JSON, no explanations or markdown.
```

## API Response Format

```json
{
  "items": [
    {
      "name": "milk",
      "category": "dairy",
      "confidence": 0.95
    },
    {
      "name": "apple",
      "category": "fruits",
      "confidence": 0.88
    }
  ]
}
```

## Permissions

The app requires the following permissions:

- `CAMERA` - To capture photos of the refrigerator
- `INTERNET` - To communicate with Gemini Vision API
- `READ_MEDIA_IMAGES` (Android 13+) - For gallery import

## Building for Release

```bash
./gradlew assembleRelease
```

The APK will be in `app/build/outputs/apk/release/`

## Troubleshooting

### Camera not working
- Ensure camera permission is granted in device settings
- Check that no other app is using the camera

### API errors
- Verify your Gemini API key is correct in `local.properties`
- Check internet connectivity
- Ensure API key has Gemini Vision access enabled

### Build errors
- Run `./gradlew clean` and rebuild
- Ensure Android SDK is properly installed
- Check that all dependencies are resolved

## License

This project is for educational purposes.

## Future Improvements

See [ROADMAP.md](ROADMAP.md) for planned features.

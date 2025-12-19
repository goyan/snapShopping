# Roadmap - Fridge Scan App

## Current Version: 1.0 (MVP)

### Completed Features
- Camera integration with CameraX
- Multi-photo capture support
- Gallery import
- AI-powered food detection (Gemini Vision)
- Food item inventory with CRUD operations
- Quantity management
- Category grouping
- Local persistence with Room
- Material 3 design

---

## Version 1.1 - Enhanced Detection

### Expiration Date Detection
- [ ] OCR integration for date reading
- [ ] Smart date parsing (various formats)
- [ ] Expiration alerts/notifications
- [ ] Color-coded freshness indicators

### Improved AI Recognition
- [ ] Fine-tune prompts for better accuracy
- [ ] Support for specific product brands
- [ ] Barcode scanning fallback
- [ ] Multi-language food names

---

## Version 1.2 - Smart Inventory

### Recipe Suggestions
- [ ] Integration with recipe API (Spoonacular/Edamam)
- [ ] Suggest recipes based on available ingredients
- [ ] Missing ingredient indicators
- [ ] Shopping list generation

### Smart Notifications
- [ ] "Use soon" reminders
- [ ] Weekly inventory summary
- [ ] Low stock alerts
- [ ] Customizable notification schedule

---

## Version 1.3 - Cloud & Sync

### Cloud Synchronization
- [ ] Firebase/Supabase backend
- [ ] User authentication
- [ ] Multi-device sync
- [ ] Backup & restore

### Sharing Features
- [ ] Share inventory with family members
- [ ] Collaborative shopping lists
- [ ] Household management

---

## Version 2.0 - Advanced Features

### Analytics Dashboard
- [ ] Consumption patterns
- [ ] Waste tracking
- [ ] Monthly/weekly reports
- [ ] Budget estimation

### Smart Shopping
- [ ] Automatic shopping list from low stock
- [ ] Price comparison integration
- [ ] Store location suggestions
- [ ] Purchase history

### Advanced AI
- [ ] Learn user preferences
- [ ] Personalized suggestions
- [ ] Dietary restriction support
- [ ] Nutritional information

---

## Version 2.5 - Platform Expansion

### iOS Version
- [ ] SwiftUI implementation
- [ ] Shared cloud backend
- [ ] Feature parity with Android

### Web Dashboard
- [ ] React/Vue web app
- [ ] Full inventory management
- [ ] Analytics and reports
- [ ] Family administration

---

## Technical Improvements

### Near Term
- [ ] Unit test coverage (80%+)
- [ ] UI tests with Compose testing
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Crash reporting (Firebase Crashlytics)

### Medium Term
- [ ] Offline-first architecture
- [ ] Image caching
- [ ] Performance profiling
- [ ] Accessibility improvements (a11y)

### Long Term
- [ ] Modular architecture
- [ ] Feature flags
- [ ] A/B testing
- [ ] Internationalization (i18n)

---

## API Alternatives to Consider

### Vision AI Options
| Provider | Pros | Cons |
|----------|------|------|
| **Gemini (Current)** | Native Android, good accuracy | Requires internet |
| OpenAI Vision | Excellent reasoning | Higher cost |
| Claude Vision | Strong context understanding | Higher latency |
| ML Kit (On-device) | Offline, free | Less accurate for food |
| Custom Model | Full control | Requires training |

### Recipe APIs
| Provider | Features | Pricing |
|----------|----------|---------|
| Spoonacular | Comprehensive, good docs | Free tier available |
| Edamam | Nutrition focus | Free tier limited |
| TheMealDB | Free, simple | Limited database |

---

## Metrics & Goals

### MVP Success Criteria
- [ ] Successful scan rate > 85%
- [ ] App launch to scan < 3 seconds
- [ ] Crash-free sessions > 99%

### User Experience Goals
- [ ] Time to add item < 5 seconds
- [ ] Inventory accuracy > 90%
- [ ] User retention (7-day) > 40%

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute to this project.

## Feedback

We welcome feedback and feature requests! Please open an issue on GitHub or contact us at [email].

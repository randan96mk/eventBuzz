# 02 — Product Design & UI/UX

---

## Screen Map (MVP)

```
                    +------------------+
                    |   Splash Screen  |
                    +--------+---------+
                             |
                    +--------v---------+
                    |   Onboarding     |
                    | (Guest / Login)  |
                    +--------+---------+
                             |
              +--------------+--------------+
              |              |              |
     +--------v----+  +-----v------+  +----v--------+
     |  Map View   |  | List View  |  |  Profile /  |
     | (Home Tab)  |  |  (Tab 2)   |  |  Settings   |
     +------+------+  +-----+------+  +-------------+
            |                |
     +------v------+  +-----v------+
     | Event Bubble|  | Event Card |
     |  (on map)   |  | (in list)  |
     +------+------+  +-----+------+
            |                |
            +-------+--------+
                    |
            +-------v--------+
            |  Event Detail  |
            +-------+--------+
                    |
            +-------v--------+
            |  Search &      |
            |  Filters       |
            +----------------+
```

---

## Screen Descriptions

### 1. Splash Screen
- App logo + name animation (< 2 seconds)
- Check auth state; route to onboarding or home

### 2. Onboarding / Auth
- **Guest mode**: "Continue as Guest" button — skips auth entirely
- **Sign up**: Email + password form (MVP); social login buttons (Phase 2)
- **Sign in**: Email + password
- Minimal fields: email, password, optional display name

```
+--------------------------------------+
|           EventBuzz Logo             |
|                                      |
|   [  Continue as Guest  ]           |
|                                      |
|   [  Sign Up with Email ]           |
|   [  Sign In             ]           |
|                                      |
|   ---- or ----                       |
|   [G] Google   [GitHub]  (Phase 2)  |
+--------------------------------------+
```

### 3. Map View (Home — Primary Tab)

The core screen. Full-screen interactive map with event bubbles.

```
+--------------------------------------+
| [Search Bar ............] [Filter]   |
|--------------------------------------|
|                                      |
|        MAP (MapLibre / OSM)          |
|                                      |
|    (o) Event    (o) Event            |
|         bubble       bubble          |
|                                      |
|              (3)  <-- cluster        |
|    (o) Event                         |
|                                      |
|   [ My Location ]                   |
|--------------------------------------|
| [Map]  [List]  [Search]  [Profile]  |
+--------------------------------------+
```

**Bubble behavior:**
- Each bubble = one event (or cluster at low zoom)
- Tap bubble -> show event preview card (bottom sheet)
- Tap preview card -> navigate to Event Detail
- Clusters show count; tap to zoom in
- Color-coded by category (music=purple, food=orange, etc.)

**Map interactions:**
- Pinch to zoom
- Pan to explore
- "My Location" FAB button to re-center
- Long-press for future "create event here" (Phase 2)

### 4. Event List View (Tab 2)

Scrollable list of events sorted by distance (default) or date.

```
+--------------------------------------+
| Sort: [Distance v] [Date] [Popular] |
|--------------------------------------|
| +----------------------------------+ |
| | [img] Concert in the Park        | |
| |       Sat, Mar 15 - 7:00 PM     | |
| |       0.5 mi away  |  Music     | |
| +----------------------------------+ |
| +----------------------------------+ |
| | [img] Food Truck Festival        | |
| |       Sun, Mar 16 - 11:00 AM    | |
| |       1.2 mi away  |  Food      | |
| +----------------------------------+ |
| ...                                  |
|--------------------------------------|
| [Map]  [List]  [Search]  [Profile]  |
+--------------------------------------+
```

**Card contents:**
- Event thumbnail image (or category placeholder)
- Event title
- Date and time
- Distance from user
- Category tag

### 5. Event Detail Screen

Full event information, opened from map bubble or list card.

```
+--------------------------------------+
| [<- Back]                [Share]     |
|--------------------------------------|
| +----------------------------------+ |
| |      Hero Image / Banner        | |
| +----------------------------------+ |
|                                      |
| Concert in the Park                 |
| ====================================|
| Date:     Sat, Mar 15, 2026         |
| Time:     7:00 PM - 10:00 PM       |
| Location: Central Park Amphitheater |
| Category: Music                      |
| Distance: 0.5 mi                    |
|                                      |
| Description:                        |
| Join us for a free outdoor concert  |
| featuring local bands...            |
|                                      |
| +----------------------------------+ |
| |     Mini Map (event location)    | |
| +----------------------------------+ |
|                                      |
| [  Get Directions  ]               |
| [  Add to Calendar ] (Phase 2)     |
+--------------------------------------+
```

### 6. Search & Filters

Accessible from search bar or filter icon on map view.

```
+--------------------------------------+
| [Search events...            ] [X]  |
|--------------------------------------|
| Recent searches:                     |
| music, food truck, yoga              |
|--------------------------------------|
| Filters:                             |
|                                      |
| Category:  [All v]                   |
|   Music  Sports  Food  Arts  Tech   |
|                                      |
| Date Range:                          |
|   [Today] [This Week] [Custom]      |
|                                      |
| Distance:                            |
|   [---o-----------] 5 mi            |
|                                      |
| [  Apply Filters  ]                 |
| [  Clear All      ]                 |
+--------------------------------------+
```

### 7. Profile / Settings (Tab 4)

```
+--------------------------------------+
| Profile                              |
|--------------------------------------|
| Guest User                           |
| [  Create Account  ] (if guest)     |
|                                      |
| Settings:                            |
|   Default distance   [5 mi v]       |
|   Map style          [Standard v]   |
|   Notifications      [OFF]          |
|   Dark mode          [System v]     |
|                                      |
| About                                |
| Privacy Policy                       |
| Terms of Service                     |
| Licenses (open-source)              |
| App Version: 1.0.0                   |
|                                      |
| [  Sign Out  ] (if signed in)       |
|--------------------------------------|
| [Map]  [List]  [Search]  [Profile]  |
+--------------------------------------+
```

---

## Component Hierarchy (Jetpack Compose)

```
App
├── NavHost
│   ├── SplashScreen
│   ├── OnboardingScreen
│   │   ├── GuestButton
│   │   ├── SignUpForm
│   │   └── SignInForm
│   ├── MainScreen (Scaffold + BottomNav)
│   │   ├── MapScreen
│   │   │   ├── SearchBar
│   │   │   ├── MapView (MapLibre Composable)
│   │   │   │   ├── EventBubbleMarker (per event)
│   │   │   │   └── ClusterMarker
│   │   │   ├── EventPreviewSheet (BottomSheet)
│   │   │   └── LocationFAB
│   │   ├── ListScreen
│   │   │   ├── SortChips
│   │   │   └── LazyColumn
│   │   │       └── EventCard (per event)
│   │   ├── SearchScreen
│   │   │   ├── SearchInput
│   │   │   ├── RecentSearches
│   │   │   └── FilterPanel
│   │   │       ├── CategoryChips
│   │   │       ├── DateRangePicker
│   │   │       └── DistanceSlider
│   │   └── ProfileScreen
│   │       ├── UserInfo
│   │       └── SettingsList
│   └── EventDetailScreen
│       ├── HeroImage
│       ├── EventInfoSection
│       ├── MiniMap
│       └── ActionButtons
└── Theme (Material 3 + Custom)
```

---

## Design System

### Color Palette (Material 3 Dynamic Color Base)

| Token | Light | Dark | Usage |
|-------|-------|------|-------|
| Primary | #6750A4 | #D0BCFF | Buttons, active states |
| Secondary | #625B71 | #CCC2DC | Secondary actions |
| Tertiary | #7D5260 | #EFB8C8 | Accents |
| Surface | #FFFBFE | #1C1B1F | Backgrounds |
| Error | #B3261E | #F2B8B5 | Error states |

### Category Colors

| Category | Color | Hex |
|----------|-------|-----|
| Music | Purple | #7C4DFF |
| Sports | Green | #00C853 |
| Food & Drink | Orange | #FF6D00 |
| Arts & Culture | Pink | #F50057 |
| Tech / Conference | Blue | #2979FF |
| Outdoor | Teal | #00BFA5 |
| Community | Yellow | #FFD600 |

### Typography (Material 3)

| Style | Font | Size | Weight | Usage |
|-------|------|------|--------|-------|
| Display Large | System | 57sp | 400 | Splash |
| Headline Medium | System | 28sp | 400 | Screen titles |
| Title Large | System | 22sp | 400 | Event title (detail) |
| Title Medium | System | 16sp | 500 | Event title (card) |
| Body Large | System | 16sp | 400 | Descriptions |
| Body Medium | System | 14sp | 400 | Secondary text |
| Label Large | System | 14sp | 500 | Buttons |
| Label Small | System | 11sp | 500 | Tags, chips |

### Spacing System

| Token | Value | Usage |
|-------|-------|-------|
| xs | 4dp | Inner padding |
| sm | 8dp | Between related items |
| md | 16dp | Section padding |
| lg | 24dp | Between sections |
| xl | 32dp | Screen padding |

---

## Accessibility

### Requirements (MVP)

- Minimum touch target: 48dp x 48dp
- Color contrast ratio: >= 4.5:1 (text), >= 3:1 (large text/icons)
- Content descriptions on all images and icons
- Screen reader support via Compose semantics
- Support for system font scaling (up to 200%)
- Keyboard/D-pad navigation for all interactive elements

### Considerations (Phase 2+)

- High contrast theme
- Reduced motion option
- Voice search
- Haptic feedback for map interactions

---

## Navigation Flow Diagram

```
Splash --> [Auth Check]
              |
    +---------+---------+
    |                   |
  Logged In          Not Logged In
    |                   |
    v                   v
  Main Screen      Onboarding
    |                   |
    |         +---------+---------+
    |         |         |         |
    |       Guest    Sign Up   Sign In
    |         |         |         |
    |         +---------+---------+
    |                   |
    +----->  Main Screen (4 tabs)
                |
    +-----------+-----------+-----------+
    |           |           |           |
  Map Tab    List Tab   Search Tab  Profile Tab
    |           |           |
    +-----+-----+           |
          |                 |
    Event Detail      Filter Results
          |
    [Get Directions -> External Maps App]
```

---

## Gesture & Interaction Summary

| Screen | Gesture | Action |
|--------|---------|--------|
| Map | Tap bubble | Show event preview sheet |
| Map | Tap cluster | Zoom to cluster bounds |
| Map | Tap preview | Navigate to event detail |
| Map | Swipe preview down | Dismiss preview |
| Map | Tap location FAB | Center on user location |
| Map | Pinch/spread | Zoom in/out |
| List | Tap card | Navigate to event detail |
| List | Pull down | Refresh events |
| List | Scroll | Lazy-load more events |
| Detail | Tap "Directions" | Open external maps app |
| Detail | Tap back | Return to previous screen |
| Search | Type in search bar | Live search with debounce |
| Filters | Adjust slider | Update distance filter |
| Filters | Tap chip | Toggle category filter |

---

*Next: [03 — System Architecture](./03-SYSTEM-ARCHITECTURE.md)*

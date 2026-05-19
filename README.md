[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.16045312.png)](https://zenodo.org/records/16045312)


# Kolekti Punkton

### 📝 Description

Kolekti Punkton was developed to address a need: extracting data points from graphs that do not provide a corresponding numerical table. 
Manually reading dozens of points is slow, repetitive, and often impractical.

This Android app allows you to load a graph image, mark points in a semi‑automatic workflow, and quickly generate a data table.
The resulting table is displayed in the app and can be exported as a CSV file for use in external software.


### 🎥 Video Demonstration
### Current Version
Click the thumbnail below to watch the most recent demonstration of the app:

[![Demo Video](https://img.youtube.com/vi/PKW_RAACAaM/hqdefault.jpg)](https://youtu.be/PKW_RAACAaM)


### Historical Version

[![Demo Video](https://img.youtube.com/vi/6WX6tYrRNYY/hqdefault.jpg)](https://youtu.be/6WX6tYrRNYY)


### 🛠️ Technical Debt & Improvement Plan

To ensure long‑term scalability, maintainability, and performance, the following improvements are planned:
1. Migrate from global constants to ViewModel + SavedStateHandle and Preferences DataStore.
2. Adopt MVVM architecture, moving calculations and database operations into ViewModels.
3. Replace legacy Activities used solely as dialogs with DialogFragment or Navigation Component dialog destinations.
4. Replace Threads / Handlers with Kotlin Coroutines.
5. Improve bitmap handling: avoid repeated creation in GraficoFragment, reuse bitmaps, or adopt a layered drawing library.
6. Integrate Coil for efficient image loading with caching.
7. Add Subsampling Scale Image View to support smooth zooming and a “Crosshair” mode or a toggle between navigation and editing modes.
8. Migrate from raw SQLite to Room Database.
9. Redesign the schema: instead of creating one table per project, use a single points table with a project identifier column.
10. Implement View Binding to replace findViewById.
11. Refactor deprecated permission‑handling APIs.
12. Replace deprecated drawingCache with Canvas on a mutable Bitmap or PixelCopy for view capture.
13. Improve UX of project creation: current CriarProjActivity acts only as a wrapper for a dialog; move to DialogFragment (?).
14. Remove legacy methods and simplify Kotlin syntax.
15. Consolidate duplicated logic across fragments and activities.
16. Improve error handling for extreme graph aspect ratios.

---

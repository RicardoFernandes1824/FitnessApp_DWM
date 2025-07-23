# FitnessApp_DWM

## Project Overview

FitnessApp_DWM is a comprehensive Android application designed to help users manage and track their fitness journey. The app allows users to create custom workout routines, log workout sessions, track progress over time, and manage their fitness profile. The primary goal is to provide an intuitive, data-driven experience that motivates users to achieve their fitness goals.

---

## App Features

- **User Authentication:** Secure login and registration with persistent sessions.
- **Profile Management:** Edit personal details, profile photo, and account settings.
- **Workout Routine Creation:** Build custom workout routines by selecting exercises and specifying sets/reps.
- **Active Workout Tracking:** Start workout sessions, log sets and reps, and mark sets as complete.
- **Session History:** View past workout sessions, including detailed set and exercise breakdowns.
- **Progress Tracking:** Visualize progress with charts for exercises and routines.
- **Exercise Library:** Browse and search exercises by category, with detailed guides.
- **Statistics:** View stats for sessions, routines, and exercises.
- **Settings:** Manage account, edit profile, and log out.
- **Account Deletion:** Permanently delete user account from within the app.

---

## Page Flow / Navigation

1. **Launch Screen:**  
   - Options to Login or Sign Up.
2. **Login / Sign Up:**  
   - Authenticate or register a new account.
3. **Create Profile:**  
   - (If new user) Complete profile with personal details.
4. **Home Page (Dashboard):**  
   - Bottom navigation for Workout, Profile, and Settings.
5. **Workout Tab:**  
   - View routines, create new routine, start workout.
6. **Active Workout:**  
   - Log sets/reps, finish or cancel workout.
7. **Profile Tab:**  
   - View session history, search/filter sessions, access stats.
8. **Session Detail:**  
   - Detailed breakdown of a past session.
9. **Stats:**  
   - View stats for routines or exercises, open progress charts.
10. **Settings Tab:**  
    - Edit profile, account settings, exercise library, logout, or delete account.

---

## Screens / Activities

- **MainActivity:** Entry point, handles login/signup navigation.
- **Login:** User authentication.
- **SignUp:** User registration.
- **CreateProfile:** Complete user profile after registration.
- **HomePage:** Main dashboard with bottom navigation.
- **Workout (Fragment):** List and manage workout routines.
- **CreateWorkout:** Build a new workout routine.
- **AddExercises:** Select exercises to add to a routine.
- **ActiveWorkoutActivity:** Log an active workout session.
- **Profile (Fragment):** View session history, search/filter, access stats.
- **SessionDetailActivity:** Detailed view of a completed session.
- **StatsActivity:** View stats for routines or exercises.
- **ProgressChartActivity:** Visualize progress with charts.
- **Settings (Fragment):** Access profile/account settings, exercise library, logout.
- **EditProfile:** Edit personal details and profile photo.
- **AccountSettings:** Change username, email, password, or delete account.
- **EditAccountSettings:** Update specific account fields.
- **ExerciseCategoriesActivity:** Browse exercises by category.
- **ExerciseGuideActivity:** (Linked, not found in code) View exercise details/guides.

---

## Architecture Overview

- **Pattern:** MVVM-inspired, with separation between UI (Activities/Fragments), data models, and API/network logic.
- **Networking:** Uses OkHttp and Gson for REST API communication with a Node.js backend.
- **Persistence:** User session and preferences stored via SharedPreferences.
- **Adapters:** RecyclerView adapters for dynamic lists (exercises, routines, sessions).
- **Charts:** MPAndroidChart for progress visualization.

---

## Backend Notes

- **Node.js API Integration:**  
  The app communicates with a Node.js backend (API endpoints at `http://10.0.2.2:8080/`).  
  - Endpoints for authentication, user management, workout routines, sessions, and exercises.
  - All major data (workouts, sessions, exercises, user info) is fetched and updated via REST API.
  - JWT tokens are used for authenticated requests.

---

## How to Run

1. **Clone the Repository:**
   ```
   git clone <your-repo-url>
   ```
2. **Open in Android Studio:**
   - Open the `FitnessApp_DWM` folder as a project.
3. **Configure Backend:**
   - Ensure the Node.js API is running locally at `http://10.0.2.2:8080/`.
   - Update API URLs in code if your backend runs elsewhere.
4. **Build the Project:**
   - Let Gradle sync and download dependencies.
5. **Run the App:**
   - Use an emulator or physical device (minSdk 24, targetSdk 34).
   - Log in or register a new account to get started.

---

## Tech Stack

- **Android SDK:** minSdk 24, targetSdk 34, compileSdk 35
- **Languages:** Kotlin
- **Libraries:**
  - **UI:** AndroidX, Material Components, ConstraintLayout, ViewBinding
  - **Navigation:** AndroidX Navigation, Compose Navigation
  - **Networking:** OkHttp, Gson
  - **Image Loading:** Glide, CircleImageView
  - **Charts:** MPAndroidChart
  - **Persistence:** SharedPreferences
  - **Testing:** JUnit, Espresso
- **Backend:** Node.js, REST API (see API_Fitness folder)

---

## Future Features / TODOs

- Social features: share progress, follow friends.
- More detailed analytics and custom charts.
- Dark mode and additional themes.
- Integration with wearables (Google Fit, etc.).
- Localization for multiple languages.

---

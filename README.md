# MealPlanner

MealPlanner is a comprehensive Android application designed to help users discover, plan, and manage their meals. Built with modern Android development practices, it offers a seamless experience for browsing recipes, saving favorites, and planning weekly meals.

## Features

- **User Authentication**: Secure Login and Sign Up powered by Firebase Authentication. Guest mode is also available.
- **Recipe Discovery**: Browse meals by Category, Country (Area), or Main Ingredient.
- **Search & Filter**: Powerful search functionality with filtering capabilities (Category, Area, Ingredient).
- **Meal Details**: Detailed view of recipes including instructions, ingredients, and video tutorials.
- **Meal Planner**: Plan your meals for the week (Breakfast, Lunch, Dinner).
- **Favorites**: Save your favorite meals for quick access.
- **Offline Support**: Access your saved meals and planned meals even without an internet connection using Room Database.
- **Network Sync**: Synchronize your data across devices using Firestore.

## Tech Stack

- **Language**: Java
- **Architecture**: MVP (Model-View-Presenter) with Repository Pattern
- **Network**: Retrofit, Gson
- **Asynchronous Programming**: RxJava 3, RxAndroid
- **Database**: Room (Local), Firestore (Remote)
- **Authentication**: Firebase Authentication, Google Sign-In
- **Image Loading**: Glide
- **UI Components**: Material Design, Navigation Component, ConstraintLayout
- **Animation**: Lottie

## Architecture

The application follows the **MVP (Model-View-Presenter)** architecture pattern to ensure a clean separation of concerns and testability.

- **Model**: Represents the data and business logic (Data Sources, Repositories).
- **View**: Displays the UI and handles user interactions (Fragments, Activities).
- **Presenter**: Acts as a bridge between the Model and View, handling UI logic.

The **Repository Pattern** is used to abstract the data sources (Local Room DB vs. Remote API/Firestore), providing a single source of truth for the app.

## Project Structure

```
com.example.mealplanner
├── api             # Retrofit service interfaces
├── datasource      # Local and Remote data sources
├── db              # Room Database entities and DAOs
├── model           # Data models (POJOs)
├── repository      # Repositories for data management
├── ui              # UI features (Login, Home, Search, etc.)
│   ├── view        # Fragments and Activities
│   └── presenter   # MVP Presenters and Contracts
└── utils           # Utility classes
```

## App Mockups

### Core Features
![Core Features Mockup](./screenshots/mockup_core_features.png)

## Screenshots

### Authentication & Onboarding
| Splash Screen | Login | Sign Up |
|:---:|:---:|:---:|
| <img src="./screenshots/01_splash.png" width="200"/> | <img src="./screenshots/02_login.png" width="200"/> | <img src="./screenshots/03_signup.png" width="200"/> |

### Home & Search
| Guest Home | Home | Search |
|:---:|:---:|:---:|
| <img src="./screenshots/04_guest.png" width="200"/> | <img src="./screenshots/05_home.png" width="200"/> | <img src="./screenshots/06_search.png" width="200"/> |

### Filtering
| Filter by Category | Filtered Results | Filter by Area |
|:---:|:---:|:---:|
| <img src="./screenshots/06b_search_filter.png" width="200"/> | <img src="./screenshots/06c_search_filtered.png" width="200"/> | <img src="./screenshots/06d_search_area_filter.png" width="200"/> |

### Meal Management
| Meal Details | Planner | Saved Meals |
|:---:|:---:|:---:|
| <img src="./screenshots/07_meal_details.png" width="200"/> | <img src="./screenshots/08_planner.png" width="200"/> | <img src="./screenshots/09_saved.png" width="200"/> |

### More
| Profile | Browse Countries | Browse Ingredients |
|:---:|:---:|:---:|
| <img src="./screenshots/10_profile.png" width="200"/> | <img src="./screenshots/11_countries.png" width="200"/> | <img src="./screenshots/12_ingredients.png" width="200"/> |

## Setup & Installation

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/Start-Tech-Academy/MealPlanner.git
    ```
2.  **Open in Android Studio**: Open the project and let Gradle sync.
3.  **Firebase Setup**:
    - Add your `google-services.json` file to the `app/` directory.
    - Enable Authentication (Email/Password, Google) and Firestore in your Firebase Console.
4.  **Run the App**: Connect a device or start an emulator and run the application.

## API Reference

This app uses [TheMealDB](https://www.themealdb.com/api.php) for recipe data.

--- 
Looking for contributors! Feel free to open issues or submit pull requests.

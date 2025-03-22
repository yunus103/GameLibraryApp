# Game Library Application
#### Video Demo:  https://youtu.be/oJymXmt8g8E
## Description

The Game Library Application is a JavaFX-based desktop application that allows users to manage and track their game collection. The application integrates with the IGDB API to fetch detailed game information, such as game titles, summaries, genres, platforms, and images. Users can search for games, apply filters, and maintain a local game database using MySQL(currently). The project is designed for single-user local usage without the need for server-side operations.

This project was developed using Java, JavaFX, and Maven for dependency management. The application features a modern UI with custom window controls, including a draggable and resizable window similar to native Windows applications.

## Project Structure

The project follows the Model-View-Controller (MVC) pattern, ensuring a clear separation of concerns. Below is an overview of the main packages and their functionalities:


**1. Model (Data Layer)**
Contains classes that represent the data and business logic of the application.

- **models.Game** - Represents a game entity with attributes such as title, genre, platform, and cover image path.
- **models.UserLibrary** - Represents a game entity that user owns with attributes such as hours played, price paid, status etc.
- **models.Platform** - Represents game platforms


**2. View(UI Layer - FXML Files)**
Defines the user interface components.

- **view/MainLayout.fxml** - The primary UI layout, including a search bar, navigation menu, and game details section.
- **view/HomePage.fxml** - Home page layout to show last added games.
- **view/SearchResults.fxml** - Displays game list that shows search results.
- **view/GameDetails.fxml** - Displays detailed information about a selected game.
- **view/GameDetailsLibrary.fxml** - Displays detailed information about a game that user already owns.
- **view/LibraryPage.fxml** - Displays all owned games by user, also has filter and sorting options.
- **view/Filter.fxml** - UI for filtering games by genre and platform.
- **view/UpdateGameDetails.fxml** - UI pop up to update game details such as hours_played, price_paid etc.
- **view/AddToLibrary.fxml** - UI pop up to add a game with details such as hours_played, price_paid etc.


**3. Controller(Application Logic)**

- **controllers.MainController** - Manages scene transitions, user interactions, search bar and close, maximize, minimize buttons.
- **controllers.HomePageController** - Loads last added games.
- **controllers.SearchController** - Handles searching games and displaying search results.
- **controllers.GameDetailsController** - Handles displaying game details that user does not have and manages to adding games to repo.
- **controllers.UserGameDetailsController** - Manages displaying game details that user has and handles update and delete game functions.
- **controllers.LibraryController** - Handles filter and sorting logic and loading user owned games to UI.
- **controllers.FilterController** - Handles filtering by genre and platform.
- **controllers.UpdateGameDetailsController** - Handles updating game information such as hours_played, price_paid etc.
- **controllers.AddToLibraryController** - Handles adding game with information such as hours_played, price_paid etc.


**4. Services(Database Connection & API Auth)**

- **service.DatabaseService** - Manages database connection reading credentials from config.properties
- **service.IGDBService** - Manages API connection and authentication
- **service.HelperService** - Helps other classes with some methods such as SearchGame



**5. Repositories(Data Handling & API Requests)**

- **repository.GameRepostiory** - Manages database operations for games such as reading data from db or writing to db.
- **repository.UserGameRepostiory** - Manages database operations for games that user owns such as reading data from db or writing to db.
- **repository.IGDBRepository** - Manages API calls and parse results such as searching a game for the first time and send data to


**6. App.java(Main Entry Point)**

- Initializes the JavaFX application.
- Loads the FXML layout and applies stylesheets.


**Other Files**

- **resources/css** - Stores css files for styling
- **data/** - Stores cached game screenshots and cover images for faster load.


## Design Choices & Justifications

1. Database Choice: MySQL

I used MySQL Workbench for database opearations as it was already installed in my computer and I'm already familiar with it.

2. Custom Window Controls

The default window decorations were removed to provide a more modern and consistent UI. Instead, I implemented custom close, minimize, and maximize buttons, along with a draggable top bar for window movement.

3. API Data Handling

Since the IGDB API calls might be slow sometimes, the application caches game images and screenshots in 'data' folder, and stores game details in the database to reduce API calls for faster performance.


## Future Improvements

**Enhanced Filtering:** Add more filter options, such as release date and multiplayer modes.

**User Profiles:** Implement multiple user accounts with separate game libraries.

**Offline Mode:** Improve caching for better functionality when an internet connection is unavailable.


**Conclusion**

The Game Library Application is a fully functional JavaFX-based desktop application that provides an intuitive and visually appealing way to manage a game collection. The decision to use MySQL ensures fast data operations, while the custom UI enhancements create a seamless user experience. Future updates will continue to refine the application based on user feedback and feature expansion.

## Installation & Usage

Clone the repository: git clone <repo-url>

Open in Eclipse or your preferred IDE.

Ensure Java and Maven are installed.

Run App.java to launch the application.

For any questions or contributions, feel free to reach out!

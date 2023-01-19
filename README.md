# UNOGame
An Android application that allows users to play UNO game with other players in real-time.

## Description
The app provides a seamless and interactive experience for playing the game, and allows users to create and join games with just a 30 seconds request.

## Features
User authentication: Users can sign in or sign up to the app using their email and password.
Reset password: Users can reset their password if they forget it.
Create and join games: Users can create new games or join existing games with just a 30 seconds request.
Real-time game play: The game is interactive and played in real-time, allowing users to see the cards and play the game with other players.

## Technical details
- The app is built using Java and follows MVP architecture pattern.
- Firebase Authentication is used for user authentication and password reset functionality.
- Firebase Realtime Database is used to store and sync information about the games and players, allowing for real-time communication between users.
- Firebase Cloud Messaging (FCM) is used to handle push notifications, alerting users when their request to join the game is accepted or rejected.
- Google Firebase is used for the real-time multiplayer functionality.
- The app uses the Pusher library to handle real-time events and WebSockets for the real-time multiplayer functionality.
- The app also uses Google Places API to search for location and Google Maps SDK for displaying the location on the map.

## Setting up the project
- Clone or download the repository.
- Open the project in Android Studio.
- Go to the Firebase Console and create a new project.
- Add the google-services.json file to the app folder in the project.
- Add the appropriate dependencies in the build.gradle file.
- Connect to a physical device or create an emulator to run the app.
- Running the app
- Connect a physical device or launch an emulator.
- Click on the Run button in Android Studio or use the command ./gradlew installDebug to install the app on the device.
- The app should now launch on the device.

## Issues
If you encounter any issues while setting up or running the app, please open an issue in the GitHub repository.

## Contribution
If you would like to contribute to the project, please fork the repository and submit a pull request.

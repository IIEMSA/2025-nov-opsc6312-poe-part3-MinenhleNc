# MoodTracker Mobile App

**Platform:** Kotlin Android  
**Database:** RoomDB & Firebase  
**Team Members:**  
- Abongile Mazongolo – st10043271@imconnect.edu.za  
- Jamie-Lee Simelane – st10263509@imconnect.edu.za  
- Minenhle Ncongwane – st10266839@imconnect.edu.za  
- Brandon Choga – st10389397@imconnect.edu.za  

---

## Project Overview
MoodTracker is a mobile application designed to help users track and manage their moods, view mood history, export mood data, and get real-time weather updates for their location. The app offers a simple and interactive interface, ensuring a smooth user experience on Android devices.

---

## Features
1. **Mood Logging**  
   - Select a mood from a predefined list of emotions.  
   - Add main and additional notes for context.  
   - Logs are saved in RoomDB and optionally synced to Firebase Realtime Database.  

2. **Mood History**  
   - View chronological mood entries with:  
     - Mood selected  
     - Main and additional notes  
     - Location and weather at logging time  
     - Timestamp  
   - Delete all mood data if needed.  

3. **Export Data**  
   - Export mood history to a `.txt` file in the device's Downloads folder.  
   - Includes all details: mood, notes, location, weather, timestamp.  

4. **Weather Display**  
   - Fetches and displays current weather for a user-specified location.  
   - Uses the OpenWeather API to fetch temperature and conditions.  
   - Weather data is linked to mood logs for context.  

5. **Settings Page**  
   - Edit profile information.  
   - Upload/change profile picture.  
   - Enable/disable notifications.  
   - Access Help & Support.  
   - Export mood data and delete account.  
   - Choose and change language (only affects settings page).  

6. **Rewards & Insights**  
   - Mood streaks and badges for consistent logging.  
   - Activity suggestions based on mood and weather.  
   - Graphical insights of mood trends over time.  

---

## Navigation Flow
- **Login → Home Page** – Quick access to mood logging and weather display.  
- **Home → Mood Logging Page** – Log moods, add notes, see weather, and save entries.  
- **Home → Mood History** – View past entries and export data.  
- **Settings Page** – Profile editing, notifications, language selection, export data, delete account, app info, help/support, logout.  

---

## Software and Tools Used
- **Android Studio** – IDE for app development.  
- **Kotlin** – Programming language for app logic.  
- **RoomDB** – Local database for mood logs.  
- **Firebase Authentication** – Secure user login.  
- **Firebase Realtime Database** – Optional cloud sync for mood logs.  
- **OpenWeather API** – Real-time weather data based on user location.  
- **GridLayout & RecyclerView** – UI components for mood selection and history display.  

---

## How to Use the App

1. **Login / Register**  
   - Secure login via Firebase Authentication.  
   - Register a new account if you don’t have one.  

2. **Logging a Mood**  
   - Select a mood from the available options.  
   - Add main notes and additional notes if desired.  
   - The app fetches the current location and weather automatically or by manual city input.  
   - Save the mood – data is stored in RoomDB and optionally synced to Firebase.  
   - Notifications are triggered when mood is logged successfully.  

3. **Mood History**  
   - View chronological logs with mood, notes, location, weather, and timestamp.  
   - Delete individual logs or all mood data.  
   - Export history to a `.txt` file.  

4. **Insights & Rewards**  
   - View graphical insights of mood trends.  
   - Earn badges and streaks for consistent logging.  
   - Get suggested activities based on mood and weather context.  

5. **Settings**  
   - Edit profile information (name, surname, phone numbers).  
   - Change language for the settings page.  
   - Enable/disable notifications.  
   - Access app info, help/support, export data, delete account, or logout.  

---

## Screenshots
![IMG-20251114-WA0015](https://github.com/user-attachments/assets/5a43b374-3f3d-48f3-b553-6a5addf61f92)
![IMG-20251114-WA0016](https://github.com/user-attachments/assets/92e4f274-b783-4f95-8eda-fd9a4a756206)
![IMG-20251114-WA0017](https://github.com/user-attachments/assets/b2164d3b-69ed-4f36-96cc-39e1e6016ea1)
![IMG-20251114-WA0018](https://github.com/user-attachments/assets/54bb1b90-9f3f-4d05-a88c-40cb9a2a5e72)
![IMG-20251114-WA0019](https://github.com/user-attachments/assets/c3ea7df0-8c08-46cf-8742-150dac41b39a)
![IMG-20251114-WA0020](https://github.com/user-attachments/assets/416f7c84-f01c-4dc2-be2d-6248e7d5d434)




---

## Device Compatibility
- Runs on Android mobile devices.  
- Fully tested and functional on physical devices.  
- Smooth navigation and reliable data management.  

---
## API Integration
- OpenWeather API is integrated using HTTP requests.  
- Weather data fetched based on user-input location or device GPS.  
- Displayed in real-time alongside mood logging.  
- API responses are parsed and stored locally for offline display.  

---

## Video Demonstration
A demonstration of the final MoodTracker app (PoE) in action can be viewed here:  
[Insert video link]

---

## Contact & Support
For issues or inquiries, contact the development team via the emails listed under **Team Members**.  

---

## License
[Insert license details if applicable]


# **NotificationTracker 🔔**

<p>
<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>  
<img src="https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white"/>

[![NotificationTracker App](https://img.shields.io/badge/NotificatinTracker✅-APK-red.svg?style=for-the-badge&logo=android)](https://github.com/Miihir79/NotificationTracker/releases/download/1.0.0/app-debug.apk)

</p>


An app that helps keep track of your 🔔Notifications🔔

### Idea behind the app

#### Ever swiped away a notification mindlessly ❌ and then never found out what it was❓ Well, that's how I ended up creating this app 💡.


### About the app

Never miss a notification again with Notigication Tracker app. This app has features like:

- List of 10 most recent notifications on home screen 🔟
- List of all notification
- List of app wise notification
- Search feature🔎 to help you find that missed notification!
- Get the text message if even if the sender deletes it 🔕 as long as you've recieved it as a notification

### 📸 Screenshots of the app
<table>
  <tr>
    <td>Home Page<img src="https://user-images.githubusercontent.com/66465511/230773107-306e83f8-24dc-4d31-8dd2-ea6acfcc1117.jpg" width="350">
    <td>All Notifs<img src="https://user-images.githubusercontent.com/66465511/230773113-2ef63c2d-57fc-4873-8a11-41132c838adf.jpg" width="350">
    <td>App wise Notifs<img src="https://user-images.githubusercontent.com/66465511/230773116-4b82d32b-98ca-4511-84b0-acbc1f01af49.jpg" width="350">
  <tr>
    <td>Search for app<img src="https://user-images.githubusercontent.com/66465511/230773120-aec7fbbc-1c0e-4fe7-835e-414b29561f63.jpg" width="350">
    <td>List of app notif<img src="https://user-images.githubusercontent.com/66465511/230773117-bb1e11c4-27cd-4a59-8fa1-a253f4cc8b60.jpg" width="350">
    <td>Search app notifs<img src="https://user-images.githubusercontent.com/66465511/230773118-da68c928-a060-4d2d-bfea-7319e6743ade.jpg" width="350">
</table>

### Package Structure
    
    com.mihir.notificationtracker    # Root Package
    .
    ├── database            # RoomDb and Interface
    |
    ├── helper              # AppObjectController and Extensions
    |
    ├── model               # Model classes
    |
    └── ui                  # UI/View layer
        ├── adapter         # RecyclerView Adapter
        ├── screens         # UI of the app
        └── vm              # ViewModel
        
## Built Using 🛠
- Kotlin
- RoomDB
- MVVM
- DataBinding and ViewBinding
- LiveData
- NotificationListenerService

## ⚠️ Warning ⚠️
This app uses speacial permissions like:
- BIND_NOTIFICATION_LISTENER_SERVICE : To get data of all the incoming notifications
- QUERY_ALL_PACKAGES : To get information like app name, icon etc of various apps present in your device

### ***Download the app from here👇***

[![NotificationTracker App](https://img.shields.io/badge/NotificatinTracker✅-APK-red.svg?style=for-the-badge&logo=android)](https://github.com/Miihir79/NotificationTracker/releases/download/1.0.0/app-debug.apk)
    
## How to contribute?
### What do you need to get started?
#### Latest version of android studio and basic android and googling skills will get you going.
All contributions are welcomed, Properly describe changes made and attach supporting ScreenShots in the PR. For major changes first open an issue.

## Author
Initial work: <a href="https://github.com/Miihir79">***Mihir Shah***</a> <br>

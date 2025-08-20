# VeloraQuip Chat App

A **real-time chat application** built using **Kotlin + Jetpack Compose** for UI and **Firebase Realtime Database + Firebase Cloud Messaging** for backend and notifications.  
This project was created as part of **Internship Task 5**.

---

## **Features**

### Core Features
- **Chat List Screen**
  - Displays all users or recent conversations fetched from Firebase.
- **Chat Detail Screen**
  - Real-time messaging between two users.
  - Shows sender/receiver bubbles with timestamps.
- **Push Notifications**
  - Sends notifications for new messages via FCM.

### UI/UX
- Clean, responsive chat interface.
- Smooth navigation between chat list and chat detail screens.
- Proper loading indicators while sending or receiving messages.

### Technical
- Firebase Realtime Database for messages storage and live sync.
- Firebase Cloud Messaging for push notifications.
- MVVM architecture with lifecycle-safe listeners.

### Optional / Extra Features for future
- User online/offline status.
- Profile pictures and usernames in chat list/detail.
- Message seen/delivered indicators.
- Basic encryption for chat data.
- Error handling for network issues.

---

## **Database Structure**

- `/users/{uid}`
  - `uid` : String
  - `username` : String
  - `profilePic` : String
  - `online` : Boolean
  - `fcmToken` : String
- `/chats/{chatId}/messages/{msgId}`
  - `id` : String
  - `senderId` : String
  - `receiverId` : String
  - `text` : String
  - `timestamp` : Long
  - `seen` : Boolean
- `/last/{uid}/{chatId}`
  - Last message for chat list.
- `/notify/{uid}/{pushId}`
  - Used to trigger FCM notifications.

---

## **Screenshots**

### Login Screen
![Login Screen][(https://github.com/Neha-Qasim/VeloraQuip/blob/82cc3d6c9050709d7fcc85e4f45244af423742bb/Screenshot%202025-08-20%20213623.png)]

### Chat List
![Chat List](screenshots/chat_list.png)

### Chat Detail
![Chat Detail](screenshots/chat_detail.png)

---

## **Setup & Run**

1. Clone the repository:

```bash
git clone https://github.com/yourusername/VeloraQuip-ChatApp.git

# 🌿 Reshme-Namma Pride
### AI-Powered Sericulture Guard for Silkworm Rearing

> Protecting Karnataka's silk farmers — one rearing house at a time.

## 🚜 Problem Statement

Karnataka is the silk capital of India, but silkworms are extremely sensitive to temperature and humidity. A 2°C deviation can destroy an entire batch of cocoons worth thousands of rupees. Small-scale farmers have no affordable digital tool to monitor and act on rearing conditions in real time.

## ✨ Features

- 🐛 **Batch Tracker** — Start and manage silkworm batches with breed name and start date
- 🌡️ **Climate Entry** — Enter temperature and humidity 3 times daily (Morning, Afternoon, Evening)
- 🤖 **AI Advisory (Gemini)** — Stage-specific guidance powered by Google Gemini AI
- 🟢 **Climate Dial** — Color-coded status: Green (Safe), Orange (Caution), Red (Danger)
- 📈 **Instar Stage Tracking** — Automatically advances through all 5 silkworm growth stages
- 🌿 **Harvest Timer** — Notifies farmer when cocoons are ready for transfer to spinning trays
- 📋 **Batch History** — Stores all completed batches with cocoon yield and notes
- 🗑️ **Delete Batch** — Long-press any batch to delete it
- 📴 **Offline Fallback** — Works without internet using built-in advisory logic

## 🧰 Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Android Studio | IDE |
| MVVM + LiveData | Architecture pattern |
| Room Database | Local offline storage |
| Gemini AI API | GenAI advisory generation |
| Retrofit + OkHttp | API communication |
| WorkManager | Harvest timer notifications |
| XML Layouts | UI design |

## 📱 Application Screenshots

### 1. Home Screen
Create a new silkworm batch using breed and start date.

<img width="300" alt="Home screen" src="https://github.com/user-attachments/assets/6c49fd3d-7196-4fd8-8f3b-0c7e6589ae83" />

### 2. Batch Tracking
View active batches, Instar stage, and days left for harvest. The application tracks all five Instar stages of silkworm growth and updates the current stage based on batch progress. 

<img width="1080" alt="Batch Tracking" src="https://github.com/user-attachments/assets/b00139c5-75b5-4f4d-9b38-28eb5c626676" />

<img width="1080" alt="Batch Tracking" src="https://github.com/user-attachments/assets/82d1bb89-de4f-4d3c-888a-30cf061dec1a" />

### 3. Climate Entry
Enter morning, afternoon, and evening temperature/humidity readings.

<img width="1080" alt="Climate Entry" src="https://github.com/user-attachments/assets/d03958bc-f6ec-4d2d-9c9d-00d0bb3b2b22" />

### 4. Climate Status
Safe / Caution / Danger based on entered values.

<img width="1080" alt="Climate Status-Safe" src="https://github.com/user-attachments/assets/851c3ef9-25dc-45ed-b875-4f10f675927e" />

<img width="1080" alt="Climate Status-Caution" src="https://github.com/user-attachments/assets/f7983e8b-61f7-4c9b-a487-41f49c7c06fd" />

<img width="1080" alt="Climate Status-Danger" src="https://github.com/user-attachments/assets/5d6a2361-f608-441f-8314-2ae55e518fd7" />

### 5. Daily Entry Validation and Reading Limit
The system restricts the farmer to entering only three climate readings per day for each batch — Morning, Afternoon, and Evening. Once a reading for a particular time slot is already entered, the application prevents duplicate entry and displays a message such as “Morning entry has already been entered.”

<img width="1080" alt="Daily Entry Validation" src="https://github.com/user-attachments/assets/210a1e12-1dd7-4da0-be03-6aced1b4144f" />

<img width="1079" alt="duplicate entry" src="https://github.com/user-attachments/assets/23d3b83d-4b71-4e5f-8334-88dc758d9279" />

### 6. Smart Advisory
AI-generated suggestions based on climate conditions.

<img width="1080" alt="Smart Advisory-Safe" src="https://github.com/user-attachments/assets/8e89916b-b44f-4410-9a19-1a8f3f9d533d" />

<img width="1080" alt="Smart Advisory-Caution" src="https://github.com/user-attachments/assets/e982aaf5-ba9a-48be-801a-79483780c02a" />

<img width="1080" alt="Smart Advisory-Danger" src="https://github.com/user-attachments/assets/cba7a6ac-9b1c-4c34-a650-a9822567325b" />

### 7. Batch History
View ongoing and completed batches with logs.

<img width="1080" alt="Batch History" src="https://github.com/user-attachments/assets/ef35f69f-d813-40c8-b302-13a83cf13542" />

### 8. Harvest Record
Once a silkworm batch is completed, the application allows the farmer to enter cocoon yield in kilograms and optional notes. After clicking Save Harvest Record, the details are stored and added to the Batch History section for future reference.

<img width="1080" alt="Harvest Record" src="https://github.com/user-attachments/assets/62655c22-76eb-4e2b-90dc-9b3cf0debde9" />

<img width="1080" alt="Harvest Record" src="https://github.com/user-attachments/assets/bd6c24f8-9345-47fb-965a-a5165653ef42" />

<img width="1080" alt="Harvest Record" src="https://github.com/user-attachments/assets/cc5e3353-4737-4d6b-b7f6-fbf3994db717" />

### 9. Harvest Notification 
Sends notification when the batch reaches harvest stage

<img width="1077" alt="Harvest Notification " src="https://github.com/user-attachments/assets/5b181477-65db-494d-9f68-d4175781832f" />

## 🚀 Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 35
- Kotlin 2.0+
- Gemini API Key from [Google AI Studio](https://aistudio.google.com/app/apikey)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/shettyneha21/ReshmeNammaPride.git

# 2. Open in Android Studio
File → Open → select the ReshmeNammaPride folder

# 3. Add your Gemini API key
# Open: app/src/main/java/com/reshmenamma/pride/util/Constants.kt
# Replace: const val GEMINI_API_KEY = "YOUR_KEY_HERE"

# 4. Build and Run
# Click Run ▶️ or press Shift+F10
```

### Minimum Requirements
- Android 7.0 (API Level 24) and above
- Internet connection for AI advisory (offline fallback available)

## ▶️ Build

Open the project in Android Studio and click Run ▶️.

The project uses Gradle build system and may take a few minutes during first sync.

## 📂 Project Structure

```text
app/src/main/java/com/reshmenamma/pride/
├── data/
│   ├── api/
│   ├── dao/
│   ├── database/
│   ├── model/
│   └── repository/
├── ui/
│   ├── climate/
│   ├── harvest/
│   ├── history/
│   └── home/
├── util/
├── viewmodel/
└── worker/
```

## 🌾 Instar Stage Reference

| Stage | Duration | Ideal Temp | Ideal Humidity |
|-------|----------|-----------|----------------|
| Instar I (Chawki) | 3 days | 26–28°C | 85–90% |
| Instar II | 3 days | 26–28°C | 85–90% |
| Instar III | 4 days | 25–27°C | 80–85% |
| Instar IV | 5 days | 24–26°C | 70–80% |
| Instar V (Pre-spinning) | 7 days | 24–26°C | 65–75% |

## 🎯 Impact Goals

- **Economic Stability** — Reduces silkworm crop failure risk for small-scale farmers
- **Agri-Tech for All** — Affordable solution requiring only a smartphone and wall thermometer
- **Karnataka Leadership** — Supports India's silk capital in maintaining premium quality

## 🔮 Future Enhancements

- IoT sensor integration via Bluetooth
- Kannada language support
- Multi-farm dashboard for extension officers
- Disease detection via camera AI
- Yield prediction ML model

## 👩‍💻 Developer

**Neha H Shetty** | 4MW22CS100  
MindMatrix Internship 2026
Project No: 52 — Android App Development using GenAI

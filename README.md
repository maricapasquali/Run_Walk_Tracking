# Run/Walk Tracking

An Android application for managing indoor and outdoor workouts, with support for statistics, vocal coach, GPS tracking, and personalized music.

_Developed by: Marica Pasquali_  
_Thesis – Bachelor's Degree in Ingegneria e Scienze Informatiche_ (Project for the "Programmazione di sistemi mobile" course)    
_Academic Year: 2018/2019 – University of Bologna, Cesena Campus_

---

## Description

**Run/Walk Tracking** is an Android app designed to help users manage, store, and view their workouts. It fully supports real-time indoor and outdoor sessions and includes advanced features such as:
- GPS tracking,
- music playback,
- detailed statistics,
- voice coaching during the workout,
- tracking body weight progress over time.

The project also includes a PHP/MySQL server-side component for data synchronization through RESTful APIs.

---

## Project Goals

- Develop a functional mobile app for daily workouts.
- Integrate advanced features (voice coach, music, weight tracking).
- Support multiple languages and measurement units.
- Ensure Client-Server data synchronization.
- Provide a free alternative to commercial apps with limited features.

---

## Key Features

- 📍 **Real-time workouts** with GPS support.
- 🎧 **Custom music playlists**.
- 🗣️ **Voice coach** (Text-to-Speech).
- 📊 **Statistics and charts** (calories, distance, weight).
- 🔐 **Authentication and password recovery**.
- 🌍 **Multi-language support**: Italian, English, Spanish.
- ⚖️ **Measurement unit support** (km/mi, kg/lb, m/ft).

---

## Technologies Used

### Mobile
- **Android (Java)**
- **SQLite** for local persistence
- **Volley** for HTTP requests
- **Google Maps API**, **GraphView**, **TextToSpeech**

### Backend
- **PHP 5+**
- **MySQL** managed via phpMyAdmin
- **DAO Pattern** on both client and server side
- Hosting: [000webhost](https://runwalktracking.000webhostapp.com/)

---

## Authentication

The system uses session-based tokens. Upon registration, a confirmation email is sent. Password reset is available in case of credential loss.

---

## Statistics

Interactive charts show the trend of:
- Average speed
- Calories burned
- Distance covered
- Body weight

Filterable by week, month, year, or all sessions.

---

## Route Tracking

The route tracking uses:
- **Google Maps API** (Polyline, MapFragment)
- Foreground/background location services
- Temporary saving of routes via `SharedPreferences`

---

## Future Enhancements

- Remote playlist management
- Web version of the app
- Per-session statistics (e.g. speed peaks)
- Smartwatch integration
- Personalized training plans
- iOS porting

---

## Documentation

Full thesis available [here](./Progettazione_Design/Pasquali_Marica_Tesi.pdf)

---

## License

This project was developed as part of a bachelor’s thesis and can be reused for educational or research purposes. Contact the author for other uses.


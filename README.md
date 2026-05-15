# 🛣️ Namma Rasthe Health – Rural Road Maintenance Tracking Application

<div align="center">

![Namma Rasthe Health](https://img.shields.io/badge/Namma%20Rasthe%20Health-Android%20App-brightgreen?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=flat-square)
![Room Database](https://img.shields.io/badge/Database-Room-4CAF50?style=flat-square&logo=sqlite)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

**A digital solution to report, monitor, and manage rural road damage efficiently**

[Features](#-key-features) • [Installation](#-installation-steps) • [Architecture](#-system-architecture) • [Contributors](#-contributors)

</div>

---

## 📱 Project Description

**Namma Rasthe Health** is an innovative Android-based application designed to digitize the process of reporting and managing rural road maintenance. It empowers citizens and Gram Panchayat (village council) officers to:

- Report road damage with precise GPS coordinates
- Monitor road health status across regions
- Track repair progress in real-time
- Access contractor information and updates
- Visualize road networks on interactive maps
- Maintain offline data synchronization

The application aims to bridge the gap between citizens and local authorities in ensuring better road infrastructure maintenance in rural areas.

---

---

## 📦 Installation Steps

### Step 1: Clone the Repository

```bash
# Using HTTPS
git clone https://github.com/llsandeep01ll/Namma-Rasthe-Health.git

# Using SSH
git clone git@github.com:llsandeep01ll/Namma-Rasthe-Health.git

# Navigate to project directory
cd Namma-Rasthe-Health
```

### Step 2: Configure Google Maps API Key

1. **Generate API Key**:
   - Visit [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project
   - Enable Google Maps Android API
   - Create an API key

2. **Add API Key to Project**:
   - Open `local.properties` file:
   ```properties
   # local.properties
   MAPS_API_KEY=YOUR_API_KEY_HERE
   ```
   - Or add to `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```

### Step 3: Open in Android Studio

1. **Open Android Studio**
2. **Select** `File → Open...`
3. **Navigate** to the cloned repository folder
4. **Click** `Open`
5. **Wait** for Gradle indexing to complete

### Step 4: Gradle Sync

```bash
# Android Studio will automatically prompt for sync
# Or manually:
# File → Sync Now
# Or use terminal:
./gradlew sync
```

### Step 5: Resolve Dependencies

```bash
# Clean and rebuild
./gradlew clean build

# If there are issues with dependencies:
./gradlew build --refresh-dependencies
```

### Step 6: Configure Emulator or Physical Device

**Using Android Emulator**:
1. **Open AVD Manager** in Android Studio
2. **Create Virtual Device** (if not already created)
3. **Select Device**: Pixel 4 or higher
4. **Select API Level**: 21 or higher
5. **Enable GPS**: In AVD settings

**Using Physical Device**:
1. **Enable Developer Mode**: Settings → About Phone → Tap Build Number 7 times
2. **Enable USB Debugging**: Settings → Developer Options → USB Debugging
3. **Connect to Computer** via USB
4. **Accept USB Debugging** prompt on device

---

## 📸 Screenshots

### Main Dashboard


<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/28cfb715-5551-49d9-86ae-797ad64791ef" />



### Road Directory

<img width="720" height="1600" alt="WhatsApp Image 2026-05-13 at 11 29 53 PM (3)" src="https://github.com/user-attachments/assets/53fa5735-dd77-4ed9-bc51-589b3fdb1811" />



### Damage Reporting

<img width="720" height="1600" alt="WhatsApp Image 2026-05-13 at 11 29 53 PM (4)" src="https://github.com/user-attachments/assets/1165db47-6e5a-4d46-98b1-e82d94511b31" />


### Google Maps

<img width="720" height="1600" alt="WhatsApp Image 2026-05-14 at 8 40 19 AM" src="https://github.com/user-attachments/assets/8767deea-018c-4704-8a83-d30d23497b40" />



---


### Core Development Team

| Name | Role | Contact |
|------|------|---------|
| **Sandeep N V** | Project Lead & Developer | [@llsandeep01ll](https://github.com/llsandeep01ll) |



---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](./LICENSE) file for details.

---



### Contact Information

- **Project Lead**: Sandeep N V
- **Email**: ll.sandeep01.ll@gmail.com
- **GitHub**: [@llsandeep01ll](https://github.com/llsandeep01ll)

---



# Namma-Rasthe-Health-main

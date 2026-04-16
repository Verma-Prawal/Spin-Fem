# FEM Viewer — Full-Stack Run Instructions

## Project Location
```
C:\Users\cse21\.gemini\antigravity\scratch\fem-viewer\
├── backend\              ← Spring Boot (Maven)
└── android\              ← Android Studio project
```

---

## Complete File Tree

```
fem-viewer/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/femviewer/
│       │   ├── FemViewerApplication.java
│       │   └── CorsConfig.java
│       └── resources/
│           ├── application.properties
│           └── static/models/
│               └── fem.glb          ← YOU MUST PLACE THIS HERE
│
└── android/
    ├── build.gradle
    ├── settings.gradle
    ├── gradle.properties
    ├── gradle/wrapper/
    │   └── gradle-wrapper.properties
    └── app/
        ├── build.gradle
        └── src/main/
            ├── AndroidManifest.xml
            ├── java/com/femviewer/
            │   └── MainActivity.java
            ├── res/
            │   ├── layout/activity_main.xml
            │   └── values/themes.xml
            └── assets/
                ├── index.html
                └── js/
                    ├── three.min.js      ← downloaded (r128, 603 KB)
                    └── GLTFLoader.js     ← downloaded (r128, 96 KB)
```

---

## STEP 1 — Place `fem.glb`

> [!IMPORTANT]
> Copy your `fem.glb` file to:
> ```
> fem-viewer\backend\src\main\resources\static\models\fem.glb
> ```
> The backend will not serve anything useful without it.

---

## STEP 2 — Run the Spring Boot Backend

### Prerequisites
- Java 17+ installed
- Maven installed (or use the Maven wrapper)

### Run

Open a terminal in `fem-viewer\backend\` and run:

```powershell
cd C:\Users\cse21\.gemini\antigravity\scratch\fem-viewer\backend
mvn spring-boot:run
```

### Verify it works

Open in your browser:
```
http://localhost:8080/models/fem.glb
```
You should see a binary file download prompt (not a 404).

> [!TIP]
> The CORS header `Access-Control-Allow-Origin: *` is added globally,
> so the Android WebView can fetch the model without issues.

---

## STEP 3 — Open the Android Project

1. Launch **Android Studio**
2. Choose **Open** (not "New Project")
3. Navigate to:
   ```
   C:\Users\cse21\.gemini\antigravity\scratch\fem-viewer\android
   ```
4. Click **OK** — Android Studio will sync Gradle automatically
5. Wait for the Gradle build to finish (first time downloads dependencies)

---

## STEP 4 — Run in the Emulator

1. In Android Studio **Device Manager**, create/start an emulator:
   - API Level **24+** (any Pixel profile works)
2. Click the **▶ Run** button (or `Shift+F10`)
3. The app installs and opens the WebView

### What you should see:
- Dark background with a loading spinner
- Spinner shows `Loading model… XX%` as the GLB downloads
- Model appears, centered, and **slowly rotates** on the Y-axis

---

## How It Works

| Layer | Technology | Key Detail |
|---|---|---|
| Backend | Spring Boot 3.2 | Serves `fem.glb` as static resource with CORS |
| Transport | HTTP | `10.0.2.2:8080` maps to host `localhost` in emulator |
| Frontend | Three.js r128 | Loaded from local assets (no CDN needed offline) |
| Renderer | WebView (Android) | JS + DOM Storage + mixed content enabled |

---

## Troubleshooting

### Model doesn't load / blank screen

1. **Check backend is running** — `http://localhost:8080/models/fem.glb` must work in browser
2. **Check CORS** — response must have `Access-Control-Allow-Origin: *`
3. **Check emulator network** — `10.0.2.2` only works in the **Android Emulator**, not a real device
4. Enable **WebView DevTools** to see console errors:
   - In Chrome browser, navigate to `chrome://inspect/#devices`
   - Select the WebView and click **Inspect**

### Gradle sync fails in Android Studio

- Go to **File → Project Structure → SDK Location**
- Ensure Android SDK and JDK 17 paths are set correctly

### `MIXED_CONTENT` warnings

Already handled — `MainActivity.java` sets:
```java
settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
```
This allows the `file://` page to fetch from `http://`.

### Real device (not emulator)

Replace `10.0.2.2` in `index.html` with your **PC's local IP**:
```javascript
var MODEL_URL = 'http://192.168.x.x:8080/models/fem.glb';
```
Then also open port 8080 in Windows Firewall if prompted.

---

## Definition of Done Checklist

- [ ] `fem.glb` placed in `backend/src/main/resources/static/models/`
- [ ] Spring Boot runs on port 8080 without errors
- [ ] `http://localhost:8080/models/fem.glb` is downloadable in browser
- [ ] Android app builds and installs in emulator
- [ ] WebView loads `index.html`
- [ ] Loading spinner appears then disappears
- [ ] 3D model is visible and rotating on screen
- [ ] No crash in Android Studio logcat

# Building VersionBridge JAR

## Why This Document?

This project requires **Gradle 8.5+** and **Fabric Loom** to build, which need to download Minecraft and Fabric dependencies from Maven repositories. If you're building in an environment with **internet access**, follow the standard path. If you're in a sandboxed/offline environment, use Docker.

## Standard Build (With Internet)

### Requirements

- Java 21+
- Gradle 8.5+ (or use Gradle Wrapper: `./gradlew`)
- ~2GB free disk space
- Internet connection (to download Minecraft, Fabric, and dependencies)

### Build Command

```bash
cd VersionBridge
./gradlew build
```

**Output:** `build/libs/VersionBridge-1.0.0.jar`

### Install & Test

1. Copy the JAR to your Minecraft mods folder:
   ```
   ~/.minecraft/mods/VersionBridge-1.0.0.jar
   ```

2. Launch Minecraft 1.21 with Fabric Loader 0.15.11+

3. Check the Mods list to verify VersionBridge loads

4. Use `/version` in-game to confirm functionality

---

## Docker Build (Offline-Safe)

If you're in an offline environment or don't want to install Java/Gradle locally, use Docker.

### Requirements

- Docker or Podman
- ~3GB disk space
- No internet needed after image pull (builds from cache)

### Dockerfile

Create `Dockerfile` in the project root:

```dockerfile
FROM gradle:8.5-jdk21

WORKDIR /build
COPY . .

RUN gradle build --no-daemon

CMD echo "Build complete. JAR: build/libs/VersionBridge-1.0.0.jar"
```

### Build with Docker

```bash
docker build -t versionbridge:latest .
docker run --rm -v $(pwd)/build:/build/build versionbridge:latest
```

**Output:** JAR copied to `./build/libs/VersionBridge-1.0.0.jar`

---

## Project Structure

```
VersionBridge/
├── src/main/java/com/versionbridge/
│   ├── VersionBridgeModInitializer.java          # Server-side entry, commands
│   ├── VersionBridgeClientInitializer.java       # Client-side entry
│   └── VersionConfig.java                         # Config file handling
├── src/main/resources/
│   ├── fabric.mod.json                           # Mod metadata
│   └── versionbridge.mixins.json                 # Mixin config (currently empty)
├── gradle/wrapper/                               # Gradle Wrapper
├── build.gradle                                  # Gradle build config
├── gradle.properties                             # Versions, properties
├── settings.gradle                               # Project name
├── gradlew / gradlew.bat                         # Gradle Wrapper scripts
├── README.md                                     # User guide
├── BUILDING.md                                   # This file
└── LICENSE                                       # MIT License
```

---

## Troubleshooting

### "Could not connect to services.gradle.org"

**Cause:** No internet to download Gradle/dependencies.

**Solutions:**
1. Use Docker (above)
2. Pre-download Gradle 8.5 from https://gradle.org/releases/ and place in `gradle/wrapper/`
3. Set `org.gradle.offline=true` in `gradle.properties` if using a pre-cached environment

### "Minecraft 1.21 not found"

**Cause:** Gradle couldn't download Minecraft mappings.

**Solution:** Ensure internet access. Gradle will auto-download on first run. May take 5–10 minutes.

### "Compilation failed: cannot find symbol"

**Cause:** Dependencies weren't downloaded.

**Solution:** 
```bash
./gradlew clean
./gradlew downloadAssets  # explicit download
./gradlew build
```

### "OutOfMemoryException"

**Cause:** Not enough heap memory.

**Solution:** Increase Gradle heap in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4G
```

---

## GitHub Actions CI/CD (Alternative)

If you want pre-built JAR uploads, you can set up GitHub Actions:

```yaml
# .github/workflows/build.yml
name: Build VersionBridge JAR

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: chmod +x gradlew
      - run: ./gradlew build --no-daemon
      - uses: actions/upload-artifact@v4
        with:
          name: VersionBridge JAR
          path: build/libs/VersionBridge-1.0.0.jar
```

This will build on every push and provide the JAR as a downloadable artifact.

---

## Expected Output

After successful build, you should see:

```
BUILD SUCCESSFUL in 2m 15s
...
VersionBridge-1.0.0.jar
├── META-INF/
│   ├── MANIFEST.MF
│   └── versions.txt
├── fabric.mod.json
├── versionbridge.mixins.json
├── com/versionbridge/*.class
└── config/...
```

The JAR is ready to drop into `.minecraft/mods/`.

---

## For Developers: Modifying the Mod

If you want to add features:

1. Edit source files in `src/main/java/com/versionbridge/`
2. Run `./gradlew build` to recompile
3. The JAR is automatically remapped to Mojang mappings by Fabric Loom
4. Test in Minecraft before distributing

---

## License

This project is MIT licensed. See LICENSE file.

# ⚡ VersionBridge — Quick Start (2 minutes)

## 🎯 Goal
Build and install a working Minecraft 1.21 Fabric mod that lets you select server versions.

---

## 📋 Prerequisites

✅ **Java 21+**
```bash
java -version  # Should show "21" or higher
```

✅ **Gradle** (will auto-download via wrapper)

✅ **~2GB disk space** (for Gradle cache + build artifacts)

✅ **Internet connection** (first build only)

---

## 🚀 Build in 3 Steps

### Step 1: Clone/Download Project
```bash
cd VersionBridge
```

### Step 2: Build the JAR
```bash
./gradlew build
```

**What happens:**
- Gradle downloads Minecraft 1.21, Fabric API, and toolchain (~800MB)
- Compiles Java source files
- Remaps to Minecraft's Mojang mappings
- Packages into JAR
- **Time:** 3–5 minutes first run, ~1 minute after

**Expected output:**
```
BUILD SUCCESSFUL
...
Created: build/libs/VersionBridge-1.0.0.jar
```

### Step 3: Install to Minecraft
```bash
# On Windows:
copy build\libs\VersionBridge-1.0.0.jar %appdata%\.minecraft\mods\

# On macOS/Linux:
cp build/libs/VersionBridge-1.0.0.jar ~/.minecraft/mods/
```

---

## 🎮 Use the Mod

1. **Launch Minecraft 1.21** with Fabric Loader installed
2. **Check Mods menu** — VersionBridge should appear
3. **Open a server or creative world**
4. **Type in chat:**
   ```
   /version
   ```
   You should see version info!

5. **Try commands:**
   ```
   /version list          → Show supported versions
   /version set 1.21.1    → Change target (demo only, won't actually work)
   /version off           → Reset to default
   ```

---

## 📁 Project Structure

```
VersionBridge/
├── README.md               ← Read this for features & limitations
├── QUICK_START.md          ← You are here
├── BUILDING.md             ← Detailed build instructions
├── PROJECT_SUMMARY.md      ← Technical deep-dive
│
├── src/main/java/          ← Java source code
│   └── com/versionbridge/  ← Main classes
├── src/main/resources/     ← Mod metadata (fabric.mod.json)
├── build.gradle            ← Gradle configuration
├── gradle.properties       ← Versions & settings
│
├── Dockerfile              ← Docker build alternative
├── .github/workflows/      ← GitHub Actions CI/CD
└── build/libs/             ← Output JAR (after build)
    └── VersionBridge-1.0.0.jar
```

---

## ❓ Troubleshooting

### "Could not download Gradle"
```
❌ No internet or network issue
✅ Check connection, try again
✅ Or use Docker: see BUILDING.md
```

### "Cannot find minecraft 1.21"
```
❌ Gradle hasn't finished downloading dependencies
✅ Wait 5+ minutes on first build
✅ Check console for progress
```

### "Failed to compile"
```
❌ Java version too old
✅ Install Java 21+: https://www.oracle.com/java/technologies/downloads/
```

### "JAR won't load in Minecraft"
```
❌ Wrong Minecraft version (mod needs 1.21 exactly)
✅ Check Fabric Loader version ≥ 0.15.11
✅ Verify JAR is in correct mods folder
✅ Check Minecraft console for errors (F3 + S opens screen)
```

---

## 🔧 What This Mod Does

✅ **Implemented:**
- `/version` command system
- Save/load target version to config file
- List of supported versions (1.21, 1.21.1, 1.21.4)

❌ **NOT Implemented:**
- Actual protocol translation (connecting to 1.21.1+ servers)
- GUI/Config screen in Mods menu
- Missing item/block fallback rendering

**Why the limitations?**  
Full protocol translation is 40,000+ lines of code (see ViaVersion). This mod is honest about what it CAN do. See README.md for details.

---

## 📚 Learn More

- **README.md** — Features, commands, installation
- **BUILDING.md** — Advanced build options (Docker, offline, CI/CD)
- **PROJECT_SUMMARY.md** — Architecture, design decisions, roadmap

---

## 🎁 Next Steps

1. ✅ Build JAR (`./gradlew build`)
2. ✅ Install to Minecraft mods folder
3. ✅ Test with `/version` command
4. 📖 Read README.md for full feature list
5. 🔧 (Optional) Modify source, rebuild

---

## 💬 Questions?

- **Build fails?** → Check BUILDING.md troubleshooting
- **Feature request?** → See PROJECT_SUMMARY.md "Future Work"
- **Code question?** → Grep for comments in `src/main/java/`

---

**VersionBridge v1.0.0** — Built with Gradle, Fabric Loom, and Java 21.

Happy modding! 🎮

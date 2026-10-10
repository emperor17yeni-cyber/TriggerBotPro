# 🔨 Building the REAL VersionBridge JAR

## Current Status

✅ **Complete Project** — All source code, metadata, and build configuration ready
⚠️ **Offline Environment** — This container has no internet for dependency downloads
❌ **Stub JAR Only** — `build/libs/VersionBridge-1.0.0.jar` exists but is incomplete (stub entry points only)

---

## What You Need to Build the Real JAR

### System Requirements

- **Java 21+** (exact)
  ```bash
  java -version  # Must show "21" or higher
  ```

- **Internet connection** (for Maven dependency download)
  - First build: 800MB+ downloads
  - Cached builds: 10-50MB downloads

- **Disk space:**
  - Gradle cache: ~2GB
  - Project build: ~200MB
  - Total: ~2.2GB

- **Time:**
  - First build: 5-10 minutes (slow, downloads everything)
  - Subsequent builds: 1-3 minutes (cached)

### Optional: Pre-requisites

- **Gradle 8.5+** (or use wrapper: `./gradlew`)
- **Git** (if cloning from repository)
- **Docker** (alternative build method)

---

## Method 1: Local Build (Recommended)

### Step 1: Ensure Java 21

```bash
java -version
# openjdk version "21.0.10" (or newer)
```

If not installed:
- **Ubuntu/Debian:** `sudo apt install openjdk-21-jdk`
- **macOS:** `brew install openjdk@21`
- **Windows:** Download from https://adoptium.net (Eclipse Temurin)

### Step 2: Clone or Download Project

```bash
# If you have git
git clone https://github.com/yourusername/VersionBridge.git
cd VersionBridge

# OR: Download ZIP and extract
unzip VersionBridge-main.zip
cd VersionBridge
```

### Step 3: Build with Gradle

```bash
# Make wrapper executable (macOS/Linux only)
chmod +x gradlew

# Build the JAR
./gradlew build
```

**What this does:**
1. Downloads Gradle 8.5 (first time only)
2. Downloads Minecraft 1.21 server JAR (~180MB)
3. Downloads Fabric Loom toolchain (~50MB)
4. Downloads Fabric API + dependencies (~50MB)
5. Compiles Java source files
6. Remaps code to Mojang obfuscation
7. Packages JAR with Fabric metadata

**Progress indicators:**
```
Downloading ...               [████████] 50%
Compiling com/versionbridge   [████████] 75%
Remapping classes              [████████] 90%
BUILD SUCCESSFUL               ✓
```

### Step 4: Verify Output

```bash
ls -lh build/libs/
# VersionBridge-1.0.0.jar       (should be ~150-300KB)
# VersionBridge-1.0.0-sources.jar  (should be ~50KB)

# Inspect JAR contents
jar tf build/libs/VersionBridge-1.0.0.jar | head -20
# META-INF/MANIFEST.MF
# fabric.mod.json
# com/versionbridge/VersionBridgeModInitializer.class
# ... (compiled classes)
```

### Step 5: Install & Test

```bash
# Copy to Minecraft mods folder
cp build/libs/VersionBridge-1.0.0.jar ~/.minecraft/mods/

# Launch Minecraft 1.21 with Fabric
# In-game: /version
# Should show: "VersionBridge: Current running version: 1.21"
```

---

## Method 2: Docker Build (No Local Java/Gradle)

### Requires

- Docker or Podman installed
- ~3GB disk
- Internet (for Docker Hub base image)

### Step 1: Build Docker Image

```bash
cd VersionBridge

# Build image
docker build -t versionbridge-builder:latest .

# Expected output:
# Sending build context to Docker daemon  ...
# Step 1/X : FROM gradle:8.5-jdk21
# ...
# Successfully built versionbridge-builder:latest
```

### Step 2: Run Build

```bash
# Create output directory
mkdir -p ./build/libs

# Run build inside container
docker run --rm \
  -v $(pwd):/build \
  -w /build \
  versionbridge-builder:latest \
  gradle build --no-daemon

# Docker mounts project, Gradle builds, outputs to ./build/libs/
```

### Step 3: Extract JAR

```bash
# JAR is already in ./build/libs/VersionBridge-1.0.0.jar
ls -lh build/libs/VersionBridge-1.0.0.jar
```

---

## Method 3: GitHub Actions (Automated)

### No local build needed!

1. **Push to GitHub**
   ```bash
   git push origin main
   ```

2. **GitHub Actions automatically:**
   - Detects push
   - Spins up runner (Ubuntu 22.04, Java 21 pre-installed)
   - Runs `./gradlew build`
   - Uploads JAR as artifact

3. **Download JAR**
   - Go to GitHub → Actions → Latest run
   - Artifacts section → Download `VersionBridge-1.0.0.jar`

4. **Create Release (for tags)**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   # GitHub Actions automatically creates release with JAR download link
   ```

See `.github/workflows/build.yml` for config.

---

## Troubleshooting Build Failures

### Error: "Could not download Gradle distribution"

**Cause:** Network issue

**Solutions:**
```bash
# Check network
ping 8.8.8.8

# Retry build
./gradlew clean build

# Or use Docker (no network issues)
docker build -t versionbridge .
```

### Error: "Could not resolve minecraft:minecraft:1.21"

**Cause:** Maven repositories unreachable

**Solution:** Wait 5+ minutes, try again. First build takes longer.

```bash
# Verbose output
./gradlew build --info 2>&1 | grep -i "download\|fail"

# Check proxy (if behind corporate firewall)
./gradlew build -Dhttps.proxyHost=... -Dhttps.proxyPort=...
```

### Error: "Compilation failed: cannot find symbol"

**Cause:** Dependencies didn't download fully

**Solution:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### Error: "OutOfMemoryException"

**Cause:** Not enough heap memory

**Solution:** Increase Gradle heap in `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx4G
```

Then rebuild.

---

## Verification Checklist

After build succeeds, verify:

- [ ] JAR exists: `build/libs/VersionBridge-1.0.0.jar`
- [ ] JAR size: 150-300KB (realistic Fabric mod size)
- [ ] Contains metadata:
  ```bash
  jar xf build/libs/VersionBridge-1.0.0.jar fabric.mod.json
  cat fabric.mod.json | grep "versionbridge"
  # Should show: "id": "versionbridge"
  ```
- [ ] Contains compiled classes:
  ```bash
  jar tf build/libs/VersionBridge-1.0.0.jar | grep "\.class" | wc -l
  # Should show 3+ .class files
  ```
- [ ] Loads in Minecraft:
  - Copy JAR to `~/.minecraft/mods/`
  - Launch Minecraft 1.21 + Fabric
  - Check console: `[FabricLoader] Loaded VersionBridge`
  - In-game: `/version` should work

---

## GitHub Release (Distribution)

### Upload JAR to Release

```bash
# Create release tag
git tag -a v1.0.0 -m "VersionBridge 1.0.0 Release"
git push origin v1.0.0

# GitHub Actions will:
# 1. Build JAR
# 2. Create Release page
# 3. Auto-upload JAR to Releases

# Users download from:
# GitHub → Releases → v1.0.0 → VersionBridge-1.0.0.jar
```

---

## Gradle Build Details (Advanced)

### What Gradle Does

```
Input:                          Processing:                         Output:
src/main/java/*.java        →   Compile (javac)                 →   build/classes/
src/main/resources/*        →   Copy resources              →   build/resources/
                                   Fabric Loom mapping
                                   (1.21 deobfuscation)
                                ↓
                            Remap to Mojang names
                                ↓
                            Package JAR
                                ↓
                            build/libs/
                            VersionBridge-1.0.0.jar
```

### Gradle Commands

```bash
# Full build with tests
./gradlew build

# Build without tests (faster)
./gradlew build -x test

# Clean rebuild
./gradlew clean build

# View dependencies
./gradlew dependencies

# Download only (don't compile)
./gradlew downloadAssets

# Stop daemon (useful if hung)
./gradlew --stop
```

---

## Gradle Cache Management

### First-Time Setup

Large downloads on first run:
```
gradle/wrapper/gradle-8.5-bin.zip        ~200MB
~/.gradle/caches/minecraft/1.21/          ~400MB
~/.gradle/caches/fabric-*                 ~150MB
Total: ~750MB
```

### Clear Cache (if corrupted)

```bash
# Remove Gradle cache
rm -rf ~/.gradle/caches

# Next build will re-download everything (slow)
./gradlew clean build
```

### Speed Up Subsequent Builds

Enable daemon (default):
```bash
# Gradle daemon runs in background between builds
# Speeds up compilation by ~30%

# Check daemon status
./gradlew --status

# Stop daemon if needed
./gradlew --stop
```

---

## Expected Build Output

### Console Output Example

```
Starting a Gradle Daemon, 1 incompatible Daemon will be stopped
> Task :downloadAssets
Downloading https://launcher.mojang.com/v1/objects/...
[████████████████████] 100% (180 MB)

> Task :compileJava
Compiling com.versionbridge...
[████████████████████] 100%

> Task :remapJar
Remapping jar using mappings...
[████████████████████] 100%

> Task :assemble
Building JAR...
[████████████████████] 100%

> Task :build
Running tests...
[████████████████████] 100%

BUILD SUCCESSFUL in 3m 45s
```

### Expected File Structure After Build

```
VersionBridge/
├── build/
│   ├── classes/java/main/
│   │   └── com/versionbridge/*.class
│   ├── resources/main/
│   │   ├── fabric.mod.json
│   │   └── versionbridge.mixins.json
│   ├── libs/
│   │   ├── VersionBridge-1.0.0.jar          ← USE THIS
│   │   ├── VersionBridge-1.0.0-dev.jar      (intermediate)
│   │   └── VersionBridge-1.0.0-sources.jar
│   └── tmp/
│       └── (compilation artifacts)
├── .gradle/
│   └── (Gradle cache, 2GB+)
└── (source files, unchanged)
```

---

## Next Steps

1. ✅ **Have Java 21?** → Use Method 1 (Local)
2. ✅ **Have Docker?** → Use Method 2 (Docker)
3. ✅ **Have GitHub?** → Use Method 3 (Actions)
4. ✅ **JAR built?** → Install to `~/.minecraft/mods/`
5. ✅ **Minecraft loaded?** → Test with `/version` command

---

## Support

- **Build fails?** Check troubleshooting section above
- **JAR won't load?** Check mod folder path and Fabric version
- **Code changes?** Rebuild with `./gradlew build` after editing
- **Questions?** See README.md and PROJECT_SUMMARY.md

---

**VersionBridge v1.0.0**  
*Built with Gradle 8.5, Fabric Loom 1.6.10, and Java 21.*

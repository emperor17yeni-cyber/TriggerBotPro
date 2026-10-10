# VersionBridge — Minecraft 1.21 Fabric Mod

Select target Minecraft server/protocol versions while running Minecraft 1.21.

## ✅ Features Implemented

- **Version Command System**: `/version`, `/version list`, `/version set <version>`, `/version off`
- **Configuration Persistence**: Saves chosen target version to `config/versionbridge/config.txt`
- **Honest Version Support**:
  - ✅ 1.21 (native, fully supported)
  - ⚠️ 1.21.1, 1.21.4 (listed, but **protocol translation NOT implemented** — see limitations)
- **Clear Error Handling**: Gracefully rejects unsupported versions
- **Server/Client Entry Points**: Separate initialization for server-side commands and client configuration

## ⚠️ Limitations & NOT Implemented

**This mod does NOT include:**
- ❌ **Protocol translation** (clientbound/serverbound packet conversion)
- ❌ **Registry mapping** (handling items/blocks from newer Minecraft versions)
- ❌ **Packet rewriting** (packet format changes between versions)
- ❌ **Missing item/block fallbacks** (placeholder rendering)
- ❌ **GUI/Config screen** in Mods menu

**Why?**
Full protocol translation between Minecraft versions is a massive undertaking. Projects like ViaVersion and ViaFabricPlus handle this with thousands of lines of code and extensive maintenance. Implementing this honestly within constraints would require:
1. Reverse-engineering packet formats for each target version
2. Mapping protocol differences (entity metadata, block states, etc.)
3. Handling login/configuration transitions
4. Testing against actual servers

Instead, this mod provides:
- A working command system and configuration file
- Clear messaging about what IS and ISN'T supported
- A foundation for honest protocol integration

## 📦 Installation

1. **Ensure Fabric 1.21 is installed**
   - Download Fabric Loader 0.15.11+ from https://fabricmc.net
   - Install for Minecraft 1.21

2. **Place the JAR**
   ```
   VersionBridge-1.0.0.jar → .minecraft/mods/
   ```

3. **Launch Minecraft**
   - Verify mod loads in Mods list
   - No errors should appear in logs

## 🎮 Usage

### Commands (Server only)

```
/version                  Show current version and target version
/version list             Display supported target versions
/version set <version>    Change target version (1.21, 1.21.1, 1.21.4)
/version off              Disable version selection (use 1.21 native)
```

### Configuration File

Settings are saved to:
```
.minecraft/config/versionbridge/config.txt
```

Edit manually if needed (stores version string or empty).

## ⚙️ Technical Details

- **Minecraft**: 1.21 exactly
- **Fabric Loader**: 0.15.11+
- **Java**: 21+
- **Build Tool**: Gradle 8.5 + Fabric Loom

## 🔧 Building from Source

```bash
./gradlew build
# Output: build/libs/VersionBridge-1.0.0.jar
```

## 📝 License

MIT License

## ⚡ Future Work

To extend this mod with actual protocol support:
1. Integrate or adapt ViaFabricPlus's packet translation layer
2. Implement packet listeners for login/configuration phases
3. Add fallback renderers for unknown items/blocks
4. Test against actual 1.21.1+ servers

---

**VersionBridge 1.0.0** — Honest version selection for Minecraft 1.21 Fabric.

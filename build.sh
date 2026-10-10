#!/bin/bash

set -e

echo "========================================"
echo "VersionBridge JAR Builder"
echo "========================================"
echo

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Install Java 21+ and try again."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]*')
echo "✓ Java $JAVA_VERSION found"

# Check internet (optional warning)
if ! ping -c 1 8.8.8.8 &> /dev/null; then
    echo "⚠ No internet detected. Build may fail."
    echo "  Consider using Docker: docker build -t versionbridge . && docker run --rm -v \$(pwd)/build:/build/build versionbridge"
    echo
fi

# Make gradlew executable
chmod +x gradlew

# Run Gradle build
echo "📦 Building VersionBridge..."
echo

if ./gradlew build --no-daemon; then
    echo
    echo "✅ Build successful!"
    echo "📁 JAR location: build/libs/VersionBridge-1.0.0.jar"
    echo
    echo "📋 Next steps:"
    echo "   1. Copy to ~/.minecraft/mods/"
    echo "   2. Launch Minecraft 1.21 with Fabric"
    echo "   3. Verify mod in /version command"
    echo
else
    echo "❌ Build failed. Check the error above."
    exit 1
fi

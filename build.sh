#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="8.9"
CACHE_DIR=".gradle-local"
DIST_DIR="$CACHE_DIR/gradle-$GRADLE_VERSION"
ZIP="$CACHE_DIR/gradle-$GRADLE_VERSION-bin.zip"

if [[ ! -x "$DIST_DIR/bin/gradle" ]]; then
  mkdir -p "$CACHE_DIR"
  echo "Gradle $GRADLE_VERSION indiriliyor..."
  curl -fL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
  rm -rf "$DIST_DIR"
  unzip -q "$ZIP" -d "$CACHE_DIR"
  rm -f "$ZIP"
fi

"$DIST_DIR/bin/gradle" build

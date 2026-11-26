#!/bin/bash

# Find project directory automatically
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

APP_NAME="AoLA"
ICON_PATH="$PROJECT_DIR/assets/ui/icon.jpeg"

# Check icon
if [ ! -f "$ICON_PATH" ]; then
    echo "Error: AoLA icon not found!"
    exit 1
fi

# Create applications directory
mkdir -p "$HOME/.local/share/applications"

# Create desktop launcher
cat > "$HOME/.local/share/applications/AoLA.desktop" <<EOF
[Desktop Entry]
Version=1.0
Type=Application
Name=AoLA
Comment=Adventure of Lil Akbar
Exec=/usr/bin/env bash "$PROJECT_DIR/gradlew" -p "$PROJECT_DIR" lwjgl3:run
Path=$PROJECT_DIR
Icon=$ICON_PATH
Terminal=false
Categories=Game;
StartupNotify=true
EOF

chmod +x "$HOME/.local/share/applications/AoLA.desktop"

# Find Desktop directory
DESKTOP_DIR="$(xdg-user-dir DESKTOP)"

if [ -d "$DESKTOP_DIR" ]; then
    cp "$HOME/.local/share/applications/AoLA.desktop" "$DESKTOP_DIR/"
    chmod +x "$DESKTOP_DIR/AoLA.desktop"

    if command -v gio >/dev/null 2>&1; then
        gio set "$DESKTOP_DIR/AoLA.desktop" metadata::trusted true
    fi
fi

echo "AoLA Desktop Launcher installed successfully!"

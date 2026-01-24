#!/bin/bash
# Script to discover Hytale APIs
# Usage: ./find_apis.sh /path/to/HytaleServer.jar

JAR_PATH="$1"

if [ -z "$JAR_PATH" ]; then
    echo "Usage: $0 /path/to/HytaleServer.jar"
    exit 1
fi

echo "=== Finding Command Argument APIs ==="
jar -tf "$JAR_PATH" | grep -i "command.*arg" | grep "\.class$"

echo ""
echo "=== Finding Teleport APIs ==="
jar -tf "$JAR_PATH" | grep -i "teleport" | grep "\.class$"

echo ""
echo "=== Finding Death/Damage APIs ==="
jar -tf "$JAR_PATH" | grep -i "death\|damage" | grep "\.class$" | head -20

echo ""
echo "=== Finding Block Event APIs ==="
jar -tf "$JAR_PATH" | grep -i "block.*event\|event.*block" | grep "\.class$"

echo ""
echo "=== Finding Notification APIs ==="
jar -tf "$JAR_PATH" | grep -i "notif\|message\|chat" | grep "\.class$" | head -20

echo ""
echo "=== Finding Entity Stat APIs ==="
jar -tf "$JAR_PATH" | grep -i "stat\|health" | grep "\.class$" | head -20

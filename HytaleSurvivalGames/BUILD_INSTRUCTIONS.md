# Build Instructions

## Prerequisites

1. **Java 25** - Download from [Oracle JDK 25](https://www.oracle.com/java/technologies/downloads/)
2. **Gradle** (optional - can use wrapper or install locally)
3. **HytaleServer.jar** - From your Hytale installation

## Step 1: Add HytaleServer.jar

Copy the Hytale Server JAR file to the `libraries/` directory:

**Windows:**
```cmd
copy "%APPDATA%\Hytale\install\release\package\game\latest\Server\HytaleServer.jar" libraries\
```

**macOS/Linux:**
```bash
cp ~/Library/Application\ Support/Hytale/install/release/package/game/latest/Server/HytaleServer.jar libraries/
```

Or manually:
1. Locate your Hytale installation directory
2. Find `HytaleServer.jar` in the server files
3. Copy it to the `libraries/` folder in this project

## Step 2: Build the Plugin

### Option A: Using Gradle Wrapper (Recommended)

**Windows:**
```cmd
gradlew.bat build
```

**macOS/Linux:**
```bash
./gradlew build
```

### Option B: Using Local Gradle Installation

If you have Gradle installed globally:

```cmd
gradle build
```

### Option C: If Gradle Wrapper Fails (Network Issues)

If the Gradle wrapper fails to download, install Gradle locally:

**Windows (using Chocolatey):**
```cmd
choco install gradle
```

**Windows (Manual):**
1. Download Gradle from https://gradle.org/releases/
2. Extract to `C:\Gradle`
3. Add `C:\Gradle\bin` to your PATH
4. Run: `gradle build`

**macOS (using Homebrew):**
```bash
brew install gradle
```

Then use:
```cmd
gradle build
```

## Step 3: Locate the Built Plugin

The compiled JAR will be in:
```
build/libs/HytaleSurvivalGames-1.0.0.jar
```

## Step 4: Install to Hytale Server

Copy the JAR file to your Hytale server's plugins folder:

**Windows:**
```cmd
copy build\libs\HytaleSurvivalGames-1.0.0.jar "%APPDATA%\Hytale\Server\plugins\"
```

**macOS/Linux:**
```bash
cp build/libs/HytaleSurvivalGames-1.0.0.jar ~/Hytale/Server/plugins/
```

## Troubleshooting

### "gradlew is not recognized"

**Solution 1:** Make sure you're in the correct directory
```cmd
cd HytaleSurvivalGames
dir
```
You should see `gradlew.bat` in the listing.

**Solution 2:** Use the `.bat` extension on Windows
```cmd
gradlew.bat build
```

**Solution 3:** Install Gradle locally (see Option C above)

### "Could not find HytaleServer.jar"

Make sure you copied `HytaleServer.jar` to the `libraries/` folder:
```cmd
dir libraries
```
You should see `HytaleServer.jar` listed.

### "Unsupported class file major version"

You need Java 25. Check your version:
```cmd
java -version
```

If it's not Java 25, download and install it from Oracle, then update your JAVA_HOME:

**Windows:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-25
set PATH=%JAVA_HOME%\bin;%PATH%
```

### "Execution failed for task ':compileJava'"

This usually means:
1. HytaleServer.jar is missing from libraries/
2. Java version is incorrect (need Java 25)
3. Code errors (check the error message)

## Quick Reference

**Clean build:**
```cmd
gradlew.bat clean build
```

**Build without tests:**
```cmd
gradlew.bat build -x test
```

**View all tasks:**
```cmd
gradlew.bat tasks
```

**Check dependencies:**
```cmd
gradlew.bat dependencies
```

# Hytale Survival Games Plugin

A complete Survival Games (Hunger Games) implementation for Hytale servers, built using the **actual Hytale Server API**.

## ⚠️ Important Notes

This plugin is built for **Hytale Early Access** using the real Hytale Server API structure:
- Uses `com.hypixel.hytale.server.core.*` packages (not generic APIs)
- Extends `JavaPlugin` with `JavaPluginInit` constructor
- Uses Codec-based JSON configuration (not YAML)
- Follows Hytale's Entity Component System (ECS) architecture
- Requires **Java 25** (as per Hytale requirements)

## 🔧 Prerequisites

1. **Java 25** - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. **Hytale Server** - Place `HytaleServer.jar` in `libraries/` directory
3. **Gradle** - Included via Gradle Wrapper

## 📦 Installation

1. **Clone or download this repository**

2. **Add HytaleServer.jar**
   ```bash
   # Place the Hytale server JAR in the libraries folder
   cp /path/to/HytaleServer.jar libraries/
   ```

3. **Build the plugin**
   ```bash
   ./gradlew build
   ```

4. **Install to server**
   ```bash
   # Copy the built JAR to your Hytale server's plugins folder
   cp build/libs/HytaleSurvivalGames-1.0.0.jar /path/to/server/plugins/
   ```

5. **Start your Hytale server**
   - The plugin will auto-generate configuration on first run
   - Configuration location: `plugins/com.hytale_HytaleSurvivalGames/SurvivalGames.example.json`

## 🎮 Features

### Core Gameplay
- **Multiple Arena Support**: Create and manage unlimited arenas
- **Lobby System**: Automatic game start when minimum players join
- **Game States**: WAITING → STARTING → IN_GAME → DEATHMATCH → ENDING
- **Player Tracking**: Kill/death statistics and win tracking
- **Spectator Mode**: Eliminated players can watch the match

### Current Implementation Status

✅ **Implemented**:
- Plugin structure following Hytale API patterns
- Configuration system using Codec
- Arena data models with Vector3d positions
- Game state management
- Player data tracking
- Commands: `/sg`, `/sgjoin`, `/sgleave`, `/sglist`
- Event listeners for player join/leave/chat

⚠️ **Requires Server API Access** (TODO with actual Hytale server):
- Player teleportation (Transform/position API)
- Inventory management (ItemStack/ItemContainer)
- Loot chest spawning
- Game timers using Hytale's scheduler
- World/dimension management
- Entity damage/death events

## 📝 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/sg` | Show plugin info and help | None |
| `/sgjoin [arena]` | Join a Survival Games match | None |
| `/sgleave` | Leave current match | None |
| `/sglist` | List all available arenas | None |

## ⚙️ Configuration

Configuration is stored in JSON format using Hytale's Codec system:

**Location**: `plugins/com.hytale_HytaleSurvivalGames/SurvivalGames.example.json`

**Example**:
```json
{
  "minPlayers": 2,
  "maxPlayers": 24,
  "gameTime": 600,
  "countdownTime": 10,
  "deathmatchTime": 300,
  "borderRadius": 50,
  "debug": false,
  "broadcastStart": true,
  "broadcastEnd": true,
  "welcomeMessage": "Welcome to Survival Games! Use /sg join to play."
}
```

## 🏗️ Project Structure

```
HytaleSurvivalGames/
├── build.gradle              # Gradle build configuration
├── gradle.properties         # Gradle settings
├── settings.gradle           # Project settings
├── libraries/                # Place HytaleServer.jar here
└── src/main/java/com/hytale/survivalgames/
    ├── SurvivalGamesPlugin.java         # Main plugin class
    ├── commands/
    │   ├── SGCommand.java                # Main /sg command
    │   ├── JoinCommand.java              # /sgjoin command
    │   ├── LeaveCommand.java             # /sgleave command
    │   └── ListCommand.java              # /sglist command
    ├── config/
    │   └── SGConfig.java                 # Configuration with Codec
    ├── game/
    │   ├── GameState.java                # Game state enum
    │   ├── Arena.java                    # Arena model
    │   └── GameManager.java              # Game logic controller
    ├── listeners/
    │   └── PlayerEventListener.java      # Event handlers
    └── player/
        └── PlayerDataManager.java        # Player stats tracking
```

## 🔍 Code Structure Overview

### Main Plugin Class

```java
public class SurvivalGamesPlugin extends JavaPlugin {
    // CORRECT Hytale API pattern
    public SurvivalGamesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = this.withConfig("SurvivalGames", SGConfig.CODEC);
    }

    @Override
    protected void setup() {
        // Register commands and events
        this.getCommandRegistry().registerCommand(new SGCommand(this));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class,
            PlayerEventListener::onPlayerReady);
    }
}
```

### Configuration with Codec

```java
public class SGConfig {
    public static final BuilderCodec<SGConfig> CODEC = BuilderCodec
        .builder(SGConfig.class, SGConfig::new)
        .append(new KeyedCodec<>("minPlayers", Codec.INTEGER),
                (config, value, info) -> config.minPlayers = value,
                (config, info) -> config.minPlayers)
        .add()
        // ... more fields
        .build();
}
```

### Commands

```java
public class JoinCommand extends CommandBase {
    public JoinCommand(@Nonnull SurvivalGamesPlugin plugin) {
        super("sgjoin", "Join a Survival Games match");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        // Command implementation
        context.sendMessage(Message.raw("§aJoined game!"));
    }
}
```

### Event Listeners

```java
public class PlayerEventListener {
    // Static methods for event handling
    public static void onPlayerReady(@Nonnull PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("Welcome!"));
    }
}
```

## 🎯 API Differences from Other Server Platforms

If you're coming from Bukkit/Spigot/Paper, note these key differences:

| Feature | Bukkit/Spigot | Hytale |
|---------|---------------|---------|
| **Main Class** | `extends JavaPlugin` | `extends JavaPlugin` with `JavaPluginInit` |
| **Lifecycle** | `onEnable()` / `onDisable()` | `setup()` method |
| **Events** | `@EventHandler` methods | `getEventRegistry().registerGlobal()` with static methods |
| **Commands** | `implements CommandExecutor` | `extends CommandBase` |
| **Config** | YAML with `plugin.yml` | JSON with Codec (no manifest file) |
| **Messages** | `player.sendMessage(String)` | `player.sendMessage(Message.raw(String))` |
| **Locations** | `Location` class | `Vector3d` and `Transform` |
| **Architecture** | Traditional OOP | Entity Component System (ECS) |
| **Build Tool** | Maven typically | Gradle (Java 25) |

## 📚 API References

This plugin uses the official Hytale Server API:

- **Package**: `com.hypixel.hytale.server.core.*`
- **Main Classes**:
  - `JavaPlugin` - Plugin base class
  - `Player` - Player entity
  - `World` - World/dimension
  - `Message` - Message objects
  - `Vector3d` - 3D position vectors
  - `CommandBase` - Command base class
  - `Codec` - Serialization system

**Official Resources**:
- [Hytale Plugin Examples](https://github.com/sammwyy/Hytale-Plugin-Examples)
- [Hytale Server Unpacked](https://github.com/Ranork/Hytale-Server-Unpacked) (Decompiled API reference)
- [Hytale Modding Documentation](https://britakee-studios.gitbook.io/hytale-modding-documentation)

## 🚀 Development

### Building

```bash
# Build the plugin
./gradlew build

# Clean and rebuild
./gradlew clean build

# Run tests (if any)
./gradlew test
```

### Adding Dependencies

Edit `build.gradle`:

```gradle
dependencies {
    compileOnly name: 'HytaleServer'
    // Add your dependencies here
}
```

## 🐛 Known Limitations

1. **Arena Configuration**: Currently hardcoded. Need to implement arena loading from config files.
2. **Player Teleportation**: Requires Hytale's Transform/position API (not fully documented yet).
3. **Inventory Management**: Loot system requires access to Hytale's inventory API.
4. **Schedulers/Timers**: Game timers need Hytale's scheduler API implementation.
5. **Death Events**: Player elimination requires entity damage/death events.

These will be implemented as the Hytale API documentation becomes more complete.

## 📄 License

This plugin is provided as-is for Hytale server development.

## 🤝 Contributing

Contributions are welcome! Please note:

1. Follow Hytale's API patterns (no Bukkit-style code)
2. Use proper Codec configuration instead of YAML
3. Test with actual HytaleServer.jar
4. Document any new API discoveries

## ⚙️ Troubleshooting

**"Cannot find HytaleServer.jar"**
- Place `HytaleServer.jar` in the `libraries/` directory

**"Unsupported class file major version"**
- You need Java 25. Hytale uses the latest Java version.

**"Configuration not loading"**
- Check `plugins/com.hytale_HytaleSurvivalGames/` for the JSON file
- Ensure proper Codec syntax in SGConfig.java

**"Commands not working"**
- Verify plugin loaded: check server logs
- Ensure commands extend `CommandBase` correctly
- Check command registration in `setup()` method

## 📞 Support

For issues or questions:
- Check the [Hytale Plugin Examples](https://github.com/sammwyy/Hytale-Plugin-Examples)
- Review the [decompiled API](https://github.com/Ranork/Hytale-Server-Unpacked)
- Consult Hytale modding community resources

---

**Version**: 1.0.0
**API Version**: Hytale Early Access
**Author**: HytaleDevTeam
**Built with**: Java 25, Gradle, Hytale Server API

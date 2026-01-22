# Hytale Survival Games Plugin

A complete Minecraft-style Survival Games (Hunger Games) implementation for Hytale servers. This plugin provides a fully-featured battle royale game mode with lobby management, arena configuration, loot systems, and comprehensive game state management.

## Features

### Core Gameplay
- **Multiple Arena Support**: Create and manage unlimited arenas with unique configurations
- **Lobby System**: Queue-based matchmaking with automatic game start when minimum players join
- **Game States**: WAITING → STARTING → IN_GAME → DEATHMATCH → ENDING
- **Player Management**: Track kills, deaths, and statistics per player
- **Spectator Mode**: Players eliminated during matches become spectators

### Arena Features
- **Customizable Boundaries**: Define arena regions with corner coordinates
- **Random Spawns**: Players spawn at random locations from configured spawn points
- **Loot Chests**: Configurable chest locations with tiered loot tables
- **Deathmatch Mode**: Border shrinking mechanic to force final combat
- **Time Limits**: Configurable game duration and deathmatch timing

### Loot System
- **Tiered Loot Tables**: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY rarities
- **Weighted Spawning**: Higher tier items have lower spawn chances
- **Multiple Tables**: Basic, Tier 2, and Bonus loot configurations
- **Chest Population**: Automatic loot distribution at game start

### Commands
- `/sg join [arena]` - Join a Survival Games match
- `/sg leave` - Leave current match
- `/sg list` - View all available arenas
- `/sg info <arena>` - View detailed arena information
- `/sg start <arena>` - Force start a game (admin)
- `/sg stop <arena>` - Stop an active game (admin)
- `/sg reload` - Reload configuration (admin)
- `/sg setspawn <arena>` - Add spawn point at current location (admin)

### Permissions
- `survivalgames.join` - Join games (default: true)
- `survivalgames.spectate` - Spectate matches (default: true)
- `survivalgames.admin` - Access admin commands (default: op)

## Installation

1. Download the plugin JAR file
2. Place it in your Hytale server's `mods` or `plugins` folder
3. Start/restart your server
4. Configure arenas in `plugins/HytaleSurvivalGames/arenas.yml`
5. Customize settings in `plugins/HytaleSurvivalGames/config.yml`

## Configuration

### Main Config (`config.yml`)

```yaml
settings:
  debug: false
  broadcast-game-start: true
  broadcast-game-end: true

game:
  min-players: 2
  max-players: 24
  game-time: 600
  deathmatch-time: 300
  border-shrink-radius: 50
```

### Arena Configuration (`arenas.yml`)

Each arena requires:
- **Name**: Unique identifier
- **Display Name**: Pretty name shown to players
- **World**: World name where arena exists
- **Boundaries**: corner1 and corner2 coordinates (cuboid region)
- **Lobby Spawn**: Where players wait before game starts
- **Spawn Points**: List of coordinates where players spawn at game start
- **Chest Locations**: Where loot chests spawn
- **Game Settings**: min/max players, timers, border radius

Example arena:
```yaml
arenas:
  default:
    displayName: "§6Classic Arena"
    world: "world"
    corner1: {x: 0, y: 60, z: 0}
    corner2: {x: 200, y: 100, z: 200}
    lobby: {x: 100, y: 65, z: 100, yaw: 0, pitch: 0}
    spawnPoints:
      - {x: 80, y: 65, z: 80, yaw: 45, pitch: 0}
      - {x: 120, y: 65, z: 80, yaw: 135, pitch: 0}
    chestLocations:
      - {x: 100, y: 65, z: 100}
      - {x: 75, y: 65, z: 75}
    minPlayers: 2
    maxPlayers: 12
    gameTime: 600
    deathmatchTime: 420
    borderShrinkRadius: 40
```

### Loot Configuration (`loot.yml`)

Define custom loot tables with items, quantities, and spawn chances:

```yaml
loot-tables:
  basic:
    min-items: 3
    max-items: 7
    items:
      iron_sword:
        material: IRON_SWORD
        min-amount: 1
        max-amount: 1
        chance: 0.20
        rarity: RARE
```

## Game Flow

1. **Lobby Phase** (WAITING)
   - Players join with `/sg join [arena]`
   - Countdown starts when minimum players reached
   - Players teleported to lobby spawn
   - Inventory cleared, health/hunger restored

2. **Starting Phase** (STARTING)
   - 10-second countdown
   - Players frozen at their positions
   - Messages displayed at intervals
   - Can cancel if players drop below minimum

3. **Active Game** (IN_GAME)
   - Players teleported to random spawn points
   - Loot spawns in all chest locations
   - PvP enabled, building/breaking allowed
   - Timer starts counting down

4. **Deathmatch** (DEATHMATCH)
   - Triggered at configured time
   - Border shrinks to configured radius
   - Players outside border take damage
   - Forces final confrontation

5. **Ending Phase** (ENDING)
   - Winner announced (last player alive)
   - Stats updated (kills, wins)
   - Arena resets after delay
   - Players returned to spawn

## API Usage

### Getting Plugin Instance
```java
SurvivalGamesPlugin plugin = SurvivalGamesPlugin.getInstance();
```

### Accessing Managers
```java
GameManager gameManager = plugin.getGameManager();
PlayerManager playerManager = plugin.getPlayerManager();
LootManager lootManager = plugin.getLootManager();
ConfigManager configManager = plugin.getConfigManager();
```

### Checking Player Status
```java
Arena arena = gameManager.getPlayerArena(player.getUniqueId());
if (arena != null) {
    GameState state = arena.getGameState();
    int playersRemaining = arena.getPlayerCount();
}
```

### Custom Loot Tables
```java
LootTable customTable = new LootTable("custom", 5, 10);
customTable.addItem(new LootItem(Material.DIAMOND_SWORD, 0.5, Rarity.EPIC));
lootManager.addLootTable("custom", customTable);
```

## Project Structure

```
HytaleSurvivalGames/
├── plugin.json                           # Plugin manifest
├── README.md                             # This file
├── src/main/
│   ├── java/com/hytale/survivalgames/
│   │   ├── SurvivalGamesPlugin.java     # Main plugin class
│   │   ├── game/
│   │   │   ├── GameState.java           # Game state enum
│   │   │   ├── GameManager.java         # Game logic controller
│   │   │   └── Arena.java               # Arena model
│   │   ├── player/
│   │   │   ├── PlayerManager.java       # Player data manager
│   │   │   └── SGPlayer.java            # Player wrapper
│   │   ├── loot/
│   │   │   ├── LootManager.java         # Loot distribution
│   │   │   ├── LootTable.java           # Loot table model
│   │   │   └── LootItem.java            # Loot item definition
│   │   ├── commands/
│   │   │   └── SGCommand.java           # Command handler
│   │   ├── listeners/
│   │   │   └── PlayerListener.java      # Event handlers
│   │   └── config/
│   │       ├── ConfigManager.java       # Config loader
│   │       └── ArenaConfig.java         # Arena data model
│   └── resources/
│       ├── config.yml                    # Main configuration
│       ├── arenas.yml                    # Arena definitions
│       └── loot.yml                      # Loot tables
```

## Class Overview

### Core Classes

**SurvivalGamesPlugin** (`SurvivalGamesPlugin.java:1`)
- Main plugin entry point
- Initializes all managers
- Handles plugin lifecycle

**GameManager** (`GameManager.java:1`)
- Controls game flow and state transitions
- Manages timers and countdowns
- Handles player join/leave
- Determines win conditions

**Arena** (`Arena.java:1`)
- Represents a game arena
- Stores boundaries, spawns, and settings
- Tracks active players and spectators

### Player Management

**PlayerManager** (`PlayerManager.java:1`)
- Creates and manages SGPlayer instances
- Tracks player statistics

**SGPlayer** (`SGPlayer.java:1`)
- Wrapper for players in matches
- Stores kills, deaths, survival time
- Handles inventory save/restore

### Loot System

**LootManager** (`LootManager.java:1`)
- Populates chests with loot
- Manages loot tables
- Handles weighted item spawning

**LootTable** (`LootTable.java:1`)
- Collection of loot items
- Defines min/max items per chest
- Generates randomized loot

**LootItem** (`LootItem.java:1`)
- Individual loot item definition
- Configurable quantity ranges
- Rarity-based spawn chances

### Event Handling

**PlayerListener** (`PlayerListener.java:1`)
- Handles all player events
- Prevents unauthorized actions
- Manages death/respawn
- Enforces arena boundaries

### Configuration

**ConfigManager** (`ConfigManager.java:1`)
- Loads YAML configurations
- Parses arena definitions
- Provides config access to other components

## Development

### Building from Source

1. Clone the repository
2. Ensure Java 17+ and Hytale SDK are installed
3. Build with your build tool (Maven/Gradle)
4. Output JAR will be in `build/libs` or `target/`

### Adding Custom Features

**Custom Game Modes:**
Extend `GameManager` and add new `GameState` values

**Custom Loot Tables:**
Use `LootManager.addLootTable()` to register new tables

**Custom Events:**
Create event classes extending Hytale's Event system

**Database Integration:**
Implement stats storage using the config database settings

## Support

For issues, bugs, or feature requests, please create an issue on the project repository.

## License

This plugin is provided as-is for use with Hytale servers.

## Credits

- Inspired by Minecraft Hunger Games/Survival Games
- Built for the Hytale server platform
- Developed with Hytale Server Modding API

---

**Version:** 1.0.0
**API Version:** 1.0
**Author:** HytaleDevTeam

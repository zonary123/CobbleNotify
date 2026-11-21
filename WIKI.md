# 📖 UltraNotify (CobbleNotify) Complete Wiki & Configuration Guide

Welcome to the **UltraNotify** (CobbleNotify) documentation! UltraNotify is an enterprise-grade alert, broadcast, and community engagement engine designed for Minecraft Cobblemon servers. It monitors Pokémon life-cycle events—wild spawns, captures, battle defeats, and player trades—dispatching stylized in-game broadcasts, interactive clickable coordinates, sound effects, and rich Discord Webhook embeds.

---

## 📌 Table of Contents
1. [Overview & Core Architecture](#-overview--core-architecture)
2. [Commands & Permissions](#-commands--permissions)
3. [The 4 Event Triggers](#-the-4-event-triggers)
4. [Dual-Delivery Pipelines](#-dual-delivery-pipelines)
   - [In-Game Alerts (Chat, Actionbar, Titles, Sounds)](#in-game-alerts)
   - [Discord Webhook Rich Embeds](#discord-webhook-rich-embeds)
5. [Priority Engine & Filtering Architecture](#-priority-engine--filtering-architecture)
   - [Priority Weighting](#priority-weighting)
   - [Filter Criteria (Labels, Aspects, Properties, NBT)](#filter-criteria)
   - [World & Biome Restrictions](#world--biome-restrictions)
6. [Interactive In-Game Historical Log GUI](#-interactive-in-game-historical-log-gui)
7. [Directory & File Organization](#-directory--file-organization)
8. [Complete JSON Configuration Schema](#-complete-json-configuration-schema)
   - [Global Settings (`config.json`)](#global-settings-configjson)
   - [Modular Notification Rules (`notifications/<id>.json`)](#modular-notification-rules-notificationsidjson)
9. [Production JSON Configuration Examples](#-production-json-configuration-examples)
   - [Example 1: Legendary Spawns, Catches & Defeats (`legendary.json`)](#example-1-legendary-spawns-catches--defeats-legendaryjson)
   - [Example 2: Wild Shiny Alert with Discord Sprite Embed (`shiny.json`)](#example-2-wild-shiny-alert-with-discord-sprite-embed-shinyjson)
   - [Example 3: World Boss Encounter Notification (`boss.json`)](#example-3-world-boss-encounter-notification-bossjson)
10. [Troubleshooting & FAQ](#-troubleshooting--faq)

---

## 🌟 Overview & Core Architecture

- **Real-Time Event Monitoring**: Listens asynchronously to Cobblemon's internal event buses (`CatchEvent`, `DefeatEvent`, `SpawnEvent`, and `TradeEvent`), executing notification pipelines on a dedicated background worker pool (`AsyncContext`) without causing server tick drops.
- **Dual-Delivery Architecture**: Every rule can independently send in-game Minecraft alerts and external Discord Webhook notifications.
- **Clickable Chat Coordinates**: In-game broadcast messages support interactive formatting. Players can click coordinates in chat to copy coordinates or run teleport commands (if permitted).
- **Discord Rich Embeds**: Broadcasts events to your community Discord channels formatted with official Cobblemon animated/static sprites, player avatars, Pokémon IV percentages, natures, abilities, biomes, and timestamps.
- **Historical Event Logging**: Stores recent spawns, catches, and trades in SQLite, MongoDB, or JSON. Players and staff can inspect history through `/ultranotify spawns`, `/ultranotify catches`, and `/ultranotify trades`.

---

## 💻 Commands & Permissions

Base command: `/ultranotify`

### Staff & Admin Commands
*(Requires permission node `ultranotify.admin` or Minecraft Level 2 / OP)*

| Command | Description |
| :--- | :--- |
| `/ultranotify` | Opens the main notification hub GUI. |
| `/ultranotify reload` | Hot-reloads all configurations, language strings, and notification rule files. |
| `/ultranotify spawns` | Opens the graphical history browser of recently spawned rare Pokémon. |
| `/ultranotify catches` | Opens the graphical history browser of recently captured Pokémon. |
| `/ultranotify trades` | Opens the graphical history browser of recent player-to-player trades. |

---

## ⚡ The 4 Event Triggers

Every notification rule in `notifications/` can selectively subscribe to any combination of four events:

1. **`SPAWN` (`setSpawn: true`)**: Fires when a wild Pokémon spawns into the world. Perfect for alerting trainers to wild Legendary, Mythical, or Shiny Pokémon nearby.
2. **`CAUGHT` (`setCaught: true`)**: Fires when a trainer successfully catches a wild Pokémon with a PokéBall.
3. **`DEFEAT` (`setDefeat: true`)**: Fires when a wild Pokémon or World Boss is knocked out in battle.
4. **`TRADE` (`setTrade: true`)**: Fires when two trainers complete a Pokémon exchange.

---

## 📡 Dual-Delivery Pipelines

### In-Game Alerts
Configurable inside `notificationOptions`:
- **Chat Broadcast**: Formatted message sent to all players on the server.
- **Clickable Teleport**: Embeds clickable coordinates into chat:
  `[Click to Teleport]` runs `/tp %x% %y% %z%`.
- **Actionbar & Titles**: Shows bold on-screen titles or subtle actionbar text.
- **Sound Effects**: Plays vanilla Minecraft sound cues (e.g. `minecraft:ui.toast.challenge_complete`).

### Discord Webhook Rich Embeds
Configurable inside `webHookOptions`:
- Sends formatted embeds to your designated Discord channel webhook.
- Displays Pokémon icon thumbnail, trainer skin avatar, IV wheel breakdown, nature, ability, held item, coordinates, and biome name.

---

## 🎯 Priority Engine & Filtering Architecture

### Priority Weighting
If a spawning Pokémon matches multiple rules (for example, a **Shiny Legendary Rayquaza** matches both `legendary.json` and `shiny.json`):
- UltraNotify evaluates rules in descending order of their `priority` integer (e.g. `priority: 15` runs before `priority: 5`).
- The highest-priority rule takes precedence.

### Filter Criteria
Rules match Pokémon using flexible criteria:
- **Labels (`filter.labels`)**: Match Cobblemon tags such as `"legendary"`, `"mythical"`, `"ultra_beast"`, `"paradox"`.
- **Aspects (`filter.aspects`)**: Match visual attributes such as `"shiny"`, regional forms, or custom model textures.
- **Cobblemon Properties (`properties`)**: String expressions such as `"shiny=true level=>50"`.
- **Persistent Data / NBT (`persistentDataMap`)**: Match custom tags like `"boss": "*"` from CobbleBosses.

---

## 📁 Directory & File Organization

All configurations and data files reside inside `config/ultranotify/`:

```text
config/ultranotify/
├── config.json          # Main settings, global webhook, blacklist, database
├── WIKI.md              # Auto-extracted in-game reference guide
├── lang/
│   └── en_us.json       # Translatable messages, prefixes, and GUI templates
└── notifications/       # Individual notification rules
    ├── legendary_notification.json
    ├── shiny_notification.json
    └── boss_notification.json
```

---

## 📝 Complete JSON Configuration Schema

### Global Settings (`config/ultranotify/config.json`)

```json
{
  "debug": false,
  "affectCommand": true,
  "lang": "en_us",
  "worldFilter": {
    "whitelist": false,
    "worlds": []
  },
  "database": {
    "type": "JSON",
    "database": "ultranotify",
    "url": "mongodb://localhost:27017",
    "user": "",
    "password": ""
  },
  "webHookData": {
    "url": "https://discord.com/api/webhooks/YOUR_WEBHOOK_URL",
    "name": "Cobblemon Announcer",
    "avatar": "https://example.com/avatar.png"
  },
  "globalBlackList": {
    "whitelist": false,
    "pokemons": [],
    "aspects": [
      "plushie"
    ]
  }
}
```

| Field | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `debug` | `Boolean` | `false` | Enables verbose console logging for event triggers. |
| `affectCommand` | `Boolean` | `true` | Whether Pokémon spawned via commands (`/pokespawn`) trigger notifications. |
| `lang` | `String` | `"en_us"` | Selected language code loaded from `lang/<lang>.json`. |
| `worldFilter` | `Object` | *WorldFilter* | Global dimension whitelist or blacklist. |
| `database` | `Object` | *DataBaseConfig* | Storage backend for historical logs (`JSON`, `SQLITE`, or `MONGODB`). |
| `webHookData` | `Object` | *WebHookData* | Global fallback Discord Webhook credentials. |
| `globalBlackList` | `Object` | *PokemonBlackList* | Species or aspects banned from ever generating notifications. |

---

### Modular Notification Rules (`config/ultranotify/notifications/<id>.json`)

```json
{
  "identifier": "legendary",
  "priority": 10,
  "icon": {
    "item": "cobblemon:master_ball",
    "displayname": "&5&lLegendary Alerts"
  },
  "worldFilter": {
    "whitelist": false,
    "worlds": []
  },
  "useProperties": false,
  "properties": "shiny=true",
  "filter": {
    "labels": ["legendary"],
    "aspects": [],
    "pokemons": []
  },
  "notificationOptions": {
    "spawn": true,
    "caught": true,
    "defeat": true,
    "trade": false,
    "notificationMessages": { ... }
  },
  "webHookOptions": {
    "spawn": true,
    "caught": true,
    "defeat": true,
    "trade": false,
    "webHookMessages": { ... }
  }
}
```

| Field | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `identifier` | `String` | `"notification"` | Unique rule name (corresponds to filename). |
| `priority` | `Integer` | `0` | Evaluation priority (higher numbers evaluate first). |
| `icon` | `Object` | *ItemModel* | Icon representing this rule inside the `/ultranotify` GUI. |
| `worldFilter` | `Object` | *WorldFilter* | Dimension-specific filters for this rule. |
| `useProperties` | `Boolean` | `false` | If `true`, matches against `properties` string. |
| `properties` | `String` | `"shiny=true"` | Cobblemon property filter expression. |
| `filter` | `Object` | *PokemonBlackList* | Whitelist/blacklist of labels, species names, or aspects. |
| `notificationOptions` | `Object` | *Options* | In-game chat, title, and sound parameters. |
| `webHookOptions` | `Object` | *WebHook* | Discord Webhook embed parameters. |

---

## 💡 Production JSON Configuration Examples

### Example 1: Legendary Spawns, Catches & Defeats (`legendary.json`)
Broadcasts wild Legendary spawns with coordinates and alerts the community when caught or defeated:

```json
{
  "identifier": "legendary",
  "priority": 10,
  "icon": {
    "item": "cobblemon:master_ball",
    "displayname": "&5&lLegendary Notifications"
  },
  "worldFilter": {
    "whitelist": false,
    "worlds": []
  },
  "useProperties": false,
  "properties": "",
  "filter": {
    "whitelist": true,
    "labels": ["legendary"],
    "aspects": [],
    "pokemons": []
  },
  "notificationOptions": {
    "spawn": true,
    "caught": true,
    "defeat": true,
    "trade": false,
    "notificationMessages": {
      "messageSpawn": "&5&l[LEGENDARY] &dA wild &5%pokemon% &dhas spawned in &e%biome% &7(%x%, %y%, %z%)!",
      "messageCaught": "&5&l[LEGENDARY] &e%player% &7has captured &5%pokemon%&7!",
      "messageDefeat": "&5&l[LEGENDARY] &e%player% &7defeated a wild &5%pokemon%&7!"
    }
  },
  "webHookOptions": {
    "spawn": true,
    "caught": true,
    "defeat": false,
    "trade": false,
    "webHookMessages": {
      "titleSpawn": "⭐ Wild Legendary Spawned!",
      "descSpawn": "**%pokemon%** has manifested in the **%biome%** dimension at `%x%, %y%, %z%`!",
      "color": "#9B59B6"
    }
  }
}
```

---

### Example 2: Wild Shiny Alert with Discord Sprite Embed (`shiny.json`)
Celebrates when a player catches or encounters a shiny Pokémon:

```json
{
  "identifier": "shiny",
  "priority": 5,
  "icon": {
    "item": "cobblemon:shiny_stone",
    "displayname": "&6&lShiny Alerts"
  },
  "worldFilter": {
    "whitelist": false,
    "worlds": []
  },
  "useProperties": false,
  "properties": "",
  "filter": {
    "whitelist": true,
    "labels": [],
    "aspects": ["shiny"],
    "pokemons": []
  },
  "notificationOptions": {
    "spawn": true,
    "caught": true,
    "defeat": false,
    "trade": false,
    "notificationMessages": {
      "messageSpawn": "&6&l[SHINY] &eA rare Shiny &6%pokemon% &ehas appeared near &b%nearest_player%&e!",
      "messageCaught": "&6&l[SHINY] &6%player% &ehas successfully captured a Shiny &6%pokemon%&e!"
    }
  },
  "webHookOptions": {
    "spawn": false,
    "caught": true,
    "defeat": false,
    "trade": false,
    "webHookMessages": {
      "titleCaught": "✨ Shiny Pokémon Captured!",
      "descCaught": "Trainer **%player%** caught a gleaming **Shiny %pokemon%**!\n\n**IVs:** `%ivs%%` | **Nature:** `%nature%` | **Ability:** `%ability%`",
      "color": "#F1C40F"
    }
  }
}
```

---

## ❓ Troubleshooting & FAQ

### Why did a spawned Pokémon not trigger any message?
1. Check `globalBlackList` in `config.json` to verify the species is not excluded.
2. If spawned using `/pokespawn`, ensure `"affectCommand": true` is enabled in `config.json`.
3. Verify that the Pokémon is not uncatchable or AI-disabled.

### How do I link Discord Webhooks?
Paste your Discord channel webhook URL into `"webHookData.url"` inside `config/ultranotify/config.json`, or inside individual rule files for channel-specific alerts.

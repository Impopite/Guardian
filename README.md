# Protect

[![Version](https://img.shields.io/badge/version-1.0-blue)](https://github.com/Impopite/Protect/releases)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-orange)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21+-red)](https://papermc.io/)

A comprehensive grief protection and logging plugin for Minecraft servers, built with Paper API.

---

## Features

- **Block Logging** — Tracks all block place and break actions with full block data
- **Container Logging** — Monitors item movements in chests, barrels, hoppers, dispensers, and droppers
- **Item Logging** — Records item pickups and drops across the server
- **Interaction Logging** — Logs door and container open/close events
- **Inspect Mode** — Toggle inspect to view logs by clicking on blocks and containers in-world
- **Rollback** — Revert block and container changes within a radius and time window
- **Player Lookup** — View all logs for any player with clickable pagination
- **Player Stats** — Per-player statistics with activity timeline
- **Auto Cleanup** — Configurable automatic purging of old logs
- **MySQL Storage** — Persistent data with HikariCP connection pooling
- **Multilingual** — Built-in English and Italian language support, easily extensible
- **Interactive UI** — Clickable coordinates (teleport), hover tooltips with item details, clickable pagination
- **Developer API** — Public API module for integration with other plugins
- **WorldEdit Compatible** — Soft dependency on FastAsyncWorldEdit

---

## Requirements

- Java **21+**
- Paper **1.21+** (Spigot/Bukkit compatible)
- MySQL **5.7+** or MariaDB **10.3+**

---

## Installation

1. Download the latest `Protect.jar` from the [releases page](https://modrinth.com/plugin/protect)
2. Place the JAR in your server's `plugins/` folder
3. Start or restart the server
4. Edit `plugins/Protect/config.yml` with your database credentials
5. Reload or restart the server to apply changes

---

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/protect` | Show all available subcommands | `protect.staff` |
| `/protect inspect` | Toggle inspect mode | `protect.inspect` |
| `/protect rollback blocks <radius> <time>` | Rollback block changes | `protect.rollback` |
| `/protect rollback containers <radius> <time>` | Rollback container changes | `protect.rollback` |
| `/protect lookup <player>` | View all logs for a player | `protect.inspect` |
| `/protect stats <player>` | View player statistics | `protect.inspect` |
| `/protect reload` | Reload the configuration | `protect.staff` |
| `/inspect` | Alias for `/protect inspect` | `protect.inspect` |

### Time Format

The time argument supports combinations of `s` (seconds), `m` (minutes), `h` (hours), `d` (days).

Examples: `15m`, `1h30m`, `2d`, `30s`, `1h30m45s`

---

## Permissions

| Permission | Description |
|------------|-------------|
| `protect.staff` | Access to root `/protect` and `/protect reload`. Also marks logs as staff actions. |
| `protect.inspect` | Toggle inspect mode, use `/protect lookup` and `/protect stats` |
| `protect.rollback` | Use rollback commands for blocks and containers |

---

## Configuration

```yaml
generic:
  lang: IT_it              # Language file (available: EN_us, IT_it)

database:
  host: "localhost"
  username: "root"
  password: ""
  port: 3306
  name: "ImpooProjects"
  ssl: false

inspect:
  page-size: 10             # Log entries per page

cleanup:
  interval-hours: 24        # Auto-cleanup interval
  retention-days: 60        # Delete logs older than this
```

---

## Documentation

Full documentation including commands, permissions, configuration, and API reference:

**[DOCUMENTATION](https://impoo.gitbook.io/protect)**

---

## Bug Reports & Feature Requests

Found a bug or have a suggestion? Open an issue on [GitHub Issues](https://github.com/Impopite/Protect/issues).

---

## License

This project is licensed under the [MIT License](LICENSE).

---

Developed by **zImpoo**

- Telegram: [@tentava](https://t.me/tentava)
- Discord: [Impopite](https://discord.com/users/Impopite)

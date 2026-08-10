# Changelog

## [1.1.8] - 11-08-2026

### Bug Fixes

- Fixed `NoSuchElementException` in `DefeatEvent` when checking player actions in battles without active player participants.

## [1.1.7] - 22-07-2026

### Optimizations

- Optimized spawn notifications to reduce server load when checking spawning Pokémon.

## [1.1.6] - 26-06-2026

### Bug Fixes

- Notifications for uncatchable Pokémon or those without an AI have been disabled to prevent notification clutter.
- Fixed default configuration.

## [1.1.5] - 18-01-2026

### Bug Fixes

- Fixed Notifications WebHooks information.
- Fixed '/cobblenotify caught' command not working.

## [1.1.4] - 2025-12-03

### Features

- The mod has been completely remade, making it easier to program and reducing the amount of errors.
- More ways to send messages. Examples are in the `/cobbleutils sendMessage` command.
- Added formula, this is more flexible than filters.
- Added trade notifications, which can be customized in the config.
- Added global ban option in the config. This will ban Pokémon like plushies globally.
- Added history command to view past actions taken by the mod.
- Added placeholders `%player%` and `%nearest%` in spawn notifications.
- The `%nearest%` placeholder now **ignores spectators and vanished players**.
- History supports databases: MongoDB. (JSON implemented — test this before using it in production.)
- Removed formula; now you can use properties (these properties are the same ones used in the `/pokegive <properties>`
  command).
- Global world blacklist.

### Bug Fixes

- Fixed notifications for non-wild Pokémon on death.
- Fixed webHooks notifications.

### Optimizations

- The configuration has been reorganized into separate sections to allow more customization.
- The mod is now more optimized, providing better overall performance.

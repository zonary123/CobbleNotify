# Changelog

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

### Bug Fixes

- Fixed notifications for non-wild Pokémon on death.

### Optimizations

- The configuration has been reorganized into separate sections to allow more customization.
- The mod is now more optimized, providing better overall performance.

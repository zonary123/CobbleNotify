# Cobblehunt

## Description

Cobblehunt is a minigame plugin that allows players to catch custom pokemons in the world. The plugin is inspired by the
popular game Pokemon Go. Players can catch pokemons by throwing pokeballs at them. The game features different
categories
for a varied and engaging gameplay experience. The plugin is designed to be lightweight and easy to use. It is also
highly
configurable, allowing server owners to customize the game to their liking.

## Custom Pokemons

In Cobblehunt, you have the ability to create custom Pokemons. These custom Pokemons can have unique characteristics and
abilities that set them apart from the standard Pokemons in the game.

However, it's important to note that custom Pokemons with a Pokedex number of 9999 will not be counted in the game. This
means they will not contribute to your total Pokemon count, and they will not be considered in any game calculations
that involve the number of Pokemons you have.

If you want your custom Pokemons to be counted in the game, they should not have a Pokedex number of 9999. Instead,
assign them a unique Pokedex number that is not currently in use.

Additionally, if you want your custom Pokemons to be able to spawn in the game world, you will need to configure their
spawn settings. This includes setting the locations where they can spawn, the conditions under which they spawn, and the
frequency of their spawns.

## Hunts

Cobblehunt offers various types of hunts to cater to different play styles:

- **Normal (Individual)**: This is the standard mode where players catch pokemons using pokeballs.
- **Global**: In this mode, all players participate in the hunt. Rewards are given based on the number of pokemons
  caught.
- **Party**: This mode allows a group of players to hunt together.
- **Legendary**: This mode features legendary pokemons for an added challenge.
- **Shiny**: In this mode, players can hunt for shiny pokemons.

## Experience

Experience points are awarded based on the rewards from the hunts. (Note: We are still deciding whether the experience
points should be awarded to the user or the pokemon.)

## Rewards

Rewards are given for successful hunts. We are considering whether to give rewards automatically or require players to
click on the rewards.

## Options

Cobblehunt offers various configuration options for players for the hunts:

- **AutoRewards**: This option allows rewards to be given automatically.
- **AutoExp**: This option allows experience points to be awarded automatically.
- **Messages**: This option allows you to customize the messages for all the hunts.

## Config

The config file for Cobblehunt is located at `config/cobblehunt/config.json`.

```json
{
  "lang": "en",
  "prefix": "§6[§cWondertrade§6] §f",
  "economycommand": "eco give %player% %amount%",
  "title": "§6Wondertrade",
  "maxivs": 25,
  "coincidences": 0,
  "rarity": {
    "commonPokemonRarity": 7.0,
    "uncommonPokemonRarity": 2.5,
    "rarePokemonRarity": 0.3,
    "legendaryPokemonRarity": 0.1
  },
  "levelupgrades": true,
  "levelupgrade": {
    "categoryteamlevel": 0,
    "categorygloballevel": 0,
    "categorylegendarylevel": 0,
    "categoryshinylevel": 0
  },
  "activecategoryteam": true,
  "activecategoryglobal": true,
  "activecategorylegendary": true,
  "activecategoryshiny": true,
  "rewardslevelisalwayssame": true,
  "pokeblacklist": [
    "magikarp"
  ]
}
```

The `coincidences` field in the configuration file represents the number of attributes of a captured Pokemon that must
match certain criteria. These attributes can include the Pokemon's IVs (Individual Values), gender, ability, and nature.
The maximum value for `coincidences` is 4, indicating that all four attributes must match the specified criteria.

Here's a brief explanation of each attribute:

- **IVs (Individual Values)**: These are hidden stats that affect how good a Pokemon's other stats can potentially be.
- **Gender**: The gender of the Pokemon.
- **Ability**: The special skill or characteristic that the Pokemon has.
- **Nature**: This affects the growth of the Pokemon's stats.

If `coincidences` is set to 4, then the captured Pokemon must have the same IVs, gender, ability, and nature as
specified in the criteria for it to be considered a match.

The `rarity` field in the configuration file is used to set the rarity levels for different types of Pokemon. These
rarity levels can influence the rewards given to players when they catch these Pokemon. Here's a brief explanation of
each field:

- **commonPokemonRarity**: This sets the rarity level for common Pokemon. The higher the value, the more common these
  Pokemon are.
- **uncommonPokemonRarity**: This sets the rarity level for uncommon Pokemon. The higher the value, the more uncommon
  these Pokemon are.
- **rarePokemonRarity**: This sets the rarity level for rare Pokemon. The higher the value, the rarer these Pokemon are.
- **legendaryPokemonRarity**: This sets the rarity level for legendary Pokemon. The higher the value, the more legendary
  these Pokemon are.

The rewards given to players can be configured based on these rarity levels. For example, catching a legendary Pokemon
could yield higher rewards due to its high rarity level.

## Commands

## Permissions

## Configuration

## Dependencies

- [Cobblemon](https://modrinth.com/mod/cobblemon) (v1.5.0)
- [GooeyLibs](https://modrinth.com/mod/gooeylibs) (v3.0.0)


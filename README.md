# Quozul's Survival Games
This plugin is a mix bewteen UHC, Survival Games (or Hunger Games) and KTP (french UHC).
The plugin is currently only in French, will be translated asap.

## Features
- Game basics
    - World border
    - Teams
    - Random spawn within the border
    - New world

## Usage
- Generate a new world with `/regenerateworlds`
- Join a team with `/jointeam <Color>` (example: `/jointeam Rouge`)
- Start a game (as an op player) with `/start`
- Kill everyone

## To-do
- Fix `/start` when less than 2 players are in teams
- Cool down before starting game
- Random loot chests on map (as colored shulker boxes with in-game loot tables)
- Handle game logic with custom events
- Pre-generate worlds to reduce lag
- Add a bossbar to indicate position of player
- Fix border damage (damage buffer?)
- Exponential damage for world border
- Reduce border to a radius of 0
- Display coordinates of player on the side bar or on bossbar (as bossbar's name)
- Change bossbar's name to border radius
- Change `/start <time> <radius>` (override config, optional parameters)
- Config file
- Create own teams `/newteam <name> <color>`, team will be removed when everyone leaves it
- Async world generation?
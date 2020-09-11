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
- Start a game (as an op player) with `/start <time> <radius>` (override config, optional parameters)
- Kill everyone
- Random loot chests on map (as colored shulker boxes with in-game loot tables)
- Add a bossbar to indicate position of player
- Display coordinates of player on bossbar name
- Change bossbar's name to border radius
- Cool down before starting game

## To-do
- Fix `/start` when less than 2 players are in teams
- Pre-generate worlds to reduce lag
- Exponential damage for world border
- Display coordinates of player on the side bar
- Create own teams `/newteam <name> <color>`, team will be removed when everyone leaves it
- ~~Async world generation?~~
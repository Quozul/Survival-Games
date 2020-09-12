# Quozul's Survival Games
This plugin is a mix bewteen UHC, Survival Games (or Hunger Games) and KTP (french UHC).
The plugin is currently only in French, will be translated asap.

## Usage
- Generate a new world with `/regenerateworlds`
> A world gets created on server startup if not present
- Join a team with `/jointeam <team name>` (example: `/jointeam red`)
> Or you can give your player the permission for the Minecraft's default command `/team join <team name>`, the above command does not require additional permission and uses the Minecraft teams.  
> You can create a team using `/team add <team name>`
- Start a game (as an op player) with `/start <time> <radius>` (override config, optional parameters)
- Kill everyone

## Features
- Game basics
    - World border
    - Random spawn within the border
    - Uses a world for the game that can be regenerated
    - Teams
- Random loot chests on map (as colored shulker boxes with in-game loot tables)
    - Display distance to player from chest
- Bossbars that indicates...
    - the x/z position of the player and relative to the border's radius
    - the radius of the border
- Cool down before starting game

## To-do
- Fix `/start` when less than 2 players are in teams
- Pre-generate worlds to reduce lag
- Create own teams `/newteam <team name> <color>`, team will be removed when everyone leaves it
> Or give player the permission for the command `/team add <team name>`
- Dead players can _sponsors_ (like in the Hunger Games movie) alive players by spending kills they made during the current game (give random item from a loot table)
- Scoreboard for won, played and list games
- Add a `/wanttoplay` command, start countdown when 2 or more players ran the command and has a team
- Add a GUI menu for team management

- ~~Async world generation?~~
# RTPFarm

A Paper/Spigot plugin with a full farmworld system:
- Random teleport on join
- Farmworld selection menu
- Tree break optimization
- Automatic daily reset (2:00 AM by default)

## Features
- `/rtp [farmworld1|farmworld2]`
- `/farmworld` opens build menu
- `/farmworld tp <farmworld1|farmworld2>`
- `/farmworld reload`
- `/farmworld reset`
- Farmworld 1: no structures + new chunk sanitizing for only oak/birch/spruce wood and coal/iron/copper ores
- Farmworld 2: normal structures enabled + reset + RTP
- Permissions:
  - `rtpfarm.use`
  - `rtpfarm.admin`
  - `rtpfarm.bypasscooldown`

## Install
1. Copy the jar into your server `plugins/` folder.
2. Start/restart the server.
3. Edit `plugins/RTPFarm/config.yml` as needed.
4. Run `/farmworld reload` or restart.

## Commands
- `/rtp [farmworld1|farmworld2]`
- `/farmworld`
- `/farmworld tp <farmworld1|farmworld2>`
- `/farmworld reload`
- `/farmworld reset`

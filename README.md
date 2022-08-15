<img src="https://i.imgur.com/40cLW6Q.png" alt="Icon" width="64" height="64" />

## Fluidlogged

**REQUIRES:** [Fabric Loader](https://fabricmc.net/), [Fabric API](https://modrinth.com/mod/fabric-api) and [MidnightLib](https://modrinth.com/mod/midnightlib)

Allows all waterloggable blocks to be lavalogged and also logged by any other fluids from other mods.

**IMPORTANT:**

This mod can cause issues with other mods. If you experience any crashes or other issues after installing this mod, try enabling compatibility mode in the config. This should fix most issues but will disallow waterloggables from other mods to be logged with any other fluid.

<details><summary>Known incompatibilities</summary>

- Origins
- Very Many Players (vmp)

</details>
<details><summary>Adding fluids to the config</summary>

Due to the mod loading order, fluids from other mods have to be defined inside the config.
In there you find a section named fluids. Simply add the fluid id to this list but consider using still fluids instead of flowing fluids.

If you don't know the ids of the those fluids, you can enable "printFluidIds". This will print the ids into the output log after the game has finished loading.

#### Example with the Create mod

![example](https://i.imgur.com/vM6q0gf.png)

</details>
<details><summary>FAQ</summary>

#### Will this corrupt my previous worlds?

No, but let me know if there is one. Keep in mind that changing the config afterward or removing the mod will cause fluids inside blocks may disappear or get mixed up with other ones.

#### Forge?

[click here.](https://www.curseforge.com/minecraft/mc-mods/fluidlogged-forge)

#### Can I use it for my modpack?

Sure.

</summary>

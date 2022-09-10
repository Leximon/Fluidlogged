<img src="https://i.imgur.com/40cLW6Q.png" alt="Icon" width="64" height="64" />

## Fluidlogged

**REQUIRES:** [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://modrinth.com/mod/fabric-api).

Allows all waterloggable blocks to be lavalogged and also logged by any other fluids from other mods.

**IMPORTANT:**

This mod can cause issues with other mods. If you experience any crashes or other issues after installing this mod, try enabling compatibility mode in the config. This should fix most issues but will disallow waterloggables from other mods to be logged with any other fluid.

<details><summary>Known incompatibilities</summary>

- Origins
- Very Many Players (vmp)

</details>
<details><summary>Adding custom fluids to the config</summary>
This can be achieved by opening the configuration menu using [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu).
Alternatively, you can edit the `fluidlogged.json` file in the config folder.

</details>
<details><summary>FAQ</summary>

#### Will this corrupt my previous worlds?

No, but let me know if there is one. Keep in mind that changing the config afterward or removing the mod will cause fluids inside blocks may disappear or get mixed up with other ones.

#### Forge?

[click here.](https://www.curseforge.com/minecraft/mc-mods/fluidlogged-forge)

#### Can I use it for my modpack?

You can.

</details>

## Developers
This mod provides a small API for mod developers to allow their fluids to be added by default to the fluidlogged list.<br>
To use it, add the property `fluidlogged` inside the `custom` object of your `fabric.mod.json` file. This must target a json file.<br>
Example:
```json
"custom": {
    "fluidlogged": "fluidlogged.mod.json"
}
```
The fluid ids belong into the `fluidloggeed.mod.json` file.
Here is an example:
```json
{
  "fluids": [
    "create:honey",
    "create:chocolate"
  ]
}
```

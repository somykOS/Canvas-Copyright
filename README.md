## Canvas Copyright

This mod is designed to note the author of Canvas from [PolyDecorations](https://modrinth.com/mod/polydecorations). <br>
Allows to disable canvas copying.

---

#### Default config values

```yml
# Up to 5 players can be displayed in a lore
displayAuthorsLore: true

# Nobody can make a copy of a canvas (except authors if 'authorsCanCopy' is 'true')
disableCopy: true
authorsCanCopy: true
```

To use `/canvas add <player>` or `/canvas remove <player>` you need to be main author (the first who modified canvas) or have the `canvas-copyright.add-author`, `canvas-copyright.remove-author` permissions. <br>

This mod uses [fabric-permission-api](https://github.com/lucko/fabric-permissions-api/). <br>
To manage these permission, you can use [LuckPerms](https://modrinth.com/mod/luckperms) or any other mod that can be used in this way. <br>
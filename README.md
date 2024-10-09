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
Also you can allow anybody to copy your canvas with `/canvas to-public` command. After transferring canvas to public domain it can be crafted by crafter.

This mod uses [fabric-permission-api](https://github.com/lucko/fabric-permissions-api/). <br>
To manage these permission, you can use [LuckPerms](https://modrinth.com/mod/luckperms) or any other mod that can be used in this way. <br>

---
You can visit my little [contact card](https://somykos.github.io/web/), <br>
And you are welcome to support me via the following links:<br>
<a href="https://ko-fi.com/somyk">
<img src="https://raw.githubusercontent.com/somykOS/web/c03742bd86ca2ce0f6f39bcd3cfe683ad98926a2/public/external/kofi_s_logo_nolabel.svg" alt="ko-fi" width="100"/>
</a>
<a href="https://send.monobank.ua/jar/8RCzun35pC">
<img src="https://raw.githubusercontent.com/somykOS/web/5ac2e685429eb0cc369dc220ce3b93d2a22893c0/public/external/monobank_logo.svg" alt="monobank" width="80"/>
</a>
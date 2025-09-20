# 📊 TrialPoll

TrialPoll is a modern and lightweight Minecraft plugin that allows server owners to create and manage polls directly in-game.  
Players can vote, view results, and interact with polls through a clean and intuitive interface.

---

## ✨ Features

- Create polls with custom durations (`30s`, `1m`, `5m`, `1h`, `1d`)  
- View and manage poll results in-game  
- Permission-based command access  
- Smart autocomplete for commands based on player permissions  
- Fully localized messages

---

## ❇️ Images

![Poll Menu Preview](https://i.imgur.com/7Detndf.png)
![Poll Menu Preview](https://i.imgur.com/nXmOrT8.png)
![Poll Menu Preview](https://i.imgur.com/bq6kOzI.png)
![Poll Menu Preview](https://i.imgur.com/MPV639Z.png)


---

## 📦 Installation

1. Download the latest `.jar` from the [Releases](../../releases) page.  
2. Place the `.jar` in your server's `plugins/` folder.  
3. Restart your server.  

---

## 🛠 Commands

| Command                          | Permission            | Description                       |
|---------------------------------|----------------------|-----------------------------------|
| `/poll`                          | `trialpoll.use`      | Opens the poll menu               |
| `/poll results`                  | `trialpoll.results`  | View poll results                 |
| `/poll close <id>`               | `trialpoll.close`    | Close an active poll              |
| `/poll remove <id>`              | `trialpoll.remove`   | Remove a poll                     |
| `/createpoll <time> <question>`  | `trialpoll.create`   | Create a new poll                 |

---

## 🔒 Permissions

- `trialpoll.use` → Open polls  
- `trialpoll.results` → View results  
- `trialpoll.close` → Close polls  
- `trialpoll.remove` → Remove polls  
- `trialpoll.create` → Create new polls  

---

## 👨‍💻 Development

- Compatible with **Spigot/Paper 1.21 – 1.21.8** (Tested versions)  
- Licensed under **MIT License**

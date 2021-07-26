import json
from pathlib import Path

class PackEntry:
    def __init__(self, pathstring, key):
        self.url = pathstring
        self.key = key

    def __str__(self):
        return "entry={url: " + self.url + ", key: " + self.key + "}"

class Packer:

    def __init__(self, name, path, defaultType):
        self.json = { }
        self.name = name
        self.json[name] = {
                "files": [],
                "path": path,
                "defaultType": defaultType
                }

    def addEntry(self, entry):
        self.json[self.name]['files'].append(entry)

    def saveToJson(self):
        with open(f"./{self.name}.json", "w") as f:
            json.dump(self.json, f)


generalUIdir = Path('./general-interface')
generalUI = Packer("general-ui",
        "assets/images/general-interface",
        "image")

battleUIdir = Path('./battle-interface')
battleUI = Packer("battle-ui",
        "assets/images/battle-interface",
        "image")


for f in generalUIdir.glob("*.png"):
    entry = PackEntry(f.name, f.stem)
    generalUI.addEntry(entry.__dict__)

generalUI.saveToJson()

for f in battleUIdir.glob("*.png"):
    entry = PackEntry(f.name, f.stem)
    battleUI.addEntry(entry.__dict__)

battleUI.saveToJson()

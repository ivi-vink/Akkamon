import { client } from '../../app';
import { baseStack, Queue, queueFromArray } from '../DataWrappers';
import type { BasePhaserScene, GConstructor } from '../PhaserTypes';
import type { Direction } from '../render/Direction';
import { AkkamonMenu, Dialogue, Menu, MenuText, Picker } from './UIElement';
import type { WorldScene } from './WorldScene';


export default class BattleScene extends Phaser.Scene {

    menus = baseStack<AkkamonMenu>();
    dialogue?: BattleDialogue;

    constructor() {
        super('BattleScene')
    }

    create() {
        console.log("Creating Battle Scene");
        let initialDialogue = new BattleDialogue(
            this,
            new Phaser.GameObjects.Group(this),
            10);
        this.pushMenu(initialDialogue);

        initialDialogue!.setDialogueQueue(queueFromArray([
            "hello",
            "Bye"
        ]));

        client.requestInitBattle(this);
    }

    update(time: number, delta: number) {
        client.updateBattle(delta);
    }

    pushMenu(menu: AkkamonMenu) {
        this.menus.push(menu);
        this.menuTakesUIControl(this.input, menu);
        console.log("New menu stack:");
        console.log(this.menus);
    }

    popMenu() {
        return this.menus.pop();
    }

    peekMenu() {
        return this.menus.peek();
    }


    traverseMenusBackwards() {
        this.popMenu();
        if (this.menus.size() > 1) {
            this.menuTakesUIControl(this.input, this.menus.peek()!);
            this.menus.peek()!.setMenuVisible(true);
        }
        console.log("menu stack after traversing back:");
        console.log(this.menus);
    }

    menuTakesUIControl(input: Phaser.Input.InputPlugin, menu: AkkamonMenu): void {
        client.setBattleControls(input, menu);
    }
}

class BattleDialogue extends Dialogue {
    battleScene: BattleScene

    constructor(scene: BattleScene, group: Phaser.GameObjects.Group, depth: number) {
        super(scene, group, depth)
        this.battleScene = scene;
    }

    setDialogueQueue(queue: Queue<string>) {
        this.messageQueue! = queue;
        this.displayNextDialogue();
    }

    confirm() {
        if (this.messageQueue.isEmpty()) {
            this.battleScene.pushMenu(new BattleOptions(
                this.battleScene
            ));
        }
        this.displayNextDialogue();
    }

}

enum BattleOptionsPos {
    TOP_RIGHT = "PKMN",
    TOP_LEFT = "FIGHT",
    BOT_RIGHT = "ITEM",
    BOT_LEFT = "RUN"
}

class BattleOptions extends Phaser.GameObjects.Image implements AkkamonMenu {
    battleScene: BattleScene;
    group: Phaser.GameObjects.Group;
    camera: Phaser.Cameras.Scene2D.Camera;
    buttons: Map<string, MenuText>;
    groupDepth?: number;

    constructor(scene: BattleScene) {
        let camera = scene.cameras.main;
        super(scene, camera.scrollX + camera.width, camera.scrollY + camera.height, "options-dialogue")
        this.setOrigin(1,1)
        this.setVisible(true)
        this.setDisplaySize(0.56 * camera.width, 0.28 * camera.width);



        this.battleScene = scene;
        this.group = new Phaser.GameObjects.Group(scene);
        this.camera = camera;


        this.group = new Phaser.GameObjects.Group(scene);

        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Map();
        this.groupDepth = 20;
        this.setDepth(this.groupDepth);

        this.setButtons();
        this.setPicker(BattleOptionsPos.TOP_LEFT);

    }

    setButtons() {
        for (let pos in BattleOptionsPos) {
            let x;
            let y;

            switch (pos) {
                case BattleOptionsPos.TOP_LEFT:
                    break;
                case BattleOptionsPos.TOP_RIGHT:
                    break;
                case BattleOptionsPos.BOT_LEFT:
                    break;
                case BattleOptionsPos.BOT_RIGHT:
                    break;

            }

            this.buttons.set(
                pos,
                new MenuText(
                    this.battleScene,
                    this.group,
                    this.groupDepth!,
                    this.x,
                    this.y,
                    pos
                )
            )
        }
    }

    selectButton() {
    }

    destroyAndGoBack() {
    }

    confirm() {
    }

    destroyGroup() {
    }

    setMenuVisible() {
    }

    setPicker(position: BattleOptionsPos) {
    }
}

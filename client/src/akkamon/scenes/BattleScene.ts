import { client } from '../../app';
import { baseStack, Queue, queueFromArray } from '../DataWrappers';
import type { BasePhaserScene, GConstructor } from '../PhaserTypes';
import { Direction } from '../render/Direction';
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

enum BattleOptionsButtons {
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

    buttonSpacing: number;

    picker?: Picker;
    pickerPosition?: BattleOptionsButtons;

    paddingX: number;
    paddingY: number;

    constructor(scene: BattleScene) {
        let camera = scene.cameras.main;
        super(scene, camera.scrollX + camera.width, camera.scrollY + camera.height, "options-dialogue")
        this.setOrigin(1,1)
        this.setVisible(true)
        this.setDisplaySize(0.56 * camera.width, 0.28 * camera.width);


        this.paddingX = 120;
        this.paddingY = 60;
        this.buttonSpacing = 30;

        this.battleScene = scene;
        this.group = new Phaser.GameObjects.Group(scene);
        this.camera = camera;

        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Map();
        this.groupDepth = 20;
        this.setDepth(this.groupDepth);

        this.setButtons();

        this.picker = new Picker(
            this.scene,
            this.group,
            0,
            0,
            "menupicker"
        );
        this.picker.setDepth(this.groupDepth + 1);

        this.setPicker(BattleOptionsButtons.TOP_LEFT);

    }

    setButtons() {
        for (let pos in BattleOptionsButtons) {

            const position: BattleOptionsButtons = BattleOptionsButtons[pos as keyof typeof BattleOptionsButtons];
            var x = this.x;
            var y = this.y;

            //console.log(pos)
            switch (position) {
                case BattleOptionsButtons.TOP_LEFT:
                    x = x - this.displayWidth + this.paddingX;
                    y = y - this.displayHeight + this.paddingY;
                    break;
                case BattleOptionsButtons.TOP_RIGHT:
                    x = x - this.paddingX;
                    y = y - this.displayHeight + this.paddingY;
                    break;
                case BattleOptionsButtons.BOT_LEFT:
                    x = x - this.displayWidth + this.paddingX;
                    y = y - this.paddingY;
                    break;
                case BattleOptionsButtons.BOT_RIGHT:
                    x = x - this.paddingX;
                    y = y - this.paddingY;
                    break;
            }

            let text = new MenuText(
                    this.battleScene,
                    this.group,
                    this.groupDepth!,
                    x,
                    y,
                    position
                )
            text.setOrigin(0.5, 0.5);

            console.log(position);
            console.log(x);
            console.log(y);
            this.buttons.set(
                position,
                text
            )
        }
    }

    setPicker(position: BattleOptionsButtons) {
        let button = this.buttons.get(position);
        this.pickerPosition = position;

        console.log(button);
        this.picker!.setPosition(
            button!.x - this.buttonSpacing,
            button!.y
        );

    }

    selectButton(direction: Direction) {
        this[direction]();
    }

    [Direction.NONE]() {
    }

    [Direction.LEFT]() {
        switch (this.pickerPosition) {
            case BattleOptionsButtons.TOP_RIGHT:
                this.setPicker(BattleOptionsButtons.TOP_LEFT);
                break;
            case BattleOptionsButtons.BOT_RIGHT:
                this.setPicker(BattleOptionsButtons.BOT_LEFT);
                break;
        }
    }

    [Direction.RIGHT]() {
        switch (this.pickerPosition) {
            case BattleOptionsButtons.TOP_LEFT:
                this.setPicker(BattleOptionsButtons.TOP_RIGHT);
                break;
            case BattleOptionsButtons.BOT_LEFT:
                this.setPicker(BattleOptionsButtons.BOT_RIGHT);
                break;
        }
    }

    [Direction.UP]() {
        switch (this.pickerPosition) {
            case BattleOptionsButtons.BOT_RIGHT:
                this.setPicker(BattleOptionsButtons.TOP_RIGHT);
                break;
            case BattleOptionsButtons.BOT_LEFT:
                this.setPicker(BattleOptionsButtons.TOP_LEFT);
                break;
        }
    }

    [Direction.DOWN]() {
        switch (this.pickerPosition) {
            case BattleOptionsButtons.TOP_RIGHT:
                this.setPicker(BattleOptionsButtons.BOT_RIGHT);
                break;
            case BattleOptionsButtons.TOP_LEFT:
                this.setPicker(BattleOptionsButtons.BOT_LEFT);
                break;
        }
    }

    destroyGroup() {
        this.group!.destroy(true);
    }

    destroyAndGoBack() {
        this.battleScene.traverseMenusBackwards();
        this.destroyGroup();
    }

    confirm() {
        this[this.pickerPosition!]();
    }

    [BattleOptionsButtons.TOP_RIGHT]() {
    }

    [BattleOptionsButtons.TOP_LEFT]() {
        // fight menu
    }

    [BattleOptionsButtons.BOT_LEFT]() {
    }

    [BattleOptionsButtons.BOT_RIGHT]() {
    }

    setMenuVisible() {
    }
}

import { client } from '../../app';
import type { Mon } from '../client/IncomingEvents';
import { baseStack, Queue, queueFromArray } from '../DataWrappers';
import type { BasePhaserScene, GConstructor } from '../PhaserTypes';
import type { BattleUIEvent, DialogueUIEvent, InstantUIEvent } from '../render/BattleEngine';
import { BattleDialogue, MonInterface, OpponentInterface, PlayerInterface } from '../render/battleUI';
import { Direction } from '../render/Direction';
import type { AkkamonMenu, Dialogue, Menu, MenuText, Picker } from './UIElement';
import type { WorldScene } from './WorldScene';


export default class BattleScene extends Phaser.Scene {

    menus = baseStack<AkkamonMenu>();
    dialogueBox?: BattleDialogue;
    busy = false;

    playerSprites: Phaser.GameObjects.Image[] = [];
    monInterfaces: Map<string, MonInterface> = new Map();

    constructor() {
        super('BattleScene')
    }

    create() {
        console.log("Creating Battle Scene");
        this.dialogueBox = new BattleDialogue(
            this,
            new Phaser.GameObjects.Group(this),
            10);
        client.battleEngineSceneRegister(this);
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

    pushEvents(events: DialogueUIEvent[], endBehavior: () => void, before?: InstantUIEvent) {
        this.toggleVisible(false);
        this.menuTakesUIControl(this.input, this.dialogueBox!);
        if (before) {
            this.busy = true;
            before.callback();
        }
        this.dialogueBox!.pushEvents(
            events,
            endBehavior
        );
    }

    toggleVisible(value: boolean) {
        for (let i = 0 ; i < this.menus._data.length ; i++) {
            this.menus._data[i].setMenuVisible(value)
        }
    }

    pushUIEvent(event: () => void) {
        client.pushUIEvent(event);
    }

    isBusy() {
        return this.busy;
    }

    showPlayerSpritesAndBalls() {
        this.showPlayerSprites();
        this.showBalls();
    }

    showPlayerSprites() {
        let playerBack = this.add.image(0,0, "playerBack")
        .setOrigin(0, 1)
        .setPosition(0 + 100, (1 - 0.28) * this.cameras.main.displayHeight - 20)
        .setDisplaySize(200, 200)

        let opponentFront = this.add.image(0,0, "opponentFront")
        .setOrigin(1, 0.5)
        .setPosition(this.cameras.main.displayWidth - 100, 150)
        .setDisplaySize(200, 200)

        this.playerSprites.push(playerBack);
        this.playerSprites.push(opponentFront);
    }

    showBalls() {

    }

    removePlayerSprites() {
        for (let sprite of this.playerSprites) {
            sprite.destroy();
        }
    }

    showPlayerMonInterface(trainer: string, monObj: Mon) {
        this.monInterfaces.set(trainer,
                               new PlayerInterface(
                                   this,
                                   monObj,
                                   "hp-player",
                                   {x: this.cameras.main.displayWidth - 20, y: (1 - 0.28) * this.cameras.main.displayHeight - 400 * 0.4 / 1.5},
                                   {x: 1, y: 0.5},
                                   0.4
                               ));
    }

    showOpponentMonInterface(trainer: string, monObj: Mon) {
        this.monInterfaces.set(trainer,
                               new OpponentInterface(
                                   this,
                                   monObj,
                                   "hp-opponent",
                                   {x: 30, y: 30},
                                   {x: 0, y: 0},
                                   0.28
                               ));

    }

    clearDialogue() {
        this.dialogueBox!.clearText();
    }
}

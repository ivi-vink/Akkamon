import { client } from '../../app';
import { baseStack, Queue, queueFromArray } from '../DataWrappers';
import type { BasePhaserScene, GConstructor } from '../PhaserTypes';
import type { BattleUIEvent, DialogueUIEvent } from '../render/BattleEngine';
import { BattleDialogue } from '../render/battleUI';
import { Direction } from '../render/Direction';
import type { AkkamonMenu, Dialogue, Menu, MenuText, Picker } from './UIElement';
import type { WorldScene } from './WorldScene';


export default class BattleScene extends Phaser.Scene {

    menus = baseStack<AkkamonMenu>();
    dialogueBox?: BattleDialogue;
    busy = false;

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

    pushEvents(events: DialogueUIEvent[], endBehavior: () => void, before?: BattleUIEvent) {
        this.toggleVisible(false);
        this.menuTakesUIControl(this.input, this.dialogueBox!);
        if (before) {
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
}

import type { Mon, Move, Stat } from "../client/IncomingEvents";
import { baseQueue, Queue } from "../DataWrappers";
import type BattleScene from "../scenes/BattleScene";
import { AkkamonMenu, Dialogue, MenuText, Picker } from "../scenes/UIElement";
import type { DialogueUIEvent } from "./BattleEngine";
import { Direction } from "./Direction";

function delay(ms: number) {
    return new Promise(resolve => {
        setTimeout(resolve, ms);
    });
}

export class BattleDialogue extends Dialogue {
    battleScene: BattleScene
    endBehaviour: () => void;
    eventQueue = baseQueue<DialogueUIEvent>();

    currentlyWriting?: string;
    timeEvent?: Phaser.Time.TimerEvent;
    timeCallback?: () => void

    waitingPrinter?: Phaser.Time.TimerEvent;

    constructor(scene: BattleScene, group: Phaser.GameObjects.Group, depth: number) {
        super(scene, group, depth)
        this.battleScene = scene;
        this.endBehaviour = () => {
            scene.pushMenu(new BattleOptions(
                scene
            ));
        };
    }

    setDialogueQueue(queue: Queue<string>, endBehaviour: any) {
        this.messageQueue! = queue;
        this.displayNextDialogue();
        this.endBehaviour = endBehaviour;
    }

    confirm() {
        console.log("UI event queue:");
        console.log(this.eventQueue);
        if (!this.isCurrentlyWriting()) {
            console.log("not currentlyWriting!");
            this.confirmNextBehavior();
        } else {
            console.log("currentlyWriting removing timed printer!");
            this.timeEvent!.destroy();
            this.timeEvent!.remove();
            this.timeCallback!();
            this.displayedText.text = this.currentlyWriting!;
            this.currentlyWriting = undefined;
        }
    }

    isCurrentlyWriting(): boolean {
        return this.currentlyWriting !==  undefined;
    }

    confirmNextBehavior() {
        if (this.eventQueue.isEmpty()) {
            this.displayedText.text = '';
            this.endBehaviour();
        } else {
            console.log("Pushing ui trigger to client update handling!");
            this.pushTriggerWhenNotBusy(() => { this.playNextEvent(); });
        }
    }

    pushTriggerWhenNotBusy(trigger: () => void) {
        // let trigger = () => {
        //     console.log("The trigger is fired!");
        //     callback();
        // }
        // console.log(trigger);

        // this.scene!.pushMen this.playNextEvent();
        this.battleScene!.pushUIEvent(
            trigger
        );
    }

    pushEvents(events: DialogueUIEvent[], endBehaviour: () => void) {
        this.endBehaviour = endBehaviour;
        this.eventQueue.pushArray(events);
        this.pushTriggerWhenNotBusy(() => { this.playNextEvent(); } );
    }

    playNextEvent() {
        this.battleScene.busy = true;
        this.displayedText.text = '';
        if (!this.eventQueue.isEmpty()) {
            let uiEvent = this.eventQueue.pop()!;
            console.log("Playing event now:");
            console.log(uiEvent);
            this.timeEvent = this.typewriteText(uiEvent.dialogue, uiEvent.callback, () => {this.currentlyWriting = undefined;});
            this.timeCallback = uiEvent.callback;
            this.currentlyWriting = uiEvent.dialogue;
        }
    }

    clearText() {
        this.displayedText.text = '';
    }

    async waitUntilBattleEvent() {
        await delay(1000);
        this.displayedText.text = '';
        let callback = () => {this.waitUntilBattleEvent();}

        let scene = this.battleScene;

        this.typewriteText("Waiting until next battle event...", () => {
            if (scene.isBusy()) {
                console.log("Printing wait message again!");
                callback();
            } else {
                console.log("Not printing wait message again, scene not busy?!");
                this.displayedText.text = '';
            }
        }, () => {});
    }
}

enum BattleOptionsButtons {
    TOP_RIGHT = "PKMN",
    TOP_LEFT = "FIGHT",
    BOT_RIGHT = "ITEM",
    BOT_LEFT = "RUN"
}

export class BattleOptions extends Phaser.GameObjects.Image implements AkkamonMenu {
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
        this.buttonSpacing = 45;

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
        this.battleScene.pushMenu(new MovesOptions(
            this.battleScene
        ));
    }

    [BattleOptionsButtons.BOT_LEFT]() {
    }

    [BattleOptionsButtons.BOT_RIGHT]() {
    }

    setMenuVisible() {
    }

}

export enum MoveSlot {
    FIRST = "FIRST",
    SECOND = "SECOND",
    THIRD = "THIRD",
    FOURTH = "FOURTH"
}

class MoveDescription extends Phaser.GameObjects.Image {
    battleScene: BattleScene;
    group: Phaser.GameObjects.Group;
    moves: {
        [slot in MoveSlot]: Move
    }
    moveType: MenuText;
    PP: MenuText;

    constructor(
        scene: BattleScene,
                group: Phaser.GameObjects.Group,
                groupDepth: number,
                pos: {x: number, y: number},
                size: {width: number, height: number},
                ori: {x: number, y: number},
                moves: {[slot in MoveSlot]: Move},
    ) {
        super(scene, pos.x, pos.y, "move-type-info");
        this.setDisplaySize(size.width, size.height);
        this.setOrigin(ori.x, ori.y);
        this.battleScene = scene;
        this.scene.add.existing(this);
        this.group = group;
        this.group.add(this);
        this.setDepth(groupDepth);

        this.moves = moves;

        let topleft = {x: this.x - this.displayWidth, y: this.y - this.displayHeight}
        let typeHeader = new MenuText(
            scene,
            group,
            groupDepth,
            topleft.x + 40, topleft.y + 40,
            "TYPE/"
        );

        this.moveType = new MenuText(
            scene,
            group,
            groupDepth,
            topleft.x + this.displayWidth / 2 - 40,
            topleft.y + this.displayHeight / 2 - 20,
            ""
        );

        this.PP = new MenuText(
            scene,
            group,
            groupDepth,
            this.x - 140, this.y - 70,
            ""
        );

    }

    setDescribedMove(slot: MoveSlot) {
        let move = this.moves[slot];
        this.moveType.text = move.type.name;
        this.PP.text = move.PP.effective + " / " + move.PP.base;
    }
}

export class MovesOptions extends Phaser.GameObjects.Image implements AkkamonMenu {
    battleScene: BattleScene;
    group: Phaser.GameObjects.Group;
    camera: Phaser.Cameras.Scene2D.Camera;
    buttons: Map<MoveSlot, MenuText>;
    groupDepth?: number;

    mon: Mon;

    paddingX: number;
    paddingY: number;


    movesSpacing: number;
    buttonSpacing: number;

    picker?: Picker;
    pickerPosition?: MoveSlot;

    moveDescription: MoveDescription

    constructor(scene: BattleScene) {
        let camera = scene.cameras.main;
        super(scene,
              camera.scrollX + camera.width,
              camera.scrollY + camera.height,
              "moves-dialogue")

        this.mon = scene.getActiveMon();

        this.setOrigin(1,1)
        this.setVisible(true)
        let height = 0.28 * camera.width;
        this.setDisplaySize((2.78) * height, height);

        this.paddingX = 100;
        this.paddingY = 50;

        this.battleScene = scene;
        this.group = new Phaser.GameObjects.Group(scene);
        this.camera = camera;

        this.buttonSpacing = 40;
        this.movesSpacing = 40;

        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Map();
        this.groupDepth = 30;
        this.setDepth(this.groupDepth);
        this.group.setDepth(30);

        this.setButtons(this.mon);

        this.picker = new Picker(
            this.scene,
            this.group,
            0,
            0,
            "menupicker"
        );
        this.picker.setDepth(this.groupDepth + 1);

        this.moveDescription = new MoveDescription(
            this.battleScene,
            this.group,
            this.groupDepth,
            {x: this.x - this.displayWidth * (1 - (530/1191.8)), y: this.y - this.displayHeight * (1 - (55 / 432))},
            {width: (560 + 306) / 1191.8 * this.displayWidth, height: (560 + 306) / 1191.8 * this.displayWidth * 0.378},
            {x: 1, y: 1},
            this.mon.moves
        );

        this.setPicker(MoveSlot.FIRST);

    }

    setPicker(position: MoveSlot) {
        let button = this.buttons.get(position);
        this.pickerPosition = position;

        console.log(button);
        this.picker!.setPosition(
            button!.x - this.buttonSpacing,
            button!.y
        );
        this.moveDescription.setDescribedMove(position);
    }

    setMenuVisible() {
    }

    destroyGroup() {
        this.group!.destroy(true);
    }

    destroyAndGoBack() {
        console.log("Destroy and go back of: ");
        console.log(this);
        this.battleScene.traverseMenusBackwards();
        this.destroyGroup();
    }

    confirm() {
        this[this.pickerPosition!]();
    }

    [MoveSlot.FIRST]() {
        this.battleScene.makeFightBattleRequest(this.mon.moves[MoveSlot.FIRST]);
    }
    [MoveSlot.SECOND]() {
        this.battleScene.makeFightBattleRequest(this.mon.moves[MoveSlot.SECOND]);
    }
    [MoveSlot.THIRD]() {
        this.battleScene.makeFightBattleRequest(this.mon.moves[MoveSlot.THIRD]);
    }
    [MoveSlot.FOURTH]() {
        this.battleScene.makeFightBattleRequest(this.mon.moves[MoveSlot.FOURTH]);
    }

    setButtons(mon: Mon) {
        for (let pos in MoveSlot) {
            console.log("Need to set move " + pos + " for mon: ");
            console.log(mon);

            const position: MoveSlot = MoveSlot[pos as keyof typeof MoveSlot];
            var x = this.x - this.displayWidth + this.paddingX;
            var y = this.y - this.displayHeight;

            //console.log(pos)
            switch (position) {
                case MoveSlot.FIRST:
                    console.log("setting " + pos);
                    y = y + this.paddingY;
                    break;
                case MoveSlot.SECOND:
                    console.log("setting " + pos);
                    y = y + this.paddingY + this.movesSpacing;
                    break;
                case MoveSlot.THIRD:
                    console.log("setting " + pos);
                    y = y + this.paddingY + 2 * this.movesSpacing;
                    break;
                case MoveSlot.FOURTH:
                    console.log("setting " + pos);
                    y = y + this.paddingY + 3 * this.movesSpacing;
                    break;
            }

            let text = new MenuText(
                    this.battleScene,
                    this.group,
                    this.groupDepth!,
                    x,
                    y,
                    mon.moves[position].name
                )
            text.setOrigin(0, 0.5);

            console.log(position);
            console.log(x);
            console.log(y);
            this.buttons.set(
                position,
                text
            )
        }
    }

    selectButton(direction: Direction) {
        this[direction]();
    }

    [Direction.NONE]() {
    }
    [Direction.UP]() {
        switch (this.pickerPosition) {
            case MoveSlot.SECOND:
                this.setPicker(MoveSlot.FIRST);
                break;
            case MoveSlot.THIRD:
                this.setPicker(MoveSlot.SECOND);
                break;
            case MoveSlot.FOURTH:
                this.setPicker(MoveSlot.THIRD);
                break;
            default:
                break;
        }
    }
    [Direction.LEFT]() {
        this.destroyAndGoBack();
    }
    [Direction.RIGHT]() {
    }
    [Direction.DOWN]() {
        switch (this.pickerPosition) {
            case MoveSlot.FIRST:
                this.setPicker(MoveSlot.SECOND);
                break;
            case MoveSlot.SECOND:
                this.setPicker(MoveSlot.THIRD);
                break;
            case MoveSlot.THIRD:
                this.setPicker(MoveSlot.FOURTH);
                break;
            default:
                break;
        }
    }

}

class HPBar extends Phaser.GameObjects.Rectangle {
    constructor(
        scene: BattleScene,
                group: Phaser.GameObjects.Group,
                groupDepth: number,
                pos: {x: number, y: number},
                size: {width: number, height: number},
                ori: {x: number, y: number},
    ) {
        super(scene, pos.x, pos.y, size.width, size.height, 0x00ff00);
        this.setOrigin(ori.x, ori.y);
        scene.add.existing(this);
        group.add(this);
        this.setDepth(groupDepth);
    }

    setHP(hp: Stat) {
    }
}

export class MonInterface extends Phaser.GameObjects.Image {

    battleScene: BattleScene;
    group: Phaser.GameObjects.Group;
    groupDepth: number;

    mon: Mon

    monName?: MenuText;
    level?: MenuText;

    hpBar?: HPBar;
    baseHP?: MenuText;
    effectiveHP?: MenuText;

    constructor(
        scene: BattleScene,
                mon: Mon,
                imageKey: string,
                pos: {x: number, y: number},
                ori: {x: number, y: number},
                aspect: number
    ) {
        super(
            scene,
            pos.x,pos.y,
            imageKey
        );
        let width = scene.cameras.main.displayWidth / 2.2;
        this.setDisplaySize(width, aspect * width);
        this.setOrigin(ori.x, ori.y);
        scene.add.existing(this);
        this.group = new Phaser.GameObjects.Group(scene);
        this.groupDepth = 10;
        this.group.setDepth(this.groupDepth);
        this.battleScene = scene;

        this.mon = mon;
    }

}

export class PlayerInterface extends MonInterface {
    constructor(scene: BattleScene,
                mon: Mon,
                imageKey: string,
                pos: {x: number, y: number},
                ori: {x: number, y: number},
                aspect: number) {
                    super(scene, mon, imageKey, pos, ori, aspect);
                    this.setMonName(mon.name);
                    this.setLevel(mon.stats.level);
                    this.setHP(mon.stats.HP);
                    this.setSprite(mon.name);
    }

    setSprite(name: string) {
        console.log("Setting back sprite for " + name);
        let image = this.scene.add.image(this.x - this.displayWidth - 200, this.y, name.toLowerCase() + "-back")
        .setDisplaySize(200,200)
        .setOrigin(0.5, 0.5)

        this.group.add(image);
    }

    setMonName(name: string) {
        this.monName = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x - this.displayWidth + 20, this.y - this.displayHeight / 2 - 20,
            name
        );
    }

    setLevel(level: number) {
        this.level = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x - this.displayWidth * (1 - 490 / 790), this.y - this.displayHeight / 2,
            level.toString()
        );
    }

    setHP(hp: Stat) {
        this.baseHP = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x - this.displayWidth * (1 - 500 / 790), this.y + this.displayHeight * ((220 - 178) / 314),
            hp.base.toString()
        );

        this.effectiveHP = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x - this.displayWidth * (1 - 320 / 790), this.y + this.displayHeight * ((220 - 178) / 314),
            hp.base.toString()
        );

        this.hpBar = new HPBar(
            this.battleScene,
            this.group,
            this.groupDepth,
            {x: this.x - this.displayWidth * (1 - 245 / 790), y: this.y - this.displayHeight / 2 * (103 / 314)},
            {width: this.displayWidth * ((731 - 237) / 790), height: this.displayHeight * ((129 - 105) / 314)},
            {x: 0, y: 0}
        );
    }
}

export class OpponentInterface extends MonInterface {
    constructor(scene: BattleScene,
                mon: Mon,
                imageKey: string,
                pos: {x: number, y: number},
                ori: {x: number, y: number},
                aspect: number) {
                    super(scene, mon, imageKey, pos, ori, aspect);
                    this.setMonName(mon.name);
                    this.setLevel(mon.stats.level);
                    this.setHP(mon.stats.HP);
                    this.setSprite(mon.name);
    }

    setSprite(name: string) {
        console.log("Setting front sprite for " + name);
        let image = this.scene.add.image(this.x + this.displayWidth + 200, this.y + this.displayHeight / 2 + 60, name.toLowerCase() + "-front")
        .setDisplaySize(200,200)
        .setOrigin(0.5, 0.5)

        this.group.add(image);
    }

    setMonName(name: string) {
        this.monName = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x + 10, this.y - 20,
            name
        );
    }

    setLevel(level: number) {
        this.level = new MenuText(
            this.battleScene,
            this.group,
            this.groupDepth,
            this.x + this.displayWidth * (289 / 788), this.y + this.displayHeight * (24 / 223) - 10,
            level.toString()
        );
    }

    setHP(hp: Stat) {
        this.hpBar = new HPBar(
            this.battleScene,
            this.group,
            this.groupDepth,
            {x: this.x + this.displayWidth * (217 / 788), y: this.y + this.displayHeight * (106 / 223)},
            {width: this.displayWidth * ((705.14 - 215.66) / 788), height: this.displayHeight * ((130.5 - 104.2) / 223)},
            {x: 0, y: 0}
        );
    }

}

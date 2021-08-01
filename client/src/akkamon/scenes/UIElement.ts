import type { WorldScene } from '../scenes/WorldScene';

import { client } from '../../app';

import { Direction } from '../render/Direction';
import {
    baseQueue,
    Queue
} from '../DataWrappers';
import type BattleScene from './BattleScene';


export class MenuText extends Phaser.GameObjects.Text {
    public static TEXT_HEIGHT: number = 16;

    constructor(scene: Phaser.Scene, group: Phaser.GameObjects.Group, groupDepth: number, x: number, y: number, text: string) {
        let style: Phaser.Types.GameObjects.Text.TextStyle = {
                fontFamily: 'Courier',
                fontSize: `${MenuText.TEXT_HEIGHT}px`,
                fontStyle: '',
                backgroundColor: undefined,
                color: '#000000',
                stroke: '#000000',
                strokeThickness: 0,
                align: 'left',  // 'left'|'center'|'right'|'justify'
        }
        super(scene, x, y, text, style);
        scene.add.existing(this);
        group.add(this);
        this.setDepth(groupDepth);

    }

}

class WrappedMenuText extends MenuText {
    constructor(scene: Phaser.Scene, group: Phaser.GameObjects.Group, groupDepth: number, x: number, y: number, text: string, wrapWidth: number) {
        super(scene, group, groupDepth, x, y, text);
        this.setStyle({
                fontFamily: 'Courier',
                fontSize: '16px',
                fontStyle: '',
                backgroundColor: undefined,
                color: '#000000',
                stroke: '#000000',
                strokeThickness: 0,
                align: 'left',  // 'left'|'center'|'right'|'justify'
                wordWrap: {
                    width: wrapWidth,
                    useAdvancedWrap: true
                }
        });
    }

    destroy() {
        super.destroy();
    }
}

export class Picker extends Phaser.GameObjects.Image {
    constructor(scene: Phaser.Scene, group: Phaser.GameObjects.Group, x: number, y: number, name: string) {
        super(scene, x, y, name);

        this.setDisplaySize(20,33);
        this.setOrigin(0.5,0.5);
        scene.add.existing(this);
        group.add(this);
    }
}

export interface AkkamonMenu {
    selectButton: (direction: Direction) => void
    destroyAndGoBack: () => void
    confirm: () => void
    destroyGroup: () => void
    setMenuVisible: (v: boolean) => void
}


export class Menu extends Phaser.GameObjects.Image implements AkkamonMenu {

    akkamonScene: WorldScene

    public group?: Phaser.GameObjects.Group;

    groupDepth?: number

    buttons?: Array<MenuText>;

    private picker?: Picker;

    index?: number;

    private camera?: Phaser.Cameras.Scene2D.Camera;

    ySpacing?: number
    yOffsetFromTop?: number
    xOffsetFromRight?: number
    pickerOffset?:number

    setMenuVisible(value: boolean) {
        this.group!.setVisible(value);
    }

    destroyGroup() {
        this.group!.destroy(true);
    }

    destroyAndGoBack() {
        this.akkamonScene.traverseMenusBackwards();
        this.destroyGroup();
    }

    confirm() {
        // communicate with client
        throw new Error('Confirm method should be present in a Menu implementation');
    }

    constructor(scene: WorldScene, imageKey: string) {
        let camera = scene.cameras.main;

        super(scene, camera.scrollX, camera.scrollY, imageKey)
        this.setOrigin(1,0)
        this.setVisible(true)
        this.setDisplaySize(296, 400)

        this.akkamonScene = scene;
        this.camera = camera;

        this.group = new Phaser.GameObjects.Group(scene);

        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Array();

        this.index = 0;

        this.ySpacing = 100;
        this.yOffsetFromTop = 40;
        this.xOffsetFromRight = 150;

        this.groupDepth = 20;
    }

    setPicker(index: number) {
        let pickerY = this.indexToYpixel(index);
        if (!this.picker) {
            let pickerX = this.x - this.displayWidth + 40;
            this.picker = new Picker(
                this.scene,
                this.group!,
                pickerX,
                pickerY,
                "menupicker")
        } else {
            this.picker.setY(
                pickerY
            );
        }
    }

    private indexToYpixel(index: number) {
        return index * this.ySpacing! + this.yOffsetFromTop! + 7 + this.y;
    }

    clearButtons() {
        for (let button of this.buttons!) {
            button.destroy();
        }
        this.buttons = new Array();
    }

    selectButton(direction: Direction) {
        if (direction === Direction.UP && this.index! !== 0) {
            this.setPicker(this.index! - 1);
            this.index! -= 1;
        } else if (direction === Direction.DOWN && this.index !== this.buttons!.length - 1) {
            this.setPicker(this.index! + 1);
            this.index! += 1;
        }
    }
}

export class PauseMenu extends Menu implements AkkamonMenu {
    constructor(scene: WorldScene) {
        super(scene, "pause-menu")
        let camera = scene.cameras.main;
        this.setPosition(this.x + camera.width, this.y);
        this.setPicker(0);
        this.setButtons([
            'POKéDEX',
            'POKéMON',
            'PHONE',
            'CLOSE'
        ]);
        this.group!.setDepth(this.groupDepth!);
    }

    setButtons(buttonTextArray: Array<string>) {
        for (let i = 0; i < buttonTextArray.length; i++) {
            this.buttons!.push(new MenuText(this.scene, this.group!, this.groupDepth!, this.x - this.xOffsetFromRight!, this.y + this.yOffsetFromTop! + i * this.ySpacing!, buttonTextArray[i]));
        }
    }


    confirm() {
        if (this.buttons![this.index!].text === 'PHONE') {
            this.akkamonScene.pushMenu(new RemotePlayerList(this.akkamonScene, this.akkamonScene.getRemotePlayerNames()));
        }
    }
}

class ListMenu extends Menu implements AkkamonMenu {
    options: Array<any>

    viewTop: number = 0;

    viewBot: number = 4;

    constructor(
        scene: WorldScene,
        options: any[]
    ) {
        super(scene, "pause-menu")
        let camera = scene.cameras.main;
        this.setPosition(this.x + camera.width, this.y);
        this.options = options;

        if (this.viewBot > this.options.length) {
            this.viewBot = this.options.length;
        }

        this.xOffsetFromRight = 210;
        this.yOffsetFromTop = 50;

        this.setPicker(0);

        this.groupDepth = 30;
        this.group!.setDepth(this.groupDepth);
    }

    setButtons(visibleOptions: any[]) {}

    selectButton(direction: Direction) {
        if (direction === Direction.UP) {
            if (this.index! !== 0) {
                this.setPicker(this.index! - 1);
                this.index! -= 1;
            } else if (this.viewTop !== 0) {
                console.log("traversing the list upwards!");
                this.viewTop -= 1;
                this.viewBot -= 1;
                this.clearButtons();
                this.setButtons(
                    this.options.slice(
                        this.viewTop,
                        this.viewBot
                    ));
            }
        } else if (direction === Direction.DOWN) {
            if (this.index! !== this.buttons!.length - 1) {
                this.setPicker(this.index! + 1);
                this.index! += 1;
            } else if (this.viewBot !== this.options.length) {
                console.log("traversing the list downwards!");
                this.viewTop += 1;
                this.viewBot += 1;
                this.clearButtons();
                this.setButtons(
                    this.options.slice(
                        this.viewTop,
                        this.viewBot
                    ));
            }
        }
    }

}

class RemotePlayerList extends ListMenu implements AkkamonMenu {

    constructor(
        scene: WorldScene,
        options: {id: string, scene: string}[]
    ) {
        super(scene, options)

        let title = new MenuText(
            this.scene,
            this.group!,
            this.groupDepth!,
            this.x - this.xOffsetFromRight!,
            this.y + 20,
            "Nearby trainers:")

        this.setButtons(
            this.options.slice(
                this.viewTop,
                this.viewBot
            )
        );

        // this.yOffsetFromTop
        // this.ySpacing

    }

    setButtons(visibleOptions: {id: string, scene: string}[]) {
        for (let i = 0; i < visibleOptions.length; i++) {

            this.buttons!.push(new MenuText(
                this.scene,
                this.group!,
                this.groupDepth!,
                this.x - this.xOffsetFromRight!,
                this.y + this.yOffsetFromTop! + i * this.ySpacing!,
                visibleOptions[i].id));
        }
    }

    confirm() {
        this.akkamonScene.pushMenu(new ChallengeDialogue(
            this.akkamonScene,
            ['YES', 'NO'],
            {
                'trainerID': this.options![this.index! + this.viewTop]
            }));
    }
}

class ConfirmationDialogue extends Menu implements AkkamonMenu {
    dialogueData?: {[key: string]: string}
    options?: Array<string>
    dialogueBox?: Dialogue

    constructor(scene: WorldScene, options: Array<string>, dialogueData: {[key: string]: string} | string | {}) {
        super(scene, "confirmation-dialogue");
        let camera = scene.cameras.main;
        this.setDisplaySize(200, 0.83 * 200)
        this.setPosition(this.x + camera.width, this.y + (camera.height - 0.28 * camera.width - this.displayHeight));
        this.xOffsetFromRight = 0.5 * this.displayWidth;
        this.yOffsetFromTop = 0.33 * this.displayHeight - MenuText.TEXT_HEIGHT;
        this.ySpacing! = 0.33 * this.displayHeight;

        this.setPicker(0);
        this.setButtons(options);
        this.groupDepth = 40;
        this.group!.setDepth(this.groupDepth!);

        this.dialogueBox = new Dialogue(scene, this.group!, this.groupDepth);
    }

    setButtons(buttonTextArray: Array<string>) {
        for (let i = 0; i < buttonTextArray.length; i++) {
            this.buttons!.push(new MenuText(this.scene, this.group!, this.groupDepth!, this.x - this.xOffsetFromRight!, this.y + this.yOffsetFromTop! + i * this.ySpacing!, buttonTextArray[i]));
        }
    }


}

export class Dialogue extends Phaser.GameObjects.Image implements AkkamonMenu {
    public messageQueue: Queue<string>;
    public displayedText: MenuText;
    public akkamonScene: WorldScene | BattleScene;
    public group: Phaser.GameObjects.Group;

    constructor(scene: WorldScene | BattleScene, group: Phaser.GameObjects.Group, depth: number) {
        let camera = scene.cameras.main;
        super(scene, camera.scrollX, camera.scrollY, "general-dialogue-box")
        this.setOrigin(0,1);
        this.setPosition(camera.scrollX, camera.scrollY + camera.height)
        this.setDisplaySize(camera.width, 0.28 * camera.width);

        scene.add.existing(this);
        group.add(this);
        this.setDepth(depth);

        this.group = group;
        this.akkamonScene = scene;

        this.messageQueue = baseQueue();
        this.displayedText = new WrappedMenuText(
            this.akkamonScene,
            this.group,
            depth,
            this.x + 40,
            this.y - this.displayHeight + 40,
            '',
            camera.width
        );
    }

    setMenuVisible(value: boolean) {
        this.group!.setVisible(value);
    }

    selectButton() {
    }
    confirm() {
    }

    destroyGroup() {
        const clonedChildren = [... this.group.getChildren()]
        for (let child of clonedChildren) {
            console.log("destroying child with:");
            console.log(child);
            console.log(child.destroy);
            child.destroy();
        }
        console.log("destroying group of dialogue");
        console.log(this.group);
        this.group.destroy(true)
    }

    destroyAndGoBack() {
        console.log("Destroying dialogue box!");
        this.akkamonScene.traverseMenusBackwards();
        this.destroyGroup();
    }

    push(messageData: string | string[]): void {
        if (typeof messageData === 'string') {
            this.messageQueue.push(messageData);
        } else if (Array.isArray(messageData)) {
            this.messageQueue.pushArray(messageData);
        }
    }

    displayNextDialogue() {
        this.displayedText.text = '';
        if (this.messageQueue.peek() !== undefined) {
            this.typewriteText(this.messageQueue.pop()!);
        }
    }

    typewriteText(text: string, uiEventCallback?: () => void) {
            const length = text.length
            let i = 0
            if (uiEventCallback) {
                let timeEvent = this.scene.time.addEvent({
                    callback: () => {
                        this.displayedText.text += text[i]
                        ++i
                        if (i === length - 1) {
                            uiEventCallback();
                        }
                    },
                    repeat: length - 1,
                    delay: 20
                })
                return timeEvent;
            } else {
                let timeEvent = this.scene.time.addEvent({
                    callback: () => {
                        this.displayedText.text += text[i]
                        ++i
                    },
                    repeat: length - 1,
                    delay: 20
                })
                return timeEvent;
            }
        }

}

class ChallengeDialogue extends ConfirmationDialogue implements AkkamonMenu {
    challengedTrainerID: {id: string, scene: string};

    constructor(scene: WorldScene, options: Array<string>, dialogueData: {trainerID: {id: string, scene: string}}) {

        super(scene, options, dialogueData);
        this.challengedTrainerID = dialogueData.trainerID;
        this.dialogueBox!.push(
            `Do you want to challenge ${this.challengedTrainerID.id} to a battle?`
        );
        this.dialogueBox!.displayNextDialogue();
    }

    confirm() {
        if (this.buttons![this.index!].text === "YES") {
            this.akkamonScene.requestBattle(this.challengedTrainerID);
            this.akkamonScene.clearMenus();
            this.akkamonScene.pushMenu(new WaitingDialogue(this.akkamonScene, new Phaser.GameObjects.Group(this.scene), 20, 'Awaiting request initialisation...'));
        } else {
            this.destroyAndGoBack();
        }
    }

    destroy() {
        this.scene.time.removeAllEvents();
        super.destroy();
    }
}

export class InteractionRequestDialogue extends ConfirmationDialogue implements AkkamonMenu {

    private requestName: string;

    dialogueData;

    constructor(scene: WorldScene, options: Array<string>, dialogueData: {name: string, requestType: string, requestName: string}) {
        super(scene, options, dialogueData);
        this.dialogueData = dialogueData;

        this.requestName = dialogueData.requestName;

        this.dialogueBox!.push(
            `Do you want to ${dialogueData.requestType} with ${dialogueData.name}?`
        );
        this.dialogueBox!.displayNextDialogue();
    }

    confirm() {
        if (this.buttons![this.index!].text === "YES") {
            console.log("Sending true!");
            this.akkamonScene.requestConfirmInteractionReply(true, this.requestName);
            this.akkamonScene.clearMenus();
            this.akkamonScene.pushMenu(new WaitingDialogue(this.akkamonScene, new Phaser.GameObjects.Group(this.scene), 20, `Waiting for ${this.dialogueData.requestType} to start...`));
        } else {
            console.log("Sending false!");
            this.akkamonScene.requestConfirmInteractionReply(false, this.requestName);
            this.destroyAndGoBack();
        }
    }

    destroy() {
        this.scene.time.removeAllEvents();
        super.destroy();
    }
}


export class WaitingDialogue extends Dialogue {
    text: string;
    waitingPrinter: any
    constructor(scene: WorldScene, group: Phaser.GameObjects.Group, depth: number, text: string) {
        super(scene, group, depth);
        this.text = text;
        this.typewriteText(this.text);
        this.waitingPrinter = setInterval(() => {
            this.displayedText.text = '';
            this.typewriteText(this.text);
        }, 3000);
    }

    destroyAndGoBack() {
        super.destroyAndGoBack();
    }

    destroy() {
        console.log("destroying waiting dialogue!");
        clearInterval(this.waitingPrinter);
        this.scene.time.removeAllEvents();
        super.destroy();
    }

}

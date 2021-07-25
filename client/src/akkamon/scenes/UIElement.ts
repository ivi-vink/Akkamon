import type { AkkamonWorldScene } from '../scenes/AkkamonWorldScene';
import { Direction } from '../render/Direction';


class MenuText extends Phaser.GameObjects.Text {
    constructor(scene: Phaser.Scene, group: Phaser.GameObjects.Group, x: number, y: number, text: string) {
        let style: Phaser.Types.GameObjects.Text.TextStyle = {
                fontFamily: 'Courier',
                fontSize: '16px',
                fontStyle: '',
                backgroundColor: undefined,
                color: '#000000',
                stroke: '#000000',
                strokeThickness: 0,
                align: 'left',  // 'left'|'center'|'right'|'justify'
        }
        super(scene, x, y, text, style);
        console.log("adding text to scene");
        scene.add.existing(this);
        group.add(this);
    }
}

class Picker extends Phaser.GameObjects.Image {
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
    destroyMe: () => void
    confirm: () => void
}


class Menu extends Phaser.GameObjects.Image implements AkkamonMenu {

    private akkamonScene: AkkamonWorldScene

    public group?: Phaser.GameObjects.Group;

    private buttons?: Array<MenuText>;

    private picker?: Picker;

    private index?: number;

    destroyMe() {
        console.log(this.group);
        console.log("destroying group");
        this.group!.destroy(true);
        this.akkamonScene.isUsingGridControls();
    }

    confirm() {
        // communicate with client
    }

    constructor(scene: AkkamonWorldScene) {
        console.log("Making pause Menu");
        const { width, height } = scene.scale;
        super(scene, width * 0.95, height * 0.05, "menu")
        this.setOrigin(1,0)
        this.setVisible(true)
        this.setDisplaySize(296, 400)

        this.akkamonScene = scene;

        this.group = new Phaser.GameObjects.Group(scene);

        console.log("Adding this to scene");
        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Array();

        this.index = 0;
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
                "picker")
        } else {
            this.picker.setY(
                pickerY
            );
        }
    }

    private indexToYpixel(index: number) {
        return index * 100 + 7;
    }

    setButtons(buttonTextArray: Array<string>) {
        for (let i = 0; i < buttonTextArray.length; i++) {
            this.buttons!.push(new MenuText(this.scene, this.group!, this.x - 150, this.y + 40 + i * 100, buttonTextArray[i]));
        }
    }

    clearButtons() {
        this.buttons = new Array();
    }

    selectButton(direction: Direction) {
        if (direction === Direction.UP && this.index! !== 0) {
            this.picker!
            .setPosition(this.picker!.x, this.picker!.y - 100)
            this.index! -= 1;
        } else if (direction === Direction.DOWN && this.index !== this.buttons!.length - 1) {
            this.picker!
            .setPosition(this.picker!.x, this.picker!.y + 100)
            this.index! += 1;
        }
    }
}

export class PauseMenu extends Menu implements AkkamonMenu {
    constructor(scene: AkkamonWorldScene) {
        super(scene)
        this.setPicker(0);
        this.setButtons([
            'POKéDEX',
            'POKéMON',
            'PHONE',
            'CLOSE'
        ]);
    }
}

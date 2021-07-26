import type { AkkamonWorldScene } from '../scenes/AkkamonWorldScene';
import { Direction } from '../render/Direction';


class MenuText extends Phaser.GameObjects.Text {
    constructor(scene: Phaser.Scene, group: Phaser.GameObjects.Group, groupDepth: number, x: number, y: number, text: string) {
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
        scene.add.existing(this);
        group.add(this);
        this.setDepth(groupDepth);
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

    akkamonScene: AkkamonWorldScene

    public group?: Phaser.GameObjects.Group;

    groupDepth?: number

    buttons?: Array<MenuText>;

    private picker?: Picker;

    index?: number;

    private camera?: Phaser.Cameras.Scene2D.Camera;

    ySpacing?: number
    yOffsetFromTop?: number
    xOffsetFromRight?: number

    destroyMe() {
        this.akkamonScene.traverseMenusBackwards();
        this.group!.destroy(true);
    }

    confirm() {
        // communicate with client
        throw new Error('Confirm method should be present in a Menu implementation');
    }

    constructor(scene: AkkamonWorldScene) {
        let camera = scene.cameras.main;

        super(scene, camera.scrollX + camera.width, camera.scrollY, "pause-menu")
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

    setButtons(buttonTextArray: Array<string>) {
        for (let i = 0; i < buttonTextArray.length; i++) {
            this.buttons!.push(new MenuText(this.scene, this.group!, this.groupDepth!, this.x - this.xOffsetFromRight!, this.y + this.yOffsetFromTop! + i * this.ySpacing!, buttonTextArray[i]));
        }
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
    constructor(scene: AkkamonWorldScene) {
        super(scene)
        this.ySpacing
        this.setPicker(0);
        this.setButtons([
            'POKéDEX',
            'POKéMON',
            'PHONE',
            'CLOSE'
        ]);
        this.group!.setDepth(this.groupDepth!);
    }

    confirm() {
        if (this.buttons![this.index!].text === 'PHONE') {
            this.akkamonScene.pushMenu(new RemotePlayerList(this.akkamonScene, this.akkamonScene.getRemotePlayerNames()));
        }
    }
}

class ListMenu extends Menu implements AkkamonMenu {
    options: Array<string>

    viewTop: number = 0;

    viewBot: number = 4;

    constructor(
        scene: AkkamonWorldScene,
        options: Array<string>
    ) {
        super(scene)
        this.options = options;

        this.xOffsetFromRight = 210;
        this.yOffsetFromTop = 50;

        let contacts = new MenuText(this.scene, this.group!, this.groupDepth!, this.x - this.xOffsetFromRight, this.y + 20, "Nearby trainers:")
        // this.yOffsetFromTop
        // this.ySpacing

        this.setPicker(0);
        this.setButtons(
            this.options.slice(
                this.viewTop,
                this.viewBot
            )
        );

        this.groupDepth = 30;
        this.group!.setDepth(this.groupDepth);
    }

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
}

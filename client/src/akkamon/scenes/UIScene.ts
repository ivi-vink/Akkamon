import { Direction } from '../render/Direction';
import { eventsCenter } from '../../akkamon/scenes/AkkamonWorldScene';

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
        scene.add.existing(this);
        group.add(this);
    }
}

interface AkkamonMenu {
}

class Menu extends Phaser.GameObjects.Image implements AkkamonMenu {

    private buttons?: Array<MenuText>;

    private buttonSelector?: Phaser.GameObjects.Image;

    private selectedButton?: string;

    private index?: number;

    public group?: Phaser.GameObjects.Group;

    constructor(scene: Phaser.Scene) {
        const { width, height } = scene.scale;
        super(scene, width * 0.95, height * 0.05, "menu")
        this.setOrigin(1,0)
        this.setVisible(true)
        this.setDisplaySize(296, 400)

        this.group = new Phaser.GameObjects.Group(scene);

        scene.add.existing(this);
        this.group.add(this);

        this.buttons = new Array();

        this.setMainButtons();

        this.buttonSelector = scene.add.image(
            this.x - this.displayWidth + 40,
            this.buttons![0].y + 7,
            "menupicker")
            .setDisplaySize(20,33)
            .setOrigin(0.5,0.5);
        this.group.add(this.buttonSelector);

        this.index = 0;
        this.selectedButton = this.buttons[0].text;
    }

    resetPicker() {
        this.buttonSelector!
        .setPosition(this.x - this.displayWidth + 40,
                    this.buttons![0].y + 7);
    }

    setMainButtons() {
        this.buttons!.push(new MenuText(this.scene, this.group!, this.x - 150, this.y + 40, 'POKéDEX'));
        this.buttons!.push(new MenuText(this.scene, this.group!, this.x - 150, this.y + 140, 'POKéMON'));
        this.buttons!.push(new MenuText(this.scene, this.group!, this.x - 150, this.y + 240, 'PHONE'));
        this.buttons!.push(new MenuText(this.scene, this.group!, this.x - 150, this.y + 340, 'CLOSE'));
    }

    clearButtons() {
        this.buttons = new Array();
    }

    selectButton(direction: Direction) {
        if (direction === Direction.UP && this.index! !== 0) {
            this.buttonSelector!
            .setPosition(this.buttonSelector!.x, this.buttonSelector!.y - 100)
            this.index! -= 1;
        } else if (direction === Direction.DOWN && this.index !== this.buttons!.length - 1) {
            this.buttonSelector!
            .setPosition(this.buttonSelector!.x, this.buttonSelector!.y + 100)
            this.index! += 1;
        }

        this.selectedButton = this.buttons![this.index!].text;
    }
}

export class UIScene extends Phaser.Scene
{

    // private uiControls: UIControls;

    private cursors?: Phaser.Types.Input.Keyboard.CursorKeys;

    private menu?: Menu;

    constructor ()
    {
        super('AkkamonUI');
    }

    create () {
        //this.uiControls = new UIControls(this.input);


        this.menu = new Menu(this);
        this.menu.group!.setVisible(false);

        eventsCenter.on('open-menu', () =>
                             {
                                 this.menu!.group!.setVisible(true);
                             });

    }

}

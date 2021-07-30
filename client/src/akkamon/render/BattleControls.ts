import { Direction } from './Direction';
import type { BattleEngine } from './BattleEngine';
import type { AkkamonMenu } from '../scenes/UIElement';

export class BattleControls {
    private cursors: Phaser.Types.Input.Keyboard.CursorKeys;

    constructor(
        private input: Phaser.Input.InputPlugin,
        private menu: AkkamonMenu,
    ) {
        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update() {
        console.log("updating battle controls");
        if (Phaser.Input.Keyboard.JustDown(this.cursors.left)) {
            console.log("left");
            this.menu.selectButton(Direction.LEFT);
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.right)) {
            console.log("right");
            this.menu.selectButton(Direction.RIGHT);
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.up)) {
            console.log("up");
            this.menu.selectButton(Direction.UP);
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.down)) {
            console.log("down");
            this.menu.selectButton(Direction.DOWN);
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.space)) {
            console.log("confirm");
            this.menu.confirm();
        }
    }
}

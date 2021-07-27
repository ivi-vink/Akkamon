import { Direction } from '../render/Direction';
import type { AkkamonMenu } from '../scenes/UIElement';

export class UIControls {
    private cursors: Phaser.Types.Input.Keyboard.CursorKeys;

    constructor(
        private input: Phaser.Input.InputPlugin,
        private menu: AkkamonMenu,
    ) {
        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update() {
        if (Phaser.Input.Keyboard.JustDown(this.cursors.left)) {
            this.menu.destroyAndGoBack();
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.right)) {
            this.menu.confirm();
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.up)) {
            this.menu.selectButton(Direction.UP);
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.down)) {
            this.menu.selectButton(Direction.DOWN);
        }
    }
}

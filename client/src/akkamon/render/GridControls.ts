import { Direction } from './Direction';
import type { GridPhysics } from './GridPhysics';

export class GridControls {
    private cursors: Phaser.Types.Input.Keyboard.CursorKeys;

    constructor(
        private input: Phaser.Input.InputPlugin,
        private gridPhysics: GridPhysics,
    ) {
        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update() {
        if (this.cursors.left.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.LEFT);
        } else if (this.cursors.right.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.RIGHT);
        } else if (this.cursors.up.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.UP);
        } else if (this.cursors.down.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.DOWN);
        }
    }


}

import { Direction } from './Direction';
import type { GridPhysics } from './GridPhysics';

export class GridControls {
    constructor(
        private input: Phaser.Input.InputPlugin,
        private gridPhysics: GridPhysics
    ) { }

    update() {
        const cursors = this.input.keyboard.createCursorKeys();
        if (cursors.left.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.LEFT);
        } else if (cursors.right.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.RIGHT);
        } else if (cursors.up.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.UP);
        } else if (cursors.down.isDown) {
            this.gridPhysics.movePlayerSprite(Direction.DOWN);
        }
    }


}

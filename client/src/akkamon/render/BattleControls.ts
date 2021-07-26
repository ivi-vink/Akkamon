import { Direction } from './Direction';
import type { BattleEngine } from './BattleEngine';

export class BattleControls {
    private cursors: Phaser.Types.Input.Keyboard.CursorKeys;

    constructor(
        private input: Phaser.Input.InputPlugin,
        private battleEngine: BattleEngine,
    ) {
        this.cursors = this.input.keyboard.createCursorKeys();
    }

    update() {
        if (Phaser.Input.Keyboard.JustDown(this.cursors.left)) {
            // this.battleEngine.
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.right)) {
            // this.battleEngine.
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.up)) {
            // this.battleEngine.
        } else if (Phaser.Input.Keyboard.JustDown(this.cursors.down)) {
            // this.battleEngine.
        }
    }
}

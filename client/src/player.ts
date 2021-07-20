import type Phaser from 'phaser';

type Sprite = Phaser.Types.Physics.Arcade.SpriteWithDynamicBody;

type PlayerConfig = {
    trainerId: string,
    position: Phaser.Math.Vector2;
}

type Input = {
    cursors: Phaser.Types.Input.Keyboard.CursorKeys,
}

export class Player
{
    trainerId: string
    position: Phaser.Math.Vector2

    constructor({trainerId, position}: PlayerConfig) {
        this.trainerId = trainerId;
        this.position = position
    }

}

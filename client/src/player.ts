import type Phaser from 'phaser';

type Sprite = Phaser.Types.Physics.Arcade.SpriteWithDynamicBody;

type PlayerConfig = {
    name: string,
    position: {x: number, y: number}
}

type Input = {
    cursors: Phaser.Types.Input.Keyboard.CursorKeys,
}

export class Player
{
    name: string
    position: {x:number, y: number}
    sprite: Sprite | undefined;
    input: Input | undefined;

    constructor({name, position}: PlayerConfig) {
        this.name = name;
        this.position = position
    }

    setSprite(sprite: Sprite) {
        this.sprite = sprite;
    }

    setInput(input: Input) {
        this.input = input;
    }
}

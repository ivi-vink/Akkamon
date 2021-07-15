import Phaser from 'phaser';
import type Player from './player';

type PlayerSpriteConfig = {
    scene: Phaser.Scene,
    x: number,
    y: number,
    texture: Phaser.Textures.Texture | string,
    player: Player
}

export default class PlayerSprite extends Phaser.Physics.Arcade.Sprite implements Phaser.Types.Physics.Arcade.SpriteWithDynamicBody {

    body: Phaser.Physics.Arcade.Body;
    player: Player

    constructor(config: PlayerSpriteConfig) {
        super(config.scene, config.x, config.y, config.texture);

        this.body = new Phaser.Physics.Arcade.Body(config.scene.physics.world, this);
        this.player = config.player;
    }
}

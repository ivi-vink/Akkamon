import Phaser from 'phaser';
import type Player from './player';

type PlayerSpriteConfig = {
    scene: Phaser.Scene,
    x: number,
    y: number,
    texture: Phaser.Textures.Texture | string,
    frame?: string,
    player: Player,
    moveControls?: Phaser.Types.Input.Keyboard.CursorKeys;
}

interface AkkamonDynamicSprite extends Phaser.Types.Physics.Arcade.SpriteWithDynamicBody {
    player: Player
    moveControls: Phaser.Types.Input.Keyboard.CursorKeys | undefined
}

export default class PlayerSprite extends Phaser.Physics.Arcade.Sprite implements AkkamonDynamicSprite {

    body: Phaser.Physics.Arcade.Body;
    player: Player;
    moveControls: Phaser.Types.Input.Keyboard.CursorKeys | undefined;

    constructor(config: PlayerSpriteConfig) {
        super(config.scene, config.x, config.y, config.texture, config.frame!);

        this.body = new Phaser.Physics.Arcade.Body(config.scene.physics.world, this);
        this.player = config.player;

        if (config.moveControls) this.moveControls = config.moveControls;
    }

    renderUpdate({x, y}: Player["position"]) {
        this.x = x;
        this.y = y;
    }
}

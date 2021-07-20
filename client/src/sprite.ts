import Phaser from 'phaser';
import AkkamonStartScene from './scene';
import type { Player } from './player';
import type { Direction } from './Direction';

type PlayerSpriteConfig = {
    scene: Phaser.Scene,
    tilePos: Phaser.Math.Vector2,
    texture: Phaser.Textures.Texture | string,
    frame?: string,
    player: Player,
}

interface AkkamonPlayerSprite extends Phaser.GameObjects.Sprite {
    player: Player
}

export class PlayerSprite extends Phaser.GameObjects.Sprite implements AkkamonPlayerSprite {

    player: Player;
    tilePos: Phaser.Math.Vector2;

    constructor(config: PlayerSpriteConfig) {
        const offsetX = AkkamonStartScene.TILE_SIZE / 2;
        const offsetY = AkkamonStartScene.TILE_SIZE;

        super(config.scene,
              config.tilePos.x * AkkamonStartScene.TILE_SIZE + offsetX,
              config.tilePos.y * AkkamonStartScene.TILE_SIZE + offsetY,
              config.texture,
              config.frame);

        this.player = config.player;
        this.tilePos = new Phaser.Math.Vector2(config.tilePos.x, config.tilePos.y);

        this.setOrigin(0.5, 1);
    }

    getPosition(): Phaser.Math.Vector2 {
        return this.getBottomCenter();
    }

    newPosition(position: Phaser.Math.Vector2): void {
        super.setPosition(position.x, position.y);
    }

    stopAnimation(direction: Direction) {
        const animationManager = this.anims.animationManager;
        const standingFrame = animationManager.get(direction).frames[1].frame.name;

        this.anims.stop();
        this.setFrame(standingFrame);
    }

    startAnimation(direction: Direction) {
        this.anims.play(direction);
    }

    getTilePos(): Phaser.Math.Vector2 {
        return this.tilePos.clone();
    }

    setTilePos(tilePosition: Phaser.Math.Vector2): void {
        this.tilePos = tilePosition.clone();
    }
}

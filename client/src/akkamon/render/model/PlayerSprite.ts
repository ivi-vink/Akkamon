import Phaser from 'phaser';
import { AkkamonWorldScene } from '../../scenes/AkkamonWorldScene';

import type { Direction } from '../Direction';

type PlayerSpriteConfig = {
    scene: Phaser.Scene,
    tilePos: Phaser.Math.Vector2,
    texture: Phaser.Textures.Texture | string,
    frame?: string,
}

interface AkkamonPlayerSprite extends Phaser.GameObjects.Sprite {
}

export class PlayerSprite extends Phaser.GameObjects.Sprite implements AkkamonPlayerSprite {

    tilePos: Phaser.Math.Vector2;

    constructor(config: PlayerSpriteConfig) {
        const offsetX = AkkamonWorldScene.TILE_SIZE / 2;
        const offsetY = AkkamonWorldScene.TILE_SIZE;

        super(config.scene,
              config.tilePos.x * AkkamonWorldScene.TILE_SIZE + offsetX,
              config.tilePos.y * AkkamonWorldScene.TILE_SIZE + offsetY,
              config.texture,
              config.frame);

        this.tilePos = new Phaser.Math.Vector2(config.tilePos.x, config.tilePos.y);

        this.setOrigin(0.5, 1);

        // add to scene!
        config.scene.add.existing(this);
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

    getScene(): string {
        return this.scene.scene.key;
    }
}

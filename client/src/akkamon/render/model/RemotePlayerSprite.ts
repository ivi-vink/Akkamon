import Phaser from 'phaser';
import { AkkamonWorldScene } from '../../scenes/AkkamonWorldScene';
import { PlayerSprite } from '../model/PlayerSprite';
import { GridPhysics } from '../engine/GridPhysics';
import { Direction } from '../Direction';

export class Queue<T> {
    private _data = new Array();

    constructor(data?: Array<T>) {
        if (data !== undefined) {
            this._data = data;
        }
    }

    push(element: T): void {
        this._data.push(element);
    }

    pushArray(arr: T[]): void {
        for (var element of arr) {
            this._data.push(element);
        }
    }

    pop(): T | undefined {
        return this._data.shift();
    }

    isEmpty(): boolean {
        return this._data.length == 0;
    }

    peek() {
        return this._data[0];
    }
}

type RemotePlayerSpriteConfig = {
    scene: Phaser.Scene,
    tilePos: Phaser.Math.Vector2,
    texture: Phaser.Textures.Texture | string,
    frame?: string,
    moveQueue: Queue<Direction>
}

export class RemotePlayerSprite extends PlayerSprite  {

    private lastTilePos?: Phaser.Math.Vector2;
    private moveQueue: Queue<Direction> = new Queue();

    private movementDirection: Direction = Direction.NONE;

    private speedPixelsPerSecond: number = AkkamonWorldScene.TILE_SIZE * 4;

    private tileSizePixelsWalked: number = 0;

    constructor(config: RemotePlayerSpriteConfig) {
        super(config);
    }

    push(moveQueue: Array<Direction>): void {
        for (var direction of moveQueue) {
            if (direction !== Direction.NONE) {
                this.moveQueue.push(direction);
            }
        }
        // console.log(this.moveQueue);
    }

    updatePixelPosition(delta: number): void {
        const pixelsToWalkThisUpdate = this.getPixelsToWalk(delta);

        if (!this.willCrossTileBorderThisUpdate(pixelsToWalkThisUpdate)) {
            this.move(pixelsToWalkThisUpdate);
        } else if (this.shouldContinueMoving()) {
            this.move(pixelsToWalkThisUpdate);
        } else {
            this.move(AkkamonWorldScene.TILE_SIZE - this.tileSizePixelsWalked);
            this.stopMoving();
        }
    }

    shouldContinueMoving(): boolean {
        if (this.moveQueue.peek() == this.movementDirection) {
            console.log("continueing to move.");
            this.moveQueue.pop();
            return true;
        }
        return false;
    }

    willCrossTileBorderThisUpdate(pixelsToWalkThisUpdate: number): boolean {
        return (this.tileSizePixelsWalked + pixelsToWalkThisUpdate) >= AkkamonWorldScene.TILE_SIZE;
    }

    move(pixelsToMove: number): void {
        this.tileSizePixelsWalked += pixelsToMove;
        this.tileSizePixelsWalked %= AkkamonWorldScene.TILE_SIZE;

        const directionVec = GridPhysics.movementDirectionVectors[this.movementDirection]!.clone();

        const moveVec = directionVec.multiply(
            new Phaser.Math.Vector2(pixelsToMove)
        );

        const newPosition = this.getPosition().add(moveVec);
        this.newPosition(newPosition);
    }

    getPixelsToWalk(delta: number): number {
        const deltaInSeconds = delta / 1000;
        return this.speedPixelsPerSecond * deltaInSeconds;
    }

    hasMovesLeft(): boolean {
        return !this.moveQueue.isEmpty();
    }

    isMoving(): boolean {
        return this.movementDirection !== Direction.NONE;
    }

    startMoving(): void {
        if (!this.moveQueue.isEmpty()) {
            this.movementDirection = this.moveQueue.pop()!;
            this.startAnimation(this.movementDirection);
            // console.log("remote player now walking in direction: " + this.movementDirection);
        } else {
            // console.log("moveQueue empty!");
        }
    }

    stopMoving(): void {
        this.stopAnimation(this.movementDirection);
        this.movementDirection = Direction.NONE;
    }
}

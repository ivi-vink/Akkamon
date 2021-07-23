import Phaser from 'phaser';

import type { AkkamonWorldScene } from '../../scenes/AkkamonWorldScene';
import { AkkamonEngine } from '../engine/AkkamonEngine';

import type { Direction } from '../Direction';

import {
    Queue,
    RemotePlayerSprite
} from '../model/RemotePlayerSprite';

import type {
    RemoteMovementQueues
} from '../../client/Events';

export class RemotePlayerEngine extends AkkamonEngine {

    private scene: AkkamonWorldScene;

    private trainerIdToRemotePlayerSprite: Map<string, RemotePlayerSprite> = new Map();

    constructor(scene: AkkamonWorldScene) {
        super();
        this.scene = scene;
    }

    push(remoteMovementQueues: RemoteMovementQueues) {
        this.updateMembers(remoteMovementQueues);
        this.pushMovesToSprites(remoteMovementQueues);
    }

    pushMovesToSprites(remoteMovementQueues: RemoteMovementQueues) {
        this.trainerIdToRemotePlayerSprite.forEach((remoteSprite: RemotePlayerSprite, key: string) => {
            remoteSprite.push(remoteMovementQueues[key].value);
        });
    }

    update(delta: number): void {
        this.trainerIdToRemotePlayerSprite.forEach((remoteSprite: RemotePlayerSprite, key: string) => {
            if (remoteSprite.isMoving()) {
                console.log("remote player currently walking");
                remoteSprite.updatePixelPosition(delta);
            } else if (remoteSprite.hasMovesLeft()) {
                console.log("remote player starts moving");
                remoteSprite.startMoving();
            }
        });
    }

    updateMembers(newRemoteMovementQueues: RemoteMovementQueues) {
        const traineridToQueueValue = newRemoteMovementQueues;

        Object.keys(newRemoteMovementQueues).forEach((key: string) => {

            var moveQueue = traineridToQueueValue[key].value;
            if (moveQueue !== undefined) {

                // console.log("-> key: " + key + " has position " + newTilePos.x + ", " + newTilePos.y);

                if (!this.trainerIdToRemotePlayerSprite.has(key)) {
                    // console.log("adding remote player sprite for " + key);
                    this.trainerIdToRemotePlayerSprite.set(key,
                                                          new RemotePlayerSprite({
                                                              scene: this.scene,
                                                              tilePos: new Phaser.Math.Vector2(this.scene.spawnPointTilePos!),
                                                              texture: this.scene.textures.get("atlas"),
                                                              frame: "misa-front",
                                                              moveQueue: new Queue(moveQueue)
                                                              }
                                                          ));
                } else {
                    // console.log("key: " + key + " already had a sprite!");
                }
            }

        });

        this.trainerIdToRemotePlayerSprite.forEach((value: RemotePlayerSprite, key: string) => {
            if (!(key in newRemoteMovementQueues)) {
                // console.log("removing remote player sprite for " + key);
                this.trainerIdToRemotePlayerSprite.get(key)!.destroy();
                this.trainerIdToRemotePlayerSprite.delete(key);
            } else {
                // console.log("Player " + key + " was not removed!");
            }
        });
    }
}

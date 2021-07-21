import Phaser from 'phaser';
import { akkamonClient } from './app';

export class RemotePlayerEngine {

    private scene: Phaser.Scene

    constructor(scene: Phaser.Scene) {
        this.scene = scene;
    }

    update() {
    }
}

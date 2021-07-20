// import Phaser from 'phaser';
import AkkamonStartScene from './scene';
import { Client } from './client';

const serviceUrl = 'ws://localhost:8080';

export const akkamonClient = new Client(serviceUrl);

const config: Phaser.Types.Core.GameConfig & Phaser.Types.Core.RenderConfig = {
    type: Phaser.AUTO,
    backgroundColor: '#125555',
    width: 800,
    height: 600,
    pixelArt: true,
    scene: AkkamonStartScene,
    physics: {
        default: "arcade",
        arcade: {
            gravity: { y: 0 }
        }
    }
};

const game: Phaser.Game = new Phaser.Game(config);

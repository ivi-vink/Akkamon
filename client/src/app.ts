// import Phaser from 'phaser';
import AkkamonStartScene from './scene';
import { AkkamonUI } from './uiScene';
import { Client } from './client';

const serviceUrl = 'ws://localhost:8080';

export const akkamonClient = new Client(serviceUrl);

const config: Phaser.Types.Core.GameConfig & Phaser.Types.Core.RenderConfig = {
    type: Phaser.AUTO,
    backgroundColor: '#125555',
    width: 800,
    height: 600,
    pixelArt: true,
    scene: [AkkamonStartScene, AkkamonUI]
};

const game: Phaser.Game = new Phaser.Game(config);


import Phaser from 'phaser';
import gameConfig from './app/gameConfig.js';

function newGame () {
  if (game) return;
  game = new Phaser.Game(gameConfig);
}

function destroyGame () {
  if (!game) return;
  game.destroy(true);
  game.runDestroy();
  game = null;
}

let game;

if (!game) newGame();

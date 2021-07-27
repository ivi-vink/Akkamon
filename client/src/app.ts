// // import Phaser from 'phaser';
// import AkkamonStartScene from './scene';
// import { AkkamonUI } from './uiScene';
// import { Client } from './client';
//
// const serviceUrl = 'ws://localhost:8080';
//
// export const akkamonClient = new Client(serviceUrl);
//
//
// const game: Phaser.Game = new Phaser.Game(config);


import Phaser from 'phaser';
import { gameConfig } from './akkamon/GameConfig';
import { Client } from './akkamon/client/Client';

function newGame () {
  if (game) return;
  game = new Phaser.Game(gameConfig);
}

function destroyGame () {
  if (!game) return;
  game.destroy(true);
  game = null;
}

export let client = new Client('ws://localhost:8080');
let game: Phaser.Game | null | undefined;


function delay(ms: number) {
    return new Promise(resolve => {
        setTimeout(resolve, ms);
    });
}

async function awaitRegistrationReplyAndStart() {
    if (!game) {
        while (client.getSessionTrainerId() === undefined) {
            console.log("can't start game, this trainerId is still undefined");
            await delay(1000);
        }
        newGame();
    }
}

awaitRegistrationReplyAndStart();

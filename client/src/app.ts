// import Phaser from 'phaser';
import GameState from './GameState';
import Socket from './socket';
import Client from './client';
import Player from './player';
import { Event } from './events';

const url = 'ws://localhost:8080';
const session = Socket.getInstance('ws://localhost:8080', {name: "", password: ""});
const client = Client.getInstance();
client.setSession(session);

import AkkamonStartScene from './game';

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

function delay(ms: number) {
    return new Promise( resolve => setTimeout(resolve, ms) );
}

function gameStateSender() {
    Client.getInstance().out({
        type: "clientSidePosUpdate",
        gameState: GameState.getInstance().withoutSprite()
    });
}

async function startGame() {
    while (true) {
        console.log(GameState.getInstance().currentPlayer);
        if (GameState.getInstance().currentPlayer) {
            const game: Phaser.Game = new Phaser.Game(config);
            setInterval(
                () => {gameStateSender()}, 200
            );
            break;
        }
        await delay(1000);
    }
}

startGame();

import Player from './player';
import type { Event } from './events';

export default class GameState {

    static instance: GameState;

    currentPlayer: Player | undefined;
    remotePlayers: { [name: string]: Player } = {};

    static getInstance() {
        if (GameState.instance) return GameState.instance;
        else {
            GameState.instance = new GameState();
            return GameState.instance;
        }
    }

    setCurrentPlayer(player: Player) {
        this.currentPlayer = player;
    }

    posUpdate(receivedState: GameState) {
        if (this.currentPlayer === undefined) {
            this.currentPlayer = receivedState.currentPlayer!;
        }

        Object.keys(receivedState.remotePlayers)
        .forEach(key => this.remotePlayers[key] = receivedState.remotePlayers[key]);

    }

    withoutSprite() {
        let spriteLess: GameState = new GameState();
        spriteLess.currentPlayer = new Player({
            name: this.currentPlayer!.name,
            position: this.currentPlayer!.position
        });
        // spriteLess.remotePlayers = this.remotePlayers;
        return spriteLess;
    }
}

import type { Player } from './player';

export class GameState {

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

}

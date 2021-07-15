import Player from './player';
import type { Event } from './events';

export default class GameState {

    static instance: GameState;

    currentPlayer: Player | undefined;
    remotePlayers: { [name: string]: Player } | undefined

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
        console.log("--> Game is updating positions");
        if (this.currentPlayer === undefined) {
            console.log("--> getting current player object");
            console.log(receivedState.currentPlayer!);
            this.currentPlayer = new Player(receivedState.currentPlayer!);
        }
    }
}

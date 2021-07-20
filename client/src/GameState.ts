import type { Player } from './player';

export class GameState {

    localPlayerState?: Player;
    remoteTrainerIdToPlayerState: { [trainerid: string]: Player } = {};

    getLocalMutablePlayerState(): Player {
        return this.localPlayerState!;
    }
}

import Phaser from 'phaser';
import type Player from './player';
import type GameState from './GameState';
import type { Direction } from './Direction';

export interface AkkamonEvent {
    type: string
}

export class PlayerRegistrationEvent implements AkkamonEvent {

    public type: string =  "PlayerRegistrationEvent";

    constructor(
    ) { }
}

export class GridMoveStartEvent implements AkkamonEvent {

    public type: string = "GridMoveStartEvent";

    constructor(
        public direction: Direction
    ) { }
}


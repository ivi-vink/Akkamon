import Phaser from 'phaser';
import type { Player } from './player';
import type { GameState } from './GameState';
import type { Direction } from './Direction';

export enum EventType {
    HEART_BEAT = "HeartBeat",
    PLAYER_REGISTRATION = "PlayerRegistrationEvent",
    GRID_MOVE_START =  "GridMoveStartEvent"
}

export interface AkkamonEvent {
    type: EventType
}

export class PlayerRegistrationEvent implements AkkamonEvent {

    public type: EventType = EventType.PLAYER_REGISTRATION;

    constructor(
    ) { }
}

export class GridMoveStartEvent implements AkkamonEvent {

    public type: EventType = EventType.GRID_MOVE_START;

    constructor(
        public direction: Direction
    ) { }
}

export class HeartBeatReplyEvent implements AkkamonEvent {

    public type: EventType = EventType.HEART_BEAT;

    constructor(
    ) { }
}

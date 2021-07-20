import Phaser from 'phaser';
import type { Player } from './player';
import type { GameState } from './GameState';
import type { Direction } from './Direction';

export enum EventType {
    HEART_BEAT = "HeartBeat",
    PLAYER_REGISTRATION = "PlayerRegistrationEvent",
    START_MOVING =  "StartMoving"
}

export interface AkkamonEvent {
    type: EventType
}

export class PlayerRegistrationEvent implements AkkamonEvent {

    public type: EventType = EventType.PLAYER_REGISTRATION;

    constructor(
    ) { }
}

export class StartMovingEvent implements AkkamonEvent {

    public type: EventType = EventType.START_MOVING;

    constructor(
        public direction: Direction,
    ) { }
}

export class HeartBeatReplyEvent implements AkkamonEvent {

    public type: EventType = EventType.HEART_BEAT;

    constructor(
    ) { }
}

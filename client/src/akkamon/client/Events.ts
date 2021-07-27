import Phaser from 'phaser';
import type { Direction } from '../render/Direction';

export enum EventType {
    HEART_BEAT = "HeartBeat",
    TRAINER_REGISTRATION_REQUEST = "TrainerRegistrationRequestEvent",
    TRAINER_REGISTRATION_REPLY = "TrainerRegistrationReplyEvent",
    START_MOVING =  "StartMoving",
    STOP_MOVING = "StopMoving",
    NEW_TILE_POS = "NewTilePos",
    INIT_BATTLE_REQUEST = "InitBattleRequestEvent",
    INIT_BATTLE_REPLY = "InitBattleReplyEvent",
    BATTLE_ABORTED = "BattleAborted"
}

export interface AkkamonEvent {
    type: EventType
}

export type TrainerPosition = { x: number, y: number }

export type RemoteMovementQueues = {
    [trainerId: string]: { value: Array<Direction> }
}

// INCOMING EVENTS
export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?: RemoteMovementQueues
    trainerId?: string
}

export class HeartBeatReplyEvent implements IncomingEvent {

    public type: EventType = EventType.HEART_BEAT;

    constructor(
    ) { }
}

export class PlayerRegistrationReplyEvent implements IncomingEvent {

    public type: EventType = EventType.TRAINER_REGISTRATION_REPLY;

    constructor(
        public trainerId: string
    ) { }
}

export class InitBattleReplyEvent implements IncomingEvent {

    public type: EventType = EventType.INIT_BATTLE_REPLY;

    constructor(
    ) { }
}

// OUTGOING EVENTS
export class PlayerRegistrationRequestEvent implements AkkamonEvent {

    public type: EventType = EventType.TRAINER_REGISTRATION_REQUEST;

    constructor(
    ) { }
}


export class StartMovingEvent implements AkkamonEvent {

    public type: EventType = EventType.START_MOVING;

    constructor(
        public sceneId: string,
        public direction: Direction,
    ) { }
}

export class StopMovingEvent implements AkkamonEvent {

    public type: EventType = EventType.STOP_MOVING;

    constructor(
        public sceneId: string,
        public direction: Direction,
    ) { }
}

export class NewTilePosEvent implements AkkamonEvent {

    public type: EventType = EventType.NEW_TILE_POS;

    constructor(
        public sceneId: string,
        public tilePos: {x: number, y: number}
    ) { }
}


export class InitBattleRequestEvent implements AkkamonEvent {

    public type: EventType = EventType.INIT_BATTLE_REQUEST;

    constructor(
        public thisTrainer: string,
        public otherTrainer: string
    ) { }
}

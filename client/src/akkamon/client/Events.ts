import Phaser from 'phaser';
import type { Direction } from '../render/Direction';

export type TrainerPosition = { x: number, y: number }

export type RemoteMovementQueues = {
    [trainerId: string]: { value: Array<Direction> }
}

export type Interaction = {
    type: string,
    requestingTrainerId: string,
    receivingTrainerIds: string[]
}


export enum EventType {
    HEART_BEAT = "HeartBeat",
    TRAINER_REGISTRATION_REQUEST = "TrainerRegistrationRequestEvent",
    TRAINER_REGISTRATION_REPLY = "TrainerRegistrationReplyEvent",
    START_MOVING =  "StartMoving",
    STOP_MOVING = "StopMoving",
    NEW_TILE_POS = "NewTilePos",
    INTERACTION_REQUEST = "InteractionRequestEvent",
    INTERACTION_REPLY = "InteractionReplyEvent",
    INTERACTION_ABORTED = "InteractionAbortedEvent"
}

export interface AkkamonEvent {
    type: EventType
}

export interface InteractionEvent extends AkkamonEvent {
    interaction: Interaction
}

export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?: RemoteMovementQueues
    trainerId?: string
}

// INCOMING EVENTS
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

// export class InitBattleReplyEvent implements IncomingEvent {
//
//     public type: EventType = EventType.INIT_BATTLE_REPLY;
//
//     constructor(
//     ) { }
// }

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



export class InteractionRequestEvent implements InteractionEvent {

    public type: EventType = EventType.INTERACTION_REQUEST;

    constructor(
        public sceneId: string,
        public interaction: Interaction,
    ) { }
}

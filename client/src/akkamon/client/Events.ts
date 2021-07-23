import Phaser from 'phaser';
import type { Direction } from './Direction';

export enum EventType {
    HEART_BEAT = "HeartBeat",
    PLAYER_REGISTRATION = "PlayerRegistrationEvent",
    START_MOVING =  "StartMoving",
    STOP_MOVING = "StopMoving",
    NEW_TILE_POS = "NewTilePos"
}

export interface AkkamonEvent {
    type: EventType
}

export type TrainerPosition = { x: number, y: number }

export type RemoteMovementQueues = {
    [trainerId: string]: { value: Array<Direction> }
}

export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?: RemoteMovementQueues
}

export class PlayerRegistrationEvent implements AkkamonEvent {

    public type: EventType = EventType.PLAYER_REGISTRATION;

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

export class HeartBeatReplyEvent implements AkkamonEvent {

    public type: EventType = EventType.HEART_BEAT;

    constructor(
    ) { }
}

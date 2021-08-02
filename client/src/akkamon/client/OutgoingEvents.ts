import type { Direction } from '../render/Direction';

import { EventType, AkkamonEvent } from './EventType';
import type { Move } from './IncomingEvents';

export type TrainerID = {
    id: string,
    scene: string
}

interface OutgoingEvent extends AkkamonEvent {
    trainerID: TrainerID;
}

export type RemoteMovementQueues = {
    [trainerID: string]: { value: Array<Direction> }
}

export type Interaction = {
    type: string,
    receivingtrainerIDs: TrainerID[]
}

export interface InteractionEvent extends OutgoingEvent {
    interaction: Interaction
}

export enum RequestBattleAction {
    FIGHT = "FIGHT",
}

export type BattleRequestBody = {
    requestAction: RequestBattleAction
    move?: Move
}


export class OutgoingBattleRequest implements OutgoingEvent {
    public type: EventType = EventType.BATTLE_REQUEST;

    constructor(
        public trainerID: TrainerID,
        public body: BattleRequestBody
    ) { }
}


export class HeartBeatReplyEvent implements AkkamonEvent {

    public type: EventType = EventType.HEART_BEAT;

    constructor(
    ) { }
}


export class PlayerRegistrationRequestEvent implements AkkamonEvent {

    public type: EventType = EventType.TRAINER_REGISTRATION_REQUEST;

    constructor(
    ) { }
}


export class StartMovingEvent implements OutgoingEvent {

    public type: EventType = EventType.START_MOVING;

    constructor(
        public trainerID: TrainerID,
        public direction: Direction,
    ) { }
}

export class StopMovingEvent implements OutgoingEvent {

    public type: EventType = EventType.STOP_MOVING;

    constructor(
        public trainerID: TrainerID,
        public direction: Direction,
    ) { }
}

export class NewTilePosEvent implements OutgoingEvent {

    public type: EventType = EventType.NEW_TILE_POS;

    constructor(
        public trainerID: TrainerID,
        public tilePos: {x: number, y: number}
    ) { }
}



export class OutgoingInteractionRequestEvent implements OutgoingEvent {

    public type: EventType = EventType.INTERACTION_REQUEST;

    constructor(
        public trainerID: TrainerID,
        public interaction: Interaction,
    ) { }
}

export class InteractionReplyEvent implements OutgoingEvent {
    public type: EventType = EventType.INTERACTION_REPLY;

    constructor(
        public trainerID: TrainerID,
        public requestName: string,
        public value: boolean
    ) { }
}


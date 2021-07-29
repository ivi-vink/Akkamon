import type { Direction } from '../render/Direction';

import { EventType, AkkamonEvent } from './EventType';

export type RemoteMovementQueues = {
    [trainerId: string]: { value: Array<Direction> }
}

export type Interaction = {
    type: string,
    requestingTrainerId: string,
    receivingTrainerIds: string[]
}

export interface InteractionEvent extends AkkamonEvent {
    interaction: Interaction
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



export class OutgoingInteractionRequestEvent implements InteractionEvent {

    public type: EventType = EventType.INTERACTION_REQUEST;

    constructor(
        public sceneId: string,
        public interaction: Interaction,
    ) { }
}

export class InteractionReplyEvent implements AkkamonEvent {
    public type: EventType = EventType.INTERACTION_REPLY;

    constructor(
        public trainerId: string,
        public sceneId: string,
        public requestName: string,
        public value: boolean
    ) { }
}


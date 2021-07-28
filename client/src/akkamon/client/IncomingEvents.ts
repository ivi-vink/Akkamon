import { EventType, AkkamonEvent } from './EventType';
import type { Direction } from '../render/Direction';



export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?:{[trainerId: string]: { value: Array<Direction> }}
    trainerId?: string
    requestId?: number
}

export class IncomingInteractionRequest implements IncomingEvent {

    public type: EventType = EventType.INTERACTION_REQUEST;

    constructor(
        public interactionType: string,
        public trainerId: string,
        public requestName: string
    ) { }
}

export class HeartBeat implements IncomingEvent {

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


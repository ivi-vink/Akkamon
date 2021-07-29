import { EventType, AkkamonEvent } from './EventType';
import type { Direction } from '../render/Direction';


type TrainerID = {id: string, scene: string}

export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?:{[trainerID: string]: { value: Array<Direction> }}
    trainerID?: TrainerID
    requestId?: number
    requestName?: string
    interactionType?: string
}

export class IncomingInteractionRequest implements IncomingEvent {

    public type: EventType = EventType.INTERACTION_REQUEST;

    constructor(
        public interactionType: string,
        public trainerID: TrainerID,
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
        public trainerID: TrainerID
    ) { }
}

// export class InitBattleReplyEvent implements IncomingEvent {
//
//     public type: EventType = EventType.INIT_BATTLE_REPLY;
//
//     constructor(
//     ) { }
// }


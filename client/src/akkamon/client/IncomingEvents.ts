import { EventType, AkkamonEvent } from './EventType';
import type { Direction } from '../render/Direction';
import type { BattleEventType } from '../render/BattleEngine';


type TrainerID = {id: string, scene: string}

export type BattleEvent = {
    id: BattleEventType
}

export type BattleMessage = {
    eventsToPlay: BattleEvent[]
    state: BattleState
}

export type BattleState = {
        teams: {
            [id: string]: {
                activeMon: Mon,
                team: Mon[]
            }
        }
    }

export type Mon = {
    name: string,
    stats: {
        HP: number,
        Attack: number,
        Defence: number,
        SpecialAttack: number,
        SpecialDefence: number,
        Speed: number,
        accuracy: number,
        evasion: number,
    },
    status: {}
}

export interface IncomingEvent extends AkkamonEvent {
    remoteMovementQueues?:{[trainerID: string]: { value: Array<Direction> }}
    trainerID?: TrainerID
    requestId?: number
    requestName?: string
    interactionType?: string
    participants?: string[]
    message?: BattleMessage
}

export class BattleInitEvent implements IncomingEvent {
    public type: EventType = EventType.BATTLE_INIT

    constructor(
        public participants: string[],
        public message: BattleMessage
    ) { }
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


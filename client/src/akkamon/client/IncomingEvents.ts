import { EventType, AkkamonEvent } from './EventType';
import type { Direction } from '../render/Direction';
import type { BattleEventType } from '../render/BattleEngine';
import type { MoveSlot } from '../render/battleUI';


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

export type Stat = {
    base: number,
    effective: number
}

export type Type = {
    name: string
}

export type Move = {
    name: string,
    type: Type
    category: string,
    PP: Stat,
    power: number,
    accuracy: number
}

export type Mon = {
    name: string,
    stats: {
        level: number,
        HP: Stat,
        Attack: Stat,
        Defence: Stat,
        SpecialAttack: Stat,
        SpecialDefence: Stat,
        Speed: Stat,
        accuracy: Stat,
        evasion: Stat,
    },
    status: {}
    moves: {
        [slot in MoveSlot]: Move
    }
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


import type { BattleEvent, BattleInitEvent, BattleMessage, BattleState, Mon } from '../client/IncomingEvents';
import { baseQueue, queueFromArray } from '../DataWrappers';
import type BattleScene from '../scenes/BattleScene';
import type { WorldScene } from '../scenes/WorldScene';
import { BattleDialogue, BattleOptions } from './battleUI';
import {
    AkkamonEngine
} from './engine/AkkamonEngine';

import { client } from '../../app';

function delay(ms: number) {
    return new Promise(resolve => {
        setTimeout(resolve, ms);
    });
}


export enum BattleEventType {
    INTRODUCTION = "INTRODUCTION"
}

export interface BattleUIEvent {
}

export class InstantUIEvent implements BattleUIEvent {
    constructor(
        public callback: () => void
    ) { }
}

export class DialogueUIEvent implements BattleUIEvent {
    constructor(
        public dialogue: string,
        public callback: () => void
    ) { }
}

export class BattleEngine extends AkkamonEngine {

    public scene?: BattleScene

    private eventsToPlay = baseQueue<BattleEvent>();
    private state: BattleState

    private uiEventTriggers = baseQueue<() => void>();

    private playerName: string
    private opponentName: string

    constructor(
        message: BattleMessage,
    ) {
        super();
        this.state = message.state;
        this.eventsToPlay.pushArray(message.eventsToPlay);

        this.playerName = client.getTrainerID()!.id;
        this.opponentName = this.getOpponentName();

    }

    getOpponentName(): string {
        console.log(this.state.teams);
        for (let key in this.state.teams) {
            if (key !== client.getTrainerID()!.id) {
                return key;
            }
        }
        return '';
    }

    update() {
        if (this.scene && !this.eventsToPlay.isEmpty()) {
            console.log("Playing battle event!");
            console.log(this.eventsToPlay.peek()!);
            this.scene.stopWaitingOnEvent();
            let eventToPlay = this.eventsToPlay.pop()!;
            this[eventToPlay.id](eventToPlay)
        }

        if (!this.scene!.isBusy() && !this.uiEventTriggers.isEmpty()) {
            console.log("Scene is no longer busy! Triggering:");
            console.log(this.uiEventTriggers.peek());
            this.uiEventTriggers.pop()!();
        }
    }

    pushUIEvent(event: () => void) {
        this.uiEventTriggers.push(event);
    }

    [BattleEventType.INTRODUCTION](eventToPlay: BattleEvent) {
        let scene = this.scene!;

        // scene.showPlayerSprites();

        scene.pushEvents(
                         [
                             new DialogueUIEvent(this.opponentName + " wants to fight!", async () => {
                                 scene.removePlayerSprites();
                                 scene.busy = false;
                             }),
                             new DialogueUIEvent(this.opponentName + " sent out " + this.state.teams[this.opponentName].activeMon.name + "!", async () => {
                                 scene.showOpponentMonInterface(this.opponentName, this.state.teams[this.opponentName].activeMon);
                                 scene.busy = false;
                             }),
                             new DialogueUIEvent("Go! " + this.state.teams[this.playerName].activeMon.name + "!", async () => {
                                 scene.showPlayerMonInterface(this.playerName, this.state.teams[this.playerName].activeMon);
                                 scene.busy = false;
                             }),
                         ],
                         () => {
                             scene.clearDialogue();
                             scene.pushMenu(new BattleOptions(
                                                              scene
                                                             ));
                         },
                         new InstantUIEvent(async () => {
                             scene.showPlayerSpritesAndBalls();
                             await delay(1000);
                             scene.busy = false;
                         })
        );


    }

    getActiveMon(): Mon {
        return this.state.teams[this.playerName].activeMon;
    }
}

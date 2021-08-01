import type { BattleEvent, BattleInitEvent, BattleMessage, BattleState } from '../client/IncomingEvents';
import { baseQueue, queueFromArray } from '../DataWrappers';
import type BattleScene from '../scenes/BattleScene';
import type { WorldScene } from '../scenes/WorldScene';
import { BattleDialogue, BattleOptions } from './battleUI';
import {
    AkkamonEngine
} from './engine/AkkamonEngine';

import { client } from '../../app';

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

    constructor(
        message: BattleMessage,
    ) {
        super();
        this.state = message.state;
        this.eventsToPlay.pushArray(message.eventsToPlay);
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
            console.log(this.eventsToPlay.peek()!);
            console.log(BattleEventType.INTRODUCTION);
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

        let opponentName = this.getOpponentName();

        // scene.showPlayerSprites();

        scene.pushEvents(
            [
            new DialogueUIEvent(opponentName + " wants to fight!", () => {scene.busy = false;}),
            new DialogueUIEvent(opponentName + " sent out " + this.state.teams[opponentName].activeMon.name + "!", () => {scene.busy = false;})
            ],
        () => {
            scene.pushMenu(new BattleOptions(
                scene
            ));
        });


    }
}

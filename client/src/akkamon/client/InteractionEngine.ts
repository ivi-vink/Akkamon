import { AkkamonEngine } from '../render/engine/AkkamonEngine';

import type { WorldScene } from '../scenes/WorldScene';

import { baseQueue } from '../DataWrappers';

import {
    InteractionRequestDialogue,
    WaitingDialogue
} from '../scenes/UIElement';

import type {
    IncomingInteractionRequest
} from './IncomingEvents';


export class InteractionEngine extends AkkamonEngine {

    private scene: WorldScene;

    private messageQueue = baseQueue();

    private awaitingInit: boolean = false;

    private waitingForResponseOf: String | undefined;

    private requestBackLog = baseQueue<IncomingInteractionRequest>();

    private answering: boolean = false;

    constructor(scene: WorldScene) {
        super();
        this.scene = scene;
    }

    playerIsBusy() {
        return this.waitingForResponseOf || this.awaitingInit || this.answering
    }

    update() {
        if (!this.requestBackLog.isEmpty()
            && !this.playerIsBusy()
            && this.scene.menus.isEmpty()) {
                let message = this.requestBackLog.pop();
                this.scene.pushMenu(new InteractionRequestDialogue(this.scene, ["YES", "NO"], {name: message!.trainerId, requestType: message!.type}));
        }
    }

    setAwaitingInteractionRequestInitiation(value: boolean) {
        this.awaitingInit = value;
    }

    push(event: IncomingInteractionRequest) {
        // check trainerId
        if (this.awaitingInit) {
            this.waitingForResponseOf = event.requestName;
            this.scene.clearMenus();

            this.scene.pushMenu(new WaitingDialogue(this.scene, new Phaser.GameObjects.Group(this.scene), 20, 'Awaiting player response...'));

            this.awaitingInit = false;
        } else {
            this.requestBackLog.push(event);
        }
    }

}

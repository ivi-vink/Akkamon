import { AkkamonEngine } from '../render/engine/AkkamonEngine';

import type { WorldScene } from '../scenes/WorldScene';

import { baseQueue } from '../DataWrappers';

import type {
    IncomingInteractionRequest
} from './IncomingEvents';


export class InteractionEngine extends AkkamonEngine {

    private messageQueue = baseQueue();

    constructor(scene: WorldScene) {
        super();
    }

    update() {
    }

    setAwaitingResponse() {
    }

    push() {
    }
}

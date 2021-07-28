import Phaser from 'phaser';
import type { Direction } from '../render/Direction';

import { EventType, AkkamonEvent } from './EventType';

// INCOMING EVENTS
// OUTGOING EVENTS
export class PlayerRegistrationRequestEvent implements AkkamonEvent {

    public type: EventType = EventType.TRAINER_REGISTRATION_REQUEST;

    constructor(
    ) { }
}



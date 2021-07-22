import type AkkamonSession from './session';
import { Socket } from './socket';
import type { GridPhysics } from './GridPhysics';
import type { RemotePlayerEngine } from './RemotePlayerEngine';

import {
    EventType,
    HeartBeatReplyEvent,
    IncomingEvent,
    AkkamonEvent,
} from './events';


export class Client
{

    private session: AkkamonSession;
    private gridPhysics?: GridPhysics;
    private remotePlayerEngine?: RemotePlayerEngine;

    constructor(
        url: string
    ) {
        this.session = new Socket(url, this);
    }

    in(eventString: string) {
        let event: IncomingEvent = JSON.parse(eventString);
        switch (event.type) {
            case EventType.HEART_BEAT:
                if (this.remotePlayerEngine !== undefined) {
                    this.remotePlayerEngine.push(event.remoteMovementQueues!);
                }
                this.send(new HeartBeatReplyEvent());
                break;
            case EventType.PLAYERS_NEARBY:
                this.ui.setPlayersNearby();
                break;
            default:
                console.log("ignored incoming event, doesn't match EventType interface.");
                break;
        }
    }

    send(event: AkkamonEvent) {
        // console.log("-> client is now sending out message:");
        // console.log(event)
        if (this.session) {
            this.session.send(JSON.stringify(event));
        }
    }

    setRemotePlayerEngine(engine: RemotePlayerEngine) {
        this.remotePlayerEngine = engine;
    }

}

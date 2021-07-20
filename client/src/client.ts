import type AkkamonSession from './session';
import { Socket } from './socket';
import {
    EventType,
    HeartBeatReplyEvent,
    AkkamonEvent
} from './events';


export class Client
{

    private session: AkkamonSession;

    constructor(
        url: string
    ) {
        this.session = new Socket(url, this);
    }

    setSession(akkamonSession: AkkamonSession) {
        this.session = akkamonSession;
    }

    in(eventString: string) {
        let event: AkkamonEvent = JSON.parse(eventString);
        // console.log("-> client is handling incoming event:");
        // console.log(event);
        switch (event.type) {
            case EventType.HEART_BEAT:
                this.send(new HeartBeatReplyEvent());
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

    login(user: {name:string, password: string}) {
        console.log("Sending the login message");
        if (this.session) {
            this.session.send(JSON.stringify(
                {
                    type: 'login',
                    user: {
                        name: user.name,
                        password: user.password
                    }
                }
            ));
        }
    }
}

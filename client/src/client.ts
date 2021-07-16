import type {
    Event,
} from './events';
import type AkkamonSession from './session';
import GameState from './GameState';


export default class Client
{
    static instance: Client
    session: AkkamonSession | undefined;

    static getInstance() {
        if (Client.instance) return Client.instance;
        else {
            Client.instance = new Client();
            return Client.instance;
        }
    }

    setSession(akkamonSession: AkkamonSession) {
        this.session = akkamonSession;
    }

    in(eventString: string) {
        let event: Event = JSON.parse(eventString);
        // console.log("-> client is handling incoming event:");
        // console.log(event);
        switch (event.type) {
            case 'serverSidePosUpdate':
                GameState.getInstance().posUpdate(event.gameState!);
                break;
        }
    }

    out(event: Event) {
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

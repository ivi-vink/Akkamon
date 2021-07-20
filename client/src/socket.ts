import Phaser from 'phaser';
import type { Client } from './client'
import type AkkamonSession from './session'
import {
    PlayerRegistrationEvent
} from './events';

export class Socket extends WebSocket implements AkkamonSession
{
    constructor(
        url: string,
        client: Client
    ) {
        super(url);

        this.onopen = function echo(this: WebSocket, ev: Event) {
            console.log("opening socket");
            console.log("this is the websocket");
            console.log(this);
            console.log("logging in the session to the server");
            client.send(new PlayerRegistrationEvent());
        }

        this.onmessage = function incomingMessage(this: WebSocket, ev: MessageEvent) {
            //console.log("received message from the server!");
            console.log("-> " + ev.data);
            // console.log("calling client.in:");
            client.in(ev.data);
        }
    }

}

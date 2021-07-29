import Phaser from 'phaser';
import type { Client } from './Client'
import type AkkamonSession from './Session'
import {
    PlayerRegistrationRequestEvent
} from './Events';

export class Socket extends WebSocket implements AkkamonSession
{
    public trainerID?: {id: string, scene: string};

    constructor(
        url: string,
        client: Client
    ) {
        super(url);

        this.onopen = function echo(this: WebSocket, ev: Event) {
            console.log("Sending PlayerRegistrationEvent.");
            client.send(new PlayerRegistrationRequestEvent());
        }

        this.onmessage = function incomingMessage(this: WebSocket, ev: MessageEvent) {
            client.in(ev.data);
        }

        // this.onerror = function socketFailure(this: WebSocket, ev: Event) {
        //     client.retryConnection();
        // }

        this.onclose = function socketClose(this: WebSocket, ev: Event) {
            client.retryConnection();
        }
    }

}

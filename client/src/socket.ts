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
            console.log("Sending PlayerRegistrationEvent.");
            client.send(new PlayerRegistrationEvent());
        }

        this.onmessage = function incomingMessage(this: WebSocket, ev: MessageEvent) {
            client.in(ev.data);
        }
    }

}

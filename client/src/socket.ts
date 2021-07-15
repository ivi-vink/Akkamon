import Phaser from 'phaser';
import Client from './client'
import type AkkamonSession from './session'

export default class Socket extends WebSocket implements AkkamonSession
{
    static instance: AkkamonSession;

    static getInstance(url: string, user: {name: string, password: string}) {
        if (Socket.instance) return Socket.instance;
        else {
            Socket.instance = new Socket(url, user);
            return Socket.instance;
        }
    }

    constructor(url: string, user: {name: string, password: string}) {
        super(url);

        let client = Client.getInstance();

        let session = this;

        this.onopen = function echo(this: WebSocket, ev: Event) {
            console.log("opening socket");
            console.log("this is the websocket");
            console.log(this);
            console.log("logging in the session to the server");
            client.login(user);
        }

        this.onmessage = function incomingMessage(this: WebSocket, ev: MessageEvent) {
            console.log("received message from the server!");
            console.log("-> " + ev.data);
            console.log("calling client.in:");
            client.in(ev.data);
        }
    }

}

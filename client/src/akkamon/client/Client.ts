import type AkkamonSession from './Session';
import { Socket } from './Socket';

import { PlayerSprite } from '../render/model/PlayerSprite';
import { GridPhysics } from '../render/engine/GridPhysics';

import { GridControls } from '../render/GridControls';
import  { UIControls } from '../render/UIControls';


import { RemotePlayerEngine } from '../render/engine/RemotePlayerEngine';

import { InteractionEngine } from './InteractionEngine';

import type { AkkamonClient } from './AkkamonClient';

import type { WorldScene } from '../scenes/WorldScene';

import { DirectionToAnimation } from '../render/DirectionToAnimation';
import { Direction } from '../render/Direction';

function delay(ms: number) {
    return new Promise(resolve => {
        setTimeout(resolve, ms);
    });
}

import {
    EventType,
    AkkamonEvent
} from './EventType'

import type {
    IncomingEvent,
    IncomingInteractionRequest
} from './IncomingEvents';

import {
    HeartBeatReplyEvent,
    Interaction,
    OutgoingInteractionRequestEvent,
    InteractionReplyEvent
} from './OutgoingEvents';


export class Client implements AkkamonClient
{

    private session: AkkamonSession;

    private scene?: WorldScene;
    private gridPhysics?: GridPhysics;
    private controls?: GridControls | UIControls;

    private remotePlayerEngine?: RemotePlayerEngine;

    private interactionEngine?: InteractionEngine

    constructor(
        private url: string
    ) {

        this.session = new Socket(url, this);

    }

    retryConnection() {
        this.tryAgainLater(1000);
        this.session = new Socket(this.url, this);
    }

    async tryAgainLater(ms: number) {
        await delay(ms);
    }

    in(eventString: string) {
        let event: IncomingEvent = JSON.parse(eventString);
        // this[event.type](event);
        // console.log(event);
        switch (event.type) {
            case EventType.HEART_BEAT:
                if (this.remotePlayerEngine !== undefined) {
                    this.remotePlayerEngine.push(event.remoteMovementQueues!);
                }
                this.send(new HeartBeatReplyEvent());
                break;
            case EventType.TRAINER_REGISTRATION_REPLY:
                if (event.trainerId !== undefined) {
                    console.log("setting Session trainerId to: " + event.trainerId);
                    this.session.trainerId = event.trainerId;
                }
                break;
            case EventType.INTERACTION_REQUEST:
                console.log("Received an interaction request!");
                console.log(event);
                this.interactionEngine!.push(event as IncomingInteractionRequest);
                break;
            case EventType.INTERACTION_START:
                console.log("Received interaction starting event!");
                console.log(event);
                if (!this.interactionEngine!.getWaitingForInteractionToStart()) {
                    this.interactionEngine!.setWaitingForInteractionToStart(true);
                    this.interactionEngine!.setWaitingDialogue(`Waiting for ${event.interactionType!} to start...`);
                }
                break;
            default:
                console.log("ignored incoming event, doesn't match EventType interface.");
                console.log(event.type);
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

    updateScene(delta: number): void {
        this.controls!.update();
        this.gridPhysics!.update(delta);
        this.remotePlayerEngine!.update(delta);
        this.interactionEngine!.update();
    }

    setUIControls(input: Phaser.Input.InputPlugin, menu: any) {
        console.log("setting ui controls!");
        this.controls = new UIControls(input, menu);
    }

    async setGridControls() {
        function delay(ms: number) { return new Promise( resolve => setTimeout(resolve, ms) ); }
        await delay(100);
        console.log('setting grid control');
        this.controls = new GridControls(this.scene!.input, this.gridPhysics!);
    }

    requestInitWorldScene(
        scene: WorldScene
    ): void {

        this.scene = scene;

        let playerSprite = new PlayerSprite({
            scene: scene,
            tilePos: new Phaser.Math.Vector2(scene.spawnPointTilePos!),
            texture: scene.textures.get("atlas"),
            frame: "misa-front"
        });

        this.gridPhysics = new GridPhysics(
            playerSprite,
            scene.map!
        );

        this.controls = new GridControls(
            scene.input,
            this.gridPhysics
        );

        this.remotePlayerEngine = new RemotePlayerEngine(
            scene
        );

        this.interactionEngine = new InteractionEngine(
            scene
        );

        this.initAnimation(scene, playerSprite);
    }

    private initAnimation(
        scene: WorldScene,
        player: PlayerSprite
    ): void {

        this.createPlayerAnimation(scene, Direction.LEFT, 0, 3);
        this.createPlayerAnimation(scene, Direction.RIGHT, 0, 3);
        this.createPlayerAnimation(scene, Direction.UP, 0, 3);
        this.createPlayerAnimation(scene, Direction.DOWN, 0, 3);

        // Phaser supports multiple cameras, but you can access the default camera like this:
        const camera = scene.cameras.main;
        camera.startFollow(player);
        camera.roundPixels = true;
        camera.setBounds(0, 0, scene.map!.widthInPixels, scene.map!.heightInPixels);
    }

    private createPlayerAnimation(scene: WorldScene, direction: Direction, start: number, end: number) {
        let characterAnimations = DirectionToAnimation.directionToAnimation['misa'];

        scene.anims.create({
            key: direction, // "misa-left-walk",
            frames: scene.anims.generateFrameNames("atlas", { prefix: characterAnimations[direction] + ".", start: start, end: end, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });

    }

    requestPlayerPixelPosition() {
        return this.gridPhysics!.getPlayerPixelPos();
    }

    requestInitBattle() {
    }

    requestRemotePlayerData() {
        return this.remotePlayerEngine!.getData();
    }

    sendInteractionRequest(interaction: Interaction) {
        console.log("sent an interaction request!");
        console.log(this.getCurrentSceneKey());
        console.log(JSON.stringify(interaction));

        this.interactionEngine!.setAwaitingInteractionRequestInitiation(true);

        this.send(new OutgoingInteractionRequestEvent(
            this.getCurrentSceneKey(),
            interaction
        ));
    }

    getSessionTrainerId() {
        return this.session.trainerId;
    }

    getCurrentSceneKey() {
        return this.scene!.scene.key;
    }

    sendInteractionReply(value: boolean, requestName: string) {
        console.log("Sending interaction reply event!");
        this.interactionEngine!.setAnswering(false);
        this.interactionEngine!.setWaitingForInteractionToStart(true);
        this.send(new InteractionReplyEvent(
            this.getSessionTrainerId()!,
            this.getCurrentSceneKey()!,
            requestName,
            value
        ));
    }
}

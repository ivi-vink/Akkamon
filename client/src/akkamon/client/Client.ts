import type AkkamonSession from './Session';
import { Socket } from './Socket';

import { PlayerSprite } from '../render/model/PlayerSprite';
import { GridPhysics } from '../render/engine/GridPhysics';

import { GridControls } from '../render/GridControls';
import  { UIControls } from '../render/UIControls';


import { RemotePlayerEngine } from '../render/engine/RemotePlayerEngine';

import { InteractionEngine } from './InteractionEngine';

import { BattleEngine } from '../render/BattleEngine';

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
    IncomingInteractionRequest,
    BattleInitEvent,
    Mon
} from './IncomingEvents';

import {
    HeartBeatReplyEvent,
    Interaction,
    OutgoingInteractionRequestEvent,
    InteractionReplyEvent,
    StartMovingEvent,
    NewTilePosEvent,
    StopMovingEvent,
    BattleRequestBody,
    RequestBattleAction,
    BattleActionRequest
} from './OutgoingEvents';

import type BattleScene from '../scenes/BattleScene';
import { BattleControls } from '../render/BattleControls';


export class Client implements AkkamonClient
{

    private session: AkkamonSession;

    private scene?: WorldScene | BattleScene;
    private gridPhysics?: GridPhysics;
    private controls?: GridControls | UIControls | BattleControls;

    private remotePlayerEngine?: RemotePlayerEngine;

    private interactionEngine?: InteractionEngine

    private BattleEngine?: BattleEngine

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
                if (this.remotePlayerEngine !== undefined && event.remoteMovementQueues) {
                    this.remotePlayerEngine.push(event.remoteMovementQueues!);
                }
                this.send(new HeartBeatReplyEvent());
                break;
            case EventType.TRAINER_REGISTRATION_REPLY:
                if (event.trainerID !== undefined) {
                    console.log("setting Session trainerID to: ");
                    console.log(event.trainerID);
                    this.session.trainerID = event.trainerID;
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
            case EventType.BATTLE_INIT:
                console.log("Received battle init");
                console.log(event);
                if (!this.BattleEngine) {
                    this.BattleEngine = new BattleEngine(event.message!);
                    (this.scene! as WorldScene).switchToBattleScene();
                } else {
                    console.log("There was already a battle engine!");
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

    updateBattle(delta: number) {
        this.controls!.update();
        this.BattleEngine!.update();
    }

    setUIControls(input: Phaser.Input.InputPlugin, menu: any) {
        console.log("setting ui controls!");
        this.controls = new UIControls(input, menu);
    }

    async setGridControls() {
        function delay(ms: number) { return new Promise( resolve => setTimeout(resolve, ms) ); }
        await delay(100);
        console.log('setting grid control');
        if (!this.BattleEngine) {
            this.controls = new GridControls(this.scene!.input, this.gridPhysics!);
        }
    }

    requestInitWorldScene(
        scene: WorldScene
    ): void {

        this.scene = scene;

        if (this.BattleEngine) {
            this.BattleEngine = undefined;
        }

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

    pushUIEvent(event: () => void) {
        this.BattleEngine!.pushUIEvent(event)
    }

    requestPlayerPixelPosition() {
        return this.gridPhysics!.getPlayerPixelPos();
    }

    setScene(scene: BattleScene | WorldScene) {
        this.scene = scene;
    }

    battleEngineSceneRegister(scene: BattleScene) {
        this.BattleEngine!.scene = scene;
        this.setScene(scene);
    }

    requestRemotePlayerData() {
        return this.remotePlayerEngine!.getData();
    }

    sendInteractionRequest(interaction: Interaction) {
        console.log("sent an interaction request!");
        console.log(this.getTrainerID());
        console.log(JSON.stringify(interaction));

        this.interactionEngine!.setAwaitingInteractionRequestInitiation(true);

        this.send(new OutgoingInteractionRequestEvent(
            this.getTrainerID()!,
            interaction
        ));
    }

    getTrainerID() {
        return this.session.trainerID;
    }

    sendInteractionReply(value: boolean, requestName: string) {
        console.log("Sending interaction reply event!");
        this.interactionEngine!.setAnswering(false);
        this.interactionEngine!.setWaitingForInteractionToStart(true);
        this.send(new InteractionReplyEvent(
            this.getTrainerID()!,
            requestName,
            value
        ));
    }

    sendStartMove(direction: Direction) {
        this.send(new StartMovingEvent(this.getTrainerID()!, direction));
    }

    sendNewTilePos(tilePos: {x: number, y: number}) {
        this.send(new NewTilePosEvent(
                this.getTrainerID()!, tilePos
            )
        );
    }

    sendStopMoving(direction: Direction) {
        this.send(
            new StopMovingEvent(
                this.getTrainerID()!,
                direction
            )
        );

    }

    setBattleControls(input: Phaser.Input.InputPlugin, menu: any) {
        console.log("setting battle controls!");
        this.controls! = new BattleControls(input, menu);
    }

    getActiveMon(): Mon {
        return this.BattleEngine!.getActiveMon();
    }

    makeBattleRequest(body: BattleRequestBody) {
        this.send(
            new BattleActionRequest(
                this.getTrainerID()!,
                body
            )
        );
    }
}

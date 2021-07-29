import Phaser from 'phaser';

import { client } from '../../app';

import { PauseMenu, AkkamonMenu } from '../scenes/UIElement';

import type {
    BasePhaserScene
} from '../PhaserTypes';

import {
    Stack,
    baseStack
} from '../DataWrappers';

export let TILE_SIZE = 32;

export interface WorldScene extends Phaser.Scene {

    map?: Phaser.Tilemaps.Tilemap
    spawnPoint?: Phaser.Types.Tilemaps.TiledObject;
    spawnPointTilePos?: {
        x: number,
        y: number
    };


    create: () => void

    menus: Stack<AkkamonMenu>;

    menuTakesUIControl: (input: Phaser.Input.InputPlugin, menu: AkkamonMenu) => void

    isUsingGridControls: () => void

    pushMenu: (menu: AkkamonMenu) => void
    popMenu: () => void

    traverseMenusBackwards: () => void

    getPlayerPixelPosition: () => Phaser.Math.Vector2

    getRemotePlayerNames: () => {id: string, scene: string}[]

    requestBattle: (remotePlayerData: {id: string, scene: string}) => void

    clearMenus: () => void

    requestConfirmInteractionReply: (v: boolean, requestName: string) => void
}


export function createWorldScene<PhaserScene extends BasePhaserScene>(scene: PhaserScene, sceneKey: string, mapKey: string, tileSetKey: string) {
    return class WorldScene extends scene {
        map?: Phaser.Tilemaps.Tilemap;

        menus = baseStack<AkkamonMenu>();

        spawnPoint?: Phaser.Types.Tilemaps.TiledObject;
        spawnPointTilePos?: {
            x: number,
            y: number
        };

        constructor(... params: any[]) {
            super(sceneKey);
        }

        create() {
            this.map = this.make.tilemap({ key: mapKey });
            // Parameters are the name you gave the tileset in Tiled and then the key of the tileset image in
            // Phaser's cache (i.e. the name you used in preload)
            const tileset = this.map.addTilesetImage(tileSetKey, "tiles");
            // Parameters: layer name (or index) from Tiled, tileset, x, y
            const belowLayer = this.map.createLayer("Below Player", tileset, 0, 0);
            const worldLayer = this.map.createLayer("World", tileset, 0, 0);
            const aboveLayer = this.map.createLayer("Above Player", tileset, 0, 0);
            // By default, everything gets depth sorted on the screen in the order we created things. Here, we
            // want the "Above Player" layer to sit on top of the player, so we explicitly give it a depth.
            // Higher depths will sit on top of lower depth objects.
            aboveLayer.setDepth(10);

            this.spawnPoint = this.map.findObject("Objects", obj => obj.name === "Spawn Point");

            var tilePos = new Phaser.Math.Vector2(
                Math.floor(this.spawnPoint.x! / TILE_SIZE),
                Math.floor(this.spawnPoint.y! / TILE_SIZE),
                );
            this.spawnPointTilePos = tilePos;

            client.requestInitWorldScene(
                this
            );

            let akey = this.input.keyboard.addKey('a');
            akey.on('down', () => {
            if (this.menus.isEmpty()) {
                this.pushMenu(new PauseMenu(this));
            }
            });

        }

        menuTakesUIControl(input: Phaser.Input.InputPlugin, menu: AkkamonMenu): void {
            client.setUIControls(input, menu);
        }

        isUsingGridControls() {
            client.setGridControls();
        }

        pushMenu(menu: AkkamonMenu) {
            this.menus.push(menu);
            this.menuTakesUIControl(this.input, menu);
            console.log("New menu stack:");
            console.log(this.menus);
        }

        popMenu() {
            return this.menus.pop();
        }

        traverseMenusBackwards() {
            this.popMenu();
            if (!this.menus.isEmpty()) {
                this.menuTakesUIControl(this.input, this.menus.peek()!);
                this.menus.peek()!.setMenuVisible(true);
            } else {
                this.isUsingGridControls();
            }
            console.log("menu stack after traversing back:");
            console.log(this.menus);
        }

        getPlayerPixelPosition(): Phaser.Math.Vector2 {
            return client.requestPlayerPixelPosition();
        }

        getRemotePlayerNames(): Array<{id: string, scene: string}> {
            let remotePlayerData = client.requestRemotePlayerData();
            if (remotePlayerData.size === 0) {
                return [{id: 'Nobody Online', scene: ''}];
            } else {
                let keys = remotePlayerData.keys();
                let trainerIDs: {id: string, scene: string}[] = [];

                for (let key of keys) {
                    let trainerID: { id: string, scene: string }  = JSON.parse(key);
                    trainerIDs.push(trainerID);
                }
                return trainerIDs;
            }
        }

        requestBattle(remoteTrainerID: {id: string, scene: string}): void {
            client.sendInteractionRequest({
                type: "battle",
                receivingtrainerIDs: Array.isArray(remoteTrainerID) ? remoteTrainerID : [remoteTrainerID]
            });
        }

        clearMenus() {
            if (this.menus.size() > 0) {
                this.menus.pop()!.destroyGroup();
                console.log("stack while clearing menus:");
                console.log(this.menus.cloneData());
                this.clearMenus();
            }
        }

        requestConfirmInteractionReply(value: boolean, requestName: string) {
            client.sendInteractionReply(value, requestName);
        }

    }
}

import Phaser from 'phaser';

import { client } from '../../app';

import { PauseMenu, AkkamonMenu } from '../scenes/UIElement';

export let eventsCenter = new Phaser.Events.EventEmitter();


export class AkkamonWorldScene extends Phaser.Scene {
    static readonly TILE_SIZE = 32;

    map?: Phaser.Tilemaps.Tilemap;

    client = client;

    eventsCenter = eventsCenter;

    public spawnPointTilePos?: {
        x: number,
        y: number
    };

    spawnPoint: Phaser.Types.Tilemaps.TiledObject | undefined;

    pauseMenu?: PauseMenu;

    activeMenu?: AkkamonMenu;

    create(mapKey: string, tileSetKey: string) {
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
                Math.floor(this.spawnPoint.x! / AkkamonWorldScene.TILE_SIZE),
                Math.floor(this.spawnPoint.y! / AkkamonWorldScene.TILE_SIZE),
                );
        this.spawnPointTilePos = tilePos;

        this.client.requestInitPlayerSprite(
            this
        );

        let akey = this.input.keyboard.addKey('a');
        akey.on('down', () => {
            this.activeMenu = new PauseMenu(this);
            this.isUsingUIControls();
        });

    }

    isUsingUIControls() {
        this.client.setUIControls();
    }

    isUsingGridControls() {
        this.client.setGridControls();
    }
}

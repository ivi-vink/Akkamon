import Phaser from 'phaser';

import { AkkamonWorldScene } from './AkkamonWorldScene';

export class DemoScene extends AkkamonWorldScene
{
    constructor ()
    {
        super('DemoScene');
    }

    create ()
    {
        this.map = this.make.tilemap({ key: "map" });
        // Parameters are the name you gave the tileset in Tiled and then the key of the tileset image in
        // Phaser's cache (i.e. the name you used in preload)
        const tileset = this.map.addTilesetImage("akkamon-demo-extruded", "tiles");
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
            this.input.keyboard.enabled = false;
            this.eventsCenter.emit('open-menu');
            // this.client.disableGridControls();
        });

    }


    update(time: number, delta: number) {
        this.client.updateScene(delta);
    }


}

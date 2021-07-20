import Phaser from 'phaser';
import { akkamonClient } from './app';
import type { GameState } from './GameState';
import { Player } from './player';
import { PlayerSprite } from './sprite';
import { GridControls } from './GridControls';
import { GridPhysics } from './GridPhysics';
import { Direction } from './Direction';


type RemotePlayerStates = {
    [name: string]: Player
}

export default class AkkamonStartScene extends Phaser.Scene
{

    static readonly TILE_SIZE = 32;

    private akkamonState?: GameState
    private gridPhysics?: GridPhysics
    private gridControls?: GridControls

    directionToAnimation: {
        [key in Direction]: string
    } = {
        [Direction.UP]: "misa-back-walk",
        [Direction.DOWN]: "misa-front-walk",
        [Direction.LEFT]: "misa-left-walk",
        [Direction.RIGHT]: "misa-right-walk",
        [Direction.NONE]: "misa-front-walk"
    }

    remotePlayerSprites: {[name: string]: PlayerSprite} = {};
    spawnPoint: Phaser.Types.Tilemaps.TiledObject | undefined;


    constructor ()
    {
        super('akkamonStartScene');
    }

    preload ()
    {
        this.load.image("tiles", "assets/tilesets/akkamon-demo-extruded.png");
        // load from json!
        this.load.tilemapTiledJSON("map", "assets/tilemaps/akkamon-demo-tilemap.json");

        // An atlas is a way to pack multiple images together into one texture. I'm using it to load all
        // the player animations (walking left, walking right, etc.) in one image. For more info see:

        //  https://labs.phaser.io/view.html?src=src/animation/texture%20atlas%20animation.js
        // If you don't use an atlas, you can do the same thing with a spritesheet, see:
        //  https://labs.phaser.io/view.html?src=src/animation/single%20sprite%20sheet.js
        this.load.atlas("atlas",
                      "assets/atlas/atlas.png",
                          "assets/atlas/atlas.json");
    }


    create ()
    {
        const map = this.make.tilemap({ key: "map" });
        // Parameters are the name you gave the tileset in Tiled and then the key of the tileset image in
        // Phaser's cache (i.e. the name you used in preload)
        const tileset = map.addTilesetImage("akkamon-demo-extruded", "tiles");
        // Parameters: layer name (or index) from Tiled, tileset, x, y
        const belowLayer = map.createLayer("Below Player", tileset, 0, 0);
        const worldLayer = map.createLayer("World", tileset, 0, 0);
        const aboveLayer = map.createLayer("Above Player", tileset, 0, 0);
        // By default, everything gets depth sorted on the screen in the order we created things. Here, we
        // want the "Above Player" layer to sit on top of the player, so we explicitly give it a depth.
        // Higher depths will sit on top of lower depth objects.
        aboveLayer.setDepth(10);

        this.spawnPoint = map.findObject("Objects", obj => obj.name === "Spawn Point");

        //this.createPlayerAnimation(Direction.UP);


        // Create a sprite with physics enabled via the physics system. The image used for the sprite has
        // a bit of whitespace, so I'm using setSize & setOffset to control the size of the player's body.

        this.akkamonState = akkamonClient.getMutableState();

        var tilePos = new Phaser.Math.Vector2(
                Math.floor(this.spawnPoint.x! / AkkamonStartScene.TILE_SIZE),
                Math.floor(this.spawnPoint.y! / AkkamonStartScene.TILE_SIZE),
                );

        let player = new PlayerSprite({
            scene: this,
            tilePos: tilePos,
            texture: this.textures.get("atlas"),
            frame: "misa-front",
            player: new Player({
                trainerId: 'ash',
                position: tilePos
            })// this.akkamonState.getLocalMutablePlayerState(),
        });

        this.add.existing(player);
        this.gridPhysics = new GridPhysics(player, map);
        this.gridControls = new GridControls(
            this.input,
            this.gridPhysics
        );

        this.createPlayerAnimation(Direction.LEFT, 0, 3);
        this.createPlayerAnimation(Direction.RIGHT, 0, 3);
        this.createPlayerAnimation(Direction.UP, 0, 3);
        this.createPlayerAnimation(Direction.DOWN, 0, 3);

        // Phaser supports multiple cameras, but you can access the default camera like this:
        const camera = this.cameras.main;
        camera.startFollow(player);
        camera.roundPixels = true;
        camera.setBounds(0, 0, map.widthInPixels, map.heightInPixels);

    }


    update(time: number, delta: number) {
        this.gridControls!.update();
        this.gridPhysics!.update(delta);
    }

    private createPlayerAnimation(direction: Direction, start: number, end: number) {
        this.anims.create({
            key: direction, // "misa-left-walk",
            frames: this.anims.generateFrameNames("atlas", { prefix: this.directionToAnimation[direction] + ".", start: start, end: end, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });

// anims.create({
// key: "misa-left-walk",
// frames: anims.generateFrameNames("atlas", { prefix: "misa-left-walk.", start: 0, end: 3, zeroPad: 3 }),
// frameRate: 10,
// repeat: -1

    }

}

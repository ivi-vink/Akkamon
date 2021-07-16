import Phaser from 'phaser';
import type Player from './player';
import Client from './client';
import GameState from './GameState';
import PlayerSprite from './sprite';

type RemotePlayerStates = {
    [name: string]: Player
}

export default class AkkamonStartScene extends Phaser.Scene
{
    currentPlayerSprite: PlayerSprite | undefined;
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

        worldLayer.setCollisionByProperty({collides: true});

        // By default, everything gets depth sorted on the screen in the order we created things. Here, we
        // want the "Above Player" layer to sit on top of the player, so we explicitly give it a depth.
        // Higher depths will sit on top of lower depth objects.
        aboveLayer.setDepth(10);

        const spawnPoint = map.findObject("Objects", obj => obj.name === "Spawn Point");
        this.spawnPoint = spawnPoint;

        // Create a sprite with physics enabled via the physics system. The image used for the sprite has
        // a bit of whitespace, so I'm using setSize & setOffset to control the size of the player's body.


        let player = new PlayerSprite({
            scene: this,
            x: spawnPoint.x!,
            y: spawnPoint.y!,
            texture: this.textures.get("atlas"),
            frame: "misa-front",
            player: GameState.getInstance().currentPlayer!,
            moveControls: this.input.keyboard.createCursorKeys()
        });
        this.currentPlayerSprite = player;

        player
        .setSize(30, 40)
        .setOffset(0, 24);

        this.add.existing(player);
        this.physics.add.existing(player);
        // GameState.getInstance().currentPlayer!.setSprite(player);

        this.physics.add.collider(player, worldLayer);
        console.log(player);

        // Create the player's walking animations from the texture atlas. These are stored in the global
        // animation manager so any sprite can access them.
        const anims = this.anims;
        anims.create({
            key: "misa-left-walk",
            frames: anims.generateFrameNames("atlas", { prefix: "misa-left-walk.", start: 0, end: 3, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });
        anims.create({
            key: "misa-right-walk",
            frames: anims.generateFrameNames("atlas", { prefix: "misa-right-walk.", start: 0, end: 3, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });
        anims.create({
            key: "misa-front-walk",
            frames: anims.generateFrameNames("atlas", { prefix: "misa-front-walk.", start: 0, end: 3, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });
        anims.create({
            key: "misa-back-walk",
            frames: anims.generateFrameNames("atlas", { prefix: "misa-back-walk.", start: 0, end: 3, zeroPad: 3 }),
            frameRate: 10,
            repeat: -1
        });


        // Phaser supports multiple cameras, but you can access the default camera like this:
        const camera = this.cameras.main;
        camera.startFollow(player);
        camera.setBounds(0, 0, map.widthInPixels, map.heightInPixels);

        // Debug graphics
        this.input.keyboard.once("keydown_D", (event: Event) => {
            // Turn on physics debugging to show player's hitbox
            this.physics.world.createDebugGraphic();

            // Create worldLayer collision graphic above the player, but below the help text
            const graphics = this.add
            .graphics()
            .setAlpha(0.75)
            .setDepth(20);

            worldLayer.renderDebug(graphics, {
                tileColor: null, // Color of non-colliding tiles
                collidingTileColor: new Phaser.Display.Color(243, 134, 48, 255), // Color of colliding tiles
                faceColor: new Phaser.Display.Color(40, 39, 37, 255) // Color of colliding face edges
            });

        });

        // Constrain the camera so that it isn't allowed to move outside the width/height of tilemap
        camera.setBounds(0, 0, map.widthInPixels, map.heightInPixels);

    }

    update(time: Number, delta: Number) {
        let playerSprite = this.currentPlayerSprite!;

        const speed = 175;
        const prevVelocity = playerSprite.body.velocity.clone();

        // Stop any previous movement from the last frame
        playerSprite.body.setVelocity(0);

        this.moveSprite(playerSprite, speed, prevVelocity)

        // update player state
        playerSprite.player.position = {
            x: playerSprite.x,
            y: playerSprite.y
        }


        if (GameState.getInstance().remotePlayers !== undefined) {
            this.renderRemotePlayers(GameState.getInstance().remotePlayers!);
        }
    }

    renderRemotePlayers(remotePlayers: RemotePlayerStates) {
        console.log(remotePlayers);

        for (let playerName in remotePlayers) {
            if (playerName in this.remotePlayerSprites) {
                this.remotePlayerSprites[playerName].renderUpdate(remotePlayers[playerName].position);
            } else {
                let remotePlayer = remotePlayers[playerName];
                let remotePlayerSprite = new PlayerSprite({
                    scene: this,
                    x: remotePlayer.position.x,
                    y: remotePlayer.position.y,
                    texture: this.textures.get("atlas"),
                    player: remotePlayer,
                });
                this.add.existing(remotePlayerSprite);
                this.remotePlayerSprites[playerName] = remotePlayerSprite;
            }
        }
    }

    moveSprite(playerSprite: PlayerSprite, speed: number, prevVelocity: {x: number, y:number}) {
        let moveControls = playerSprite.moveControls!;
        // Horizontal movement
        if (moveControls.left.isDown) {
            playerSprite.body.setVelocityX(-speed);
        } else if (moveControls.right.isDown) {
            playerSprite.body.setVelocityX(speed);
        }

        // Vertical movement
        if (moveControls.up.isDown) {
            playerSprite.body.setVelocityY(-speed);
        } else if (moveControls.down.isDown) {
            playerSprite.body.setVelocityY(speed);
        }

        // Normalize and scale the velocity so that playerSprite can't move faster along a diagonal
        playerSprite.body.velocity.normalize().scale(speed);

        // Update the animation last and give left/right animations precedence over up/down animations
        if (moveControls.left.isDown) {
            playerSprite.anims.play("misa-left-walk", true);
        } else if (moveControls.right.isDown) {
            playerSprite.anims.play("misa-right-walk", true);
        } else if (moveControls.up.isDown) {
            playerSprite.anims.play("misa-back-walk", true);
        } else if (moveControls.down.isDown) {
            playerSprite.anims.play("misa-front-walk", true);
        } else {
            playerSprite.anims.stop();
            // If we were moving, pick and idle frame to use
            if (prevVelocity.x < 0) playerSprite.setTexture("atlas", "misa-left");
            else if (prevVelocity.x > 0) playerSprite.setTexture("atlas", "misa-right");
            else if (prevVelocity.y < 0) playerSprite.setTexture("atlas", "misa-back");
            else if (prevVelocity.y > 0) playerSprite.setTexture("atlas", "misa-front");
        }
    }

}

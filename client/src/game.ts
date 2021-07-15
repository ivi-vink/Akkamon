import Phaser from 'phaser';
import type Player from './player';
import Client from './client';
import GameState from './GameState';

type Sprite = Phaser.Types.Physics.Arcade.SpriteWithDynamicBody;

type Input = {
    cursors: Phaser.Types.Input.Keyboard.CursorKeys,
}


export default class AkkamonStartScene extends Phaser.Scene
{
    remotePlayers: Array<Player> = new Array();
    constructor ()
    {
        super('akkamonStartScene');
    }

    preload ()
    {
        this.load.image("tiles", "assets/tilesets/akkamon-demo.png");
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
        const tileset = map.addTilesetImage("akkamon-demo-tileset", "tiles");

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

        // Create a sprite with physics enabled via the physics system. The image used for the sprite has
        // a bit of whitespace, so I'm using setSize & setOffset to control the size of the player's body.

        let player = this.physics.add
        .sprite(spawnPoint.x as number, spawnPoint.y as number, "atlas", "misa-front")
        .setSize(30, 40)
        .setOffset(0, 24);

        GameState.getInstance().currentPlayer!.setSprite(player);

        // this.physics.add.collider(player, worldLayer);

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

        let cursors = this.input.keyboard.createCursorKeys();
        GameState.getInstance().currentPlayer!.input = { cursors };

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
        let player = GameState.getInstance().currentPlayer!;
        let playerSprite = player.sprite!;

        let input = player.input;

        const speed = 175;
        const prevVelocity = playerSprite.body.velocity.clone();

        // Stop any previous movement from the last frame
        playerSprite.body.setVelocity(0);

        if (input) {
            this.moveSprite(playerSprite, input, speed, prevVelocity)
        }

        player.position = {
            x: playerSprite.x,
            y: playerSprite.y
        }


        this.renderRemotePlayers(GameState.getInstance().remotePlayers!);
    }

    renderRemotePlayers(renderRemotePlayers) {
    }

    moveSprite(player: Sprite, input: Input, speed: number, prevVelocity: {x: number, y:number}) {

        // Horizontal movement
        if (input.cursors.left.isDown) {
            player.body.setVelocityX(-speed);
        } else if (input.cursors.right.isDown) {
            player.body.setVelocityX(speed);
        }

        // Vertical movement
        if (input.cursors.up.isDown) {
            player.body.setVelocityY(-speed);
        } else if (input.cursors.down.isDown) {
            player.body.setVelocityY(speed);
        }

        // Normalize and scale the velocity so that player can't move faster along a diagonal
        player.body.velocity.normalize().scale(speed);

        // Update the animation last and give left/right animations precedence over up/down animations
        if (input.cursors.left.isDown) {
            player.anims.play("misa-left-walk", true);
        } else if (input.cursors.right.isDown) {
            player.anims.play("misa-right-walk", true);
        } else if (input.cursors.up.isDown) {
            player.anims.play("misa-back-walk", true);
        } else if (input.cursors.down.isDown) {
            player.anims.play("misa-front-walk", true);
        } else {
            player.anims.stop();
            // If we were moving, pick and idle frame to use
            if (prevVelocity.x < 0) player.setTexture("atlas", "misa-left");
            else if (prevVelocity.x > 0) player.setTexture("atlas", "misa-right");
            else if (prevVelocity.y < 0) player.setTexture("atlas", "misa-back");
            else if (prevVelocity.y > 0) player.setTexture("atlas", "misa-front");
        }
    }

}

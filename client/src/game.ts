import 'phaser';

export default class Demo extends Phaser.Scene
{
    constructor ()
    {
        super('demo');
    }

    preload ()
    {
        this.load.image("tiles", "assets/catastrophi_tiles_16_blue.png");
        this.load.tilemapCSV("map", "assets/catastrophi_level2.csv");
    }

    create ()
    {
        const map = this.make.tilemap({ key: "map", tileWidth: 16, tileHeight: 16 });
        const tileset = map.addTilesetImage("tiles");
        const layer = map.createLayer(0, tileset, 0, 0); // layer index, tileset, x, y
    }
}

const config = {
    type: Phaser.AUTO,
    backgroundColor: '#125555',
    width: 800,
    height: 600,
    scene: Demo
};

const game = new Phaser.Game(config);

import 'phaser';

interface AkkamonScene extends Phaser.Scene {
    preload: () => void
    create: () => void
    update: (time: Number, delta: Number) => void
}

const config = {
    type: Phaser.AUTO,
    backgroundColor: '#125555',
    width: 800,
    height: 600,
    pixelArt: true,
    scene: {
        preload: preload,
        create: create,
        update: update
    }
};

const game = new Phaser.Game(config);


function preload ()
{

}

function create ()
{

}

function update(time: Number, delta: Number)
{

}

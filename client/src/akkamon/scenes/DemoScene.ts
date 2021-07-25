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
        super.create("map", "akkamon-demo-extruded");
    }


    update(time: number, delta: number) {
        this.client.updateScene(delta);
    }


}

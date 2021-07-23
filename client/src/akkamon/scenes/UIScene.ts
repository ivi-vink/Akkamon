import { UIControls } from './uiControls';

export class AkkamonUI extends Phaser.Scene
{

    private uiControls: UIControls;

    constructor ()
    {
        super('AkkamonUI');
    }

    preload() {
    }

    create () {
        this.uiControls = new UIControls(this.input);
    }

}

import { client } from '../../app';

export class BattleScene extends Phaser.Scene {

    client = client;


    constructor() {
        super('BattleScene')
    }

    create() {
        this.client.requestInitBattle(this);
    }

    update() {
    }
}

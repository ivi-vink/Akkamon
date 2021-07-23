import Phaser from 'phaser';

import { client } from '../../app';

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

}

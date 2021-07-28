import Phaser from 'phaser';

import type {
    BasePhaserScene
} from '../PhaserTypes';

import {
    WorldScene,
    createWorldScene
} from './WorldScene';

let DemoScene = createWorldScene(Phaser.Scene, "DemoScene", "map", "akkamon-demo-extruded");

export default DemoScene;

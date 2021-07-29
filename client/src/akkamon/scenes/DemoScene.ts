import Phaser from 'phaser';

import { client } from '../../app';

import type {
    BasePhaserScene
} from '../PhaserTypes';

import {
    WorldScene,
    createWorldScene
} from './WorldScene';

function updatable<Scene extends BasePhaserScene>(scene: Scene) {
    return class DemoScene extends scene {
        update(time: number, delta: number) {
            client.updateScene(delta);
        }
    }
}

let DemoScene = updatable(createWorldScene(Phaser.Scene, "DemoScene", "map", "akkamon-demo-extruded"));

export default DemoScene;

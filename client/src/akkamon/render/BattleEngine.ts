import type BattleScene from '../scenes/BattleScene';
import type { WorldScene } from '../scenes/WorldScene';
import {
    AkkamonEngine
} from './engine/AkkamonEngine';

export class BattleEngine extends AkkamonEngine {

    constructor(
        public scene: BattleScene
    ) {
        super();
    }

    update() {
    }
}

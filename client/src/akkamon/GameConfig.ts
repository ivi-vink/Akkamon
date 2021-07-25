import {
    DemoScene
} from './scenes/DemoScene';
import {
    BootScene
} from './scenes/BootScene';


export const gameConfig: Phaser.Types.Core.GameConfig & Phaser.Types.Core.RenderConfig = {
    type: Phaser.AUTO,
    backgroundColor: '#125555',
    width: 800,
    height: 600,
    pixelArt: true,
    scene: [BootScene, DemoScene]
};

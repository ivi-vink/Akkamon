import DemoScene from './scenes/DemoScene';
import BootScene from './scenes/BootScene';
import BattleScene from './scenes/BattleScene';


export const gameConfig: Phaser.Types.Core.GameConfig & Phaser.Types.Core.RenderConfig = {
    type: Phaser.AUTO,
    backgroundColor: '#FFFFFF',
    width: 800,
    height: 750,
    pixelArt: true,
    scene: [BootScene, DemoScene, BattleScene]
};

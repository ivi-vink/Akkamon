import Phaser from 'phaser';
import type Player from './player';
import type GameState from './GameState';

export interface Event {
    type: string
    gameState?: GameState
}


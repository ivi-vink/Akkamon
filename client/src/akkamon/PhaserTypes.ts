// type Constructor = new (...args: any[]) => {};
export type GConstructor<T = {}> = new (...args: any[]) => T;
export type BasePhaserScene = GConstructor<Phaser.Scene>

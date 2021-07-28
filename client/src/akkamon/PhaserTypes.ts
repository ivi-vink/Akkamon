// type Constructor = new (...args: any[]) => {};
type GConstructor<T = {}> = new (...args: any[]) => T;
export type BasePhaserScene = GConstructor<Phaser.Scene>

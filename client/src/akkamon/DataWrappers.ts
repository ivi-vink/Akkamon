
export interface Queue<T> {
    _data: T[]
    push: (ele: T) => void
    pushArray: (ele: T[]) => void
    pop: () => T | undefined
    isEmpty: () => boolean
    peek: () => T | undefined
}

export function baseQueue<T>(): Queue<T> {
    return {
        _data: <T[]> [],

        push: function (element: T): void {
            this._data.push(element);
        },

        pushArray: function (arr: T[]) {
            for (let ele of arr) {
                this._data.push(ele);
            }
        },

        pop: function (): T | undefined {
            return this._data.shift();
        },

        peek: function (): T | undefined {
            return this._data[0];
        },


        isEmpty: function (): boolean {
            return this._data.length == 0;
        },
    }
}

export function queueFromArray<T>(arr: T[]): Queue<T> {
    let base: Queue<T> = baseQueue();
    base._data = arr;
    return base;
}

export interface Stack<T> {
    _data: T[]
    pop: () => T | undefined
    peek: () => T | undefined
    push: (element: T) => void
    size: () => number
    isEmpty: () => boolean
    clear: () => void
    cloneData: () => T[]

}

export function baseStack<T>(): Stack<T> {
    return {
        _data: <T[]> [],

        size: function (): number {
            return this._data.length;
        },

        pop: function (): T | undefined {
            return this._data.pop();
        },

        peek: function (): T | undefined {
            return this._data[this.size() - 1];
        },

        push: function (ele: T): void {
            this._data.push(ele);
        },

        isEmpty: function (): boolean {
            return this.size() == 0;
        },

        clear: function (): void {
            this._data = [];
        },

        cloneData: function (): T[] {
            return [... this._data];
        }
    }
}

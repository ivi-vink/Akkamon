
export class Queue<T> {
    private _data = new Array();

    constructor(data?: Array<T>) {
        if (data !== undefined) {
            this._data = data;
        }
    }

    push(element: T): void {
        this._data.push(element);
    }

    pushArray(arr: T[]): void {
        for (var element of arr) {
            this._data.push(element);
        }
    }

    pop(): T | undefined {
        return this._data.shift();
    }

    isEmpty(): boolean {
        return this._data.length == 0;
    }

    peek() {
        return this._data[0];
    }
}

export class Stack<T> {
    private _data = new Array();

    constructor(data?: Array<T>) {
        if (data !== undefined) {
            this._data = data;
        }
    }

    pop(): T | undefined {
        return this._data.pop();
    }

    push(element: T): void {
        this._data.push(element);
    }

    peek() {
        if (this.length === 0) {
            return undefined;
        } else {
            return this._data[this.length - 1];
        }
    }

    get length() {
        return this._data.length;
    }

    isEmpty() {
        return this.length === 0;
    }

    clear() {
        this._data = new Array();
    }

    cloneData() {
        return new Array(this._data);
    }

}

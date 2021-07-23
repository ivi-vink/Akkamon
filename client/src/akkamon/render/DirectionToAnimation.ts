import { Direction } from './Direction';

export class DirectionToAnimation {
    static readonly directionToAnimation: {
        [name: string]: { [key in Direction]: string }
    } = {misa:
            {
            [Direction.UP]: "misa-back-walk",
            [Direction.DOWN]: "misa-front-walk",
            [Direction.LEFT]: "misa-left-walk",
            [Direction.RIGHT]: "misa-right-walk",
            [Direction.NONE]: "misa-front-walk"
            }
    }
}

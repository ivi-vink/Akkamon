import type Player from './player';

export default interface AkkamonSession extends WebSocket {
    trainerId?: string

}

interface User {
    name: string
    password: string
}

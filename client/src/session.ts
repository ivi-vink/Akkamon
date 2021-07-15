import type Player from './player';

export default interface AkkamonSession extends WebSocket {
    user?: User
}

interface User {
    name: string
    password: string
}

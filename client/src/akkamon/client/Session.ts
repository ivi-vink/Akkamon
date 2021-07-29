import type {
    TrainerID
} from './OutgoingEvents';

export default interface AkkamonSession extends WebSocket {
    trainerID?: TrainerID;
}

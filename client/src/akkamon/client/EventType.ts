export interface AkkamonEvent {
    type: EventType
}

export enum EventType {
    HEART_BEAT = "HeartBeat",
    TRAINER_REGISTRATION_REQUEST = "TrainerRegistrationRequestEvent",
    TRAINER_REGISTRATION_REPLY = "TrainerRegistrationReplyEvent",
    START_MOVING =  "StartMoving",
    STOP_MOVING = "StopMoving",
    NEW_TILE_POS = "NewTilePos",
    INTERACTION_REQUEST = "InteractionRequestEvent",
    INTERACTION_REPLY = "InteractionReplyEvent",
    INTERACTION_ABORTED = "InteractionAbortedEvent",
    INTERACTION_START = "InteractionStarting"
}


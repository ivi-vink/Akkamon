import { client } from '../../../app';


export class AkkamonEngine {
    client = client;

    update(delta?: number) {
        throw new Error("update must be implemented in Engines");
    }
}

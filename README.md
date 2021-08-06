# Akkamon: a demo pokemon mmo written in Phaser3 typescript and Akka Java

![core-idea](./readme-media/header.gif)

> "One trainer is no trainer."

In akkamon the goal was to let many users interact with each other in parallel like in a typical MMO game, in this case we copy the pokemon formula and make it an MMO.

- [x] You can walk around the world and see your friends walking
- [x] You can invite your friend to start an interaction with you
- [x] You can start a battle with somebody else that is online
- [ ] You can actually play the pokemon battle (UNDER CONSTRUCTION)

Like the actual battle, many things that you would expect from a pokemon MMO were not within the scope of the project. The Game Engine [^gameengine], User Interface, Websockets API, and Actor model backend had to written from scratch, which took a lot of time.



# Install from source

## Before you start

Starting the installation requires a couple things:
* Java JDK version 16.0.1 preferably
* Gradle 7.1.1 was used in this project, but `./gradlew` provided should work instead of `gradle` in the installation commands
* A recent version of Node.JS with npm

## Install steps

1. Use npm to install client side dependencies (for example phaser3, typescript, rollup)

```sh
cd client
npm i
```

# Start playing


# Tests (or lack thereof)


[^gameengine]: Not completely from scratch, but I had to add stuff to connect it with the Websockets communication.

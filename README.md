# SoPra Group 04 FS23 - STADT LAND **+**

Welcome to Stadt Land Plus! This project is the result of our Software Lab class at the University of Zurich, where we were tasked with the challenge of developing a software application. We decided to create a game, taking inspiration from the classic Swiss game, Stadt Land Fluss, also known as 'Categories' in English.

## Introduction
Embark on a thrilling journey with Stadt Land Plus, a captivating multiplayer word game! By brainstorming words within chosen categories and a surprise random letter, players are immersed in a realm of fast-paced learning and fun. The unique twist? A democratic voting system that brings a dash of unpredictability while guaranteeing fairness in validating answers. Get ready to ignite your mind!
## Motivation

Our motivation behind this game was to design a multiplayer experience that not only provides entertainment but also educational value. By choosing categories and brainstorming words, players can broaden their general knowledge and improve quick thinking skills.

## Technologies

The back-end is written in Java and uses Spring Boot framework. We use JPA for persistence and the deployment is
handled by Google Cloud. Communication between the server and the client is done with REST and websockets.


## High-level components

Stadt Land Plus revolves around three key components: [Controller](https://github.com/sopra-fs23-group-4/sopra-fs23-group-04-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller) classes, [Service](https://github.com/sopra-fs23-group-4/sopra-fs23-group-04-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs23/service) classes, and [Entity](https://github.com/sopra-fs23-group-4/sopra-fs23-group-04-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity) classes. The Controller classes manage all REST calls and Websocket communications, acting as the bridge between client and server. The Service classes encapsulate the core game logic, responding to controller requests and managing game states and flow. The quoteservice class uses an external api [Ninjas-api](https://api-ninjas.com/) that allow the user to generate personal quotes and to recieve fun facts. Lastly, the Entity classes define the main game objects and data structures, linking everything together. These components collectively ensure a seamless and engaging gaming experience.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

## Roadmap

- Death Mode: Player with the least points after a round is eliminated
- Algorithm to check certain categories for correctness e.g. city and country to improve fairness
- Possibility to play non-synchronized


## Authors and acknowledgement

SoPra Group 04 2023 consists of [Valentin Meyer](https://github.com/VaLeoMe), [Remo Wiget](https://github.com/wigeto), [Christopher Narayanan](https://github.com/Queentaker), [Lennart Tölke](https://github.com/LexuTros) and [Alexandre Bacmann](https://github.com/ABacmann).

We want to thank our teaching assistant [Hyeongkyun (Kaden) Kim](https://github.com/hk-kaden-kim) for his help and guidance during the project.

## License

Licensed under [Apache License 2.0](https://github.com/sopra-fs23-group-4/sopra-fs23-group-04-server/blob/main/LICENSE)

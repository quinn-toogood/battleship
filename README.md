# Battleship State Machine

This project was created by Quinn Toogood in support of their application. This codebase contains a state machine that
manages a game of battleship.

This project was built using IntelliJ IDEA community, but should be openable on any java IDE. As per spec, .idea files
have been included.

## Setup and Build

This project contains the maven wrapper, so can be built and tested with:

```bash
./mvnw clean install
```

The main method is empty. The functionality of the state tracker can instead be inspected by the unit tests.

## Tests

Tests can be executed separately with the maven test phase:

```bash
./mvnw test
```
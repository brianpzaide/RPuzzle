# RPuzzle
RPuzzle is an Android puzzle app inspired by the Rubik Electronic game, extended to include other topological surfaces Torus, Klien-bottle, Real Projective Plane.

![app image](docs/main.png)

The objective of RPuzzle is to match the current board pattern to that of the target board in as few moves as possible. The app implements three different board types:

* Torus
* Klien Bottle
* Real Projective Plane

Each board type adds a layer of complexity from top to bottom.

### Architecture

![architecture image](docs/architecture.png)

RPuzzle follows the MVVM (Model-View-ViewModel) architecture pattern:

- `MainActivity.kt`: Listens for user click events and passes these events as messages to a Kotlin channel.
- `PlayRPuzzleViewModel.kt`: Contains the LiveData and handles the business logic.
- `Puzzle.kt`: LiveData object.
- `RBoardAdvanced.kt`: Contains the game logic.
- `RBoardView.kt`: This file contains the game board that uses Android canvas.

MainActivity listens for user click events passes these events as messages to a kotlin channel,
a background coroutine listens on this channel, and is the only one that changes the game state,
MainActivity observes on the game state changes and updates the UI.

This architecture ensures separation of concerns and facilitates maintainability and extensibility.

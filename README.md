![ascii Trouble logo](https://i.imgur.com/IicIKx0.png)

This is a command line implementation of the board game **Trouble**. I created it to practice programming in java.

⚠️Warning⚠️ The game will not work as intended when run in an IDE's output window or a non-windows terminal. For the best experience, please run the game in the terminal on a computer running Windows.

## To Play

Clone this repository:

`git clone https://github.com/Ferrington/trouble.git`

Descend into the `/trouble` directory and build the program:

`javac -d target src/main/java/org/trouble/*.java`

Then run the program:

`java -cp ./target org.trouble.Main`

## Design Process

### Goals

I started out with a couple of goals:

- Create the game of Trouble playable via the command line
- The game should clearly represent its state visually -- no imagination required

### Is it feasible?

When I started, I wasn't sure if I could display the game board in a satisfying manner. So the first thing I did was create a mock up in a text editor.

![ascii trouble board](https://i.imgur.com/F7ff88D.png)

When I was satisfied with my board, I moved on to testing its rendering. I wanted to keep the board in a static position each time I updated its state. This is achievable by clearing the terminal and redrawing the board each time the game's state changes. I tested this by creating an animation to mimic the iconic "pop-o-matic" die roll.

This is a bit out of order, but here's what the game ended up looking like for comparison.
![final game board](https://i.imgur.com/FFOmfM5.png)

### State

Next up was deciding how to store the game's state. I ended up with the following variables:

`normalSpaces` - This is an array that stores the 28 spaces that all players can traverse and interact with each other on.
`finishLineSpaces` - This is a multidimensional array representing the final 4 spaces for each of the 4 players.
`homePegs` - This is an array that keeps track of how many pegs each player still has in reserve.

Pretty simple right? A challenge presents itself when you map those variables onto the board.

![sketch of Trouble board showing absolute numbered spaces](https://i.imgur.com/p79Ght3.png)

Consider the blue player's next move. They're going to cross over the bounds of the array and overflow back around to the start. That's not too tricky to deal with thanks to our friend the modulo operator, but turn your attention to red now. Each time a player moves, their position must be compared to the position of their finish line and that will lead to some messy conditionals.

### Relative State

I decided I would translate the board into a single path at the beginning of each player's turn.

![sketch of Trouble board showing relative numbered spaces](https://i.imgur.com/BN4oDgE.png)

When red's turn arrives now, it's trivial to decide where they can move. As long as their destination space isn't beyond the edge of the path or already occupied by a red peg, they are good to go! I can then translate the board back into absolute positions and update the state variables.

### Class - Or lack thereof

I tried to come up with some sort of scheme to pretend to be an object oriented programmer. It went ok... until it didn't.

![sketch of game logic flow](https://i.imgur.com/AU1ZqgU.png)

I began by coming up with the basic logic of the game. The Main class is simply the entry point to the game. The Game class will deal with setting up the game and checking for a winner after every turn. The player will play. This seems ok, but what about the board? Where do I render that? Ok... I'll add a board class.

![sketch of class skeletons](https://i.imgur.com/v2q01X5.png)

This is from pretty early on before I had all the methods figured out. I decided I wanted a class that would take care of the IO because I watched a youtube video on dependency injection once. I added a Board class to deal with maintaining the game's state and moving the pegs and whatnot.

What ended up happening in the end was that the Board class ended up doing most of the heavy lifting. I created an enum called PlayerColor that seemed like good design until it started appearing in places that the Player class probably should have. In short, things got a bit messy as I got close to the finish line.

### Conclusion

The game works! I played it with my wife and she says I did good. I'm happy with some of my design decisions. Hopefully as I gain more experience I will gain a better sense of what belongs in each class and how to separate responsibilities in a more defined manner.

## Backlog
The backlog follows a MoSCoW-style prioritization.

### Must have
- Users must be able to set their name when entering the application
- Users must be able to create and join game lobbies that:
  - can be either singleplayer or multiplayer
  - if multiplayer, land the player in a lobby while waiting for other players
  - automatically start a game when the required number of players joined
- Users must always be able to join a default public game room
- Games must consist of a set of questions that:
  - can be answered anonymously by players in a set amount of time
  - reward a set amount of points tallied throughout the game
  - have the correct answer shown after the set time elapses
- Games must show a leaderboard during and after the game concludes
- Users must be able to use power ups during games, such as:
  - reducing opponents’ time
  - get question hints / remove one wrong answer
  - increasing/decreasing the amount of points for the question
  - displaying current answers to the question (given by other players)
### Should have
- Users should be able to authenticate themselves with a username and password
- Users should be able to create their own, password-protected game rooms
- Users should be able to spectate games without partaking
- Users should be AFK-checked before entering the game
- Users should be ranked in a leaderboard based on:
  - the amount of games they won
  - number of games they’ve played
  - average place (w/ min amount of games required)
  - Game rooms should show current player standings
- Questions should be of different types, namely:
  - multiple choice
  - ranking
  - timed (you can answer multiple within a set period of time)
  - map-based
  - distance-based (i.e. people closest to the correct answer get the most points)
  - match (i.e. multiple questions need to be matched to answers)
- Users should be able to apply custom configs to rooms. With features like:
  - changing the points for a wrong answer
  - changing the points for a correct answer
  - changing the time available for a question
  - changing the difficulty of the questions
  - changing the number of questions
  - changing the type of questions available
  - change the visibility of the room
- Users should get bonus points for answering multiple questions in a row correctly
- Users should be able to select reactions to the questions
- Users should be able to leave a room safely

### Could have
- Users could have an option to see a map with players performance around the world
- Users could be able to recover their authentication details
- Users could be able to use text chat while in the lobby/game
- Users could choose to mute other players in their lobby/game
- Users could have profile pages containing:
  - description/bio
  - profile image
  - profile background
  - ranking
  - nationality
- Users could share links to lobbies to let other users join
- Games could have different modes (gamemodes):
  - standard
  - burn (answer as many during a set period of time)
  - lives (the game goes on indefinitely until you run out of lives)
  - team (group up in teams and vote on answers)
- Games could be based on a seed (string) where:
  - the seed determines the questions and their order
  - users can initiate games with custom seeds
- Rooms could have an elo system:
  - users start out with a base elo,
  - each room is assigned an elo based on the elo of the players in the room,
  - based on their elo and rank users are granted or subtracted points

### Won’t have
- Users won’t be able to play games offline
- Users won’t be able to play other games in the app
- Users won’t be given roles with special privileges
- Game settings won’t be mutable after the game has started
- Users won’t be able to friend other users
- Questions won’t have answers that do not pertain to energy consumption estimation

### Non-functional requirements
- the backend utilizes Java X with Spring on Gradle
- the frontend utilizes OpenJFX for the UI/UX
- the server-side application exposes a REST api
- the server-side application is containerized using Docker and deployed on AWS
- the game flow (incl. the lobby) is based on websocket for bi-directional communication
- the client-side application supports Windows 10 & 11

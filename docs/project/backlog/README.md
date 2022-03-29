# Backlog
The backlog follows a MoSCoW-style prioritization.

## Must have
- [x] Users must be able to set their name when entering the application
- [x] Users must be able to create and join game lobbies that:
  - [x] can be either singleplayer or multiplayer
    - [ ] if singleplayer, the game should be started automatically
    - [x] if multiplayer, the player should land in a lobby while waiting for other players
  - [x] can be started when the lobby cappacity is reached
- [x] Players must be able to answer questions:
  - [x] questions are shown one after another to all players in set intervals
  - [ ] answers to the questions are shown after each question
  - [x] reward a set amount of points tallied throughout the game
- [ ] Users must always be able to join a default public game room
- [ ] Games must show leaderboards, specifically:
  - [ ] a game leaderboard after the game concludes
  - [ ] a global leaderboards with best players
  - [ ] a past-N games leaderboard

## Should have
- [x] Users should have accounts and be authenticated:
  - [x] Users should be able to register
  - [x] Users should be able to log in
  - [ ] Users should be able to delete their accounts
- [ ] Users should be able to create their own, password-protected game rooms
- [ ] Users should be able to spectate games without partaking
- [ ] Users should be AFK-checked before entering the game
- [ ] Users should be ranked in a leaderboard based on different metrics:
  - [x] the amount of games they won
  - [ ] number of games they’ve played
  - [ ] average place (w/ min amount of games required)
- [ ] Game rooms should show current player standings
- [ ] Questions should be of different types, namely:
  - [x] multiple choice
  - [ ] ranking
  - [ ] timed (you can answer multiple within a set period of time)
  - [ ] map-based
  - [ ] distance-based (i.e. people closest to the correct answer get the most points)
  - [ ] match (i.e. multiple questions need to be matched to answers)
- [ ] Users should be able to apply custom configs to rooms. With features like:
  - [x] changing the points for a wrong answer
  - [x] changing the points for a correct answer
  - [x] changing the time available for a question
  - [ ] changing the difficulty of the questions
  - [x] changing the number of questions
  - [ ] changing the type of questions available
  - [x] change the visibility of the room
- [x] Users should get bonus points for answering multiple questions in a row correctly
- [ ] Users should be able to select reactions to the questions
- [x] Users should be able to leave a room safely
- [ ] Users must be able to use power ups during games, such as:
  - [ ] reducing opponents’ time
  - [ ] get question hints / remove one wrong answer
  - [ ] increasing/decreasing the amount of points for the question
  - [ ] displaying current answers to the question (given by other players)
- [ ] Users should be able to upload:
  - [ ] activities with images
  - [ ] questions linked to activities
## Could have
- [ ] Users could have an option to see a map with players performance around the world
- [ ] Users could be able to recover their authentication details
- [ ] Users could be able to use text chat while in the lobby/game
- [ ] Users could choose to mute other players in their lobby/game
- [ ] Users could have profile pages containing:
  - [ ] description/bio
  - [ ] profile image
  - [ ] profile background
  - [ ] ranking
  - [ ] nationality
- [ ] Users could share links to lobbies to let other users join
- [ ] Games could have different modes (gamemodes):
  - [x] normal
  - [ ] survival (the game goes on indefinitely until you run out of lives)
  - [ ] burn (answer as many during a set period of time)
  - [ ] team (group up in teams and vote on answers)
- [ ] Games could be based on a seed (string) where:
  - [ ] the seed determines the questions and their order
  - [ ] users can initiate games with custom seeds
- [ ] Rooms could have an elo system:
  - [ ] users start out with a base elo,
  - [ ] each room is assigned an elo based on the elo of the players in the room,
  - [ ] based on their elo and rank users are granted or subtracted points

## Won’t have
- Users won’t be able to play games offline
- Users won’t be able to play other games in the app
- Users won’t be given roles with special privileges
- Users won’t be able to friend other users
- Game settings won’t be mutable after the game has started
- Questions won’t have answers that do not pertain to energy consumption estimation

## Non-functional requirements
- backend utilizes Java 17 with JavaFX with Spring on Gradle
- frontend utilizes OpenJFX and FontAwesome for the UI/UX
- SLF4J is used for logging
- server-side application exposes a REST api
- server-side application is containerized using Docker:
  - the application runs in an isolated container, with the REST API exposed externally
  - the Postgres database is ran as a Docker service and exposed on `:5555`
  - the activity images are stored in a spearate container
- the server is deployed on AWS and accessible remotely
- the game flow (incl. the lobby) is based on REST and SSE, giving bi-directional communication
- the client-side application supports Windows 10 and Linux (Ubuntu, Mint)

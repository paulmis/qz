# Week 4 retrospective meeting

|           |                                                                                                                                                |
| --------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| Location  | Online (Discord)                                                                                                                               |
| Date      | 06/03/2022                                                                                                                                     |
| Time      | 17:00 - 18:00                                                                                                                                  |
| Attendees | Paul Misterka  <br> Giacomo Pezzali<br> Alexandru Cojocaru (Note taker) <br> David Dinucu-Jianu <br>  Aakanksh Singh <br>Rok Štular (Chairman) |


| Stories                                            | Tasks               | Assignees | Estimated time | Time spent | Done |
| -------------------------------------------------- | ------------------- | --------- | -------------- | ---------- | ---- |
| Users must be able to create and join game lobbies | Create the lobby UI | Paul      | 4              | 5          | Yes  |

| Stories                                               | Tasks                                               | Assignees                                 | Estimated time | Time spent | Done | Notes                             |
| ----------------------------------------------------- | --------------------------------------------------- | ----------------------------------------- | -------------- | ---------- | ---- | --------------------------------- |
| Games must consist of a set of questions              | Question generation                                 | Paul                                      | 5              | 5          | Yes  |                                   |
| Games must consist of a set of questions              | Implement a SSE controler                           | Rok                                       | 4              | 10         | Yes  |                                   |
| Games must consist of a set of questions              | Notify clients whether the answer is correct or not | Giacomo                                   | 4              | 3          | No   | Waiting for SSE management        |
| Setup authentication                                  | API endpoints for authentication                    | Paul                                      | 4              | 3          | Yes  | Issue mostly implemented in W3    |
| Users must be able to set their name                  | Nickname and picture screen                         | Alex                                      | 5              | 3          | Yes  | Functionality to be done in W5    |
| Users must be able to create and join game lobbies    | Game creation endpoint                              | Paul                                      | 3              | 1          | No   |                                   |
| Users must be able to create and join game lobbies    | Game joining on front-end                           | Paul                                      | 4              | 0          | No   | Pre-requisites unfinished         |
| Game should contain a leaderboard                     | Implement global leaderboard API endpoints          | Rok                                       | 1              | 4          | No   | In review                         |
| Game should contain a leaderboard                     | Create global leaderboard UI                        | David                                     | 2              | 1          | Yes  |                                   |
| Game should contain a leaderboard                     | Implement Global leaderboard in client              | David                                     | 2              | 3          | No   |                                   |
| Support for player-determined configuration           | Game configuration UI                               | David                                     | 3              | 5          | Yes  |                                   |
| Documentation                                         | Update CoC                                          | Giacomo                                   | 3              | 3          | Yes  |                                   |
| Documentation                                         | HCI final version                                   | Aakanksh, Alex, David, Giacomo, Paul, Rok | 15             | 15         | Yes  |                                   |
| Documentation                                         | Lucid charts to repo                                | Aakanksh                                  | 2              | 1          | No   | Issue might be deleted            |
| Test                                                  | Implement front end testing                         | Aakanksh                                  | 5              | 0          | No   |                                   |
| Users should be able to apply custom configs to rooms | Game configuration endpoint                         | Aakanksh                                  | 4              | 0          | No   |                                   |
| Users must be able to answer questions                | API endpoints for answering and getting a question  | Aakanksh                                  | 4              | 6          | No   | Integrating other issue into this |
| API                                                   | API design                                          | Everyone                                  |                |            |      |                                   |
| API                                                   | Spawn a default multiplayer room indefinitely       | Aakanksh, Paul                            | 4              | 0          | No   |                                   |
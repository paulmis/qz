                       # W7 retrospective

|   |                                                                                                                                              |
|---|----------------------------------------------------------------------------------------------------------------------------------------------|
| Location  | Online (Discord)                                                                                                                             |
| Date		| 28/03/2022                                                                                                                                   |
| Time		| 13:00 - 14:00                                                                                                                              |
| Attendees	| Paul Misterka (Chairman)<br> Giacomo Pezzali <br> Alexandru Cojocaru <br> David Dinucu-Jianu (Note taker)<br>  Aakanksh Singh<br>Rok Štular|

Week 7 was a really productive week. We had a really well thought out plan that enabled us to work efficiently and in parallel. We solved a lot of major issues and implemented a lot of core functionality. The MVP is almost ready and we are optimistic that we will accomplish a good overall product at the end of the course.

| Stories                                                                                                                                                              | Tasks                                                                                                                                                                                                   | Assignees                 | Estimated time | Time spent | Done   | Notes                       |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- | -------------- | ---------- | ------ | --------------------------- |
| [Allow players to safely leave a game](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/35)              | [Notify players about player changes](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/155)                                                 | Paul                      | 5              | 0          | No     |                             |
|                                                                                                                                                                      | [Allow hosts to disband games](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/159)                                                        | Aakanksh                  | 3              | 8          | Yes    | Expanded issue              |
|                                                                                                                                                                      | [Allow players to leave lobbies and games](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/124)                                            | Aakanksh                  | 4              | 6          | Yes    |                             |
| [Users must be able to use power ups during games](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/9)   | [Implement point doubling power-up](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/162)                                                   | Aakanksh                  | 4              | 0          | No     |                             |
|                                                                                                                                                                      | [Allow user to register](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/86)                                                       | Alex                      | 6              | 7          | Yes    | Delayed from W6             |
| [Users must be able to join lobbies](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/5)                 | [Implement a join random lobby button](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/108)                                        | Alex                      | 4              | 5          | Review |                             |
|                                                                                                                                                                      | [Initialize lobby scene with game details](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/110)                                    | Giacomo                   | 5              | 7          | Review |                             |
| [Games must consist of a set of questions](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/7)           | [Display questions and send answers](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/160)                                                  | Paul                      | 6              | 15         | Yes    | Fixed a lot of other issues |
|                                                                                                                                                                      | [Calculate and persist players' scores](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/128)                                               | David                     | 5              | 5          | Yes    |                             |
|                                                                                                                                                                      | [Notify players about game start](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/154)                                                     | David                     | 4              | 3          | Yes    |                             |
|                                                                                                                                                                      | [Support uploading activity pictures](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/137)                                                 | Rok                       | 3              | 10         | Review |                             |
|                                                                                                                                                                      | [Handle normal game's flow](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/120)                                                           | Rok                       | 8              | 10         | Yes    |                             |
|                                                                                                                                                                      | [Show correct answers](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/161)                                                                | Rok                       | 5              | 4          | No     |                             |
|                                                                                                                                                                      | [Add activities + pictures to the repository](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/180)                                         | Rok                       | 1              | 1          | Yes    |                             |
|                                                                                                                                                                      | [Convert answer choice to long](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/95)                                                | Giacomo                   | 1              | 1          | Yes    |                             |
|                                                                                                                                                                      | [Store users' answers](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/63)                                                         | Giacomo                   | 4              | 2          | Yes    | Delayed from W6             |
|                                                                                                                                                                      | [Improve test coverage for Answer entity](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/182)                                             | Giacomo                   | 3              | 0          | No     |                             |
| [Users must be able to create and join game lobbies](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/5) | [Design a game creation scene](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/156)                                                        | David                     | 2              | 4          | Yes    |                             |
|                                                                                                                                                                      | [Implement the game creation scene](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/157)                                                   | David                     | 5              | 6          | Review |                             |
|                                                                                                                                                                      | [Implement survival games](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/173)                                                            | David                     | 6              | 0          | No     |                             |
|                                                                                                                                                                      | [Implement private rooms](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/163)                                                             | David                     | 6              | 0          | No     |                             |
| [Create text chat](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/43)                                  | [Design Chat Controls](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/merge_requests/106)                                                        | Giacomo                   | 4              | 4          | Review |                             |
| Enhancement                                                                                                                                                          | [Safely store JWT token](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/114)                                                              | Aakanksh                  | 2              | 1          | No     |                             |
|                                                                                                                                                                      | [Support booleans and enums in automatic control generation](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/169)                          | Rok                       | 3              | 0          | No     |                             |
| Documentation                                                                                                                                                        | [Update backlog](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/171)                                                                      | Paul                      | 2              | 2          | Review |                             |
|                                                                                                                                                                      | [Add coverage badges](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/166)                                                                 | Paul                      | 2              | 0          | No     |                             |
|                                                                                                                                                                      | [W7 docs](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/184)                                                                             | Paul, David               | \-             | 2          | \-     |                             |
|                                                                                                                                                                      | [Game wiki](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/170)                                                                           | Paul, Rok, David, Giacomo | 8              | 2          | No     |                             |
|                                                                                                                                                                      | [API design](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/82)                                                                           | Everyone                  |                |            |        |                             |
|                                                                                                                                                                      | [Update endpoint collection](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/127)                                                          | Everyone                  |                |            |        |                             |
| Bug                                                                                                                                                                  | [Polish the game experience](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/183)                                                          | Paul                      | \-             | 3          | No     |                             |
|                                                                                                                                                                      | [Project does not build after #158 branch was merged](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/174)                                 | Rok                       | 1              | 1          | Yes    |                             |
|                                                                                                                                                                      | [\`/api/user\` is returning hashed user password](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/175)                                     | Rok                       | 1              | 1          | Yes    |                             |
|                                                                                                                                                                      | [When creating a new lobby, missing parameters don't get populated by default values](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/176) | Rok                       | 1              | 1          | Yes    |                             |
|                                                                                                                                                                      | [Failed authentication leads to 500 instead of 401](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template/-/issues/106)                                   | Paul                      | 3              | 1          | Yes    |                             |

## Encountered problems

We've created an `EasyRetro` [dashboard](https://easyretro.io/publicboard/CT4Ca6EZB7U2TUK1JmjMGGw1ln43/c3a0b94f-633b-470d-ae98-4bd4fd6d66b4) where everybody contributed their retrospective for the week.
### Official backlog confusion
We are not sure of the importance that the official backlog has for the grading of the project.

### A bit behind
We are a bit behind but if we continue as this pace we will finish the project and have time for more extra features.

## Adjustments for the next sprint plan
- Finish the MVP and start looking working towards the finished product.
- Discuss the confusion regarding the backlog with the TA
- Continue the pace of this week.
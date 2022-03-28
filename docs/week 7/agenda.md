## Agenda W7

|   |                                                                                                                            |
|---|----------------------------------------------------------------------------------------------------------------------------|
| Meeting: | W7 review meeting                                                                                                          |
| Location:| Drebbelweg PC1 backroom                                                                                                    |
|Datum:| 22/03/2022                                                                                                                 |
|Time: | 16:45 – 17:15                                                                                                              |
|Attendees: | Cojocaru Alexandru, Dinucu-Jianu David (Chairman), Misterka Paul, Pezzali Giacomo, Singh Aakanksh (Note taker), Rok Štular |


#### W6 retrospective
Our week 6 spring had quite a lot of issues. We didn't make a significant improvement over the last week, and had some problems with:

- planning
  - we didn't have a plan in place for the planning meeting
  - the plan wasn't very comprehensive
  - the scrum master wasn't tracking the progress throughout the week
- code coverage
  - high server-side code coverage (>90%)
  - low client-side code coverage (almost no tests)
  - planning to include more tests
- communication
  - we didn't plan the retrospective meeting in advance
  - the tasks for the week weren't clear

To aid the process in W7, we'll be delaying Aakanksh's chairman tenure to W8 so that Paul can take over and make sure we have a good plan, leading to an MVP.

#### W7 planning

This week we'll be pushing 2 major and 3 auxiliary areas of features:
- major:
  - `game management`: making sure that users can manage their lobbies and games, see relevant data, and interact with the UI
  - `game flow and questions`: having the server send game `SSE` events in an organized manner so that they can be interpreted and acted upon by the client
- auxiliary:
  - `game and question variations`: implementing survival games and estimate questions with different logic
  - `power-ups`: implementing power-up points, related `SSE` events and endpoints, logic, and UI elements
  - `chat`: implementing chat `SSE` events and endpoints, UI controls, and class structure

We'd like to create a playable game, and we'll be working on more advanced (should/could have) features, paving way for full implementation in W8 and W9.

#### Discussion
- how to approach testing on the client-side?
- are we on the same page when it comes to answer formats?

#### Questions to the TA
- can we get gitlab CI runner codes?
- when can we expect the HCI document to be graded?
- are we supposed to implement all must-haves from the template backlog?
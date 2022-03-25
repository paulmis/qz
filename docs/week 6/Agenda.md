![](RackMultipart20220321-4-qljfo4_html_b0984596b89043cd.png)

# Format agenda

**Agenda W6**

Location: Drebbelweg PC1 backroom

Datum: 15/03/2022

Time: 16.45 – 17.30

**Attendees**: 
Cojocaru Alexandru (Chairman)
Dinucu-Jianu David
Misterka Paul
Pezzali Giacomo
Singh Aakanksh
Rok Stular (Note taker)

**Agenda-items**

[16:45-16:50] **Opening by chair**

[16:45-16:50] **Check-in**

[16:45-16:50] **Buddy-check remarks**

[16:50-16:55] **Thoughts on performance this week**

- Performance was hindered by exams
- We need to push the MVP in order to have time to finish features

[16:55-17:10]_ **Present back-end progress** _

- Populate the database with activities and MCQuestions
- Notify clients wether answer is correct or not
- {Fix} game configuration DTO
- Docker container for DB
- Change activity DTO to include all fields from the question bank

[17:10-17:20] **Present front-end progress**

- Started implementing functionality on client
- Started Draft {Client to server communication}
- Automated Screen Scaling in the client

[17:20-17:30] **Plan for W6**

- Main goal : Get the single player game working
    - Finish client side functionalities, being able to answer to a question and communicating with the server.
    - On server side, familiarise with SSE and finish communication with 1 client
    - Documentation needs to be updated, UML chart and endpoint collection
- Finish delayed issues from last week
- Decide if to use spring 5 webclient instead of jakarta
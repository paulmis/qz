# Taking minutes

## Agenda for the team meeting OOPP 60

|   |   |
|---|---|
| Location:| Drebbelweg PC1 backroom |
|Datum:| 22/02/2022|
|Time: |16:45 – 17:30|
|Attendees: | Cojocaru Alexandru, Dinucu-Jianu David (Chairman), Misterka Paul, Pezzali Giacomo, Singh Aakanksh (Note taker), Rok Štular |

## Agenda items

**Opening by Chairperson**
_No minutes._

**Announcements**
Feedback for backlog and code of conduct will be received later.

**Approval of the agenda**
No additions. Agenda approved by all members.

**Present back-end and front-end progress – Update TA and Members**
- Update TA with back-end and front-end progress
- Using REST API primarily as websocket is discouraged for this project
- Progress looks good
- Ahead of schedule with coding

**Gitlab best practices – Educate all members and align understanding**
- Everyone perform at least one code review:
    o Don’t just say “good work”
    o Suggest improvements and not change in complete code
- Use the wiki for documentation

**API Endpoints – Important to discuss now for base structure**
- Spring security can take care of authentication
- Create a lucid charts to visualize API paths
- Create a state machine to show client reaction to server action and vice-versa
- SSE used for sending events to the client immediately; anything that requires constant
    polling. Websockets make REST redundant hence using SSE
       o Power-ups
       o Emojis
- Go over API design

**HCI Draft – Deadline upcoming**
- Design a logo
- Alex and David have groups to review our draft
- Everyone has to individually finish the information literacy

**Summary of action points**
Action points (Issues) have been assigned or will be assigned with its respective deadlines and are available on Gitlab.

**Feedback round**
From the TA:
- Equalize work in terms of Gitlab reviews
- Define “Done” as a issue status
- Have at least one API endpoint for GET, PUSH, PUT, DELETE
    o PUT should be idempotent
    o PUSH can modify and depend on the state
- One feature, one issue. Make smaller issues
- One or two members assigned max
- Time indication for the issues
- Everyone should to do MR. MR should include tests too otherwise needs to be worked on.
- Work with pipeline
- Merge to main every week before TA meeting

From the members:
- Assign merge requests to each other for equalized code review opportunities

**Any questions?**
- What is the different between HCI draft and Final?
    o Final is HCI draft + review from the other group

**Closing**
_No minutes_



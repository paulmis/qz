## About QZ
*Note: this proejct has been migrated from [GitLab](https://gitlab.ewi.tudelft.nl/)*

QZ is a single and multiplayer game which quizzes players to teach awareness about energy consuption. The objective of every game is to answer as many questions about energy consumption correctly as possible. To make the experience interesting, we've created a lot of features and configuration options, giving you the chance to play the game you want. QZ lets you:
- easily create accounts and log in
- play singleplayer quizz games to learn about everyday energy consumption
- make multiplayer lobbies to invite other users to play with
- answer different type of questions, including multiple choice and estimate questions
- play different types of power-ups that let you e.g. halve the remaning time to answer the question
- send cool reactions and message other people
- look up leaderboards that show how you did compared to other people

### Culture and design principles

We've created this project over the duration of a 10-week introductory software engineering course at TU Delft using Spring Boot (REST & SSE) for the backend and JavaFX for the frontend. Our main goal was to learn how to conceptualize, design, and implement a fun and engaging application together, and learn how to structure our work in short, weekly sprints. From the technical side, our app was designed to be:
- engaging - we interviewed numerous stakeholders, refined requirements, and carefully designed all components to make sure the game is fun to play
- extendible - we boast a comprehensive class hierarchy that allows for quick and painless extension
- maintainable - we used style checking, static analysis, and thoroughly reviewed MRs tools to ensure high code quality
- feature-rich - we put in a lot of time to figure out and implement cool features that go beyond the course backlog

## Development team

| Picture                                                                               | Name                       | Email                             |
| --------------------------------------------------------------------------------------------- | -------------------------- |-----------------------------------|
| ![](https://avatars.githubusercontent.com/u/34619913?v=4&size=50)                             | David Dinucu-Jianu         | D.Dinucu-Jianu@student.tudelft.nl |
| ![](https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50) | Rok Štular                 | R.Stular@student.tudelft.nl       |
| ![](https://avatars.githubusercontent.com/u/45182027?v=4&size=50)                             | Paul Misterka              | P.M.Misterka@student.tudelft.nl   |
| ![](https://secure.gravatar.com/avatar/065ab34531af46f9d554ea8c2067a07d?s=50&d=identicon)     | Alexandru-Gabriel Cojocaru | A.G.Cojocaru-2@student.tudelft.nl |
| ![](https://avatars.githubusercontent.com/u/99262358?size=50)                                 | Giacomo Pezzali            | G.Pezzali@student.tudelft.nl      |
| ![](https://secure.gravatar.com/avatar/fabe2c215ecceecd352547f2c5fbbef7?s=50&d=identicon)     | Aakanksh Singh             | A.Singh-27@student.tudelft.nl     |

## How to run it
### Set up the environment

1. Download and install prerequisites:
    * Download and install [IntelliJ](https://www.jetbrains.com/idea/).
        * Note that to use some of the features discussed in the Wiki you'll need the Ultimate edition.
    * Download and install [JavaFX](https://gluonhq.com/products/javafx/).
    * On Windows:
        * Download and install [WSL2](https://docs.microsoft.com/en-us/windows/wsl/install).
    * Download and install [Docker](https://docs.docker.com/get-docker/).
    * Download and install [Python 3](https://www.python.org/downloads/).
2. Clone this repository with `git clone https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template.git`
3. Initialize the project following the instructions below.
4. From the repository folder, build the project with `gradlew build` to verify that the source is valid.

Additionally, these tools will aid you in development and are heavily recommended:
- [Scene Builder](https://gluonhq.com/products/scene-builder/#download) - UI builder for `OpenJFX` frameworks.
- [Postman](https://www.postman.com/downloads/) - state-of-the-art API testing tool.


### Getting it started

The Quizzzz app consists of three components: the database, the server and the client.
Each of these must be run one after another:

#### Database

##### Windows

1. Open a command line window (`cmd` from the Start menu on Windows).
2. `cd` into the repository directory.
3. Spin up the docker container with `docker-compose up -d`
4. In `Docker Desktop` you should see the `oopp` container with a `postgres` subcontainer running at `:5432`
5. Verify the connection with `IntelliJ`'s database module, `psql`, or `DBeaver`
    * the connection details are provided in the `docker-compose.yml` file located in the root project directory

##### Linux

1. Spin up the docker container with `docker-compose up -d`
2. Verify that the connection is running with `docker ps`
   * The output should look similar to this:
   ```
    CONTAINER ID   IMAGE      COMMAND                  CREATED         STATUS         PORTS                                       NAMES
    7e917b97e54a   postgres   "docker-entrypoint.s…"   4 seconds ago   Up 3 seconds   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp   repository-template-db-1
    ```
3. Verify the connection with `IntelliJ`'s database module, `psql`, or `DBeaver`
   * the connection details are provided in the `docker-compose.yml` file located in the root project directory

#### Server:

1. Run the server.
   - If you are running the server from IntelliJ:
     1. Run the `Main` java file in the `/server/src/main/java` directory
         * This may take a few minutes during the initial build/run
     2. The server has launched successfully if the log ends with `Started Main in ... seconds (JVM running for ...)`
         * if you are getting a `PSQLException` when running the server or the tests, then the database connection is failing
   - Otherwise:
     1. Open a command line window (`cmd` from the Start menu on Windows).
     2. `cd` to the repository directory.
     3. Execute `gradlew bootRun` (`./gradlew bootRun` on Linux or MacOS).
2. If you are running the server for the first time, populate the database (see the section).

#### Client:

- If you are running the client from IntelliJ:
  1. Edit run/debug configuration the `Main` file in `/client/src/main/java`
  2. Add `--module-path="<LIB PATH IN JAVAFX FOLDER WHICH WAS DOWNLOADED>"--add-modules=javafx.controls,javafx.fxml` to `VM options/arguments`
  3. Run the `Main` file in `/client/src/main/java`
- Otherwise:
  1. Open a new command window.
  2. `cd` to the repository directory.
  3. Execute `gradlew run` (`./gradlew run` on Linux or MacOS), the client will spawn in the background.

### Populating the database

In order to populate the database, a helper script is provided: `populate_db.py`.
To run it, you will need the activity bank (included in `./activities` directory),
as well as an emoji bank (provided in `./reactions`).

1. Open a new command window.
2. `cd` to the repository directory.
3. Run `pip install requests` to install the `requests` [library](https://docs.python-requests.org/en/latest/).
4. Run `python populate_db.py -c 100 ./activities ./reactions`. The script will import all activities (images included) and reactions.

This script supports more configuration options; for a list and a brief description of the functionality run `python populate_db.py --help`.

## Migration
This project has been migrated from the EWI GitLab with the consent of all team members. The issues and merge requests have been ported using [this helper tool](https://github.com/piceaTech/node-gitlab-2-github). Note that a tiny number of issues and some issue comments had to be omitted, and merge requests have been converted into issues as their respective branches don't exist anymore.

## Copyright / License

##### Apache License 2.0:
* [Apache Commons](https://www.apache.org/licenses/LICENSE-2.0.txt) (frontend)
* [AssertJ](https://github.com/assertj/assertj-core) (backend)
* [Awaitility](https://github.com/awaitility/awaitility) (backend)
* [Guava](https://github.com/google/guava) (frontend + backend)
* [Jackson](https://github.com/FasterXML/jackson) (backend)
* [ModelMapper](https://github.com/modelmapper/modelmapper) (backend)
* [Spring](https://github.com/spring-projects/spring-boot) (backend)

##### Eclipse Public License 2.0:
* [JUnit 5](https://github.com/junit-team/junit5) (backend)

##### GNU General Public License:
* [OpenJavaFX](https://github.com/openjdk/jfx) (frontend)
* [Spring Boot](https://github.com/spring-projects/spring-boot) (backend)

##### MIT License:
* [Auth0](https://github.com/auth0/java-jwt) (backend)
* [Mockito](https://github.com/mockito/mockito) (backend)

##### Other:
* [Lombok](https://github.com/projectlombok/lombok) (frontend + backend)

## Description of project
This project is a quizzz app. The quizzz app is a singleplayer and multiplayer game which quizzes players to increase energy awareness. This project is made using the Spring BOOT REST API for the backend and OpenJavaFX for the frontend. Many design and feature choices were made to make this project intuitive and unique. The following details gives an overview of our choices:
`<ADD APP DETAILS HERE, SUCH AS FEATURES AND MODES>`

## Group members

| Picture                                                                               | Name                       | Email                             |
| --------------------------------------------------------------------------------------------- | -------------------------- |-----------------------------------|
| ![](https://avatars.githubusercontent.com/u/34619913?v=4&size=50)                             | David Dinucu-Jianu         | D.Dinucu-Jianu@student.tudelft.nl |
| ![](https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50) | Rok Štular                 | R.Stular@student.tudelft.nl       |
| ![](https://avatars.githubusercontent.com/u/45182027?v=4&size=50)                             | Paul Misterka              | P.M.Misterka@student.tudelft.nl   |
| ![](https://secure.gravatar.com/avatar/065ab34531af46f9d554ea8c2067a07d?s=50&d=identicon)     | Alexandru-Gabriel Cojocaru | A.G.Cojocaru-2@student.tudelft.nl |
| ![](https://avatars.githubusercontent.com/u/99262358?size=50)                                 | Giacomo Pezzali            | G.Pezzali@student.tudelft.nl      |
| ![](https://secure.gravatar.com/avatar/fabe2c215ecceecd352547f2c5fbbef7?s=50&d=identicon)     | Aakanksh Singh             | A.Singh-27@student.tudelft.nl     |

<!-- Instructions (remove once assignment has been completed -->
<!-- - Add (only!) your own name to the table above (use Markdown formatting) -->
<!-- - Mention your *student* email address -->
<!-- - Preferably add a recognizable photo, otherwise add your GitLab photo -->
<!-- - (please make sure the photos have the same size) -->

## How to run it
### Set up the environment

1. Download and install prerequisites:
    * Download and install [IntelliJ](https://www.jetbrains.com/idea/)
        * Note that to use some of the features discussed in the Wiki you'll need the Ultimate edition
    * Download and install [JavaFX](https://gluonhq.com/products/javafx/)
    * On Windows:
        * Download and install [WSL2](https://docs.microsoft.com/en-us/windows/wsl/install)
    * Download and install [Docker](https://docs.docker.com/get-docker/)
2. Clone this repository with `git clone https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/team-repositories/oopp-group-60/repository-template.git`
3. Build the project with `gradle build` to verify that the source is valid

Additionally, these tools will aid you in development and are heavily recommended:
- [Scene Builder](https://gluonhq.com/products/scene-builder/#download) - UI builder for `OpenJFX` frameworks
- [Postman](https://www.postman.com/downloads/) - state of the art API testing tool


### Getting it started

The Quizzz app consists of three components: the database, server, and client. Each of these must be run one after another:

#### Database

##### Windows

1. `cmd` into the repository directory
2. Spin up the docker container with `docker-compose up -d`
3. In `Docker Desktop` you should see the `oopp` container with a `postgres` subcontainer running at `:5432`
4. Verify the connection with `IntelliJ`'s database module, `psql`, or `DBeaver`
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

1. Populate the database (see the section)
2. Populate emoji list (see the section)
3. Run the `Main` java file in the `/server/src/main/java` directory
   * This may take a few minutes during the initial build/run
4. The server has launched successfully if the log ends with `Started Main in ... seconds (JVM running for ...)`
    * if you are getting a `PSQLException` when running the server or the tests, then the database connection is failing

#### Client:

1. Edit run/debug configuration the `Main` file in `/client/src/main/java`
    * Add `--module-path="<LIB PATH IN JAVAFX FOLDER WHICH WAS DOWNLOADED>"--add-modules=javafx.controls,javafx.fxml` to `VM options/arguments`
2. Execute `gradle run` (`./gradlew run` on MacOS and Linux) or run `Main` in `/client` to launch the client 
   * the client will spawn in the background

### Populating the database

In order to populate the database, a helper script is provided: `populate_db.py`. To run it, you will need the activity bank (included in `./activities` directory), as well as an emoji bank (provided in `./reactions/`).

Then, run: `python populate_db.py -c 100 ./activities/ ./reactions/`. The script will import all activities (images included) and reactions. Uploading images requires the `requests` [Python library](https://docs.python-requests.org/en/latest/).

For more configuration options run `python populate_db.py --help`.

## How to contribute to it
`<TBA>`

## Copyright / License

##### Apache License 2.0:
* [Apache Commons](https://www.apache.org/licenses/LICENSE-2.0.txt) (frontend)
* [AssertJ](https://github.com/assertj/assertj-core) (backend)
* [Awaitility](https://github.com/awaitility/awaitility) (backend)
* [Guava](https://github.com/google/guava) (?)
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

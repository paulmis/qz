## Description of project
This project is a quizzz app. The quizzz app is a singleplayer and multiplayer game which quizzes players to increase energy awareness. This project is made using the Spring BOOT REST API for the backend and OpenJavaFX for the frontend. Many design and feature choices were made to make this project intuitive and unique. The following details gives an overview of our choices:
`<ADD APP DETAILS HERE, SUCH AS FEATURES AND MODES>`

## Group members

| Profile Picture                                                                               | Name                       | Email                             |
| --------------------------------------------------------------------------------------------- | -------------------------- | --------------------------------- |
| ![](https://avatars.githubusercontent.com/u/34619913?v=4&size=50)                             | David Dinucu-Jianu         | D.Dinucu-Jianu@student.tudelft.nl |
| ![](https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50) | Rok Štular                 | R.Stular@student.tudelft.nl       |
| ![](https://avatars.githubusercontent.com/u/45182027?v=4&size=50)                             | Paul Misterka              | P.M.Misterka@student.tudelft.nl   |
| ![](https://secure.gravatar.com/avatar/065ab34531af46f9d554ea8c2067a07d?s=50&d=identicon)     | Alexandru-Gabriel Cojocaru | A.G.Cojocaru-2@student.tudelft.nl |
| ![](https://avatars.githubusercontent.com/u/99262358?size=50)                                 | Giacomo Pezzali            | G.Pezzali@student.tudelft.nl      |
| ![](https://secure.gravatar.com/avatar/fabe2c215ecceecd352547f2c5fbbef7?s=50&d=identicon)     | Aakanksh Singh             | A.singh-27@student.tudelft.nl     |

<!-- Instructions (remove once assignment has been completed -->
<!-- - Add (only!) your own name to the table above (use Markdown formatting) -->
<!-- - Mention your *student* email address -->
<!-- - Preferably add a recognizable photo, otherwise add your GitLab photo -->
<!-- - (please make sure the photos have the same size) -->

## How to run it


## How to setup development environment
Setting up environment:
1. Resources required for development:
    * Install an IDE of your choice ie. Intellij IDEA, Eclipse, etc.
    * Download [JavaFX](https://gluonhq.com/products/javafx/)
    * Download [Scene Builder](https://gluonhq.com/products/scene-builder/#download)
    * Download [Postman (HTTP Requests Testing Tool)](https://www.postman.com/downloads/)
2. Clone this repository
3. Import the repository folder into your chosen IDE
4. This will take a few minutes to initialize
5. You can start development

The quizzz app consists of two components; a client and a server. Both of these must be run seperatly in order to work, with the serve running first and then the client. 

Running the server:
1. Run the `Main` java file in the `server` folder
2. This may take a few minutes during the initial build/run
3. In the command line it should say `started main` in the last lines
4. The server is now running

Running the client:
1. Edit run/debug configuration the`Main` file in the `client` folder
2. Click on VM options/arguments and add the following:
`--module-path="<LIB PATH IN JAVAFX FOLDER WHICH WAS DOWNLOADED>"--add-modules=javafx.controls,javafx.fxml`
3. Run the `Main` java file in the `client` folder
4. The client is now running and is interactable

## How to contribute to it
`<TBA>`

## Copyright / License
GNU General Public License:
* [OpenJavaFX](https://github.com/openjdk/jfx) (frontend)
* [Spring Boot](https://github.com/spring-projects/spring-boot) (backend)

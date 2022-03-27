package client.scenes.leaderboard;

import commons.entities.auth.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import lombok.Generated;
import org.fxyz3d.importers.obj.ObjImporter;
import org.fxyz3d.shapes.primitives.Text3DMesh;


/**
 * Leaderboard controller class controls the inside leaderboard.
 */
@Generated
public class LeaderboardCtrl implements Initializable {

    @FXML private VBox vboxScrollPaneLeaderboard;
    @FXML private Group group;
    @FXML private ScrollPane scrollPaneLeaderboard;
    @FXML private Box firstBox;
    @FXML private Box secondBox;
    @FXML private Box thirdBox;
    @FXML private Circle firstImage;
    @FXML private Circle secondImage;
    @FXML private Circle thirdImage;
    @FXML private Label firstText;
    @FXML private Label secondText;
    @FXML private Label thirdText;
    @FXML private VBox usersBox;
    @FXML private Text3DMesh first3DText;
    @FXML private Text3DMesh second3DText;
    @FXML private Text3DMesh third3DText;

    private Timer rotationTimer;
    private Group cupModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void createCup() {

        try {
            ObjImporter importer = new ObjImporter();
            cupModel = importer.loadAsPoly(getClass().getResource("/client/models/WinnerCup.png")).getRoot();
            cupModel.setTranslateX(500);
            cupModel.setTranslateY(180);
            cupModel.setTranslateZ(-200);
            cupModel.setScaleX(0.1);
            cupModel.setScaleY(0.1);
            cupModel.setScaleZ(0.1);

            cupModel.setRotationAxis(new Point3D(1, 0, 1));
            cupModel.setRotate(180);
            final int[] counter = {0};
            rotationTimer = new Timer();

            rotationTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        matrixRotateNode(cupModel, Math.PI, 0, Math.PI / 180 * (counter[0]++ - 180));
                        counter[0] %= 360;
                    });
                }
            }, 0, 50);

            javafx.application.Platform.runLater(() -> group.getChildren().add(cupModel));


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * JavaFX 3d has no support for multiple 3d rotations
     * at the same time without computing the unit vector
     * yourself. Soo this function does that.
     * From: https://stackoverflow.com/questions/30145414/rotate-a-3d-object-on-3-axis-in-javafx-properly
     *
     * @param n the node we rotate
     * @param alf the x angle in radians we apply.
     * @param bet the y angle in radians we apply.
     * @param gam the z angle in radians we apply.
     */
    private void matrixRotateNode(Node n, double alf, double bet, double gam) {
        double a11 = Math.cos(alf) * Math.cos(gam);
        double a12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double a13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double a21 = -Math.cos(gam) * Math.sin(alf);
        double a22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double a23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double a31 = Math.sin(gam);
        double a32 = -Math.cos(gam) * Math.sin(bet);
        double a33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((a11 + a22 + a33 - 1d) / 2d);
        if (d != 0d)  {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((a32 - a23) / den, (a13 - a31) / den, (a21 - a12) / den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }

    /**
     * This function stops the rotation timer.
     */
    public void stop() {
        rotationTimer.cancel();
    }


    /**
     * This function resets the leaderboard.
     *
     * @param leaderboard the leaderboard element.
     */
    public void reset(List<UserDTO> leaderboard) {
        if (cupModel != null) {
            group.getChildren().remove(cupModel);
        }

        usersBox.getChildren().clear();

        var boxes = List.of(firstBox, secondBox, thirdBox);
        boxes.forEach(box -> box.setVisible(false));

        var images = List.of(firstImage, secondImage, thirdImage);
        images.forEach(image -> image.setVisible(false));

        var texts = List.of(firstText, secondText, thirdText);
        texts.forEach(text -> text.setVisible(false));

        var texts3D = List.of(first3DText, second3DText, third3DText);
        texts3D.forEach(text -> text.setVisible(false));

        var materials = List.of(new PhongMaterial(Color.GOLD),
                new PhongMaterial(Color.SILVER),
                new PhongMaterial(Color.BROWN));

        if (leaderboard.size() == 0) {
            first3DText.setVisible(true);
            first3DText.setText3D("No players");
            first3DText.setTranslateX(-100);
        } else {
            CompletableFuture.runAsync(this::createCup);

            usersBox.setVisible(true);
            usersBox.getChildren().addAll(
                    IntStream.range(0, leaderboard.size())
                            .mapToObj(value -> new LeaderboardEntryPane(leaderboard.get(value), value + 1))
                            .collect(Collectors.toList())
            );
        }

        var tempImagePattern =
                new ImagePattern(
                        new Image("https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg"));

        IntStream.range(0, Math.min(3, leaderboard.size())).forEach(i -> {
            boxes.get(i).setVisible(true);
            images.get(i).setVisible(true);
            texts.get(i).setVisible(true);
            texts3D.get(i).setVisible(true);
            boxes.get(i).setMaterial(materials.get(i));
            texts.get(i).setText(leaderboard.get(i).getUsername());
            images.get(i).setFill(tempImagePattern);
        });
    }
}

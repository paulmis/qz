package client.scenes.leaderboard;

import commons.entities.UserDTO;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
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
import org.fxyz3d.importers.obj.ObjImporter;
import org.fxyz3d.shapes.primitives.Text3DMesh;


/**
 * Leaderboard controller class controls the inside leaderboard.
 */
public class LeaderboardCtrl implements Initializable {

    @FXML private Group group;
    @FXML private ScrollPane scrollPane;
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

    private final List<UserDTO> leaderboard;

    public LeaderboardCtrl(List<UserDTO> leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var tempImagePattern = new ImagePattern(new Image("https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg"));

        firstBox.setVisible(false);
        secondBox.setVisible(false);
        thirdBox.setVisible(false);
        firstImage.setVisible(false);
        secondImage.setVisible(false);
        thirdImage.setVisible(false);
        firstText.setVisible(false);
        secondText.setVisible(false);
        thirdText.setVisible(false);
        first3DText.setVisible(false);
        second3DText.setVisible(false);
        third3DText.setVisible(false);
        usersBox.setVisible(false);

        switch (leaderboard.size()) {
            case 0:
                first3DText.setVisible(true);
                first3DText.setText3D("No players");
                first3DText.setTranslateX(-100);
                break;
            default:
            case 3:
                thirdBox.setVisible(true);
                thirdImage.setVisible(true);
                thirdText.setVisible(true);
                third3DText.setVisible(true);
                thirdText.setText(leaderboard.get(2).getUsername());
                thirdImage.setFill(tempImagePattern);
                PhongMaterial bronzeMaterial = new PhongMaterial(Color.BROWN);
                bronzeMaterial.setDiffuseMap(new Image("https://artx.nyc3.cdn.digitaloceanspaces.com/textures/20/11/copper-5fc4cba901b8d-1200.jpg"));
                thirdBox.setMaterial(bronzeMaterial);
            case 2:
                secondBox.setVisible(true);
                secondImage.setVisible(true);
                secondText.setVisible(true);
                second3DText.setVisible(true);
                secondText.setText(leaderboard.get(1).getUsername());
                secondImage.setFill(tempImagePattern);
                PhongMaterial silverMaterial = new PhongMaterial(Color.SILVER);
                silverMaterial.setDiffuseMap(new Image("https://www.myfreetextures.com/wp-content/uploads/2014/10/silver-brushed-metal-texture-900x900.jpg"));
                secondBox.setMaterial(silverMaterial);
            case 1:
                firstBox.setVisible(true);
                firstImage.setVisible(true);
                firstText.setVisible(true);
                first3DText.setVisible(true);
                firstText.setText(leaderboard.get(0).getUsername());
                firstImage.setFill(tempImagePattern);
                PhongMaterial goldMaterial = new PhongMaterial(Color.GOLD);
                goldMaterial.setDiffuseMap(new Image("https://thumbs.dreamstime.com/b/gold-texture-golden-gradient-smooth-material-background-textured-bright-metal-light-shiny-metallic-blank-backdrop-decorative-131647513.jpg"));
                firstBox.setMaterial(goldMaterial);

                createCup();
                usersBox.setVisible(true);
                usersBox.getChildren().addAll(
                        IntStream.range(0, leaderboard.size())
                                .mapToObj(value -> new LeaderboardEntryPane(leaderboard.get(value), value + 1))
                                .collect(Collectors.toList())
                );
        }
    }

    private void createCup() {
        try {
            ObjImporter importer = new ObjImporter();
            var model = importer.loadAsPoly(getClass().getResource("/client/models/WinnerCup.png")).getRoot();
            group.getChildren().add(model);
            model.setTranslateX(500);
            model.setTranslateY(180);
            model.setTranslateZ(-200);
            model.setScaleX(0.1);
            model.setScaleY(0.1);
            model.setScaleZ(0.1);

            model.setRotationAxis(new Point3D(1, 0, 1));
            model.setRotate(180);
            final int[] counter = {0};
            var timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        matrixRotateNode(model, Math.PI, 0, Math.PI / 180 * (counter[0]++ - 180));
                        counter[0] %= 360;
                    });
                }
            }, 0, 50);


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
}

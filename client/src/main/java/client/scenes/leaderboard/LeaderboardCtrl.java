package client.scenes.leaderboard;

import commons.entities.UserDTO;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.fxyz3d.importers.obj.ObjImporter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class LeaderboardCtrl implements Initializable {

    @FXML
    private Group group;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PhongMaterial goldMaterial = new PhongMaterial(Color.GOLD);
        goldMaterial.setDiffuseMap(new Image("https://thumbs.dreamstime.com/b/gold-texture-golden-gradient-smooth-material-background-textured-bright-metal-light-shiny-metallic-blank-backdrop-decorative-131647513.jpg"));
        firstBox.setMaterial(goldMaterial);

        PhongMaterial bronzeMaterial = new PhongMaterial(Color.BROWN);
        bronzeMaterial.setDiffuseMap(new Image("https://artx.nyc3.cdn.digitaloceanspaces.com/textures/20/11/copper-5fc4cba901b8d-1200.jpg"));
        thirdBox.setMaterial(bronzeMaterial);

        PhongMaterial silverMaterial = new PhongMaterial(Color.SILVER);
        silverMaterial.setDiffuseMap(new Image("https://www.myfreetextures.com/wp-content/uploads/2014/10/silver-brushed-metal-texture-900x900.jpg"));
        secondBox.setMaterial(silverMaterial);

        firstImage.setFill(new ImagePattern(new Image("https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg")));
        secondImage.setFill(new ImagePattern(new Image("https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg")));
        thirdImage.setFill(new ImagePattern(new Image("https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg")));

        for(int i = 0;i<100;i++)
        {
            var user = new UserDTO();
            user.setUsername("a really long nameWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
            user.setGamesPlayed(50);
            user.setScore(400);
            usersBox.getChildren().add(new LeaderboardEntryPane(user,i+1));
        }

        try {
            ObjImporter importer = new ObjImporter();
            var model = importer.loadAsPoly(getClass().getResource("/client/models/WinnerCup.obj")).getRoot();
            group.getChildren().add(model);
            model.setTranslateX(500);
            model.setTranslateY(180);
            model.setTranslateZ(-200);
            model.setScaleX(0.1);
            model.setScaleY(0.1);
            model.setScaleZ(0.1);

            model.setRotationAxis(new Point3D(1,0,1));
            model.setRotate(180);
            final int[] counter = {0};
            var timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(()->{
                        matrixRotateNode(model, Math.PI,0,Math.PI/180*(counter[0]++-180));
                        counter[0]%=360;
                    });
                }
            },0,50);


        }catch(IOException e)
        {

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
    private void matrixRotateNode(Node n, double alf, double bet, double gam){
        double A11=Math.cos(alf)*Math.cos(gam);
        double A12=Math.cos(bet)*Math.sin(alf)+Math.cos(alf)*Math.sin(bet)*Math.sin(gam);
        double A13=Math.sin(alf)*Math.sin(bet)-Math.cos(alf)*Math.cos(bet)*Math.sin(gam);
        double A21=-Math.cos(gam)*Math.sin(alf);
        double A22=Math.cos(alf)*Math.cos(bet)-Math.sin(alf)*Math.sin(bet)*Math.sin(gam);
        double A23=Math.cos(alf)*Math.sin(bet)+Math.cos(bet)*Math.sin(alf)*Math.sin(gam);
        double A31=Math.sin(gam);
        double A32=-Math.cos(gam)*Math.sin(bet);
        double A33=Math.cos(bet)*Math.cos(gam);

        double d = Math.acos((A11+A22+A33-1d)/2d);
        if(d!=0d){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }
}

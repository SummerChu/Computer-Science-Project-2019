import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

//we are going to use this code: https://www.youtube.com/watch?v=lQEEby394qg as our base project.

public class Main extends Application {

    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();

    private ArrayList<Node> platforms = new ArrayList<Node>();
    private ArrayList<Node> buttons = new ArrayList<Node>();
    private ArrayList<Node> spikes = new ArrayList<Node>();

    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();

    private Avatar player;

    private int levelWidth;

    private boolean running = true;
    
    Image tile = new Image("tile.png");
    Image buttonImage = new Image("button.png");
    Image up_spike_image = new Image("upspikes.png");
    Image down_spike_image = new Image("downspikes.png");
    Image left_spike_image = new Image("leftspikes.png");


    private void initContent() {
        Rectangle bg = new Rectangle(1280, 720);

        levelWidth = LevelData.LEVEL1[0].length() * 60;

        for (int i = 0; i < LevelData.LEVEL1.length; i++) {
            String line = LevelData.LEVEL1[i];
            for (int j = 0; j < line.length(); j++) {
                switch (line.charAt(j)) {
                    case '0':
                        break;
                    case '1':
                        Objects platform = new Objects(j*60, i*60, 60, 60, tile);
                        platforms.add(platform);
                        gameRoot.getChildren().add(platform);
                        break;
                    case '2':
                        Objects button = new Objects(j*60, i*60, 60, 60, buttonImage);
                        buttons.add(button);
                        gameRoot.getChildren().add(button);
                        break;
                    case '3':
                        Objects up_spike = new Objects(j*60, i*60, 60, 60, up_spike_image);
                        spikes.add(up_spike);
                        gameRoot.getChildren().add(up_spike);
                        break;
                    case '4':
                        Objects down_spike = new Objects(j*60, i*60, 60, 60, down_spike_image);
                        spikes.add(down_spike);
                        gameRoot.getChildren().add(down_spike);
                        break;
                    case '5':
                        Objects left_spike = new Objects(j*60, i*60, 60, 60, left_spike_image);
                        spikes.add(left_spike);
                        gameRoot.getChildren().add(left_spike);
                        break;
                }
            }
        }

        player = new Avatar(0, 600, 40, 40);
        gameRoot.getChildren().add(player);
        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 640 && offset < levelWidth - 640) {
                gameRoot.setLayoutX(-(offset - 640));
            }
        });

        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
    }

    private void update() {
        if (isPressed(KeyCode.UP) && player.getTranslateY() >= 5) {
            player.jumpPlayer();
        }

        if (isPressed(KeyCode.LEFT) && player.getTranslateX() >= 5) {
            player.movePlayerX(-5,platforms);
        }

        if (isPressed(KeyCode.RIGHT) && player.getTranslateX() + 40 <= levelWidth - 5) {
            player.movePlayerX(5,platforms);
        }

        if (player.velocity.getY() < 10) {
            player.velocity = player.velocity.add(0, 1);
        }

        player.movePlayerY((int)player.velocity.getY(),platforms);

        for (Node button : buttons) {
            if (player.getBoundsInParent().intersects(button.getBoundsInParent())) {
                button.getProperties().put("alive", false);
              
            }
        }

        for (Iterator<Node> it = buttons.iterator(); it.hasNext(); ) {
            Node button = it.next();
            if (!(Boolean)button.getProperties().get("alive")) {
                it.remove();
                gameRoot.getChildren().remove(button);
                System.exit(0);
            }
        }
        //Spikes*****************
        for (Node spike : spikes) {
            if (player.getBoundsInParent().intersects(spike.getBoundsInParent())) {
                spike.getProperties().put("alive", false);
              
            }
        }
        for (Iterator<Node> it = spikes.iterator(); it.hasNext(); ) {
            Node spike = it.next();
            if (!(Boolean)spike.getProperties().get("alive")) {
                it.remove();
                System.out.println("you died!");
                gameRoot.getChildren().remove(player);
                player = new Avatar(0, 600, 40, 40);
                gameRoot.getChildren().add(player);
                //initialize the location to default
            }
        }
    }

    private Rectangle createEntity(int x, int y, int w, int h, Color color) {
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);

        gameRoot.getChildren().add(entity);
        return entity;
    }

    private boolean isPressed(KeyCode key) {
        return keys.getOrDefault(key, false);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initContent();

        Scene scene = new Scene(appRoot);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("My Nightmare");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (running) {
                    update();
                }

            
            }
        };
        timer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

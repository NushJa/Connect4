package Logic;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    //Određivanje finalnih promenljivih za veličine polja i broj kolona i izgled tabele
    public static final int COLUMNS = 7;
    public static final int ROWS = 6;
    public static final int GRID_TILE_SIZE = 80;

    private static String PLAYER_ONE = "Red Player";
    private static String PLAYER_TWO = "Yellow Player";

    private boolean isPlayerOneTurn = true;

    private Disc[][] discArray = new Disc[ROWS][COLUMNS];

    //Povezivanje nodes sa fxml fajlom
    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscPane;

    @FXML
    public Label playerLabel;

    @FXML
    public Label turnLabel;

    @FXML
    public Label redCountLabel;

    @FXML
    public Label yellowCountLabel;


    private int redCounter = 0;
    private int yellowCounter = 0;

    private boolean isInsertable = true;



    //Kreiranje polja za igranje
    public void createPlayField() {

        Shape rectanglesAndCircles = createCircles();

        rootGridPane.add(rectanglesAndCircles,0,2);

        List<Rectangle> rectangles = createEventsOnColumns();
        for (Rectangle rectangle: rectangles) {
            rootGridPane.add(rectangle,0,2);
        }
    }

    //Kreiranje krugova
    public Shape createCircles() {

        Shape rectanglesAndCircles = new Rectangle((COLUMNS + 1) * GRID_TILE_SIZE, (ROWS + 1) * GRID_TILE_SIZE);

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLUMNS ; col++) {

                Circle circle = new Circle();
                circle.setRadius(GRID_TILE_SIZE / 2);
                circle.setCenterX(GRID_TILE_SIZE / 2);
                circle.setCenterY(GRID_TILE_SIZE / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (GRID_TILE_SIZE + 5) + GRID_TILE_SIZE / 4);
                circle.setTranslateY(row * (GRID_TILE_SIZE + 5) + GRID_TILE_SIZE / 4);

                rectanglesAndCircles = Shape.subtract(rectanglesAndCircles, circle);

            }
        }

        //Igranje sa dubinom osvetljenja radi bolje preglednosti
        Light.Distant light = new Light.Distant();
        light.setAzimuth(80.0);
        light.setElevation(60.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(0.2);

        rectanglesAndCircles.setEffect(lighting);
        rectanglesAndCircles.setFill(Color.LIGHTSTEELBLUE);

        return rectanglesAndCircles;
    }

    //Kreiranje akcija za miša
    public List<Rectangle> createEventsOnColumns(){

        List<Rectangle> rectangles = new ArrayList<>();

        for (int col = 0; col < COLUMNS; col++) {

            Rectangle rectangle = new Rectangle(GRID_TILE_SIZE, (ROWS + 1) * GRID_TILE_SIZE);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (GRID_TILE_SIZE + 5) + GRID_TILE_SIZE / 4);

            rectangle.setOnMouseEntered(evt -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(evt -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;

            rectangle.setOnMouseClicked(evt -> {

                if (isInsertable) {
                    isInsertable = false;
                    insertDisc(new Disc(isPlayerOneTurn), column);
                    if(isPlayerOneTurn) {
                        redCounter++;
                        redCountLabel.setText("Red count: " + redCounter);
                    }
                    else {
                        yellowCounter++;
                        yellowCountLabel.setText("Yellow count: " + yellowCounter);
                    }
                }
            });

            rectangles.add(rectangle);
        }
        return rectangles;
    }


    //Ubacivanje diskova i provera da li već postoji disk od najvišeg reda pa na dole
    public void insertDisc(Disc disc, int column) {

        int row = ROWS - 1;

        while (row >= 0) {
            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }

        if (row < 0) {
            return;
        }

        discArray[row][column] = disc;
        insertedDiscPane.getChildren().addAll(disc);

        disc.setTranslateX(column * (GRID_TILE_SIZE + 5) + GRID_TILE_SIZE / 4);

        int currentRow = row;

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), disc);
        translateTransition.setToY(row * (GRID_TILE_SIZE + 5) + GRID_TILE_SIZE / 4);
        translateTransition.setOnFinished(event -> {

            isInsertable = true;
            if (gameEnded(currentRow, column)) {
                gameOver();
            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
            playerLabel.setTextFill(isPlayerOneTurn ? Color.RED : Color.YELLOW);
        });
        translateTransition.play();
    }


    //Metod koji ukazuje programu kako se vezuju diskovi
    public boolean gameEnded(int row, int column) {

        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
                .mapToObj(r -> new Point2D(r, column))
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
                .mapToObj(col -> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i, -i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());


        return ( checkSolutions(verticalPoints) || checkSolutions(horizontalPoints)
                || checkSolutions(diagonal1Points) || checkSolutions(diagonal2Points) );

    }

    //Provera koliko ima diskova jedan uz drugi i ako nema dovoljno za pobedu menja igrača
    public boolean checkSolutions(List<Point2D> points) {

        int chain = 0;

        for (Point2D point: points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {

                chain++;
                if ( chain == 4) {
                    return true;
                }
            } else  {
                chain = 0;
            }
        }
        return false;
    }


    //Proveravanje granica polja za igru
    private Disc getDiscIfPresent(int row, int column) {

        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0) {
            return null;
        }

        return discArray[row][column];
    }

    //Kraj igre
    public void gameOver() {

        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result");
        alert.setHeaderText("Congratulations! The Winner is : " + winner);
        alert.setContentText("Do you want to play again?");

        String musicFile = "Cheering.mp3";
        Media sound = new Media(new File(musicFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No, Exit...");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater( () -> {

            Optional<ButtonType> clickedBtn = alert.showAndWait();
            if (clickedBtn.isPresent() && clickedBtn.get() == yesBtn) {
                resetGame();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    //Kreiranje nove igre
    public void resetGame() {

        insertedDiscPane.getChildren().clear();

        for (int row = 0; row < discArray.length; row++) {
            for (int col = 0; col < discArray[row].length; col++) {

                discArray[row][col] = null;
            }
        }

        isPlayerOneTurn = true;
        playerLabel.setText(PLAYER_ONE);
        redCounter = 0;
        yellowCounter = 0;
        createPlayField();
    }

    //Inicijalizacija metoda
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

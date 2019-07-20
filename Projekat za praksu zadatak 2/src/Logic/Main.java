package Logic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        GridPane root = loader.load();

        controller = loader.getController();
        controller.createPlayField();

        Pane pane = (Pane) root.getChildren().get(0);

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        pane.getChildren().add(menuBar);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private MenuBar createMenu() {

        Menu fileMenu = new Menu("File");

        MenuItem newGameItem = new MenuItem("New Game");

        newGameItem.setOnAction(event -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitMenuItem = new MenuItem("Exit");

        exitMenuItem.setOnAction(event -> {
            exitGame();
        });

        fileMenu.getItems().addAll(newGameItem, separatorMenuItem, exitMenuItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Game");

        aboutGame.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");

        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;

    }

    private void aboutMe() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About me");
        alert.setHeaderText("Miloš Januš, aspiring developer");
        alert.setContentText("Dream Big");

        alert.show();
    }

    private void aboutConnect4() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect4 is a two-player connection game in which the " +
                "players first choose a color and then take turns dropping colored discs " +
                "from the top into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, " +
                "or diagonal line of four of one's own discs. Connect4 is a solved game. " +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {

        Platform.exit();
        System.exit(0);

    }
}
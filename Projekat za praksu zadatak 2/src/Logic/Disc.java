package Logic;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Disc extends Circle {

   public final boolean isPlayerOneMove;

    private static final String discColor1 = "E81313";
    private static final String discColor2 = "FFFF00";

    public Disc(boolean isPlayerOneMove) {

        this.isPlayerOneMove = isPlayerOneMove;
        setRadius(Controller.GRID_TILE_SIZE / 2);
        setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2));
        setCenterX(Controller.GRID_TILE_SIZE / 2);
        setCenterY(Controller.GRID_TILE_SIZE / 2);

    }
}
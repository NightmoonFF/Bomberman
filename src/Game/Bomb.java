package Game;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.*;

import static Game.Generel.board;

public class Bomb {

    private final Player player;
    private final Position position;
    private final int range;

    private final Timeline bombFuseDelay = new Timeline();
    private final Timeline animationTimeline = new Timeline();
    private final int animationSpeed;

    private final ImageView bombImageView = new ImageView();
    private Image[] bombImages;
    public static Image bomb1, bomb2, bomb3;
    public static Image explosion1;


    public Bomb(Player player) {

        this.player = player;
        this.position = player.getPosition();
        this.range = 5;
        this.animationSpeed = 100;
        init();
    }

    private void init(){

        initResources();
        bombImageView.setFitWidth(Gui.fieldImageSize);
        bombImageView.setFitHeight(Gui.fieldImageSize);
        bombImages = new Image[]{bomb1, bomb2, bomb3};

        animateFuse();

        int timeToExplode = 4;
        bombFuseDelay.getKeyFrames().add( new KeyFrame(Duration.seconds(timeToExplode), e -> explode()) );
        bombFuseDelay.play();
    }

    /**
     * Handles the Fuse Animation that plays until the bomb explodes
     */
    private void animateFuse(){

        for (int i = 0; i < bombImages.length; i++) {
            int finalI = i;
            Duration delay = Duration.millis(i * animationSpeed);
            KeyFrame keyFrame = new KeyFrame(delay, e -> bombImageView.setImage(bombImages[finalI]));
            animationTimeline.getKeyFrames().add(keyFrame);
        }

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }


    /**
     * Occurs after the fuseDelay has finished. <br>
     * Spreads out the explosion from the origin position, with Breadth-first search algorithm. <br>
     * BFS is not the algorithm used with Bomberman-like games, as you cannot hide behind boxes, <br>
     * but given limited time and lacking features, this is what was chosen.
     */
    private void explode() {

        animationTimeline.stop();
        bombFuseDelay.stop();
        Gui.removeBombOnScreen(position);

        int steps = range;
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        int[] dx = {0, 0, 1, -1}; // delta X
        int[] dy = {1, -1, 0, 0}; // delta Y

        //we start at this.position
        queue.add(position);
        visited.add(position);

        HashSet<Player> playersHitByBomb = new HashSet<>();

        while (!queue.isEmpty() && steps > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Position current = queue.poll();
                int x = current.getX();
                int y = current.getY();

                // Update current Field
                ImageView explosionImageView = new ImageView(explosion1);
                Gui.placeExplosionOnScreen(current, explosionImageView); // Place explosion image at current position

                // Add players in range to HashSet (no dupes) for damaging

                Player player = GameLogic.getPlayerAt(x, y);
                if (player != null) {
                    playersHitByBomb.add(player);
                }


                // Scale transition
                ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), explosionImageView);
                scaleTransition.setFromX(1.2);
                scaleTransition.setFromY(1.2);
                scaleTransition.setToX(0.5);
                scaleTransition.setToY(0.5);

                // Fade transition
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), explosionImageView);
                fadeTransition.setToValue(0);

                // Combine scale and fade transitions
                ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
                parallelTransition.setOnFinished(event -> Gui.removeExplosionOnScreen(current));
                parallelTransition.play();

                // Add neighbor cells to queue
                for (int j = 0; j < 4; j++) {
                    int nx = x + dx[j];
                    int ny = y + dy[j];
                    //check if path is valid based on the totally intuitive Common.board String[]
                    if (GameLogic.isValidPosition(nx, ny) && !visited.contains(new Position(nx, ny)) && board[ny].charAt(nx) != 'w') {
                        queue.add(new Position(nx, ny));
                        visited.add(new Position(nx, ny));
                    }
                }
            }
            steps--;
        }

        // Damage the found players
        for(Player p : playersHitByBomb){
            GameLogic.damagePlayer(p);
        }

    }


    /**
     * Initializes appropriate bomb sprites in accordance with the Player's assigned color.
     */
    private void initResources(){

        explosion1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/explosion1.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);

        switch (player.getPlayerColor()) {
            case RED:
                bomb1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_red1.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_red2.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_red3.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                break;
            case BLUE:
                bomb1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_blue1.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_blue2.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_blue3.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                break;
            case GREEN:
                bomb1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_green1.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_green2.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_green3.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                break;
            case PINK:
                bomb1 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_pink1.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_pink2.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                bomb3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bomb/bomb_pink3.png")), Gui.fieldImageSize, Gui.fieldImageSize, false, false);
                break;
            default:
                throw new RuntimeException("Player color could not be found");
        }
    }

    public Position getPosition() { return position; }

    public ImageView getBombImageView() { return bombImageView; }
}

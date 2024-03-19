package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.Objects;

public class Bomb {

    private final Player player;
    private final Position position;
    private final int range;

    private final int timeToExplode;
    private final Timeline bombFuseDelay = new Timeline();

    private final Timeline animationTimeline = new Timeline();
    private final int animationSpeed;

    private final ImageView bombImageView = new ImageView();
    private Image[] bombImages;
    public static Image bomb1, bomb2, bomb3;


    public Bomb(Player player) {

        this.player = player;
        this.position = player.getPosition();
        this.range = 5;
        this.timeToExplode = 5;
        this.animationSpeed = 100;
        init();
    }

    private void init(){

        initResources();
        bombImageView.setFitWidth(Gui.fieldImageSize);
        bombImageView.setFitHeight(Gui.fieldImageSize);
        bombImages = new Image[]{bomb1, bomb2, bomb3};

        animate();

        bombFuseDelay.getKeyFrames().add( new KeyFrame(Duration.seconds(timeToExplode), e -> explode()) );
        bombFuseDelay.play();
    }

    private void animate(){

        for (int i = 0; i < bombImages.length; i++) {
            int finalI = i;
            Duration delay = Duration.millis(i * animationSpeed);
            KeyFrame keyFrame = new KeyFrame(delay, e -> bombImageView.setImage(bombImages[finalI]) );
            animationTimeline.getKeyFrames().add(keyFrame);
        }

        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();
    }


    /**
     * Event that occurs after the fuseDelay as finished
     */
    private void explode() {
        animationTimeline.stop();
        bombFuseDelay.stop();
        Gui.removeBombOnScreen(position);

        //TODO: more functionality, like the explosion visuals to know where you've been hit
    }


    /**
     * Initializes appropriate bomb sprites in accordance with the Player's assigned color.
     */
    private void initResources(){

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

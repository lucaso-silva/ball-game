import javafx.scene.shape.Circle;

/**
 * Ball extends javaFX Circle
 * Each ball has its own velocity
 */
public class Ball extends Circle {
    private int ballVelocity;

    /**
     * Constructs a Ball with a specified position and radius
     * @param x the horizontal position for the center of the ball
     * @param y the vertical position for the center of the ball
     * @param radius the radius of the ball
     * Initially each new ball velocity is set to 1;
     */
    public Ball(double x, double y, double radius)
    {
        super(x, y, radius);
        ballVelocity = 1;
    }

    /**
     * Gets the value of the property ball velocity
     * @return the ball velocity
     */
    public int getBallVelocity() {
        return ballVelocity;
    }

    /**
     * Sets the value of the ball velocity adding a specified value
     * @param value the value to add to the velocity
     */
    public void setBallVelocity(int value)
    {
        ballVelocity += value;
    }
}

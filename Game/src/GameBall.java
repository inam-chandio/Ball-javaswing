import java.awt.*;

class GameBall {
    int x, y, radius;
    double vx, vy;
    Color color;

    public GameBall(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.vx = 0;
        this.vy = 0;
    }

    public boolean intersects(GameBall other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        int radiusSum = this.radius + other.radius;
        return dx * dx + dy * dy <= radiusSum * radiusSum;
    }
}
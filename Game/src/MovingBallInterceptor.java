import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MovingBallInterceptor extends JPanel implements ActionListener {
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final Timer timer;
    private GameBall movingTarget;  // Ball moving across screen
    private GameBall interceptor;   // Ball trying to intercept
    private ArrayList<Point> trajectoryPath;
    private double targetVelocityX = 2;  // Target ball speed
    private boolean isIntercepting = false;
    private int pathCount = 0;
    private ArrayList<ArrayList<Point>> previousPaths;

    public MovingBallInterceptor() {
        JFrame frame = new JFrame("Moving Ball Interceptor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        trajectoryPath = new ArrayList<>();
        previousPaths = new ArrayList<>();

        // Initialize balls
        movingTarget = new GameBall(50, 350, 10, Color.BLACK);  // Target starts on left
        interceptor = new GameBall(400, HEIGHT - 100, 20, new Color(139, 69, 19));  // Interceptor starts on bottom right

        // Add mouse listener for launching interceptor
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isIntercepting && pathCount < 12) {
                    launchInterceptor(e.getPoint());
                }
            }
        });

        timer = new Timer(20, this);
        timer.start();
    }

    private void launchInterceptor(Point clickPoint) {
        isIntercepting = true;
        pathCount++;
        trajectoryPath = new ArrayList<>();
        previousPaths.add(trajectoryPath);

        // Reset interceptor position
        interceptor.x = 400;
        interceptor.y = HEIGHT - 100;

        // Calculate initial velocity for interception
        double dx = clickPoint.x - interceptor.x;
        double dy = clickPoint.y - interceptor.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Set velocity components
        interceptor.vx = dx / 20;
        interceptor.vy = dy / 20 - 2;  // Add upward component for parabolic motion
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid and axes
        drawGridAndAxes(g2d);

        // Draw previous paths
        for (ArrayList<Point> path : previousPaths) {
            float hue = (float) previousPaths.indexOf(path) / 12.0f;
            g2d.setColor(Color.getHSBColor(hue, 0.8f, 1.0f));
            for (Point p : path) {
                g2d.fillOval(p.x - 2, p.y - 2, 4, 4);
            }
        }

        // Draw current path
        if (isIntercepting) {
            g2d.setColor(Color.GRAY);
            for (Point p : trajectoryPath) {
                g2d.fillOval(p.x - 2, p.y - 2, 4, 4);
            }
        }

        // Draw target ball
        g2d.setColor(movingTarget.color);
        g2d.fillOval(movingTarget.x - movingTarget.radius,
                movingTarget.y - movingTarget.radius,
                movingTarget.radius * 2, movingTarget.radius * 2);

        // Draw interceptor
        g2d.setColor(interceptor.color);
        g2d.fillOval(interceptor.x - interceptor.radius,
                interceptor.y - interceptor.radius,
                interceptor.radius * 2, interceptor.radius * 2);

        // Display path information
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(String.format("Paths: %d, path: %d", 12, pathCount), 10, 20);
    }

    private void drawGridAndAxes(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);

        // Vertical lines and x-axis values
        for (int x = 0; x <= 400; x += 100) {
            g.drawLine(x, 0, x, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(x), x - 10, HEIGHT - 10);
            g.setColor(Color.LIGHT_GRAY);
        }

        // Horizontal lines and y-axis values
        for (int y = 0; y <= 500; y += 100) {
            int yPos = HEIGHT - y;
            g.drawLine(0, yPos, WIDTH, yPos);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(y), 5, yPos + 5);
            g.setColor(Color.LIGHT_GRAY);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move target ball
        movingTarget.x += targetVelocityX;
        if (movingTarget.x > WIDTH || movingTarget.x < 0) {
            targetVelocityX = -targetVelocityX;
        }

        // Move interceptor if active
        if (isIntercepting) {
            interceptor.vy += 0.2;  // Gravity effect
            interceptor.x += interceptor.vx;
            interceptor.y += interceptor.vy;

            // Record trajectory
            trajectoryPath.add(new Point(interceptor.x, interceptor.y));

            // Check for collision
            if (interceptor.intersects(movingTarget)) {
                isIntercepting = false;
                interceptor.x = 400;
                interceptor.y = HEIGHT - 100;
            }

            // Check if interceptor is out of bounds
            if (interceptor.x < 0 || interceptor.x > WIDTH ||
                    interceptor.y < 0 || interceptor.y > HEIGHT) {
                isIntercepting = false;
                interceptor.x = 400;
                interceptor.y = HEIGHT - 100;
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovingBallInterceptor());
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MovingBallGame extends JPanel implements ActionListener {
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    private final Timer timer;
    private GameBall shooter;   // Ball that will be launched
    private GameBall movingTarget;  // Moving target ball
    private ArrayList<Point> trajectoryPath;
    private boolean isLaunched = false;
    private int pathCount = 0;
    private ArrayList<ArrayList<Point>> previousPaths;
    private double targetVelocityX = 3;  // Target ball horizontal speed
    private int hits = 0;  // Counter for successful hits

    public MovingBallGame() {
        JFrame frame = new JFrame("Moving Ball Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        trajectoryPath = new ArrayList<>();
        previousPaths = new ArrayList<>();

        // Initialize shooter
        shooter = new GameBall(400, HEIGHT - 100, 15, new Color(139, 69, 19));

        // Initialize moving target
        movingTarget = new GameBall(50, 200, 20, Color.RED);

        // Add mouse listener for launching shooter
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isLaunched && pathCount < 12) {
                    launchBall(e.getPoint());
                }
            }
        });

        timer = new Timer(20, this);
        timer.start();
    }

    private void launchBall(Point clickPoint) {
        isLaunched = true;
        pathCount++;
        trajectoryPath = new ArrayList<>();
        previousPaths.add(trajectoryPath);

        // Reset shooter position
        shooter.x = 400;
        shooter.y = HEIGHT - 100;

        // Calculate initial velocity for launch
        double dx = clickPoint.x - shooter.x;
        double dy = clickPoint.y - shooter.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Set velocity components
        shooter.vx = dx / 20;
        shooter.vy = dy / 20 - 2;  // Add upward component for parabolic motion
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
        if (isLaunched) {
            g2d.setColor(Color.GRAY);
            for (Point p : trajectoryPath) {
                g2d.fillOval(p.x - 2, p.y - 2, 4, 4);
            }
        }

        // Draw moving target
        g2d.setColor(movingTarget.color);
        g2d.fillOval(movingTarget.x - movingTarget.radius,
                movingTarget.y - movingTarget.radius,
                movingTarget.radius * 2, movingTarget.radius * 2);

        // Draw shooter
        g2d.setColor(shooter.color);
        g2d.fillOval(shooter.x - shooter.radius,
                shooter.y - shooter.radius,
                shooter.radius * 2, shooter.radius * 2);

        // Display information
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(String.format("Attempts: %d/12", pathCount), 10, 20);
        g2d.drawString(String.format("Hits: %d", hits), 10, 40);
    }

    private void drawGridAndAxes(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);

        // Draw grid lines
        for (int x = 0; x <= WIDTH; x += 100) {
            g.drawLine(x, 0, x, HEIGHT);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(x), x - 10, HEIGHT - 10);
            g.setColor(Color.LIGHT_GRAY);
        }

        for (int y = 0; y <= HEIGHT; y += 100) {
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
            targetVelocityX = -targetVelocityX;  // Reverse direction at edges
        }

        if (isLaunched) {
            shooter.vy += 0.2;  // Gravity effect
            shooter.x += shooter.vx;
            shooter.y += shooter.vy;

            // Record trajectory
            trajectoryPath.add(new Point(shooter.x, shooter.y));

            // Check for collision with moving target
            if (shooter.intersects(movingTarget)) {
                hits++;
                isLaunched = false;
                shooter.x = 400;
                shooter.y = HEIGHT - 100;

                // Optional: Increase difficulty after each hit
                targetVelocityX *= 1.1;
            }

            // Check if shooter is out of bounds
            if (shooter.x < 0 || shooter.x > WIDTH ||
                    shooter.y < 0 || shooter.y > HEIGHT) {
                isLaunched = false;
                shooter.x = 400;
                shooter.y = HEIGHT - 100;
            }
        }

        // Check if game is over
        if (pathCount >= 12) {
            timer.stop();
            JOptionPane.showMessageDialog(this,
                    String.format("Game Over! You hit the target %d times!", hits));
        }

        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MovingBallGame());
    }
}
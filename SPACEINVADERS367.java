import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SPACEINVADERS367 extends JPanel implements ActionListener, KeyListener {

    // ---------------- CONSTANTS ----------------
    private static final int W = 1440; 
    private static final int H = 900;
    private static final int DANGER_Y = 800; 

    // ---------------- PLAYER ----------------
    private int playerX = W / 2;
    private final int drawWidth = 250;
    private final int drawHeight = 200;
    private final int playerY = DANGER_Y - drawHeight + 30; 

    private Image playerImage;
    private final int hitboxW = 100;
    private final int hitboxH = 70;

    // Note: Ensure these paths are correct on your local machine
    private Image png1 = new ImageIcon("C:\\Users\\tgardner2\\Documents\\67SPEEDJAVA\\Frames\\FRAME 0.png")
            .getImage().getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH);

    private Image png2 = new ImageIcon("C:\\Users\\tgardner2\\Documents\\67SPEEDJAVA\\Frames\\FRAME 2.png")
            .getImage().getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH);

    // ---------------- ENTITIES ----------------
    private List<Rectangle> enemies = new ArrayList<>();
    private List<Shooter> shooters = new ArrayList<>();
    private List<Rectangle> bullets = new ArrayList<>();
    private List<Rectangle> enemyBullets = new ArrayList<>();

    private class Shooter {
        Rectangle rect;
        int cooldown;
        Color color;
        int hitFlash;

        Shooter(Rectangle r) {
            this.rect = r;
            this.cooldown = 60;
            this.color = brainrotColor();
            this.hitFlash = 0;
        }
    }

    private Random rand = new Random();
    private Timer gameLoop;

    // ---------------- GAME STATE ----------------
    private int tick = 0;
    private int enemySpeed = 2; 
    private int spawnDelay = 45; 
    private int score = 0;
    private int lives = 3; 
    private int invincibilityFrames = 0; 

    // ---------------- SHAKE ----------------
    private int shakeTimer = 0;
    private int shakeIntensity = 0;

    // ---------------- BOSS SYSTEM ----------------
    private int bossPhase = 0; // 0: None, 1: Mini Boss, 2: Final Boss 67
    private boolean bossActive = false;
    private boolean bossDescending = false; 
    private Rectangle boss;
    private int bossHealth = 30;
    private final int bossFinalY = 50;
    private float currentBossSize = 400; 

    public SPACEINVADERS367() {
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        playerImage = png1;
        gameLoop = new Timer(30, this);
        gameLoop.start();
    }

    private Color brainrotColor() {
        Color[] colors = {Color.CYAN, Color.PINK, Color.MAGENTA, Color.GREEN, Color.YELLOW, Color.ORANGE};
        return colors[rand.nextInt(colors.length)];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tick++;
        if (shakeTimer > 0) shakeTimer--;
        if (invincibilityFrames > 0) invincibilityFrames--;

        // --- BOSS TRIGGER LOGIC ---
        // Trigger Boss 1 at score 15
        if (score >= 15 && bossPhase == 0 && !bossDescending) {
            triggerBoss(1, 40, Color.MAGENTA);
        }
        // Trigger Final Boss 67 at score 60 (Harder!)
        if (score >= 60 && bossPhase == 1 && !bossActive && !bossDescending) {
            triggerBoss(2, 100, Color.YELLOW);
        }

        // NORMAL ENEMIES (Only spawn if no boss is active)
        if (!bossActive && !bossDescending && tick % spawnDelay == 0) {
            enemies.add(new Rectangle(rand.nextInt(W - 60), 0, 45, 45));
        }
        if (!bossActive && !bossDescending && tick % (spawnDelay * 3) == 0) {
            shooters.add(new Shooter(new Rectangle(rand.nextInt(W - 60), 0, 40, 40)));
        }

        moveEntities();
        updateBullets();
        repaint();
    }

    private void triggerBoss(int phase, int health, Color color) {
        bossPhase = phase;
        bossDescending = true;
        currentBossSize = 450;
        boss = new Rectangle(W/2 - 225, -500, 450, 450);
        bossHealth = health;
    }

    private void moveEntities() {
        for (Rectangle e1 : enemies) e1.y += enemySpeed;
        for (Shooter s : shooters) {
            s.rect.y += enemySpeed;
            s.cooldown--;
            if (s.cooldown <= 0) {
                enemyBullets.add(new Rectangle(s.rect.x + 18, s.rect.y + 20, 6, 12));
                s.cooldown = 90;
            }
        }

        // BOSS LOGIC
        if (bossDescending && boss != null) {
            shake(4);
            if (boss.y < bossFinalY) {
                boss.y += 5;
                if (currentBossSize > 250) {
                    currentBossSize -= 2.0f;
                    boss.width = (int)currentBossSize;
                    boss.height = (int)currentBossSize;
                    boss.x = W/2 - (boss.width/2);
                }
            } else {
                bossDescending = false;
                bossActive = true;
            }
        } else if (bossActive && boss != null) {
            // Harder movement for Final Boss
            int speedMult = (bossPhase == 2) ? 12 : 5;
            boss.x += (int)(Math.sin(tick * 0.08) * speedMult);

            // Boss Attack Patterns
            if (bossPhase == 1 && rand.nextInt(15) == 0) {
                enemyBullets.add(new Rectangle(boss.x + (boss.width/2), boss.y + boss.height, 15, 30));
            } else if (bossPhase == 2 && rand.nextInt(10) == 0) {
                // FINAL BOSS 67: SPREAD SHOT
                enemyBullets.add(new Rectangle(boss.x + 20, boss.y + boss.height, 15, 30));
                enemyBullets.add(new Rectangle(boss.x + (boss.width/2) - 7, boss.y + boss.height, 15, 30));
                enemyBullets.add(new Rectangle(boss.x + boss.width - 35, boss.y + boss.height, 15, 30));
            }
        }

        // Check lose condition
        for (Rectangle r : enemies) if (r.y > DANGER_Y) gameOver();
        for (Shooter s : shooters) if (s.rect.y > DANGER_Y) gameOver();
    }

    private void updateBullets() {
        List<Rectangle> removeBullets = new ArrayList<>();
        List<Rectangle> removeEnemies = new ArrayList<>();
        List<Shooter> removeShooters = new ArrayList<>();
        List<Rectangle> removeEnemyBullets = new ArrayList<>();

        for (Rectangle b : bullets) {
            b.y -= 15;
            for (Rectangle e : enemies) if (b.intersects(e)) { removeBullets.add(b); removeEnemies.add(e); score++; shake(3); }
            for (Shooter s : shooters) if (b.intersects(s.rect)) { removeBullets.add(b); removeShooters.add(s); score += 2; shake(5); }
            
            if (bossActive && boss != null && b.intersects(boss)) {
                removeBullets.add(b);
                bossHealth--;
                shake(bossPhase == 2 ? 12 : 7);
                if (bossHealth <= 0) {
                    bossActive = false;
                    boss = null;
                    score += (bossPhase == 2) ? 100 : 25;
                    if (bossPhase == 2) winGame(); 
                }
            }
        }

        Rectangle playerRect = new Rectangle(playerX - hitboxW / 2, playerY + 50, hitboxW, hitboxH);
        for (Rectangle eb : enemyBullets) {
            eb.y += (bossPhase == 2) ? 12 : 8; // Faster bullets in Final Boss
            if (eb.intersects(playerRect)) {
                removeEnemyBullets.add(eb);
                if (invincibilityFrames <= 0) {
                    lives--;
                    invincibilityFrames = 45;
                    shake(30);
                    if (lives <= 0) gameOver();
                }
            }
        }

        bullets.removeAll(removeBullets);
        enemies.removeAll(removeEnemies);
        shooters.removeAll(removeShooters);
        enemyBullets.removeAll(removeEnemyBullets);
        bullets.removeIf(b -> b.y < 0);
        enemyBullets.removeIf(eb -> eb.y > H);
    }

    private void shoot() {
        bullets.add(new Rectangle(playerX - 3, playerY + 50, 8, 20));
    }

    private void shake(int intensity) {
        shakeTimer = 5;
        shakeIntensity = intensity;
    }

    private void gameOver() {
        gameLoop.stop();
        JOptionPane.showMessageDialog(this, "GAME OVER! Final Score: " + score);
        System.exit(0);
    }

    private void winGame() {
        gameLoop.stop();
        JOptionPane.showMessageDialog(this, "CONGRATULATIONS! You defeated the Final 67 Boss!\nScore: " + score);
        System.exit(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int offsetX = 0, offsetY = 0;
        if (shakeTimer > 0) {
            offsetX = rand.nextInt(shakeIntensity * 2 + 1) - shakeIntensity;
            offsetY = rand.nextInt(shakeIntensity * 2 + 1) - shakeIntensity;
        }
        g.translate(offsetX, offsetY);

        // PLAYER
        if (invincibilityFrames % 4 == 0) g.drawImage(playerImage, playerX - (drawWidth/2), playerY, null);

        // ENEMIES
        Font enemyFont = new Font("Impact", Font.BOLD, 35);
        for (Rectangle e : enemies) drawGlowText(g, "41", e.x, e.y, Color.RED, Color.WHITE, enemyFont);
        for (Shooter s : shooters) drawGlowText(g, "41", s.rect.x, s.rect.y, s.color.darker(), s.color, new Font("Arial", Font.BOLD, 25));

        // BULLETS
        g.setColor(Color.YELLOW);
        for (Rectangle b : bullets) g.fillRect(b.x, b.y, b.width, b.height);
        g.setColor(Color.ORANGE);
        for (Rectangle eb : enemyBullets) g.fillRect(eb.x, eb.y, eb.width, eb.height);

        // BOSS DRAWING
        if ((bossActive || bossDescending) && boss != null) {
            g.setColor(bossPhase == 2 ? Color.YELLOW : Color.MAGENTA);
            g.fillRect(boss.x, boss.y, boss.width, boss.height);
            g.setColor(Color.WHITE);
            String name = bossPhase == 2 ? "FINAL BOSS 67" : "MINI BOSS";
            g.drawString(name + " HP: " + bossHealth, boss.x, boss.y - 10);
        }

        // HUD & Danger Zone
        g.setColor(new Color(80, 0, 0));
        g.fillRect(0, DANGER_Y, W, H - DANGER_Y);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.drawString("Score: " + score, 20, 40);
        g.setColor(Color.RED);
        String hearts = "";
        for(int i=0; i<lives; i++) hearts += "❤ ";
        g.drawString(hearts, 20, 80);

        g.translate(-offsetX, -offsetY);
    }

    private void drawGlowText(Graphics g, String text, int x, int y, Color glow, Color main, Font font) {
        g.setFont(font);
        g.setColor(glow);
        g.drawString(text, x-2, y-2); g.drawString(text, x+2, y+2);
        g.setColor(main);
        g.drawString(text, x, y);
    }

    @Override public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) playerX -= 30;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerX += 30;
        playerX = Math.max(50, Math.min(W - 50, playerX));
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '6') playerImage = png1;
        if (e.getKeyChar() == '7') { playerImage = png2; shoot(); }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Invaders 67 - Dual Boss Edition");
        SPACEINVADERS367 game = new SPACEINVADERS367();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
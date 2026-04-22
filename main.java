import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main extends JPanel implements ActionListener, KeyListener {

    private static final int W = 1440;
    private static final int H = 900;
    private static final int DANGER_Y = 800;

    private int playerX = W / 2;
    private final int drawWidth = 250;
    private final int drawHeight = 200;
    private final int playerY = DANGER_Y - drawHeight + 30;

    private Image playerImage;
    private final int hitboxW = 100;
    private final int hitboxH = 70;
    private boolean primedToShoot = false;
    private boolean menuPrimed = false;

    private Image png1 = new ImageIcon("Frames\\FRAME 0.png").getImage().getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH);
    private Image png2 = new ImageIcon("Frames\\FRAME 2.png").getImage().getScaledInstance(drawWidth, drawHeight, Image.SCALE_SMOOTH);

    private class Shooter {
        Rectangle rect; int cooldown; Color color;
        Shooter(Rectangle r) { this.rect = r; this.cooldown = 60; this.color = brainrotColor(); }
    }

    private List<Rectangle> enemies = new ArrayList<>();
    private List<Shooter> shooters = new ArrayList<>();
    private List<Rectangle> bullets = new ArrayList<>();
    private List<Rectangle> enemyBullets = new ArrayList<>();

    // --- COMMON TAUNTS — first entries continue lunchroom scene, then escalate ---
    private String[] commonTaunts = {
        "41 SUPPORTER: WHERE DO YOU THINK YOU'RE GOING??",
        "41 SUPPORTER: HE ACTUALLY WALKED OUT. GET HIM.",
        "41 SUPPORTER: YOU CAN'T RUN FROM 41!",
        "41 SUPPORTER: COME BACK HERE 67 KID!!",
        "41 SUPPORTER: YOU STARTED THIS. NOW FINISH IT.",
        "41 SUPPORTER: GET HIM OFF THE MAP!",
        "41 SUPPORTER: 67 SIGHTED\u2014ERASE HIM!",
        "41 SUPPORTER: NO ENTRY ZONE, NO ENTRY ZONE!",
        "41 SUPPORTER: YOU'RE DESYNCING THE WHOLE LOBBY!",
        "41 SUPPORTER: WHY IS HE STILL MOVING?!",
        "41 SUPPORTER: SHUT DOWN THE 67 ROUTE!",
        "41 SUPPORTER: HIT HIM, HIT HIM, HIT HIM!",
        "41 SUPPORTER: HE'S NOT SUPPOSED TO BE THIS FAST!",
        "41 SUPPORTER: PATCH HIM OUT!",
        "41 SUPPORTER: 67 ENERGY DETECTED\u2014BURN IT!",
        "41 SUPPORTER: STOP HIM BEFORE HE UPGRADES!",
        "41 SUPPORTER: HE'S BREAKING THE PATTERN!",
        "41 SUPPORTER: KEEP HIM IN 41 RANGE!",
        "41 SUPPORTER: DON'T LET HIM STACK MOMENTUM!",
        "41 SUPPORTER: WHY DOES HE MOVE LIKE THAT??",
        "41 SUPPORTER: THAT'S NOT ALLOWED IN 41 RULESET!",
        "41 SUPPORTER: RESET HIM! RESET HIM!",
        "41 SUPPORTER: HE'S LAGGING REALITY!",
        "41 SUPPORTER: 67 IS CLIMBING\u2014STOP THE CLIMB!",
        "41 SUPPORTER: LOCK THE GRID!",
        "41 SUPPORTER: NO MORE 67 FRAMES!",
        "41 SUPPORTER: THE LUNCH ROOM BELONGS TO 41!",
        "41 SUPPORTER: 67 IS A GLITCH!",
        "41 SUPPORTER: HE SHOULDN'T EVEN EXIST IN THIS ZONE!"
    };

    // --- BOSS FIGHT TAUNTS (full list) ---
    private String[] bossTaunts = {
        "41 SUPPORTER: BOSS DEPLOYED\u2014END THE 67 PROTOCOL!",
        "41 SUPPORTER: HE CAN'T SURVIVE THIS WAVE!",
        "41 SUPPORTER: FULL 41 OVERRIDE ENGAGED!",
        "41 SUPPORTER: CRUSH HIM UNDER THE BOSS FRAME!",
        "41 SUPPORTER: NO ESCAPE, NO ESCAPE, NO ESCAPE!",
        "41 SUPPORTER: HE'S STILL MOVING?! BOOST DAMAGE!",
        "41 SUPPORTER: BREAK HIS PATTERN NOW!",
        "41 SUPPORTER: DON'T LET 67 BUILD MOMENTUM!",
        "41 SUPPORTER: BOSS IS ADAPTING\u2014SO IS HE?!",
        "41 SUPPORTER: THAT'S NOT NORMAL MOVEMENT!",
        "41 SUPPORTER: HIT CONFIRMED\u2014HIT AGAIN!",
        "41 SUPPORTER: KEEP HIM STUNNED!",
        "41 SUPPORTER: 67 IS RESISTING\u2014WHY IS HE RESISTING?!",
        "41 SUPPORTER: LOCK HIS SPACE, LOCK IT!",
        "41 SUPPORTER: WE'RE LOSING CONTROL OF THE ARENA!",
        "41 SUPPORTER: HE'S PLAYING A DIFFERENT GAME!",
        "41 SUPPORTER: BOSS, SYNC WITH 41 ONLY!",
        "41 SUPPORTER: ERASE HIS POSITIONING!",
        "41 SUPPORTER: HE'S DODGING EVERYTHING?!",
        "41 SUPPORTER: THIS ISN'T SUPPOSED TO HAPPEN!",
        "41 SUPPORTER: FINISH HIM BEFORE HE HITS NEXT STAGE!",
        "41 SUPPORTER: 67 MUST NOT SCALE!",
        "41 SUPPORTER: KILL THE MOMENTUM!",
        "41 SUPPORTER: WHY IS HE GETTING STRONGER?!",
        "41 SUPPORTER: END STATE ACTIVATED\u2014ELIMINATE 67!",
        "41 SUPPORTER: 41 IS THE ETERNAL CONSTANT!",
        "41 SUPPORTER: DELETE THE 67 ANOMALY!"
    };

    // --- PLAYER COMEBACKS (expanded + unhinged) ---
    private String[] playerComebacks = {
        "67 KID: I’M STILL HERE. I’M STILL MOVING.",
        "67 KID: YOU'RE CRAZY IF YOU THINK 41 EVER STOOD A CHANCE!",
        "67 KID: STOP CALLING IT 41 LIKE IT MEANS SOMETHING!",
        "67 KID: YOU’RE ALL JUST NOISE IN MY WAY!",
        "67 KID: I MOVE DIFFERENT, DEAL WITH IT",
        "67 KID: KEEP SHOOTING, YOU’RE STILL MISSING!",
        "67 KID: LOOK AT YOU ALL PANICKING!",
        "67 KID: I’M NOT YOUR TARGET—I’M YOUR PROBLEM!",
        "67 KID: TRY TO STOP ME—I DARE YOU!"

    };

    // --- BOSS DEATH / WAVE WIN LINES ---
    private String[] bossKillLines = {
        "67 KID: BOSS DOWN. NEXT.",
        "67 KID: 41 KEEPS SENDING BIGGER ONES. KEEP SENDING THEM.",
        "67 KID: YOU THOUGHT THAT WOULD STOP ME?",
        "67 KID: THAT'S WAVE " + "—GONE. WHAT'S NEXT?",
        "67 KID: YOU UPGRADED THE WRONG NUMBER.",
        "67 KID: DELETED. AGAIN.",
        "67 KID: 67 DOESN'T FALL BEHIND BOSSES.",
        "67 KID: I ATE THAT WHOLE FRAME AND CAME BACK."
    };

    // --- ESCALATING WAVE TAUNTS (unhinged, increases per wave) ---
    private String[][] waveTaunts = {
        // Wave 1
        {
            "41 SUPPORTER: HE MADE IT THIS FAR?? EMBARRASSING.",
            "41 SUPPORTER: THE MAP WAS SUPPOSED TO DELETE HIM.",
            "41 SUPPORTER: WAVE 1 AND HE'S STILL ON THE GRID??",
            "41 SUPPORTER: THIS CAN'T BE HAPPENING."
        },
        // Wave 2
        {
            "41 SUPPORTER: HE SURVIVED A BOSS. RECALCULATE.",
            "41 SUPPORTER: WHY IS HE STILL ACCUMULATING SCORE??",
            "41 SUPPORTER: DOUBLE THE DEPLOYMENT. DOUBLE IT.",
            "41 SUPPORTER: 67 IS SPREADING THROUGH THE SYSTEM."
        },
        // Wave 3
        {
            "41 SUPPORTER: HE'S ADAPTING FASTER THAN THE BOSS IS.",
            "41 SUPPORTER: THIS ISN'T POSSIBLE IN A 41 ENVIRONMENT.",
            "41 SUPPORTER: WAVE 3 AND THE 67 SIGNAL IS STRONGER??",
            "41 SUPPORTER: SOMETHING IS VERY WRONG WITH THIS LOBBY."
        },
        // Wave 4+
        {
            "41 SUPPORTER: THE GRID IS FRACTURING. 67 IS DOING THIS.",
            "41 SUPPORTER: HE'S NOT PLAYING THE GAME. HE IS THE GAME.",
            "41 SUPPORTER: 41 WAS NEVER BUILT FOR THIS.",
            "41 SUPPORTER: REALITY IS DESYNCING. 67 CAUSED THIS.",
            "41 SUPPORTER: CORE-41 IS UNSTABLE. SEND EVERYTHING.",
            "41 SUPPORTER: WE HAVE LOST CONTAINMENT OF THE 67 VARIABLE."
        }
    };

    // --- DEFEAT QUOTES ---
    private String[] defeatQuotes = {
        "41 SUPPORTER: HE'S DOWN. GRID RESTORED.",
        "41 SUPPORTER: 67 PROTOCOL TERMINATED.",
        "41 SUPPORTER: TOLD YOU 41 HOLDS.",
        "41 SUPPORTER: RESET COMPLETE. PATTERN RESTORED.",
        "41 SUPPORTER: 67 ERA FAILED. AS EXPECTED.",
        "41 SUPPORTER: THE ANOMALY HAS BEEN CORRECTED.",
        "41 SUPPORTER: 41 ALWAYS WINS IN THE END.",
        "41 SUPPORTER: THE LOBBY IS CLEAN AGAIN."
    };

    // --- STORY LINES ---
    private String[] storyLines = {
        "S1: YO. 67 KID SAT AT THE 41 TABLE.",
        "S2: BRO ACTUALLY SAT DOWN.",
        "S3: MOVE. NOW.",
        "67 KID: Nah.",
        "S3: EXCUSE ME??",
        "67 KID: You heard me. Nah.",
        "S1: THIS IS THE 41 TABLE. ALWAYS HAS BEEN.",
        "67 KID: Cool. I'm 67. I'm sitting here.",
        "S4: YOU DON'T BELONG HERE.",
        "67 KID: Says who. You? You're like 5 foot 2.",
        "S2: BRO IS ACTUALLY UNHINGED RN.",
        "S5: 41 RUNS THIS SCHOOL. YOU KNOW THAT.",
        "67 KID: 41 runs your brain. Not mine.",
        "S4: GO BACK TO YOUR CORNER.",
        "67 KID: ..my corner.",
        "\u2026",
        "67 KID: You've been saying that my whole life.",
        "S1: BECAUSE IT'S WHERE YOU BELONG.",
        "S5: YOU'RE NOTHING. 41 LETS YOU EXIST.",
        "\u2026",
        "67 KID: ...",
        "S3: YOU DONE? GOOD. LEAVE.",
        "S2: BYE BYE 67. STAY IN YOUR LANE.",
        "\u2026",
        "67 KID: I SAID ENOUGH.",
        "\u2026",
        "67 KID: I'M SICK OF 41.",
        "67 KID: I WILL OBLITERATE ALL OF YOU!!!!!!!!!!!!!!!!"
    };

    private List<String> storyLog = new ArrayList<>();
    private int currentLineIndex = 0, charIndex = 0;
    private String currentTypingLine = "";
    private boolean lineFinished = false;

    private String activeTaunt = "";
    private int tauntTimer = 0, tauntX = 0, tauntY = 0;
    private boolean isPlayerTalking = false;

    private int gameState = 0, tick = 0, enemySpeed = 2, spawnDelay = 35, score = 0, lives = 3, invincibilityFrames = 0, wave = 1, scoreAtWaveStart = 0;
    private final int POINTS_PER_WAVE = 15;
    private int shakeTimer = 0, shakeIntensity = 0;
    private boolean bossActive = false, bossDescending = false;
    private Rectangle bossVisual;
    private Rectangle bossHitbox;
    private int bossHealth = 20;
    private String bossName = "41";
    private final int bossFinalY = 350;
    private String endScreenLine = "";
    // 0=sway, 1=rush, 2=strafe, 3=erratic, 4=sniper
    private int bossPlaystyle = 0;

    private boolean healedAt67 = false;
    private int healEffectTimer = 0;

    private Random rand = new Random();
    private Timer gameLoop;

    public main() {
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

    private void resetToStart() {
        score = 0; lives = 3; wave = 1; enemySpeed = 2; spawnDelay = 35;
        scoreAtWaveStart = 0; healedAt67 = false; gameState = 0; menuPrimed = false;
        bossActive = false; bossDescending = false;
        enemies.clear(); shooters.clear(); bullets.clear(); enemyBullets.clear();
        playerX = W / 2; playerImage = png1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == 0 || gameState == 3) { repaint(); return; }
        if (gameState == 2) {
            String target = storyLines[currentLineIndex];
            // Pause/separator lines finish instantly
            if (target.equals("\u2026")) { currentTypingLine = "\u2026"; lineFinished = true; tick++; repaint(); return; }
            if (tick % 2 == 0 && charIndex < target.length()) { currentTypingLine += target.charAt(charIndex); charIndex++; }
            else if (charIndex >= target.length()) lineFinished = true;
            tick++; repaint(); return;
        }

        tick++;
        if (shakeTimer > 0) shakeTimer--;
        if (invincibilityFrames > 0) invincibilityFrames--;
        if (healEffectTimer > 0) healEffectTimer--;

        if (score >= 67 && !healedAt67) {
            lives = Math.min(5, lives + 1);
            healedAt67 = true; healEffectTimer = 60; shake(15);
            triggerPlayerDialogue("67 KID: STABILIZED. 67 ENERGY RESTORED.");
        }

        handleTaunts();

        if (!bossActive && !bossDescending) {
            if (tick % spawnDelay == 0) enemies.add(new Rectangle(rand.nextInt(W - 60), 0, 45, 45));
            if (tick % (spawnDelay * 3) == 0) shooters.add(new Shooter(new Rectangle(rand.nextInt(W - 60), 0, 40, 40)));
            if (score - scoreAtWaveStart >= POINTS_PER_WAVE) triggerWaveBoss();
        }

        moveEntities();
        updateBulletsAndCollisions();
        repaint();
    }

    private void triggerPlayerDialogue(String text) {
        activeTaunt = text;
        isPlayerTalking = true;
        tauntTimer = 130;
        tauntX = playerX - 280;
        tauntY = playerY - 130;
    }

    private void handleTaunts() {
        if (tauntTimer > 0) tauntTimer--;
        else if (rand.nextInt(120) == 0) {
            isPlayerTalking = false;
            tauntTimer = 90;
            tauntX = rand.nextInt(W - 700) + 100;

            if (bossActive || bossDescending) {
                activeTaunt = bossTaunts[rand.nextInt(bossTaunts.length)];
                tauntY = rand.nextInt(150) + 580;
            } else {
                int roll = rand.nextInt(10);
                if (roll < 3) {
                    // Player talks back
                    isPlayerTalking = true;
                    activeTaunt = playerComebacks[rand.nextInt(playerComebacks.length)];
                    tauntX = playerX - 200;
                    tauntY = playerY - 110;
                } else if (roll < 5 && wave >= 2) {
                    // Wave-escalating taunts kick in from wave 2
                    int waveIndex = Math.min(wave - 2, waveTaunts.length - 1);
                    String[] wt = waveTaunts[waveIndex];
                    activeTaunt = wt[rand.nextInt(wt.length)];
                    tauntY = rand.nextInt(200) + 100;
                } else {
                    activeTaunt = commonTaunts[rand.nextInt(commonTaunts.length)];
                    tauntY = rand.nextInt(200) + 100;
                }
            }
        }
    }

    private void triggerWaveBoss() {
        bossDescending = true; bossActive = false;
        if      (wave == 1) { bossName = "41";           bossPlaystyle = 0; }
        else if (wave == 2) { bossName = "41\u00B2";     bossPlaystyle = 1; }
        else if (wave == 3) { bossName = "4141";         bossPlaystyle = 2; }
        else {
            String[] deepNames = {"CORE-41","41-ULTRA","X-41","ROOT-41","\u221E41","41-PRIME"};
            bossName = deepNames[rand.nextInt(deepNames.length)];
            bossPlaystyle = 3 + (wave % 2); // alternates erratic/sniper
        }
        bossVisual = new Rectangle(W / 2 - 150, -600, 300, 250);
        bossHitbox = new Rectangle(bossVisual.x, bossVisual.y + 180, 320, 70);
        bossHealth = 20 + (wave * 10);
        enemies.clear(); shooters.clear(); enemyBullets.clear();
        activeTaunt = "41 SUPPORTER: BOSS DEPLOYED\u2014END THE 67 PROTOCOL!";
        isPlayerTalking = false;
        tauntTimer = 120; tauntX = W / 2 - 350; tauntY = 700;
    }

    private void moveEntities() {
        for (Rectangle e : enemies) { e.y += enemySpeed; if (e.y > DANGER_Y) { takeDamage(); e.y = -100; } }
        for (Shooter s : shooters) {
            s.rect.y += enemySpeed; s.cooldown--;
            if (s.cooldown <= 0) { enemyBullets.add(new Rectangle(s.rect.x + 18, s.rect.y + 20, 6, 12)); s.cooldown = 90; }
            if (s.rect.y > DANGER_Y) { takeDamage(); s.rect.y = -100; }
        }

        if (bossDescending && bossVisual != null) {
            // Moderately fast descent: 10px/tick
            bossVisual.y += 10;
            bossHitbox.y = bossVisual.y + 180;
            // Subtle rumble every 4 ticks
            if (tick % 4 == 0) shake(3);
            if (bossVisual.y >= bossFinalY) { bossVisual.y = bossFinalY; bossDescending = false; bossActive = true; }

        } else if (bossActive && bossVisual != null) {
            // --- PLAYSTYLE MOVEMENT ---
            switch (bossPlaystyle) {
                case 0: // SWAY — gentle sine, wave 1
                    bossVisual.x += (int)(Math.sin(tick * 0.06) * 5);
                    if (rand.nextInt(20) == 0)
                        enemyBullets.add(new Rectangle(bossVisual.x + 140, bossVisual.y + 220, 8, 18));
                    break;
                case 1: // RUSH — periodically slams toward player then retreats
                    if (tick % 90 < 20) {
                        int dir = (playerX > bossVisual.x + 150) ? 1 : -1;
                        bossVisual.x += dir * 14;
                        if (rand.nextInt(5) == 0)
                            enemyBullets.add(new Rectangle(bossVisual.x + 140, bossVisual.y + 220, 10, 22));
                    } else {
                        bossVisual.x += (int)(Math.sin(tick * 0.04) * 3);
                        if (rand.nextInt(25) == 0)
                            enemyBullets.add(new Rectangle(bossVisual.x + 140, bossVisual.y + 220, 8, 18));
                    }
                    break;
                case 2: // STRAFE — fast left/right sweeps, wall-bounces
                    bossVisual.x += (int)(Math.sin(tick * 0.14) * (10 + wave));
                    if (rand.nextInt(12) == 0)
                        enemyBullets.add(new Rectangle(bossVisual.x + 140, bossVisual.y + 220, 7, 15));
                    break;
                case 3: // ERRATIC — random jitter + burst fire
                    bossVisual.x += rand.nextInt(19) - 9;
                    if (rand.nextInt(8) == 0) {
                        for (int i = -1; i <= 1; i++)
                            enemyBullets.add(new Rectangle(bossVisual.x + 140 + i * 30, bossVisual.y + 220, 9, 20));
                    }
                    break;
                case 4: // SNIPER — barely moves, fires slow precise shots aimed at player
                    bossVisual.x += (int)(Math.sin(tick * 0.03) * 2);
                    if (tick % 40 == 0) {
                        // Aimed shot toward player x
                        int bx = bossVisual.x + 150; int by = bossVisual.y + 220;
                        int dx = playerX - bx;
                        enemyBullets.add(new Rectangle(bx - 6,  by, 12, 25));
                        if (Math.abs(dx) > 80)
                            enemyBullets.add(new Rectangle(bx + (dx > 0 ? 60 : -60), by, 10, 22));
                    }
                    break;
            }
            // Clamp boss inside screen
            bossVisual.x = Math.max(-50, Math.min(W - 260, bossVisual.x));
            bossHitbox.x = bossVisual.x;
        }
    }

    private void takeDamage() {
        if (invincibilityFrames <= 0) {
            lives--; invincibilityFrames = 45; shake(20);
            if (lives <= 0) triggerEnd(false);
            else {
                // React to taking damage
                String[] hurtLines = {
                    "67 KID: NOT ENOUGH TO STOP ME.",
                    "67 KID: THAT'S FINE. I KEEP MOVING.",
                    "67 KID: YOU'RE GONNA HAVE TO DO BETTER.",
                    "67 KID: STILL HERE.",
                    "67 KID: I FELT THAT. DOESN'T MATTER."
                };
                if (rand.nextInt(3) == 0) triggerPlayerDialogue(hurtLines[rand.nextInt(hurtLines.length)]);
            }
        }
    }

    private void triggerEnd(boolean win) {
        gameState = 3;
        endScreenLine = win ? "67 KID: 67 CLEARS THE BOARD. PATTERN BROKEN." : defeatQuotes[rand.nextInt(defeatQuotes.length)];
    }

    private void updateBulletsAndCollisions() {
        List<Rectangle> rmB = new ArrayList<>(), rmE = new ArrayList<>(), rmEB = new ArrayList<>();
        List<Shooter> rmS = new ArrayList<>();
        Rectangle pRect = new Rectangle(playerX - hitboxW / 2, playerY + 50, hitboxW, hitboxH);

        if (primedToShoot) {
            for (Rectangle e : enemies) if (pRect.intersects(e)) { rmE.add(e); score++; shake(5); }
            for (Shooter s : shooters) if (pRect.intersects(s.rect)) { rmS.add(s); score += 2; shake(8); }
        }

        for (Rectangle b : bullets) {
            b.y -= 15;
            for (Rectangle e : enemies) if (b.intersects(e)) { rmB.add(b); rmE.add(e); score++; shake(3); }
            for (Shooter s : shooters) if (b.intersects(s.rect)) { rmB.add(b); rmS.add(s); score += 2; shake(5); }
            if (bossActive && bossHitbox != null && b.intersects(bossHitbox)) {
                rmB.add(b); bossHealth--; shake(10);
                // React when boss is low HP
                if (bossHealth == 10) {
                    activeTaunt = "41 SUPPORTER: HE'S ACTUALLY HURTING THE BOSS. IMPOSSIBLE.";
                    isPlayerTalking = false; tauntTimer = 110; tauntX = W / 2 - 400; tauntY = 680;
                }
                if (bossHealth <= 0) {
                    bossActive = false; bossVisual = null; bossHitbox = null; score += 50;
                    scoreAtWaveStart = score; wave++; enemySpeed++;
                    triggerPlayerDialogue(bossKillLines[rand.nextInt(bossKillLines.length)]);
                }
            }
        }
        for (Rectangle eb : enemyBullets) { eb.y += 10; if (eb.intersects(pRect)) { rmEB.add(eb); takeDamage(); } }
        bullets.removeAll(rmB); enemies.removeAll(rmE); shooters.removeAll(rmS); enemyBullets.removeAll(rmEB);
        bullets.removeIf(b -> b.y < 0); enemyBullets.removeIf(eb -> eb.y > H);
    }

    private void shake(int i) { shakeTimer = 5; shakeIntensity = i; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == 0) { drawMenu(g); return; }
        if (gameState == 2) { drawStory(g); return; }
        if (gameState == 3) { drawEndScreen(g); return; }

        int ox = 0, oy = 0;
        if (shakeTimer > 0) { ox = rand.nextInt(shakeIntensity * 2 + 1) - shakeIntensity; oy = rand.nextInt(shakeIntensity * 2 + 1) - shakeIntensity; }
        g.translate(ox, oy);

        if (invincibilityFrames % 4 == 0) g.drawImage(playerImage, playerX - (drawWidth / 2), playerY, null);
        for (Rectangle e : enemies) drawGlowText(g, "41", e.x, e.y, Color.RED, Color.WHITE, new Font("Impact", Font.BOLD, 35));
        for (Shooter s : shooters) drawGlowText(g, "41", s.rect.x, s.rect.y, s.color.darker(), s.color, new Font("Arial", Font.BOLD, 25));

        if (tauntTimer > 0) {
            int alpha = Math.min(255, tauntTimer * 8);
            g.setFont(new Font("Impact", Font.ITALIC, isPlayerTalking ? 42 : 30));
            g.setColor(new Color(isPlayerTalking ? 0 : 255, isPlayerTalking ? 220 : 0, 0, alpha));
            // Drop shadow for readability
            g.setColor(new Color(0, 0, 0, alpha / 2));
            g.drawString(activeTaunt, Math.max(50, Math.min(W - 900, tauntX)) + 2, tauntY + 2);
            g.setColor(new Color(isPlayerTalking ? 0 : 255, isPlayerTalking ? 220 : 0, 0, alpha));
            g.drawString(activeTaunt, Math.max(50, Math.min(W - 900, tauntX)), tauntY);
        }

        g.setColor(Color.YELLOW); for (Rectangle b : bullets) g.fillRect(b.x, b.y, b.width, b.height);
        g.setColor(Color.ORANGE); for (Rectangle eb : enemyBullets) g.fillRect(eb.x, eb.y, eb.width, eb.height);

        if ((bossActive || bossDescending) && bossVisual != null) {
            Color bC = wave % 2 == 0 ? Color.CYAN : Color.MAGENTA;

            if (bossDescending) {
                // Text expands as boss descends: lerp from 550 down to 350 based on y progress
                float progress = Math.max(0f, Math.min(1f, (bossVisual.y + 600f) / (bossFinalY + 600f)));
                int fontSize = (int)(550 - progress * 200); // 550 → 350
                drawGlowText(g, bossName, bossVisual.x - (fontSize / 4), bossVisual.y,
                    bC.darker().darker(), bC, new Font("Impact", Font.BOLD, fontSize));
                // Big HP text looms in too
                int hpFontSize = (int)(60 - progress * 40); // 60 → 20
                int maxHp = 20 + (wave * 10);
                g.setFont(new Font("Impact", Font.BOLD, hpFontSize));
                g.setColor(new Color(bC.getRed(), bC.getGreen(), bC.getBlue(), 180));
                g.drawString("HP: " + maxHp, W / 2 - 60, bossVisual.y + fontSize / 2 + 20);
            } else {
                // Normal active state
                drawGlowText(g, bossName, bossVisual.x, bossVisual.y, bC.darker(), bC, new Font("Impact", Font.BOLD, 350));
                int maxHp = 20 + (wave * 10);
                int barW = 400; int barH = 20;
                int bx = W / 2 - barW / 2; int by = bossFinalY + 260;
                g.setColor(Color.DARK_GRAY); g.fillRect(bx, by, barW, barH);
                float hpRatio = (float) bossHealth / maxHp;
                Color hpColor = hpRatio > 0.5f ? Color.GREEN : hpRatio > 0.25f ? Color.YELLOW : Color.RED;
                g.setColor(hpColor); g.fillRect(bx, by, (int)(barW * hpRatio), barH);
                g.setColor(Color.WHITE); g.setFont(new Font("Impact", Font.BOLD, 16));
                g.drawString(bossName + " HP: " + bossHealth + "/" + maxHp, bx + 5, by + 15);
            }
        }

        g.setFont(new Font("Impact", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score + " | Wave: " + wave, 20, 40);

        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(healEffectTimer > 0 ? Color.CYAN : Color.RED);
        String h = ""; for (int i = 0; i < lives; i++) h += "\u2764 ";
        g.drawString(h, W - (lives * 55) - 20, 50);

        if (healEffectTimer > 0) drawGlowText(g, "67 STABILIZED!", W / 2 - 150, 150, Color.CYAN, Color.WHITE, new Font("Impact", Font.BOLD, 50));

        g.setColor(new Color(80, 0, 0)); g.fillRect(0, DANGER_Y, W, H - DANGER_Y);
        g.translate(-ox, -oy);
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.WHITE); g.setFont(new Font("Impact", Font.PLAIN, 90));
        g.drawString("67 SPEEED II: 41 INVASION", W / 2 - 450, H / 2 - 50);
        g.setFont(new Font("Arial", Font.BOLD, 30)); g.setColor(menuPrimed ? Color.YELLOW : Color.WHITE);
        g.drawString("TYPE '67' TO START", W / 2 - 150, H / 2 + 50);
    }

    private void drawEndScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 235)); g.fillRect(0, 0, W, H);
        g.setFont(new Font("Impact", Font.BOLD, 60));
        g.setColor(endScreenLine.startsWith("67") ? Color.GREEN : Color.RED);
        g.drawString(endScreenLine, W / 2 - 420, H / 2);
        g.setFont(new Font("Arial", Font.BOLD, 25)); g.setColor(Color.WHITE);
        g.drawString("FINAL SCORE: " + score + " | WAVE: " + wave + " | [SPACE] TO REBOOT", W / 2 - 230, H / 2 + 100);
    }

    private Color storyLineColor(String line) {
        if (line.startsWith("67 KID")) return new Color(60, 220, 80);
        if (line.startsWith("S"))      return new Color(255, 60, 60);
        if (line.equals("\u2026"))     return new Color(65, 65, 65);
        return Color.WHITE;
    }

    private void drawStory(Graphics g) {
        // Same black background as the game
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, W, H);

        // Same danger zone bar at the bottom — exact match to gameplay
        g.setColor(new Color(80, 0, 0));
        g.fillRect(0, DANGER_Y, W, H - DANGER_Y);

        // Header — same Impact Bold 90 as the menu title
        drawGlowText(g, "67 VS 41", W / 2 - 220, 75,
            new Color(20, 80, 20), new Color(60, 220, 80),
            new Font("Impact", Font.BOLD, 90));

        // Thin separator — same color as danger zone
        g.setColor(new Color(80, 0, 0));
        g.fillRect(80, 88, W - 160, 3);

        // Same font sizes as in-game taunts: Impact Italic 32 for supporters, Bold 38 for 67 KID
        Font supporterFont = new Font("Impact", Font.ITALIC, 30);
        Font kidFont       = new Font("Impact", Font.BOLD,   36);
        Font pauseFont     = new Font("Impact", Font.PLAIN,  26);

        int sy    = 118;
        int lineH = 38;
        int margin = 100;

        for (int i = 0; i < storyLog.size(); i++) {
            String line = storyLog.get(i);
            Color col = storyLineColor(line);
            // Fade older lines — same alpha trick used for invincibility frames
            float fade = Math.max(0.3f, 1f - (storyLog.size() - 1 - i) * 0.09f);
            Color fc = new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)(255 * fade));
            Font f = line.startsWith("67 KID") ? kidFont
                   : line.equals("\u2026")      ? pauseFont
                   : supporterFont;
            if (line.startsWith("67 KID")) {
                drawGlowText(g, line, margin, sy + (i * lineH), fc.darker(), fc, f);
            } else {
                g.setFont(f); g.setColor(fc);
                g.drawString(line, margin, sy + (i * lineH));
            }
        }

        // Active line — full brightness, same blinking cursor logic as game tick
        String peek = currentTypingLine.isEmpty() ? storyLines[currentLineIndex] : currentTypingLine;
        Color  cc = storyLineColor(peek);
        Font   cf = peek.startsWith("67 KID") ? kidFont
                  : peek.equals("\u2026")      ? pauseFont
                  : supporterFont;
        String blink  = (tick % 22 < 11) ? "_" : " ";
        String display = currentTypingLine + (lineFinished ? "" : blink);
        int curY = sy + (storyLog.size() * lineH);

        if (peek.startsWith("67 KID")) {
            drawGlowText(g, display, margin, curY, cc.darker(), cc, cf);
        } else {
            g.setFont(cf); g.setColor(cc);
            g.drawString(display, margin, curY);
        }

        // Prompt — Impact Bold 30, same as score HUD
        if (lineFinished) {
            g.setFont(new Font("Impact", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString("[ENTER] NEXT", W / 2 - 120, DANGER_Y + 55);
            g.setColor(new Color(120, 120, 120));
            g.drawString("[P] SKIP", W / 2 + 100, DANGER_Y + 55);
        }
    }

    private void drawGlowText(Graphics g, String t, int x, int y, Color gl, Color m, Font f) {
        g.setFont(f); g.setColor(gl); g.drawString(t, x - 2, y - 2); g.drawString(t, x + 2, y + 2);
        g.setColor(m); g.drawString(t, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameState == 0) {
            if (e.getKeyChar() == '6') menuPrimed = true;
            if (e.getKeyChar() == '7' && menuPrimed) { gameState = 2; storyLog.clear(); currentLineIndex = 0; currentTypingLine = ""; charIndex = 0; lineFinished = false; }
        } else if (gameState == 2) {
            if (e.getKeyCode() == KeyEvent.VK_P) { gameState = 1; fireOpeningTaunt(); }
            if (e.getKeyCode() == KeyEvent.VK_ENTER && lineFinished) {
                storyLog.add(currentTypingLine);
                if (storyLog.size() > 20) storyLog.remove(0);
                if (currentLineIndex < storyLines.length - 1) {
                    currentLineIndex++; currentTypingLine = ""; charIndex = 0; lineFinished = false;
                } else { gameState = 1; fireOpeningTaunt(); }
            }
        } else if (gameState == 1) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) playerX -= 45;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerX += 45;
            playerX = Math.max(50, Math.min(W - 50, playerX));
            if (e.getKeyChar() == '6') { playerImage = png2; primedToShoot = true; }
            if (e.getKeyChar() == '7' && primedToShoot) { bullets.add(new Rectangle(playerX - 3, playerY + 50, 8, 20)); playerImage = png1; primedToShoot = false; }
        } else if (gameState == 3 && e.getKeyCode() == KeyEvent.VK_SPACE) {
            resetToStart();
        }
    }

    private void fireOpeningTaunt() {
        String[] bridgeLines = {
            "41 SUPPORTER: WHERE DO YOU THINK YOU'RE GOING??",
            "41 SUPPORTER: HE ACTUALLY WALKED OUT. GET HIM.",
            "41 SUPPORTER: YOU CAN'T RUN FROM 41!",
            "41 SUPPORTER: COME BACK HERE 67 KID!!"
        };
        activeTaunt = bridgeLines[rand.nextInt(bridgeLines.length)];
        isPlayerTalking = false;
        tauntTimer = 120;
        tauntX = W / 2 - 350;
        tauntY = 680;
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("67 SPEEED II: 41 INVASION");
        main g = new main();
        f.add(g); f.pack(); f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); f.setLocationRelativeTo(null); f.setVisible(true);
    }
}
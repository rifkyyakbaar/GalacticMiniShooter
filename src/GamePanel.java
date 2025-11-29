import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private Main mainApp;
    private KoneksiDatabase db = new KoneksiDatabase(); 

    // === PLAYER EFEK SUARA ===
    private SoundPlayer sfxPlayer = new SoundPlayer(); 

    private Timer timer;
    private Random rand = new Random();

    // === WAKTU PERMAINAN ===
    private long startTime; 

    // === Tank player ===
    private int tankX = 600;
    private int tankY = 600;
    private int tankSpeed = 10;
    private int hp = 100;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    // === GAMBAR ASET ===
    private Image inGameBg; 
    private Image pHull, pGun, pTrack;
    private Image eHull1, eGun1, eTrack1; 
    private Image eHull2, eGun2, eTrack2; 
    private Image bHull, bGun, bTrack;
    
    // === GAMBAR EFEK & PELURU ===
    private Image expImg1, expImg2, expImg3;
    private Image bulletImg1, bulletImg2; 
    private Image bossBulletImg;
    
    // GAMBAR PELURU MUSUH BARU
    private Image eBulletImg1; // Peluru Musuh Lv 1
    private Image eBulletImg2; // Peluru Musuh Lv 2

    // === Peluru ===
    private ArrayList<Rectangle> bullets = new ArrayList<>();
    private int bulletSpeed = 12;
    private ArrayList<Rectangle> bossBullets = new ArrayList<>(); 
    private int bossBulletSpeed = 8; 
    private int bossXSpeed = 3; 

    // === PELURU MUSUH BIASA (UPDATE: Pakai Class EnemyBullet) ===
    private ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    private int enemyBulletSpeed = 7;

    private ArrayList<Explosion> explosions = new ArrayList<>();

    // === MUSUH ===
    private ArrayList<Enemy> enemies = new ArrayList<>(); 
    private int enemySpeed = 2; 
    private int spawnRate = 60; 

    // === Item ===
    private ArrayList<Rectangle> hpItems = new ArrayList<>();
    private ArrayList<Rectangle> powerItems = new ArrayList<>();
    private int itemSpeed = 3;

    // === Level & Boss ===
    private int level = 1;
    private int score = 0;
    private int frameCount = 0;
    private Rectangle boss;
    private int bossHp = 200;
    private int maxBossHp = 200; 
    private boolean bossActive = false;

    // === Back button ===
    private JButton btnBack;

    public GamePanel(Main mainApp) {
        this.mainApp = mainApp;
        db.initialize(); 
        startTime = System.currentTimeMillis(); 

        // === LOAD BACKGROUND ===
        inGameBg = new ImageIcon("assets/background3.jpg").getImage();

        // === LOAD GAMBAR PELURU PLAYER & BOSS ===
        bulletImg1 = new ImageIcon("assets/Plasma.png").getImage();
        bulletImg2 = new ImageIcon("assets/Flame_D.png").getImage();
        bossBulletImg = new ImageIcon("assets/Laser.png").getImage();

        // === LOAD GAMBAR PELURU MUSUH (BARU) ===
        eBulletImg1 = new ImageIcon("assets/EnemyBullet1.png").getImage();
        eBulletImg2 = new ImageIcon("assets/EnemyBullet2.png").getImage();

        // === LOAD PLAYER ===
        pHull  = new ImageIcon("assets/Hull_01.png").getImage();
        pGun   = new ImageIcon("assets/Gun_01.png").getImage();
        pTrack = new ImageIcon("assets/Track_01.png").getImage();

        // === LOAD MUSUH LV 1 (02) ===
        eHull1  = new ImageIcon("assets/Hull_02.png").getImage();
        eGun1   = new ImageIcon("assets/Gun_02.png").getImage();
        eTrack1 = new ImageIcon("assets/Track_02.png").getImage();

        // === LOAD MUSUH LV 2 (04) ===
        eHull2  = new ImageIcon("assets/Hull_04.png").getImage();
        eGun2   = new ImageIcon("assets/Gun_04.png").getImage();
        eTrack2 = new ImageIcon("assets/Track_04.png").getImage();

        // === LOAD BOSS (03) ===
        bHull  = new ImageIcon("assets/Hull_03.png").getImage();
        bGun   = new ImageIcon("assets/Gun_03.png").getImage();
        bTrack = new ImageIcon("assets/Track_03.png").getImage();

        // === LOAD EFEK LEDAKAN ===
        expImg1 = new ImageIcon("assets/Explosion_01.png").getImage();
        expImg2 = new ImageIcon("assets/Explosion_02.png").getImage();
        expImg3 = new ImageIcon("assets/Explosion_03.png").getImage(); 

        setLayout(null);
        setFocusable(true);
        addKeyListener(this);

        btnBack = new JButton("Back");
        btnBack.setBounds(20, 20, 120, 40);
        btnBack.setFocusable(false); 
        btnBack.addActionListener(e -> {
            mainApp.playButtonSound();
            mainApp.showMainMenu();
        });
        add(btnBack);

        timer = new Timer(16, this); 
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frameCount++;

        // === Movement Player ===
        if (moveLeft && tankX > 20) tankX -= tankSpeed;
        if (moveRight && tankX < 1150) tankX += tankSpeed;

        // === SPAWN MUSUH ===
        if (!bossActive && frameCount % spawnRate == 0) {
            int spawnX = rand.nextInt(1200);
            if (level == 1) enemies.add(new Enemy(spawnX, -40, 40, 40, 1, 1));
            else enemies.add(new Enemy(spawnX, -60, 60, 60, 2, 2));
        }

        // === Spawn Item ===
        if (frameCount % 500 == 0) hpItems.add(new Rectangle(rand.nextInt(1200), -40, 30, 30));
        if (frameCount % 700 == 0) powerItems.add(new Rectangle(rand.nextInt(1200), -40, 30, 30));

        // === Update Ledakan ===
        for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion exp = explosions.get(i);
            exp.update(); 
            if (!exp.isAlive()) explosions.remove(i); 
        }

        // === Update Peluru Player ===
        for (int i = 0; i < bullets.size(); i++) {
            Rectangle b = bullets.get(i);
            b.y -= bulletSpeed;
            if (b.y < -20) bullets.remove(i);
        }

        // === Update Peluru Musuh Biasa ===
        for (int i = 0; i < enemyBullets.size(); i++) {
            EnemyBullet eb = enemyBullets.get(i);
            eb.y += enemyBulletSpeed;

            // Kena Player
            if (eb.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 10; 
                sfxPlayer.playSFX("assets/tabraksound.wav");
                explosions.add(new Explosion(tankX, tankY, expImg2));
                enemyBullets.remove(i);
                if (hp <= 0) gameOver();
            } else if (eb.y > 720) enemyBullets.remove(i);
        }

        // === Update Peluru Boss ===
        for (int i = 0; i < bossBullets.size(); i++) {
            Rectangle bb = bossBullets.get(i);
            bb.y += bossBulletSpeed; 
            if (bb.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 15; 
                sfxPlayer.playSFX("assets/tabraksound.wav"); 
                explosions.add(new Explosion(tankX, tankY, expImg2));
                bossBullets.remove(i);
                if (hp <= 0) gameOver();
            } else if (bb.y > 720) bossBullets.remove(i);
        }

        // === Update Musuh ===
        for (int i = 0; i < enemies.size(); i++) {
            Enemy en = enemies.get(i);
            en.y += enemySpeed;

            // === LOGIKA MUSUH NEMBAK ===
            // Cek Level Musuh untuk menentukan peluru apa yang keluar
            if (en.enemyLevel >= 2 && !bossActive) {
                if (rand.nextInt(100) < 1) {
                    // Munculkan EnemyBullet dengan membawa info level si penembak
                    enemyBullets.add(new EnemyBullet(en.x + 20, en.y + 60, 20, 40, en.enemyLevel));
                }
            }
            // (Opsional) Kalau mau Musuh Lv 1 nembak juga, bisa tambah else if di sini

            if (en.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 10;
                sfxPlayer.playSFX("assets/tabraksound.wav");
                explosions.add(new Explosion(tankX, tankY, expImg2));
                enemies.remove(i);
                if (hp <= 0) gameOver();
            } else if (en.y > 720) {   
                score = Math.max(0, score - 5); 
                enemies.remove(i);   
            }
        }

        // === Collision Peluru Player -> Musuh ===
        for (int i = 0; i < bullets.size(); i++) {
            Rectangle b = bullets.get(i);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy en = enemies.get(j);
                if (b.intersects(en)) {
                    bullets.remove(i);
                    sfxPlayer.playSFX("assets/tembaksound.wav");
                    
                    if (level == 1) explosions.add(new Explosion(en.x, en.y, expImg1)); 
                    else explosions.add(new Explosion(en.x, en.y, expImg3)); 

                    en.hp -= 1;
                    if (en.hp <= 0) {
                        enemies.remove(j);
                        score += 10;
                    }
                    break;
                }
            }
        }

        // === Collision Items ===
        for (int i = 0; i < hpItems.size(); i++) {
            Rectangle it = hpItems.get(i);
            it.y += itemSpeed;
            if (it.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                sfxPlayer.playSFX("assets/itemsound.wav"); 
                hp = Math.min(100, hp + 15);
                hpItems.remove(i);
            }
        }
        for (int i = 0; i < powerItems.size(); i++) {
            Rectangle it = powerItems.get(i);
            it.y += itemSpeed;
            if (it.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                sfxPlayer.playSFX("assets/itemsound.wav");
                score += 10;
                powerItems.remove(i);
            }
        }

        // === Level Up ===
        if (score > 100 && level == 1) { level = 2; enemySpeed = 3; }
        if (score > 250 && level == 2) { level = 3; enemySpeed = 4; startBoss(); }

        // === Boss Logic ===
        if (bossActive && boss != null) {
            if (boss.y < 50) boss.y += 2; 
            boss.x += bossXSpeed;
            if (boss.x >= 1280 - boss.width || boss.x <= 0) bossXSpeed = -bossXSpeed; 

            if (frameCount % 50 == 0) bossBullets.add(new Rectangle(boss.x + (boss.width / 2) - 40, boss.y + 100, 80, 120));

            for (int i = 0; i < bullets.size(); i++) {
                if (bullets.get(i).intersects(boss)) {
                    sfxPlayer.playSFX("assets/tembaksound.wav");
                    explosions.add(new Explosion(bullets.get(i).x - 20, bullets.get(i).y, expImg3));
                    bullets.remove(i);
                    bossHp -= 5;
                    if (bossHp <= 0) {
                        score += 500;
                        explosions.add(new Explosion(boss.x, boss.y, expImg3));
                        bossActive = false;
                        boss = null;
                        bossBullets.clear(); 
                        winGame();
                    }
                }
            }
        }
        repaint();
    }

    private void startBoss() { bossActive = true; maxBossHp = 300; bossHp = maxBossHp; boss = new Rectangle(400, -200, 400, 200); }

    private void gameOver() {
        timer.stop();
        if (mainApp.getCurrentUserId() != -1) {
            long endTime = System.currentTimeMillis();
            int durationSeconds = (int) ((endTime - startTime) / 1000);
            db.saveScore(mainApp.getCurrentUserId(), level, score, durationSeconds); 
        }
        mainApp.showGameOver(score);
    }

    private void winGame() {
        timer.stop();
        if (mainApp.getCurrentUserId() != -1) {
            long endTime = System.currentTimeMillis();
            int durationSeconds = (int) ((endTime - startTime) / 1000);
            db.saveScore(mainApp.getCurrentUserId(), level, score, durationSeconds);
        }
        mainApp.showWinPanel(score);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. BACKGROUND
        if (inGameBg != null) g2d.drawImage(inGameBg, 0, 0, getWidth(), getHeight(), this);
        else { g2d.setColor(Color.BLACK); g2d.fillRect(0, 0, getWidth(), getHeight()); }

        // 2. TANK PLAYER
        if (pTrack != null) g2d.drawImage(pTrack, tankX, tankY, 80, 80, null);
        if (pHull != null) g2d.drawImage(pHull, tankX, tankY, 80, 80, null);
        else { g2d.setColor(Color.GREEN); g2d.fillRect(tankX, tankY, 80, 80); }
        if (pGun != null) g2d.drawImage(pGun, tankX, tankY, 80, 80, null);

        // 3. MUSUH
        for (Enemy en : enemies) {
            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(180), en.x + en.width/2, en.y + en.height/2);

            if (en.enemyLevel == 1) {
                if (eTrack1 != null) g2d.drawImage(eTrack1, en.x, en.y, en.width, en.height, null);
                if (eHull1 != null) g2d.drawImage(eHull1, en.x, en.y, en.width, en.height, null);
                else { g2d.setColor(Color.RED); g2d.fillRect(en.x, en.y, en.width, en.height); }
                if (eGun1 != null) g2d.drawImage(eGun1, en.x, en.y, en.width, en.height, null);
            } 
            else {
                if (eTrack2 != null) g2d.drawImage(eTrack2, en.x, en.y, en.width, en.height, null);
                if (eHull2 != null) g2d.drawImage(eHull2, en.x, en.y, en.width, en.height, null);
                else { g2d.setColor(Color.BLUE); g2d.fillRect(en.x, en.y, en.width, en.height); }
                if (eGun2 != null) g2d.drawImage(eGun2, en.x, en.y, en.width, en.height, null);
            }
            g2d.setTransform(old);

            if (en.enemyLevel >= 2) {
                int barWidth = 40; int barHeight = 5;
                int barX = en.x + (en.width - barWidth) / 2; int barY = en.y - 10;
                g2d.setColor(Color.RED); g2d.fillRect(barX, barY, barWidth, barHeight);
                g2d.setColor(Color.GREEN);
                int hpGreenWidth = (int) (((double) en.hp / en.maxHp) * barWidth);
                g2d.fillRect(barX, barY, hpGreenWidth, barHeight);
            }
        }

        // 4. BOSS
        if (bossActive && boss != null) {
            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(180), boss.x + boss.width/2, boss.y + boss.height/2);
            if (bTrack != null) g2d.drawImage(bTrack, boss.x, boss.y, boss.width, boss.height, null);
            if (bHull != null) g2d.drawImage(bHull, boss.x, boss.y, boss.width, boss.height, null);
            else { g2d.setColor(Color.MAGENTA); g2d.fillRect(boss.x, boss.y, boss.width, boss.height); }
            if (bGun != null) g2d.drawImage(bGun, boss.x, boss.y, boss.width, boss.height, null);
            g2d.setTransform(old);

            g2d.setColor(Color.RED); g2d.fillRect(boss.x, boss.y - 30, boss.width, 20);
            g2d.setColor(Color.GREEN);
            int barWidth = (int) (((double) bossHp / maxBossHp) * boss.width);
            g2d.fillRect(boss.x, boss.y - 30, barWidth, 20);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String hpText = "BOSS HP: " + bossHp + " / " + maxBossHp;
            int textWidth = g2d.getFontMetrics().stringWidth(hpText);
            g2d.drawString(hpText, boss.x + (boss.width - textWidth) / 2, boss.y - 15);
        }

        // 5. PELURU PLAYER
        for (Rectangle b : bullets) {
            if (level == 1 && bulletImg1 != null) g2d.drawImage(bulletImg1, b.x, b.y, b.width, b.height, null);
            else if (level >= 2 && bulletImg2 != null) g2d.drawImage(bulletImg2, b.x, b.y, b.width, b.height, null);
            else { g2d.setColor(Color.YELLOW); g2d.fillRect(b.x, b.y, b.width, b.height); }
        }

        // 6. PELURU BOSS
        for (Rectangle bb : bossBullets) {
            if (bossBulletImg != null) g2d.drawImage(bossBulletImg, bb.x, bb.y, bb.width, bb.height, null);
            else { g2d.setColor(Color.ORANGE); g2d.fillRect(bb.x, bb.y, bb.width, bb.height); }
        }

        // 7. PELURU MUSUH (UPDATE: GAMBAR SESUAI LEVEL PELURU)
        for (EnemyBullet eb : enemyBullets) {
            if (eb.bulletLevel == 1) {
                if (eBulletImg1 != null) g2d.drawImage(eBulletImg1, eb.x, eb.y, eb.width, eb.height, null);
                else { g2d.setColor(Color.RED); g2d.fillRect(eb.x, eb.y, eb.width, eb.height); }
            } 
            else {
                if (eBulletImg2 != null) g2d.drawImage(eBulletImg2, eb.x, eb.y, eb.width, eb.height, null);
                else { g2d.setColor(Color.BLUE); g2d.fillRect(eb.x, eb.y, eb.width, eb.height); }
            }
        }

        // 8. ITEMS
        g2d.setColor(Color.PINK); for (Rectangle it : hpItems) g2d.fillRect(it.x, it.y, it.width, it.height);
        g2d.setColor(Color.CYAN); for (Rectangle it : powerItems) g2d.fillRect(it.x, it.y, it.width, it.height);

        // 9. EXPLOSIONS
        for (Explosion exp : explosions) { if (exp.img != null) g2d.drawImage(exp.img, exp.x, exp.y, 60, 60, null); }

        // 10. HUD
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Poppins", Font.BOLD, 22));
        g2d.drawString("HP: " + hp, 20, 100);
        g2d.drawString("Score: " + score, 20, 130);
        g2d.drawString("Level: " + level, 20, 160);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) bullets.add(new Rectangle(tankX + 25, tankY - 30, 30, 50)); 
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    class Explosion {
        int x, y; Image img; int duration = 20; 
        public Explosion(int x, int y, Image img) { this.x = x; this.y = y; this.img = img; }
        public void update() { duration--; }
        public boolean isAlive() { return duration > 0; }
    }

    class Enemy extends Rectangle {
        int enemyLevel, hp, maxHp;
        public Enemy(int x, int y, int width, int height, int level, int health) {
            super(x, y, width, height);
            this.enemyLevel = level; this.hp = health; this.maxHp = health;
        }
    }

    // === CLASS BARU: ENEMY BULLET (SUPAYA INGAT LEVELNYA) ===
    class EnemyBullet extends Rectangle {
        int bulletLevel;
        public EnemyBullet(int x, int y, int width, int height, int level) {
            super(x, y, width, height);
            this.bulletLevel = level;
        }
    }
}
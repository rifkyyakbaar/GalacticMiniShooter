import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*; 

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private Main mainApp;
    private KoneksiDatabase db = new KoneksiDatabase(); 
    private SoundPlayer sfxPlayer = new SoundPlayer(); 
    private Timer timer;
    private Random rand = new Random();
    private long startTime; 

    private int tankX = 600, tankY = 600, tankSpeed = 10, hp = 100;
    private boolean moveLeft = false, moveRight = false;

    // === GAMBAR ASET ===
    private Image inGameBg; 
    private Image pHull, pGun, pTrack;
    private Image eHull1, eGun1, eTrack1; 
    private Image eHull2, eGun2, eTrack2; 
    
    // GAMBAR BOSS
    private Image bHull, bGun, bTrack;
    
    private Image expImg1, expImg2, expImg3;
    private Image bulletImg1, bulletImg2, bossBulletImg, laserImg; 
    private Image eBulletImg1, eBulletImg2;

    private ArrayList<Rectangle> bullets = new ArrayList<>();
    private ArrayList<Rectangle> bossBullets = new ArrayList<>(); 
    private ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>(); 
    private ArrayList<Rectangle> hpItems = new ArrayList<>();
    private ArrayList<Rectangle> powerItems = new ArrayList<>();

    private int bulletSpeed = 12;
    private int bossBulletSpeed = 8; 
    private int bossXSpeed = 3; 
    private int enemyBulletSpeed = 7;
    private int enemySpeed = 2; 
    private int spawnRate = 60; 
    private int itemSpeed = 3;
    private int level = 1, score = 0, frameCount = 0;
    
    private Rectangle boss;
    private int bossHp = 200, maxBossHp = 200; 
    private boolean bossActive = false;

    private JButton btnBack;

    public GamePanel(Main mainApp) {
        this.mainApp = mainApp;
        db.initialize(); 
        startTime = System.currentTimeMillis(); 

        // 1. Load Background & Peluru
        inGameBg = new ImageIcon("assets/background3.jpg").getImage();
        bulletImg1 = new ImageIcon("assets/Plasma.png").getImage();
        bulletImg2 = new ImageIcon("assets/Flame_D.png").getImage();
        laserImg   = new ImageIcon("assets/Laser.png").getImage();
        bossBulletImg = new ImageIcon("assets/Laser.png").getImage();
        eBulletImg1 = new ImageIcon("assets/EnemyBullet1.png").getImage();
        eBulletImg2 = new ImageIcon("assets/EnemyBullet2.png").getImage();

        // 2. LOAD PLAYER
        pHull  = new ImageIcon("assets/Hull_01.png").getImage();
        pGun   = new ImageIcon("assets/Gun_01.png").getImage();
        pTrack = new ImageIcon("assets/Track_01.png").getImage();

        // 3. LOAD MUSUH
        eHull1  = new ImageIcon("assets/Hull_02.png").getImage();
        eGun1   = new ImageIcon("assets/Gun_02.png").getImage();
        eTrack1 = new ImageIcon("assets/Track_02.png").getImage();

        eHull2  = new ImageIcon("assets/Hull_04.png").getImage();
        eGun2   = new ImageIcon("assets/Gun_04.png").getImage();
        eTrack2 = new ImageIcon("assets/Track_04.png").getImage();

        // === 4. LOAD BOSS (FIX ASSET ASLI) ===
        try {
            // Coba load Hull_03 (Badan)
            bHull = ImageIO.read(new File("assets/Hull_03.png"));
        } catch (IOException e) {
            // Kalau gagal, baru pinjam punya player
            System.out.println("Gagal load Hull_03, pakai Hull_01");
            bHull = pHull;
        }

        try {
            // Coba load Track_03 (Ban)
            bTrack = ImageIO.read(new File("assets/Track_03.png"));
        } catch (IOException e) {
            System.out.println("Gagal load Track_03, pakai Track_01");
            bTrack = pTrack;
        }

        try {
            // Coba load Gun_03 (Moncong)
            bGun = ImageIO.read(new File("assets/Gun_03.png"));
        } catch (IOException e) {
            bGun = pGun;
        }

        // 5. Load Efek
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
    public void addNotify() { super.addNotify(); requestFocusInWindow(); }

    @Override
    public void actionPerformed(ActionEvent e) {
        frameCount++;
        if (moveLeft && tankX > 20) tankX -= tankSpeed;
        if (moveRight && tankX < 1150) tankX += tankSpeed;

        if (!bossActive && frameCount % spawnRate == 0) {
            int spawnX = rand.nextInt(1200);
            if (level == 1) enemies.add(new Enemy(spawnX, -40, 40, 40, 1, 1));
            else enemies.add(new Enemy(spawnX, -60, 60, 60, 2, 2));
        }

        if (frameCount % 500 == 0) hpItems.add(new Rectangle(rand.nextInt(1200), -40, 30, 30));
        if (frameCount % 700 == 0) powerItems.add(new Rectangle(rand.nextInt(1200), -40, 30, 30));

        for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion exp = explosions.get(i);
            exp.update(); if (!exp.isAlive()) explosions.remove(i); 
        }

        for (int i = 0; i < bullets.size(); i++) {
            Rectangle b = bullets.get(i);
            b.y -= bulletSpeed;
            if (b.y < -20) bullets.remove(i);
        }

        for (int i = 0; i < enemyBullets.size(); i++) {
            EnemyBullet eb = enemyBullets.get(i);
            eb.y += enemyBulletSpeed;
            if (eb.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 10; sfxPlayer.playSFX("assets/tabraksound.wav");
                explosions.add(new Explosion(tankX, tankY, expImg2));
                enemyBullets.remove(i);
                if (hp <= 0) gameOver();
            } else if (eb.y > 720) enemyBullets.remove(i);
        }

        for (int i = 0; i < bossBullets.size(); i++) {
            Rectangle bb = bossBullets.get(i);
            bb.y += bossBulletSpeed; 
            if (bb.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 15; sfxPlayer.playSFX("assets/tabraksound.wav");
                explosions.add(new Explosion(tankX, tankY, expImg2));
                bossBullets.remove(i);
                if (hp <= 0) gameOver();
            } else if (bb.y > 720) bossBullets.remove(i);
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy en = enemies.get(i);
            en.y += enemySpeed;
            if (en.enemyLevel >= 2 && !bossActive && rand.nextInt(100) < 1) 
                enemyBullets.add(new EnemyBullet(en.x + 20, en.y + 60, 20, 40, en.enemyLevel));

            if (en.intersects(new Rectangle(tankX, tankY, 80, 80))) {
                hp -= 10; sfxPlayer.playSFX("assets/tabraksound.wav");
                explosions.add(new Explosion(tankX, tankY, expImg2));
                enemies.remove(i);
                if (hp <= 0) gameOver();
            } else if (en.y > 720) { score = Math.max(0, score - 5); enemies.remove(i); }
        }

        for (int i = 0; i < bullets.size(); i++) {
            Rectangle b = bullets.get(i);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy en = enemies.get(j);
                if (b.intersects(en)) {
                    bullets.remove(i); sfxPlayer.playSFX("assets/tembaksound.wav");
                    if (level == 1) explosions.add(new Explosion(en.x, en.y, expImg1)); 
                    else explosions.add(new Explosion(en.x, en.y, expImg3)); 
                    en.hp -= 1; if (en.hp <= 0) { enemies.remove(j); score += 10; }
                    break;
                }
            }
        }

        for (int i = 0; i < hpItems.size(); i++) {
            Rectangle it = hpItems.get(i);
            it.y += itemSpeed;
            if (it.intersects(new Rectangle(tankX, tankY, 80, 80))) { sfxPlayer.playSFX("assets/itemsound.wav"); hp = Math.min(100, hp + 15); hpItems.remove(i); }
        }
        for (int i = 0; i < powerItems.size(); i++) {
            Rectangle it = powerItems.get(i);
            it.y += itemSpeed;
            if (it.intersects(new Rectangle(tankX, tankY, 80, 80))) { sfxPlayer.playSFX("assets/itemsound.wav"); score += 10; powerItems.remove(i); }
        }

        if (score > 100 && level == 1) { level = 2; enemySpeed = 3; }
        if (score > 250 && level == 2) { level = 3; enemySpeed = 4; startBoss(); }

        if (bossActive && boss != null) {
            if (boss.y < 50) boss.y += 2; 
            boss.x += bossXSpeed;
            if (boss.x >= 1280 - boss.width || boss.x <= 0) bossXSpeed = -bossXSpeed; 
            if (frameCount % 50 == 0) bossBullets.add(new Rectangle(boss.x + (boss.width / 2) - 25, boss.y + 100, 50, 100));

            for (int i = 0; i < bullets.size(); i++) {
                if (bullets.get(i).intersects(boss)) {
                    sfxPlayer.playSFX("assets/tembaksound.wav");
                    explosions.add(new Explosion(bullets.get(i).x - 20, bullets.get(i).y, expImg3));
                    bullets.remove(i); bossHp -= 5;
                    if (bossHp <= 0) {
                        score += 500; explosions.add(new Explosion(boss.x, boss.y, expImg3)); bossActive = false; boss = null; bossBullets.clear(); winGame();
                    }
                }
            }
        }
        repaint();
    }

    private void startBoss() { 
        bossActive = true; maxBossHp = 300; bossHp = maxBossHp; 
        boss = new Rectangle(490, -350, 250, 250); }

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

        if (inGameBg != null) g2d.drawImage(inGameBg, 0, 0, getWidth(), getHeight(), this);
        else { g2d.setColor(Color.BLACK); g2d.fillRect(0, 0, getWidth(), getHeight()); }

        // PLAYER
        if (pTrack != null) g2d.drawImage(pTrack, tankX, tankY, 80, 80, null);
        if (pHull != null) g2d.drawImage(pHull, tankX, tankY, 80, 80, null);
        if (pGun != null) g2d.drawImage(pGun, tankX, tankY, 80, 80, null);

        // MUSUH
        for (Enemy en : enemies) {
            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(180), en.x + en.width/2, en.y + en.height/2);
            if (en.enemyLevel == 1) {
                if (eTrack1 != null) g2d.drawImage(eTrack1, en.x, en.y, en.width, en.height, null);
                if (eHull1 != null) g2d.drawImage(eHull1, en.x, en.y, en.width, en.height, null);
                if (eGun1 != null) g2d.drawImage(eGun1, en.x, en.y, en.width, en.height, null);
            } else {
                if (eTrack2 != null) g2d.drawImage(eTrack2, en.x, en.y, en.width, en.height, null);
                if (eHull2 != null) g2d.drawImage(eHull2, en.x, en.y, en.width, en.height, null);
                if (eGun2 != null) g2d.drawImage(eGun2, en.x, en.y, en.width, en.height, null);
            }
            g2d.setTransform(old);
            if (en.enemyLevel >= 2) {
                g2d.setColor(Color.RED); g2d.fillRect(en.x, en.y - 10, 40, 5);
                g2d.setColor(Color.GREEN); g2d.fillRect(en.x, en.y - 10, (int)(((double)en.hp/en.maxHp)*40), 5);
            }
        }

        // === 3. BOSS (LOGIKA DRAW YANG DIPERBAIKI) ===
        if (bossActive && boss != null) {
            AffineTransform old = g2d.getTransform();
            g2d.rotate(Math.toRadians(180), boss.x + boss.width/2, boss.y + boss.height/2);
            
            // 1. Track (Full Size)
            if (bTrack != null) g2d.drawImage(bTrack, boss.x, boss.y, boss.width, boss.height, null);
            
            // 2. Hull (Full Size)
            if (bHull != null) g2d.drawImage(bHull, boss.x, boss.y, boss.width, boss.height, null);

            // 3. Gun (KECILKAN UKURANNYA DI SINI)
            if (bGun != null) {
                // Kita bikin lebar Gun jadi setengah dari Boss (misal 150px)
                int gunW = boss.width / 2;  
                int gunH = boss.height;     
                
                // Posisikan X di tengah-tengah Boss
                int gunX = boss.x + (boss.width - gunW) / 2;

                g2d.drawImage(bGun, gunX, boss.y, gunW, gunH, null);
            }
            
            g2d.setTransform(old);

            g2d.setColor(Color.RED); g2d.fillRect(boss.x, boss.y - 30, boss.width, 20);
            g2d.setColor(Color.GREEN);
            int barWidth = (int) (((double) bossHp / maxBossHp) * boss.width);
            g2d.fillRect(boss.x, boss.y - 30, barWidth, 20);
            g2d.setColor(Color.WHITE); g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String hpText = "BOSS HP: " + bossHp + " / " + maxBossHp;
            int textWidth = g2d.getFontMetrics().stringWidth(hpText);
            g2d.drawString(hpText, boss.x + (boss.width - textWidth) / 2, boss.y - 15);
        }

        for (Rectangle b : bullets) {
            if (level == 1 && bulletImg1 != null) g2d.drawImage(bulletImg1, b.x, b.y, b.width, b.height, null);
            else if (level >= 2 && bulletImg2 != null) g2d.drawImage(bulletImg2, b.x, b.y, b.width, b.height, null);
            else { g2d.setColor(Color.YELLOW); g2d.fillRect(b.x, b.y, b.width, b.height); }
        }
        for (Rectangle bb : bossBullets) {
            if (bossBulletImg != null) g2d.drawImage(bossBulletImg, bb.x, bb.y, bb.width, bb.height, null);
            else { g2d.setColor(Color.ORANGE); g2d.fillRect(bb.x, bb.y, bb.width, bb.height); }
        }
        for (EnemyBullet eb : enemyBullets) {
            if (eb.bulletLevel == 1) {
                if (eBulletImg1 != null) g2d.drawImage(eBulletImg1, eb.x, eb.y, eb.width, eb.height, null);
                else { g2d.setColor(Color.RED); g2d.fillRect(eb.x, eb.y, eb.width, eb.height); }
            } else {
                if (eBulletImg2 != null) g2d.drawImage(eBulletImg2, eb.x, eb.y, eb.width, eb.height, null);
                else { g2d.setColor(Color.BLUE); g2d.fillRect(eb.x, eb.y, eb.width, eb.height); }
            }
        }

        g2d.setColor(Color.PINK); for (Rectangle it : hpItems) g2d.fillRect(it.x, it.y, it.width, it.height);
        g2d.setColor(Color.CYAN); for (Rectangle it : powerItems) g2d.fillRect(it.x, it.y, it.width, it.height);
        for (Explosion exp : explosions) { if (exp.img != null) g2d.drawImage(exp.img, exp.x, exp.y, 60, 60, null); }

        g2d.setColor(Color.WHITE); g2d.setFont(new Font("Poppins", Font.BOLD, 22));
        g2d.drawString("HP: " + hp, 20, 100); g2d.drawString("Score: " + score, 20, 130); g2d.drawString("Level: " + level, 20, 160);
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

    class EnemyBullet extends Rectangle {
        int bulletLevel;
        public EnemyBullet(int x, int y, int width, int height, int level) {
            super(x, y, width, height);
            this.bulletLevel = level;
        }
    }
}
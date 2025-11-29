import java.awt.*;
import javax.swing.*;

public class GameOverPanel extends JPanel {

    private Main mainApp;
    private int score;
    private Image bgImage; // Variabel untuk gambar background

    public GameOverPanel(Main mainApp, int score) {
        this.mainApp = mainApp;
        this.score = score;

        setLayout(null);

        // === LOAD GAMBAR BACKGROUND ===
        // Pastikan nama file dan ekstensinya benar (.jpeg/.jpg/.png) di folder assets
        bgImage = new ImageIcon("assets/background2.jpg").getImage();

        // === JUDUL GAME OVER ===
        JLabel title = new JLabel("GAME OVER");
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.RED); // Warna Merah biar dramatis
        // Set lebar full (1280) dan align center supaya pas di tengah
        title.setBounds(0, 100, 1280, 60);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // === SKOR ===
        JLabel scoreLbl = new JLabel("Final Score: " + score);
        scoreLbl.setFont(new Font("Poppins", Font.BOLD, 28));
        scoreLbl.setForeground(Color.WHITE);
        scoreLbl.setBounds(0, 180, 1280, 40);
        scoreLbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLbl);

        // === TOMBOL RETRY ===
        JButton btnRetry = new JButton("Retry");
        btnRetry.setFont(new Font("Poppins", Font.BOLD, 20));
        btnRetry.setBackground(new Color(218, 185, 80)); // Emas
        btnRetry.setBounds(540, 280, 200, 50);
        btnRetry.setFocusPainted(false);
        
        btnRetry.addActionListener(e -> {
            mainApp.playButtonSound(); // Bunyi klik
            mainApp.showGamePanel(); // Main lagi
        });
        add(btnRetry);

        // === TOMBOL MAIN MENU ===
        JButton btnMenu = new JButton("Main Menu");
        btnMenu.setFont(new Font("Poppins", Font.BOLD, 20));
        btnMenu.setBackground(new Color(218, 185, 80));
        btnMenu.setBounds(540, 350, 200, 50);
        btnMenu.setFocusPainted(false);
        
        btnMenu.addActionListener(e -> {
            mainApp.playButtonSound(); // Bunyi klik
            mainApp.showMainMenu();
        });
        add(btnMenu);

        // === TOMBOL QUIT ===
        JButton btnQuit = new JButton("Quit Game");
        btnQuit.setFont(new Font("Poppins", Font.BOLD, 20));
        btnQuit.setBackground(Color.GRAY);
        btnQuit.setForeground(Color.WHITE);
        btnQuit.setBounds(540, 420, 200, 50);
        btnQuit.setFocusPainted(false);
        
        btnQuit.addActionListener(e -> System.exit(0));
        add(btnQuit);
    }

    // === BAGIAN MENGGAMBAR BACKGROUND ===
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Gambar Background
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Lapisan Hitam Transparan (Supaya tulisan jelas)
        g.setColor(new Color(0, 0, 0, 200)); // Hitam pekat transparan (0-255)
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
import java.awt.*;
import javax.swing.*;

public class WinPanel extends JPanel {
    private Main mainApp;
    private int score;
    private Image bgImage; // Variabel untuk gambar

    public WinPanel(Main mainApp, int score) {
        this.mainApp = mainApp;
        this.score = score;

        setLayout(null);
        
        // === LOAD GAMBAR BACKGROUND ===
        // Pastikan file "background2.png" ada di folder assets
        bgImage = new ImageIcon("assets/background2.jpg").getImage();

        // === TULISAN MISSION ACCOMPLISHED ===
        JLabel title = new JLabel("MISSION ACCOMPLISHED!");
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.GREEN); // Warna Hijau Kemenangan
        title.setBounds(0, 100, 1280, 60);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // === SUBTITLE YOU WIN ===
        JLabel subTitle = new JLabel("YOU WIN");
        subTitle.setFont(new Font("Poppins", Font.BOLD, 36));
        subTitle.setForeground(Color.WHITE);
        subTitle.setBounds(0, 160, 1280, 50);
        subTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(subTitle);

        // === SKOR ===
        JLabel scoreLbl = new JLabel("Final Score: " + score);
        scoreLbl.setFont(new Font("Poppins", Font.PLAIN, 28));
        scoreLbl.setForeground(Color.YELLOW);
        scoreLbl.setBounds(0, 220, 1280, 40);
        scoreLbl.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLbl);

        // === TOMBOL MAIN MENU ===
        JButton btnMenu = new JButton("Main Menu");
        btnMenu.setFont(new Font("Poppins", Font.BOLD, 20));
        btnMenu.setBackground(new Color(218, 185, 80)); // Warna Emas
        btnMenu.setBounds(540, 350, 200, 50);
        btnMenu.setFocusPainted(false);
        btnMenu.addActionListener(e -> {
            mainApp.playButtonSound();
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

        // 1. Gambar Background Image (Full Layar)
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback kalau gambar tidak ketemu: Pakai warna hitam
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Tambahkan Lapisan Gelap Transparan (Supaya tulisan kebaca jelas)
        g.setColor(new Color(0, 0, 0, 180)); // Hitam Transparan (Opasitas 180/255)
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
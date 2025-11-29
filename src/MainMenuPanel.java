import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private Main mainApp;
    private Image bgImage;
    private Image backgroundImage;

    // Komponen UI
    private JLabel titleLabel;
    private JButton btnStart, btnScore, btnExit;

    public MainMenuPanel(Main mainApp) {
        this.mainApp = mainApp;
        setLayout(null); // Layout manual agar kita bisa atur posisi sendiri

        // Load Gambar
        bgImage = new ImageIcon("assets/mainmenu_bg.png").getImage();
        backgroundImage = new ImageIcon("assets/background2.jpg").getImage();

        // === JUDUL ===
        titleLabel = new JLabel("MAIN MENU");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Ukuran label judul (posisi X dan Y diatur nanti di paintComponent)
        titleLabel.setSize(1280, 60); 
        add(titleLabel);

        // === TOMBOL-TOMBOL ===
        btnStart = createButton("Play Game");
        btnScore = createButton("Score");
        btnExit  = createButton("Exit");

        add(btnStart);
        add(btnScore);
        add(btnExit);

        // === ACTION LISTENER ===
        btnStart.addActionListener(e -> {
            mainApp.playButtonSound();
            mainApp.showGamePanel();
        });

        btnScore.addActionListener(e -> {
            mainApp.playButtonSound();
            mainApp.showLeaderboard();
        });

        btnExit.addActionListener(e -> {
            mainApp.playButtonSound();
            mainApp.showLoginPanel(); 
        });
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 24));
        btn.setBackground(new Color(218, 185, 80)); 
        btn.setSize(280, 50); // Ukuran tetap
        btn.setFocusPainted(false);
        return btn;
    }

    // Fungsi pintar untuk menaruh tombol di tengah tanpa bikin lag
    private void centerComponent(JComponent comp, int y) {
        int x = (getWidth() - comp.getWidth()) / 2;
        // HANYA pindahkan jika posisinya belum pas (Mencegah Loop/Layar Putih)
        if (comp.getX() != x || comp.getY() != y) {
            comp.setLocation(x, y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        // 1. Gambar Background (Full Screen)
        if (backgroundImage != null) g.drawImage(backgroundImage, 0, 0, w, h, this);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, w, h, this);

        // 2. Kotak Transparan (Selalu di tengah)
        g.setColor(new Color(255, 255, 255, 180));
        int boxWidth = 380;
        int boxX = (w - boxWidth) / 2;
        g.fillRoundRect(boxX, 210, boxWidth, 240, 35, 35);

        // 3. === ATUR POSISI TOMBOL OTOMATIS ===
        // Judul di atas
        if (titleLabel.getWidth() != w) titleLabel.setSize(w, 60); // Sesuaikan lebar judul
        centerComponent(titleLabel, 80);

        // Tombol (Naik & Rapat)
        centerComponent(btnStart, 230);
        centerComponent(btnScore, 300);
        centerComponent(btnExit, 370);
    }
}
import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {

    private Main mainApp;

    private Image bgImage;
    private Image logoImage;
    private Image backgroundImage;

    public MainMenuPanel(Main mainApp) {
        this.mainApp = mainApp;

        setLayout(null);

        bgImage = new ImageIcon("assets/mainmenu_bg.png").getImage();
        logoImage = new ImageIcon("assets/tanktop.png").getImage();
        backgroundImage = new ImageIcon("assets/background2.jpeg").getImage();

        JLabel title = new JLabel("MAIN MENU");
        title.setFont(Theme.FONT_BOLD_48);
        title.setForeground(Color.WHITE);
        title.setBounds(500, 80, 400, 60);
        add(title);

        JButton btnStart = createButton("Mulai Game", 260);
        JButton btnVehicle = createButton("Pilih Kendaraan", 330);
        JButton btnScore = createButton("Score", 400);
        JButton btnExit = createButton("Exit", 470);

        add(btnStart);
        add(btnVehicle);
        add(btnScore);
        add(btnExit);

        // === ACTION LISTENER ===

    // 1. Tombol Start
    btnStart.addActionListener(e -> {
        mainApp.playButtonSound();  // <--- Masukkan ke dalam sini!
        mainApp.showGamePanel();
    });

    // 2. Tombol Vehicle
    btnVehicle.addActionListener(e -> {
        mainApp.playButtonSound();  // <--- Masukkan ke dalam sini!
        mainApp.showVehicleSelect();
    });

    // 3. Tombol Score
    btnScore.addActionListener(e -> {
        mainApp.playButtonSound();  // <--- Masukkan ke dalam sini!
        mainApp.showLeaderboard();
    });

    // 4. Tombol Exit
    btnExit.addActionListener(e -> {
        mainApp.playButtonSound();  // <--- Masukkan ke dalam sini!
        mainApp.showLoginPanel();
    });

    }
    private JButton createButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setFont(Theme.FONT_BOLD_24);
        btn.setBackground(Theme.GOLD);
        btn.setBounds(500, y, 280, 50);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImage != null)
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        if (logoImage != null)
            g.drawImage(logoImage, 410, 150, 450, 120, this);

        g.setColor(new Color(255, 255, 255, 180));
        g.fillRoundRect(450, 230, 380, 330, 35, 35);
        g.drawImage(backgroundImage, 0, 0, 1280, 720, this);
    }
}

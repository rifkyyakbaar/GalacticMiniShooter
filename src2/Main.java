import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame {

    // ===========================================
    // Variable Global
    // ===========================================
    private String selectedVehicle = "Tank_Default";
    
    // Player Musik & File
    private SoundPlayer musicPlayer = new SoundPlayer(); 
    // Pastikan nama file musik ini ada di folder assets
    private String musicFile = "assets/sound war 1.wav"; 

    // === [PENTING] DATA PLAYER YANG LOGIN ===
    // Variable ini dibutuhkan oleh GamePanel untuk menyimpan skor ke user yang benar
    private int currentUserId = -1; // -1 artinya belum login

    // Setter: Dipanggil di LoginPanel saat login berhasil
    public void setCurrentUser(int id) {
        this.currentUserId = id;
    }

    // Getter: Dipanggil di GamePanel saat Game Over untuk saveScore
    public int getCurrentUserId() {
        return currentUserId;
    }
    // ========================================

    public String getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(String v) {
        selectedVehicle = v;
    }

    // ===========================================
    // Constructor (Dipanggil saat new Main())
    // ===========================================
    public Main() {
        setTitle("TANK-TOP");

        // =============== FULLSCREEN BORDERLESS ===============
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setUndecorated(true);
        setSize(screen.width, screen.height);
        setLocation(0, 0);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // === MULAI MUSIK ===
        // Musik langsung main saat aplikasi dibuka
        musicPlayer.playLoop(musicFile); 
        // ===================

        showLoginPanel(); 
        setVisible(true);

        // ===========================================
        // Tombol ESC untuk Keluar
        // ===========================================
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ESCAPE"), "EXIT_APP");

        getRootPane().getActionMap().put("EXIT_APP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Apakah Anda yakin ingin keluar?",
                        "Keluar Game",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    // ===========================================
    // FUNGSI SUARA TOMBOL
    // ===========================================
    public void playButtonSound() {
        musicPlayer.playSFX("assets/soundbutton.wav");
    }

    // ===========================================
    // PANEL NAVIGATION
    // ===========================================

    public void showLoginPanel() {
        // NYALAKAN MUSIK (Login)
        musicPlayer.playLoop(musicFile); 
        
        setContentPane(new LoginPanel(this));
        revalidate();
        repaint();
    }

    public void showMainMenu() {
        // NYALAKAN MUSIK (Menu Utama)
        musicPlayer.playLoop(musicFile);

        setContentPane(new MainMenuPanel(this));
        revalidate();
        repaint();
    }

    public void showVehicleSelect() {
        // NYALAKAN MUSIK (Sub-menu)
        musicPlayer.playLoop(musicFile);
        
        setContentPane(new VehicleSelectPanel(this));
        revalidate();
        repaint();
    }

    public void showLeaderboard() {
        // NYALAKAN MUSIK (Leaderboard)
        musicPlayer.playLoop(musicFile);
        
        setContentPane(new LeaderboardPanel(this));
        revalidate();
        repaint();
    }

    public void showGamePanel() {
        // === MATIKAN MUSIK SAAT MAIN GAME ===
        if (musicPlayer != null) {
            musicPlayer.stop(); 
        }

        setContentPane(new GamePanel(this));
        revalidate();   
        repaint();
    }

    public void showGameOver(int score) {
        // NYALAKAN LAGI (Pas Game Over balik ke suasana menu)
        musicPlayer.playLoop(musicFile);
        
        setContentPane(new GameOverPanel(this, score));
        revalidate();
        repaint();
    }

    // ===========================================
    // MAIN ENTRY POINT
    // ===========================================
    public static void main(String[] args) {
        new Main(); 
    }
}
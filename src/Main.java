import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame {

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
    // PANEL NAVIGATION (AUTO SWITCH MUSIC)
    // ===========================================

    public void showLoginPanel() {
        // Minta putar lagu Menu
        // (Kalau sebelumnya sudah nyala, dia gak bakal restart)
        musicPlayer.playLoop("assets/sound war 1.wav"); 
        
        setContentPane(new LoginPanel(this));
        revalidate();
        repaint();
    }

    public void showMainMenu() {
        // Minta putar lagu Menu
        // (Dari Login ke sini lagunya sama, jadi dia bakal LANJUT terus)
        musicPlayer.playLoop("assets/sound war 1.wav");

        setContentPane(new MainMenuPanel(this));
        revalidate();
        repaint();
    }


    public void showLeaderboard() {
        musicPlayer.playLoop("assets/sound war 1.wav");
        setContentPane(new LeaderboardPanel(this));
        revalidate();
        repaint();
    }

    public void showGamePanel() {
        // === GANTI LAGU ===
        // Kita minta lagu "epicwar.wav".
        // Karena beda dengan "sound war 1", SoundPlayer akan otomatis STOP yang lama dan PUTAR yang ini.
        musicPlayer.playLoop("assets/epicwar.wav");

        setContentPane(new GamePanel(this));
        revalidate();   
        repaint();
    }

   public void showGameOver(int score) {
        // === PUTAR MUSIK GAME OVER ===
        // SoundPlayer akan otomatis mematikan musik perang dan memutar yang ini
        musicPlayer.playLoop("assets/gameovermusic.wav");
        
        setContentPane(new GameOverPanel(this, score));
        revalidate();
        repaint();
    }

    public void showWinPanel(int score) {
        // === PUTAR MUSIK KEMENANGAN ===
        // Karena beda dengan musik "epicwar", dia akan otomatis stop yang lama
        // dan memutar lagu kemenangan ini.
        musicPlayer.playLoop("assets/winsound.wav"); 
        
        setContentPane(new WinPanel(this, score));
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
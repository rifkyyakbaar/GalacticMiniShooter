import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {

    private Image backgroundImage;
    private Image welcomeImage;
    private Image logoImage;

    public JTextField usernameField;
    public JPasswordField passwordField;
    public JButton btnLogin, btnRegister;

    private Main mainApp;

    public LoginPanel(Main mainApp) {
        this.mainApp = mainApp;

        // Load Gambar
        backgroundImage = new ImageIcon("assets/background.jpg").getImage();
        welcomeImage = new ImageIcon("assets/welcometo.png").getImage();

        setLayout(null);

        // === USERNAME LABEL ===
        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Poppins", Font.BOLD, 18));
        userLbl.setForeground(Color.DARK_GRAY); // Warna gelap biar kebaca di box putih
        userLbl.setBounds(455, 470, 150, 30);
        add(userLbl);

        // === USERNAME FIELD ===
        usernameField = new JTextField();
        usernameField.setFont(new Font("Poppins", Font.PLAIN, 18));
        usernameField.setBounds(580, 470, 220, 30);
        add(usernameField);

        // === PASSWORD LABEL ===
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Poppins", Font.BOLD, 18));
        passLbl.setForeground(Color.DARK_GRAY);
        passLbl.setBounds(455, 520, 150, 30);
        add(passLbl);

        // === PASSWORD FIELD ===
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Poppins", Font.PLAIN, 18));
        passwordField.setBounds(580, 520, 220, 30);
        add(passwordField);

        // === TOMBOL LOGIN ===
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Poppins", Font.BOLD, 18));
        btnLogin.setBackground(new Color(218, 185, 80)); // Warna Emas
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(535, 568, 200, 35);
        btnLogin.setFocusPainted(false); // Hilangkan garis fokus biar rapi
        add(btnLogin);

        // === TOMBOL REGISTER ===
        btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Poppins", Font.BOLD, 18));    
        btnRegister.setBackground(new Color(70, 130, 180)); // Warna Biru Baja (Biar beda dikit)
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBounds(535, 615, 200, 35);
        btnRegister.setFocusPainted(false);
        add(btnRegister);

        // ==============================================
        // LOGIKA LOGIN (UPDATE PENTING)
        // ==============================================
        btnLogin.addActionListener(e -> {
            mainApp.playButtonSound(); // Suara tombol

            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            // 1. Cek Login via Database & Ambil ID
            int userId = KoneksiDatabase.checkLogin(user, pass);

            if (userId != -1) {
                // 2. SIMPAN ID KE MAIN APP (Supaya Score nanti tersimpan)
                mainApp.setCurrentUser(userId); 
                
                JOptionPane.showMessageDialog(this, "Login Berhasil! Selamat Datang, " + user);
                
                // 3. Pindah ke Menu
                mainApp.showMainMenu(); 
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", 
                    "Gagal Login", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ==============================================
        // LOGIKA REGISTER
        // ==============================================
        btnRegister.addActionListener(e -> {
            mainApp.playButtonSound(); // Suara tombol
            
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username/Password tidak boleh kosong!");
                return;
            }

            // Panggil fungsi register dari KoneksiDatabase
            if (KoneksiDatabase.register(user, pass)) {
                JOptionPane.showMessageDialog(this, "Registrasi berhasil! Silakan login.");
            } else {
                JOptionPane.showMessageDialog(this, "Username sudah dipakai! Coba nama lain.", 
                    "Gagal Register", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Gambar Background Full
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Gambar Header (Welcome To)
        if (welcomeImage != null) {
            // 1. Ambil ukuran asli gambar
            int imgWidth = welcomeImage.getWidth(null);
            int imgHeight = welcomeImage.getHeight(null);

            // 2. Tentukan lebar yang kamu mau (misal tetap 1000 biar lebar)
            int targetWidth = 1000; 

            // 3. Hitung tinggi otomatis (Rumus Matematika Rasio)
            // Tinggi Baru = (Lebar Baru * Tinggi Asli) / Lebar Asli
            int targetHeight = (targetWidth * imgHeight) / imgWidth;

            // 4. Gambar dengan ukuran yang sudah dihitung (biar ga gepeng)
            // X tetap 140 (atau bisa diatur biar tengah), Y tetap 60
            
            // Agar posisi X pas di tengah layar (Optional, biar lebih rapi)
            int centerX = (getWidth() - targetWidth) / 2; 
            
            g.drawImage(welcomeImage, centerX, 60, targetWidth, targetHeight, this);
        }

        // Kotak Putih Transparan di belakang form
        g.setColor(new Color(255, 255, 255, 200)); // Putih transparan
        g.fillRoundRect(430, 450, 400, 220, 40, 40); // Sedikit lebih tinggi biar muat tombol
    }
}
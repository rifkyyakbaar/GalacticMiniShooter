import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class LeaderboardPanel extends JPanel {

    private Main mainApp;
    private KoneksiDatabase db = new KoneksiDatabase();
    private Image bgImage;

    public LeaderboardPanel(Main mainApp) {
        this.mainApp = mainApp;
        setLayout(null);

        // Load Background
        bgImage = new ImageIcon("assets/background.png").getImage(); 

        // === JUDUL ===
        JLabel title = new JLabel("TOP COMMANDERS");
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 1280, 60); // Full width biar bisa rata tengah
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // ============================================
        // SETUP TABEL LEADERBOARD (4 KOLOM)
        // ============================================
        
        // 1. Nama Kolom
        String[] columns = {"Player", "Level", "Score", "Waktu"};

        // 2. Ambil Data dari Database
        db.initialize(); // Pastikan koneksi siap
        ArrayList<String[]> rawData = db.getLeaderboard();

        // 3. Konversi ke Array 2 Dimensi [Baris][4 Kolom]
        String[][] data = new String[rawData.size()][4];
        for (int i = 0; i < rawData.size(); i++) {
            data[i] = rawData.get(i);
        }

        // 4. Buat JTable
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Poppins", Font.PLAIN, 16));
        table.setRowHeight(35);
        
        // Styling Header Tabel
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(218, 185, 80)); // Warna Emas
        table.getTableHeader().setForeground(Color.BLACK);
        
        // Agar tabel tidak bisa diedit
        table.setDefaultEditor(Object.class, null);

        // Agar tulisan di sel tabel Rata Tengah (Center)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 5. Masukkan ke ScrollPane
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(340, 150, 600, 380); // Posisi Tabel di tengah
        scroll.getViewport().setBackground(Color.WHITE); 
        add(scroll);

        // ============================================
        // TOMBOL KEMBALI
        // ============================================
        JButton btnBack = new JButton("Kembali");
        btnBack.setFont(new Font("Poppins", Font.BOLD, 20));
        btnBack.setBackground(new Color(218, 185, 80)); // Warna Emas
        btnBack.setForeground(Color.WHITE);
        btnBack.setBounds(500, 560, 280, 50);
        btnBack.setFocusPainted(false);
        
        btnBack.addActionListener(e -> {
            mainApp.playButtonSound(); // Bunyi Klik
            mainApp.showMainMenu();
        });
        add(btnBack);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Gambar Background
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
        
        // Kotak Transparan di belakang tabel biar manis
        g.setColor(new Color(0, 0, 0, 150)); // Hitam transparan
        g.fillRoundRect(300, 130, 680, 500, 40, 40);
    }
}
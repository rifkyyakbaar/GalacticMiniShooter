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

        bgImage = new ImageIcon("assets/background2.jpg").getImage(); 

        JLabel title = new JLabel("TOP COMMANDERS");
        title.setFont(new Font("Poppins", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 1280, 60);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        // === UPDATE 5 KOLOM (DITAMBAH DURASI) ===
        String[] columns = {"Player", "Level", "Score", "Tanggal", "Durasi"};

        db.initialize(); 
        ArrayList<String[]> rawData = db.getLeaderboard();

        // Array 2D [Baris][5 Kolom]
        String[][] data = new String[rawData.size()][5];
        for (int i = 0; i < rawData.size(); i++) {
            data[i] = rawData.get(i);
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Poppins", Font.PLAIN, 16));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 18));
        table.getTableHeader().setBackground(new Color(218, 185, 80)); 
        table.getTableHeader().setForeground(Color.BLACK);
        table.setDefaultEditor(Object.class, null);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < 5; i++) { // Loop sampai 5 kolom
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(240, 150, 800, 380); // Lebarin dikit tabelnya biar muat
        scroll.getViewport().setBackground(Color.WHITE); 
        add(scroll);

        JButton btnBack = new JButton("Kembali");
        btnBack.setFont(new Font("Poppins", Font.BOLD, 20));
        btnBack.setBackground(new Color(218, 185, 80)); 
        btnBack.setForeground(Color.WHITE);
        btnBack.setBounds(500, 560, 280, 50);
        btnBack.setFocusPainted(false);
        
        btnBack.addActionListener(e -> {
            mainApp.playButtonSound(); 
            mainApp.showMainMenu();
        });
        add(btnBack);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(new Color(0, 0, 0, 150)); 
        g.fillRoundRect(200, 130, 880, 500, 40, 40); // Lebarin background hitamnya juga
    }
}
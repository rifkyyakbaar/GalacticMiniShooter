import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class KoneksiDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/tanktop_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn;

    public void initialize() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Koneksi MySQL Berhasil!");
            }
        } catch (Exception e) {
            System.out.println("❌ Koneksi Gagal: " + e.getMessage());
        }
    }

    public static boolean register(String username, String password) {
        // ... (Kode register sama seperti sebelumnya, tidak berubah)
        try {
            checkConnection();
            String check = "SELECT * FROM users WHERE username=?";
            PreparedStatement psCheck = conn.prepareStatement(check);
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next()) return false;

            String sql = "INSERT INTO users (username, password) VALUES (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (Exception e) { return false; }
    }

    public static int checkLogin(String username, String password) {
        // ... (Kode login sama seperti sebelumnya, tidak berubah)
        try {
            checkConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {}
        return -1;
    }

    // === UPDATE 1: SIMPAN DURASI (Seconds) ===
    public void saveScore(int userId, int level, int score, int durationSeconds) {
        try {
            checkConnection();
            String sql = "INSERT INTO scores (user_id, level, score, play_duration) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, level);
            ps.setInt(3, score);
            ps.setInt(4, durationSeconds); // Simpan detik
            ps.executeUpdate();
            System.out.println("✅ Score & Durasi tersimpan!");
        } catch (Exception e) {
            System.out.println("❌ Gagal simpan: " + e.getMessage());
        }
    }

    // === UPDATE 2: AMBIL DURASI & FORMAT JADI MENIT:DETIK ===
    public ArrayList<String[]> getLeaderboard() {
        ArrayList<String[]> list = new ArrayList<>();
        try {
            checkConnection();
            String sql = "SELECT u.username, s.level, s.score, s.score_date, s.play_duration " +
                         "FROM scores s JOIN users u ON s.user_id = u.id " +
                         "ORDER BY s.score DESC LIMIT 10";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM HH:mm");

            while (rs.next()) {
                String nama = rs.getString("username");
                String lvl = String.valueOf(rs.getInt("level"));
                String skor = String.valueOf(rs.getInt("score"));
                
                // Format Tanggal
                Timestamp tgl = rs.getTimestamp("score_date");
                String waktuMain = (tgl != null) ? sdf.format(tgl) : "-";

                // Format Durasi (Detik -> MM:SS)
                int durasiDetik = rs.getInt("play_duration");
                int menit = durasiDetik / 60;
                int detik = durasiDetik % 60;
                String durasiStr = String.format("%02d:%02d", menit, detik); // Contoh: 03:45

                // Masukkan 5 Data (Nama, Level, Score, Tanggal, Durasi)
                list.add(new String[]{nama, lvl, skor, waktuMain, durasiStr});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private static void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) new KoneksiDatabase().initialize();
        } catch (SQLException e) {}
    }
}
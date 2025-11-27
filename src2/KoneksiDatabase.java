import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList; // Import untuk format tanggal

public class KoneksiDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/tanktop_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection conn;

    // =============================
    // INITIALIZE CONNECTION
    // =============================
    public void initialize() {
        try {
            // Cek jika driver belum di-load (Opsional tapi bagus untuk kestabilan)
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("✅ Koneksi MySQL Berhasil!");
            }
        } catch (Exception e) {
            System.out.println("❌ Koneksi Gagal: " + e.getMessage());
        }
    }

    // =============================
    // REGISTER USER
    // =============================
    public static boolean register(String username, String password) {
        try {
            checkConnection(); // Pastikan koneksi hidup

            // cek username sudah ada atau belum
            String check = "SELECT * FROM users WHERE username=?";
            PreparedStatement psCheck = conn.prepareStatement(check);
            psCheck.setString(1, username);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                return false; // username sudah dipakai
            }

            // insert user baru
            String sql = "INSERT INTO users (username, password) VALUES (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Register Error: " + e.getMessage());
            return false;
        }
    }

    // =============================
    // LOGIN USER
    // =============================
    public static int checkLogin(String username, String password) {
        try {
            checkConnection(); // Pastikan koneksi hidup

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); // return user id
            }

        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return -1; // gagal login
    }

    // =================================================
    // SIMPAN SCORE (UPDATE: Tambah Level)
    // =================================================
    public void saveScore(int userId, int level, int score) {
        try {
            checkConnection();

            // Insert USER_ID, LEVEL, dan SCORE (Waktu otomatis dari MySQL)
            String sql = "INSERT INTO scores (user_id, level, score) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setInt(2, level);
            ps.setInt(3, score);

            ps.executeUpdate();
            System.out.println("✅ Score & Level tersimpan!");

        } catch (Exception e) {
            System.out.println("❌ Save Score Error: " + e.getMessage());
        }
    }

    // =================================================
    // AMBIL LEADERBOARD (UPDATE: 4 Kolom + Format Waktu)
    // =================================================
    public ArrayList<String[]> getLeaderboard() {
        ArrayList<String[]> list = new ArrayList<>();

        try {
            checkConnection();

            // Query gabungan (JOIN) untuk ambil Nama, Level, Score, dan Waktu
            String sql = 
                "SELECT u.username, s.level, s.score, s.score_date " +
                "FROM scores s " +
                "JOIN users u ON s.user_id = u.id " +
                "ORDER BY s.score DESC LIMIT 10";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Format tanggal (Contoh: 28-Nov 14:30)
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM HH:mm");

            while (rs.next()) {
                String nama = rs.getString("username");
                String lvl = String.valueOf(rs.getInt("level"));
                String skor = String.valueOf(rs.getInt("score"));
                
                // Ambil tanggal dan format jadi String
                Timestamp tgl = rs.getTimestamp("score_date");
                String waktu = (tgl != null) ? sdf.format(tgl) : "-";

                // Masukkan ke list (Array isi 4 data)
                list.add(new String[]{nama, lvl, skor, waktu});
            }

        } catch (Exception e) {
            System.out.println("❌ Leaderboard Error: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    // Helper untuk memastikan koneksi tidak null saat dipanggil static
    private static void checkConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                // Buat instance baru sebentar untuk panggil initialize
                new KoneksiDatabase().initialize();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
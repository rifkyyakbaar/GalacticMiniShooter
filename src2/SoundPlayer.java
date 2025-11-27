import java.io.File;
import javax.sound.sampled.*;

public class SoundPlayer {
    private Clip clip;

    public void playLoop(String filePath) {
        try {
            // Cek: Jika musik sudah jalan, jangan di-restart
            if (clip != null && clip.isRunning()) {
                return;
            }

            File musicPath = new File(filePath);

            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                
                // Opsional: Turunkan volume sedikit
                try {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-5.0f); 
                } catch (Exception ex) {
                    // Abaikan jika kontrol volume tidak didukung
                }

                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Ulang terus
            } else {
                System.out.println("File musik tidak ditemukan: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        System.out.println("🔍 [DEBUG] Mencoba mematikan musik..."); // Laporan 1

        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
                System.out.println("🛑 [SUKSES] Clip di-stop!"); // Laporan 2
            }
            clip.close();
            clip = null; // Kosongkan
        } else {
            System.out.println("⚠️ [INFO] Tidak ada musik yang sedang jalan (Clip null).");
        }
    }

    // === TAMBAHKAN FUNGSI INI DI PALING BAWAH CLASS SOUNDPLAYER ===
    public void playSFX(String filePath) {
        try {
            File soundPath = new File(filePath);

            if (soundPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundPath);
                
                // Kita buat Clip BARU supaya tidak mengganggu musik background
                Clip sfxClip = AudioSystem.getClip(); 
                sfxClip.open(audioInput);
                
                // Volume SFX (sedikit lebih keras)
                try {
                    FloatControl gainControl = (FloatControl) sfxClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-2.0f); 
                } catch (Exception ex) {}

                sfxClip.start(); // Mainkan sekali saja
            } else {
                System.out.println("Sound Button tidak ketemu: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
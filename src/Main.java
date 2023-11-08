import DataBase.ProcessData;
import Listen.BackgroundMusic;
import controller.GameController;
import model.MyThread;
import view.ChessGameFrame;
import Listen.BackgroundMusic;

import javax.swing.*;

public class Main {

    public static JLabel jLabel = new JLabel();

    public static void main(String[] args) {

//        ProcessData.LoadGUI();
        SwingUtilities.invokeLater(() -> {

            ChessGameFrame mainFrame = new ChessGameFrame(720, 720);
            mainFrame.setVisible(false);
        });

        MyThread.backgroundmusic = new Thread(() -> {
            while (true){
                if(GameController.page == 1){
                    BackgroundMusic.playMusic(BackgroundMusic.MainPageMusicPath, "Overall");
                }
                else {
                    BackgroundMusic.playMusic(BackgroundMusic.MusicPath,"Overall");
                }
            }
        });
        MyThread.backgroundmusic.start();
        new Thread(() -> {
            while (true) {
                jLabel.setText(" ");
                if (GameController.Time == 3 && BackgroundMusic.TimeMusic) {
                    BackgroundMusic.playMusic("sound/倒计时.wav","Time");
                }
            }
        }).start();
    }
}

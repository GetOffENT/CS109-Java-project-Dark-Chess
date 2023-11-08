package Listen;

//import com.sun.tools.javac.Main;
import controller.GameController;

import javax.sound.sampled.*;
import java.io.File;

public class BackgroundMusic {
    public static boolean MainPageMusic = true;
    public static boolean OverallMusic = true;
    public static boolean MovingMusic = true;
    public static boolean TimeMusic = true;
    public static boolean SelectMusic = true;
    public static double value = 1;// value可以用来设置音量，从0-2.0
    public static String MusicPath = "sound/新棋子音乐2.WAV";
    public static String MainPageMusicPath = "sound/王者荣耀 - 入世.wav";
    public static boolean StopMusic = false;


    public static void playMusic(String pathname, String MusicType) {// 背景音乐播放
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(pathname));
            AudioFormat aif = ais.getFormat();
            final SourceDataLine sdl;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, aif);
            sdl = (SourceDataLine) AudioSystem.getLine(info);
            sdl.open(aif);
            sdl.start();
            FloatControl fc = (FloatControl) sdl.getControl(FloatControl.Type.MASTER_GAIN);

            int nByte = 0;
            int writeByte = 0;
            final int SIZE = 1024 * 64;
            byte[] buffer = new byte[SIZE];

            while (nByte != -1) {// 判断 播放/暂停 状态

                //播放前设置音量
                float dB = (float) (Math.log(value == 0.0 ? 0.0001 : value) / Math.log(10.0) * 20.0);
                fc.setValue(dB);

                boolean flag = true;
//                switch (MusicType) {
//                    case "Move" -> flag = MovingMusic;
//                    case "Overall" -> flag = OverallMusic;
//                    case "Select" -> flag = SelectMusic;
//                    case "Time" -> flag = TimeMusic;
//                }
                if (GameController.page == 1) {
                    flag = MainPageMusic;
                } else if (GameController.page == 2) {
                    if (MusicType.equals("Move")) flag = MovingMusic;
                    else if (MusicType.equals("Overall")) flag = OverallMusic;
                    else if (MusicType.equals("Select")) flag = SelectMusic;
                    else if (MusicType.equals("Time")) flag = TimeMusic;
                }
                if (flag) {

                    nByte = ais.read(buffer, 0, SIZE);

                    if (nByte != -1) sdl.write(buffer, 0, nByte);

                } else {

                    nByte = ais.read(buffer, 0, 0);

                }
                if (MusicType.equals("Time") && (GameController.Time == 29 || GameController.Time == 30)) {
                    return;
                }
                if (StopMusic) {
                    StopMusic = false;
                    return;
                }

            }
            sdl.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

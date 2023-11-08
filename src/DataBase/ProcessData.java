package DataBase;

import Listen.BackgroundMusic;
import chessComponent.SquareComponent;
import controller.GameController;
import view.Chessboard;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProcessData {
    //第一个字符串 Eat Move Reversal 分别表示吃子，移动，翻开
    public static ArrayList<String> MoveProcess = new ArrayList<>();

    public static ArrayList<SquareComponent> ateChesses = new ArrayList<>();
    public static int[] OriginChessBoard = new int[32];

    public static void SaveInfo_EatOrMove(String NowProcess) {
        ProcessData.MoveProcess.add(NowProcess);
    }

    public static void SaveInfo_Reverse(String NowProcess) {
        ProcessData.MoveProcess.add(NowProcess);
    }

    public static void SaveInfo_OriginChessBoard(int[] chessboard) {
        System.arraycopy(chessboard, 0, ProcessData.OriginChessBoard, 0, 32);
    }

    public static void SaveInfoToFile(String path) throws IOException {
        PrintStream out = new PrintStream(path);
        for (int i = 0; i < 32; i++)
            out.printf("%d ", ProcessData.OriginChessBoard[i]);
        out.println();
        for (String str : ProcessData.MoveProcess)
            out.println(str);
    }


//    public static void LoadGUI() {
//        try {
//            //第一行主页背景音乐 第二行游戏页面背景音乐 第三行主页背景 第四行游戏界面背景
//            List<String> GUI_data = Files.readAllLines(Paths.get("res/GUI.txt"));
////            JOptionPane.showConfirmDialog(null, "结束游戏并返回主页面", "提醒", JOptionPane.OK_CANCEL_OPTION);
//            BackgroundMusic.MainPageMusicPath = GUI_data.get(0);
//            BackgroundMusic.MusicPath = GUI_data.get(1);
//            GameController.MainPagePicturePath = GUI_data.get(2);
//            GameController.GamePicturePath = GUI_data.get(3);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void SaveGUI_infoToFile() throws FileNotFoundException {
        PrintStream out = new PrintStream("res/GUI.txt");
        out.println(BackgroundMusic.MainPageMusicPath);
        out.println(BackgroundMusic.MusicPath);
        out.println(GameController.MainPagePicturePath);
        out.println(GameController.GamePicturePath);
    }

}

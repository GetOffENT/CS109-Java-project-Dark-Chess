package controller;

import chessComponent.ChessComponent;
import chessComponent.SquareComponent;
import view.Chessboard;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类主要完成由窗体上组件触发的动作。
 * 例如点击button等
 * ChessGameFrame中组件调用本类的对象，在本类中的方法里完成逻辑运算，将运算的结果传递至chessboard中绘制
 */
public class GameController {
    private Chessboard chessboard;
    public static boolean CheatingModeButtonStatement = false;
    public static int RedScores = 0;
    public static int BlackScores = 0;
    public static int[] RedAteChesses = new int[7];
    public static int[] BlackAteChesses = new int[7];
    public static String[] RedChessName = new String[]{"帥", "仕", "相", "俥", "馬", "炮", "兵"};
    public static String[] BlackChessName = new String[]{"將", "士", "象", "車", "馬", "砲", "卒"};
    public static int type = 0; //0是双人 1是人机(随机算法)
    public static boolean TwoPlayerStatement;//true表示双人模式

    public static int LoadingSpeed = 500; //加载棋局的速度

    public static boolean LoadingStatement; //为了加载棋局时禁止鼠标点击

    public static boolean SuspendStatement; //true表示暂停加载

    public static boolean AIstatement; // AI下棋状态 AIstatement = true 代表AI正在下棋;  通过这个变量暂停鼠标监听
    public static boolean isGameStart; //是否已经开始游戏
    public static boolean GameIsOver;
    public static boolean SuspendKeepTime;
    public static boolean Retract;
    public static int Time = 30;
    public static int page = 1;  //1表示主页面  2表示副页面

    public static String MainPagePicturePath = "imgs/钟1.jpg";
    public static String GamePicturePath = "imgs/试试1.jpg";


    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void loadGameFromFile(String path) {
        //检查格式是否正确
        if (!path.substring(path.indexOf(".") + 1).equals("txt")) {
            JOptionPane.showConfirmDialog(null, "加载游戏失败,文件格式错误\n请检查文件格式后重试\n错误编码： 101", "警告", JOptionPane.DEFAULT_OPTION);
        } else {
            try {
//                String dir = System.getProperty("user.dir");
//                List<String> chessData = Files.readAllLines(Paths.get(dir, path));
                List<String> chessData = Files.readAllLines(Paths.get(path));
                chessboard.loadGame(chessData);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void swap(){
        chessboard.SwapPlayerforTime();
    }


    public void RestartGame() {
        this.chessboard.Restart();
    }

    public void OpenAllChessOnBoard() {
        this.chessboard.OpenAllChess();
    }

    public void RetractChesses() {
        this.chessboard.RetractChess();
    }

}

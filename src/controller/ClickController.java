package controller;


import Listen.BackgroundMusic;
import chessComponent.SquareComponent;
import chessComponent.EmptySlotComponent;
import model.ChessColor;
import view.ChessGameFrame;
import view.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Objects;

public class ClickController extends JFrame {
    private final Chessboard chessboard;
    private SquareComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(SquareComponent squareComponent) {
        //判断第一次点击
        if (first == null) {
            if (handleFirst(squareComponent)) {
                squareComponent.setSelected(true);
                first = squareComponent;
                first.repaint();
                if (BackgroundMusic.SelectMusic) {
                    new Thread(() -> {
                        BackgroundMusic.playMusic("sound/选择声.WAV", "Select");
                    }).start();
                }
                first.paintImmediately(0, 0, first.getWidth(), first.getHeight());

                //找到可行走的棋子并绘制提醒
                chessboard.paintChessesCanMoveto(squareComponent);
            }
        } else {
            if (first == squareComponent) { // 再次点击取消选取
                //找到可行走的棋子并取消绘制提醒
                chessboard.paintChessesCanMoveto(squareComponent);

                squareComponent.setSelected(false);
                SquareComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
//                recordFirst.paintImmediately(0, 0, recordFirst.getWidth(), recordFirst.getHeight());
            } else if (handleSecond(squareComponent)) {

                //找到可行走的棋子并取消绘制提醒
                chessboard.paintChessesCanMoveto(first);

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, squareComponent);
                chessboard.clickController.swapPlayer();

                first.setSelected(false);
                first = null;

                //若开了人机模式且游戏没有结束 则人机下棋
                if (GameController.type != 0 && GameController.RedScores < 60 && GameController.BlackScores < 60) {
                    Thread AI = new Thread(() -> {
                        if (GameController.type == 1) {
                            chessboard.AI_Easy();
                        } else if (GameController.type == 2) {
                            chessboard.AI_Medium();
                        } else if (GameController.type == 3) {
                            chessboard.AI_Difficult();
                        }
                        chessboard.clickController.swapPlayer();
                    });
                    AI.start();
                }
            }
        }
    }


    /**
     * @param squareComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(SquareComponent squareComponent) {
        if (!squareComponent.isReversal() && !(squareComponent instanceof EmptySlotComponent)) {
            squareComponent.setReversal(true);
            chessboard.ProcessingSaveInfo_Reversal(squareComponent);//保存翻开信息
            System.out.printf("onClick to reverse a chess [%d,%d]\n", squareComponent.getChessboardPoint().getX(), squareComponent.getChessboardPoint().getY());
            squareComponent.repaint();
//            squareComponent.paintImmediately(0, 0, squareComponent.getWidth(), squareComponent.getHeight());
            chessboard.clickController.swapPlayer();

            //游戏没有结束 人机下棋
            if (GameController.type != 0 && GameController.RedScores < 60 && GameController.BlackScores < 60) {
                Thread AI = new Thread(() -> {
                    if (GameController.type == 1) {
                        chessboard.AI_Easy();
                    } else if (GameController.type == 2) {
                        chessboard.AI_Medium();
                    } else if (GameController.type == 3) {
                        chessboard.AI_Difficult();
                    }
                    chessboard.clickController.swapPlayer();
                });
                AI.start();
            }

            return false;
        }

        return squareComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param squareComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(SquareComponent squareComponent) {
        if (first.getChessColor() == squareComponent.getChessColor() && squareComponent.isReversal()) {
            first.setSelected(false);
            //找到可行走的棋子并取消绘制提醒
            chessboard.paintChessesCanMoveto(first);
            first.repaint();
            first = squareComponent;
            first.setSelected(true);
            first.repaint();
            if (BackgroundMusic.SelectMusic) {
                new Thread(() -> {
                    BackgroundMusic.playMusic("sound/选择声.WAV", "Select");
                }).start();
            }

            //找到可行走的棋子并绘制提醒
            chessboard.paintChessesCanMoveto(squareComponent);
            return false;
        } else {
            if (first.getRank() == 5) {
                if (squareComponent instanceof EmptySlotComponent) {
                    return false;
                } else if (squareComponent.isReversal()) {
                    return squareComponent.getChessColor() != chessboard.getCurrentColor() &&
                            first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint(), "NeedAddPoints");
                } else
                    return first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint(), "NeedAddPoints");
            }

            //没翻开或空棋子，进入if
            if (!squareComponent.isReversal()) {
                //没翻开且非空棋子不能走
                if (!(squareComponent instanceof EmptySlotComponent)) {
                    return false;
                }
            }
            return squareComponent.getChessColor() != chessboard.getCurrentColor() &&
                    first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint(), "NeedAddPoints");
        }
    }

    public void swapPlayer() {
        chessboard.setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        ChessGameFrame.getStatusLabel().setText(String.format("%s's TURN", chessboard.getCurrentColor().getName()));
        if (chessboard.getCurrentColor() == ChessColor.BLACK) {
            ChessGameFrame.getStatusLabel().setForeground(new Color(127, 126, 126, 255));
        }
        if (chessboard.getCurrentColor() == ChessColor.RED) {
            ChessGameFrame.getStatusLabel().setForeground(new Color(244, 88, 88));
        }
        if (GameController.BlackScores >= 60) {
            GameIsOver("Black");
        } else if (GameController.RedScores >= 60) GameIsOver("Red");

        GameController.Time = 30;
        ChessGameFrame.timekeeper.setText("" + GameController.Time);
    }

    public void GameIsOver(String color) {
        String Color = Objects.equals(color, "Red") ? "红方" : "黑方";
        int result = JOptionPane.showConfirmDialog(null, "游戏结束," + Color + "获胜\n是否重开", "提醒", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) this.chessboard.Restart();
        GameController.GameIsOver = true;
    }

    //控制鼠标位置
    public static void ChangeMouseLocation(int type, int x, int y) {
        Point p = MouseInfo.getPointerInfo().getLocation();
        int width = (int) p.getX() + x;
        int heigh = (int) p.getY() + y;
        if (type == 0) {
            width = x;
            heigh = y;
        }
        Robot robot;
        try {
            robot = new Robot();
            robot.mouseMove(width, heigh);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    //模拟鼠标左右键
    public static void onClick_Mouse(String lr) {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        if (lr.equals("right")) {
            assert robot != null;
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
        } else {
            assert robot != null;
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
    }
}

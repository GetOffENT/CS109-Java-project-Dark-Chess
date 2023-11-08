package chessComponent;

import DataBase.ProcessData;
import controller.ClickController;
import controller.GameController;
import model.ChessColor;
import model.ChessboardPoint;
import view.ChessGameFrame;
import view.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * 这个类是一个抽象类，主要表示8*4棋盘上每个格子的棋子情况。
 * 有两个子类：
 * 1. EmptySlotComponent: 空棋子
 * 2. ChessComponent: 表示非空棋子
 */
public abstract class SquareComponent extends JComponent {

    public static final Color squareColor = new Color(250, 220, 190);
    protected static int spacingLength;
    protected static final Font CHESS_FONT = new Font("宋体", Font.BOLD, 36);

    /**
     * chessboardPoint: 表示8*4棋盘中，当前棋子在棋格对应的位置，如(0, 0), (1, 0)等等
     * chessColor: 表示这个棋子的颜色，有红色，黑色，无色三种
     * isReversal: 表示是否翻转
     * selected: 表示这个棋子是否被选中
     */
    private ChessboardPoint chessboardPoint;
    protected final ChessColor chessColor;
    protected boolean isReversal;
    protected boolean isCanMoveto;
    private boolean selected;
    private int rank = -1;
    private int score;
    private boolean isReversedByCheating = false;
    private boolean isReversedByOpenAll = false;

    /**
     * handle click event
     */
    public final ClickController clickController;

    protected SquareComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPoint = chessboardPoint;
        this.chessColor = chessColor;
        this.selected = false;
        this.clickController = clickController;
        this.isReversal = false;
    }

    public boolean isReversal() {
        return isReversal;
    }

    public void setReversal(boolean reversal) {
        isReversal = reversal;
    }

    public boolean isCanMoveto() {
        return isCanMoveto;
    }

    public void setCanMoveto(boolean canMoveto) {
        isCanMoveto = canMoveto;
    }

    public static void setSpacingLength(int spacingLength) {
        SquareComponent.spacingLength = spacingLength;
    }

    public ChessboardPoint getChessboardPoint() {
        return chessboardPoint;
    }

    public void setChessboardPoint(ChessboardPoint chessboardPoint) {
        this.chessboardPoint = chessboardPoint;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isReversedByOpenAll() {
        return isReversedByOpenAll;
    }

    public void setReversedByOpenAll(boolean reversedByOpenAll) {
        isReversedByOpenAll = reversedByOpenAll;
    }

    /**
     * @param another 主要用于和另外一个棋子交换位置
     *                <br>
     *                调用时机是在移动棋子的时候，将操控的棋子和对应的空位置棋子(EmptySlotComponent)做交换
     */
    public void swapLocation(SquareComponent another) {
        ChessboardPoint chessboardPoint1 = getChessboardPoint(), chessboardPoint2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();
        setChessboardPoint(chessboardPoint2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPoint1);
        another.setLocation(point1);
    }

    /**
     * @param e 响应鼠标监听事件
     *          <br>
     *          当接收到鼠标动作的时候，这个方法就会自动被调用，调用监听者的onClick方法，处理棋子的选中，移动等等行为。
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if(GameController.isGameStart){
            if (e.getButton() == 1 && e.getID() == MouseEvent.MOUSE_RELEASED) {
                if (!GameController.AIstatement && !GameController.LoadingStatement) {  //AI正在下棋时、加载棋局时点击无效
                    if (GameController.RedScores < 60 && GameController.BlackScores < 60) { //游戏已经结束后点击无效
                        System.out.printf("Click [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
                        clickController.onClick(this);
                    }
                }
            } else if (e.getButton() == 3) {
                if (GameController.CheatingModeButtonStatement) {
                    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                        if (!this.isReversal) {
                            this.isReversal = true;
                            this.repaint();
                            this.isReversedByCheating = true;
                        }
                    }
                    if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                        if (this.isReversedByCheating) {
                            this.isReversedByCheating = false;
                            this.isReversal = false;
                            this.repaint();
                        }
                    }
                }
            }
        }
    }



    /**
     * @param chessboard  棋盘
     * @param destination 目标位置，如(0, 0), (0, 1)等等
     * @return this棋子对象的移动规则和当前位置(chessboardPoint)能否到达目标位置
     * <br>
     * 这个方法主要是检查移动的合法性，如果合法就返回true，反之是false。
     */
    //todo: Override this method for Cannon
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination, String type) {
        SquareComponent destinationChess = chessboard[destination.getX()][destination.getY()];
        SquareComponent NowChess = chessboard[chessboardPoint.getX()][chessboardPoint.getY()];
        boolean isAdjacent = chessboardPoint.getX() == destination.getX() && Math.abs(chessboardPoint.getY() - destination.getY()) == 1
                || chessboardPoint.getY() == destination.getY() && Math.abs(chessboardPoint.getX() - destination.getX()) == 1;
        boolean isRankLegal = destinationChess.getRank() >= NowChess.getRank();
        if ((isRankLegal && isAdjacent && destinationChess.isReversal) || (destinationChess instanceof EmptySlotComponent && isAdjacent)) {
            if(type.equals("NeedAddPoints")){
                KeepScore(destinationChess);
                ProcessingSaveInfo_EatOrMove(NowChess, destinationChess);
                ProcessData.ateChesses.add(destinationChess);
            }
        }
        return (isRankLegal && isAdjacent && destinationChess.isReversal) || (destinationChess instanceof EmptySlotComponent && isAdjacent);
    }


    public void KeepScore(SquareComponent destinationChess) {
        if (destinationChess.getRank() != -1) {
            if (destinationChess.getChessColor().getColor() == Color.RED) {
                GameController.BlackScores += destinationChess.getScore();
                ChessGameFrame.Score.setText(GameController.BlackScores + "        " + GameController.RedScores);
                GameController.RedAteChesses[destinationChess.getRank()]++;
            } else if(destinationChess.getChessColor().getColor() == Color.BLACK){
                GameController.RedScores += destinationChess.getScore();
                ChessGameFrame.Score.setText(GameController.BlackScores + "        " + GameController.RedScores);
                GameController.BlackAteChesses[destinationChess.getRank()]++;
            }
        }

        for (int i = 0; i < 7; i++) {
            if (GameController.RedAteChesses[i] == 1) {
                String picPath = ChessGameFrame.RedPicture.get(i);
                ImageIcon icon = new ImageIcon(picPath);
                icon.setImage(icon.getImage().getScaledInstance(ChessGameFrame.RedAteChess.get(i).getWidth(), ChessGameFrame.RedAteChess.get(i).getHeight(), Image.SCALE_DEFAULT));
                ChessGameFrame.RedAteChess.get(i).setIcon(icon);
//                ChessGameFrame.RedAteChess.get(i).setText(GameController.RedChessName[i]);
            } else if (GameController.RedAteChesses[i] >= 2) {
                String picPath = ChessGameFrame.RedPicture.get(i);
                ImageIcon icon = new ImageIcon(picPath);
                icon.setImage(icon.getImage().getScaledInstance(ChessGameFrame.RedAteChess.get(i).getWidth(), ChessGameFrame.RedAteChess.get(i).getHeight(), Image.SCALE_DEFAULT));
                ChessGameFrame.RedAteChess.get(i).setIcon(icon);
                ChessGameFrame.redAmount.get(i).setText("*" + GameController.RedAteChesses[i]);
            }
            if (GameController.BlackAteChesses[i] == 1) {
                String picPath = ChessGameFrame.BlackPicture.get(i);
                ImageIcon icon = new ImageIcon(picPath);
                icon.setImage(icon.getImage().getScaledInstance(ChessGameFrame.BlackAteChess.get(i).getWidth(), ChessGameFrame.BlackAteChess.get(i).getHeight(),Image.SCALE_DEFAULT ));
                ChessGameFrame.BlackAteChess.get(i).setIcon(icon);
//                ChessGameFrame.blackAmount.get(i).setText(GameController.BlackChessName[i]);
            } else if (GameController.BlackAteChesses[i] >= 2) {
                String picPath = ChessGameFrame.BlackPicture.get(i);
                ImageIcon icon = new ImageIcon(picPath);
                icon.setImage(icon.getImage().getScaledInstance(ChessGameFrame.BlackAteChess.get(i).getWidth(), ChessGameFrame.BlackAteChess.get(i).getHeight(),Image.SCALE_DEFAULT ));
                ChessGameFrame.BlackAteChess.get(i).setIcon(icon);
                ChessGameFrame.blackAmount.get(i).setText( "*" + GameController.BlackAteChesses[i]);
            }
        }
    }

    /**
     * @param NowChess
     * @param destinationChess 吃子 [类型][Current Color]
     *                         选中棋子[x][y]
     *                         目标棋子[x][y]
     *                         [当前红方得分][当前黑方得分]
     *                         移动 [类型][Current Color]
     *                         选中棋子[x][y]
     *                         目标空棋[x][y]
     *                         [当前红方得分][当前黑方得分]
     */
    public void ProcessingSaveInfo_EatOrMove(SquareComponent NowChess, SquareComponent destinationChess) {
        String NowProcess;
        if (!(destinationChess instanceof EmptySlotComponent)) {
            NowProcess = "Eat " + Chessboard.currentColor + " "
                    + chessboardPoint.getX() + " " + chessboardPoint.getY() + " " /*+ NowChess.isReversal + " "*/
                    + destinationChess.getChessboardPoint().getX() + " " + destinationChess.getChessboardPoint().getY() + " " /*+ destinationChess.isReversal + " "*/
                    + GameController.RedScores + " " + GameController.BlackScores;
        } else {
            NowProcess = "Move " + Chessboard.currentColor + " "
                    + chessboardPoint.getX() + " " + chessboardPoint.getY() + " " /*+ NowChess.isReversal + " "*/
                    + destinationChess.getChessboardPoint().getX() + " " + destinationChess.getChessboardPoint().getY() + " " /*+ destinationChess.isReversal + " "*/
                    + GameController.RedScores + " " + GameController.BlackScores;
        }
        ProcessData.SaveInfo_EatOrMove(NowProcess);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        System.out.printf("repaint chess [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
        g.setColor(squareColor);
        g.fillRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
    }
}

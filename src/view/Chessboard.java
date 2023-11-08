package view;


import DataBase.ProcessData;
import Listen.BackgroundMusic;
import chessComponent.*;
import controller.GameController;
import model.*;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 这个类表示棋盘组建，其包含：
 * SquareComponent[][]: 4*8个方块格子组件
 */
public class Chessboard extends JComponent {


    private static final int ROW_SIZE = 8;
    private static final int COL_SIZE = 4;
    public static List<String> chessData = new ArrayList<>();

    private final SquareComponent[][] squareComponents = new SquareComponent[ROW_SIZE][COL_SIZE];
    //todo: you can change the initial player
    public static ChessColor currentColor = ChessColor.BLACK;

    //all chessComponents in this chessboard are shared only one model controller
    public final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;
    public ChessGameFrame GameFrame = null;

//    public void paint(Graphics g){
//        super.paint(g);
//        g.clearRect(this.getX(),this.getY(),this.getWidth(),this.getHeight());
//    }

    public Chessboard(int width, int height, ChessGameFrame chessGameFrame) {
        setLayout(null); // Use absolute layout.
        setSize(width + 2, height);
        CHESS_SIZE = (height - 6) / 8;
        SquareComponent.setSpacingLength(CHESS_SIZE / 12);
        System.out.printf("chessboard [%d * %d], chess size = %d\n", width, height, CHESS_SIZE);

        this.GameFrame = chessGameFrame;

        initAllChessOnBoard();
    }

    public SquareComponent[][] getChessComponents() {
        return squareComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(ChessColor currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * 将SquareComponent 放置在 ChessBoard上。里面包含移除原有的component及放置新的component
     */
    public void putChessOnBoard(SquareComponent squareComponent) {
        int row = squareComponent.getChessboardPoint().getX(), col = squareComponent.getChessboardPoint().getY();
        if (squareComponents[row][col] != null) {
            remove(squareComponents[row][col]);
        }
        add(squareComponents[row][col] = squareComponent);
    }

    public void SwapPlayerforTime() {
        clickController.swapPlayer();
    }


    /**
     * 交换chess1 chess2的位置
     *
     * @param chess1
     * @param chess2
     */
    public void swapChessComponents(SquareComponent chess1, SquareComponent chess2) {
        if (!(chess2 instanceof EmptySlotComponent)) {
            if (chess2.getChessColor() == ChessColor.RED)
                ChessGameFrame.RedAteChess.get(chess2.getRank()).setVisible(true);
            else if (chess2.getChessColor() == ChessColor.BLACK)
                ChessGameFrame.BlackAteChess.get(chess2.getRank()).setVisible(true);
        }

        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));
        }

        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        squareComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        squareComponents[row2][col2] = chess2;


        if (!GameController.Retract && GameController.LoadingSpeed != 0 && BackgroundMusic.MovingMusic) {
            new Thread(() -> {
                BackgroundMusic.playMusic("sound/hi_0_落子声.WAV", "Move");
            }).start();
        }
        //只重新绘制chess1 chess2，其他不变
        chess1.repaint();
//        chess1.paintImmediately(0, 0, chess1.getWidth(), chess1.getHeight());
        chess2.repaint();
//        chess2.paintImmediately(0, 0, chess2.getWidth(), chess2.getHeight());

    }

    public void paintChessesCanMoveto(SquareComponent squareComponent) {
        if (squareComponent.getRank() != 5) {
            for (int i = 0; i < ROW_SIZE; i++)
                for (int j = 0; j < COL_SIZE; j++) {
                    if (squareComponent.getChessColor() != squareComponents[i][j].getChessColor() && squareComponent.canMoveTo(squareComponents, squareComponents[i][j].getChessboardPoint(), "Judge")) {
                        squareComponents[i][j].setCanMoveto(!squareComponents[i][j].isCanMoveto());
                        squareComponents[i][j].repaint();
                    }
                }
        } else {
            for (int i = 0; i < ROW_SIZE; i++)
                for (int j = 0; j < COL_SIZE; j++) {
                    if (squareComponents[i][j].isReversal() && squareComponent.getChessColor() != squareComponents[i][j].getChessColor() && squareComponent.canMoveTo(squareComponents, squareComponents[i][j].getChessboardPoint(), "Judge")) {
                        squareComponents[i][j].setCanMoveto(!squareComponents[i][j].isCanMoveto());
                        squareComponents[i][j].repaint();
                    } else if (!squareComponents[i][j].isReversal() && squareComponent.canMoveTo(squareComponents, squareComponents[i][j].getChessboardPoint(), "Judge")) {
                        squareComponents[i][j].setCanMoveto(!squareComponents[i][j].isCanMoveto());
                        squareComponents[i][j].repaint();
                    }
                }
        }
    }


    //FIXME:   Initialize chessboard for testing only.
    private void initAllChessOnBoard() {
        Random random = new Random();
        //10 红帅 11红仕 12红象 13红车 14红馬 15炮 16兵     2开头表示黑棋子
        int[] generate = new int[]{10, 26, 16, 21, 21, 26, 16, 25, 14, 15, 23, 26, 26, 15, 13, 23, 24, 24, 25, 16, 13, 14, 11, 16, 22, 22, 26, 11, 12, 12, 16, 20};

        int n = 100000;
        while (n-- > 0) {
            int a = random.nextInt(32), b = random.nextInt(32);
            int temp = generate[a];
            generate[a] = generate[b];
            generate[b] = temp;
        }
        SaveChessBoardInfo(generate);
        SetChesses(generate);
    }

    //对棋盘上的每个位置新建棋子
    public void SetChesses(int[] chesses) {
        for (int i = 0; i < 32; i++) {
            SquareComponent squareComponent;
            if (chesses[i] == 10) {
                ChessColor color = ChessColor.RED;
                squareComponent = new GeneralChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 11) {
                ChessColor color = ChessColor.RED;
                squareComponent = new AdvisorChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 12) {
                ChessColor color = ChessColor.RED;
                squareComponent = new MinisterChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 13) {
                ChessColor color = ChessColor.RED;
                squareComponent = new ChariotChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 14) {
                ChessColor color = ChessColor.RED;
                squareComponent = new HorseChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 15) {
                ChessColor color = ChessColor.RED;
                squareComponent = new CannonChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 16) {
                ChessColor color = ChessColor.RED;
                squareComponent = new SoldierChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 20) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new GeneralChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 21) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new AdvisorChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 22) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new MinisterChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 23) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new ChariotChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 24) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new HorseChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else if (chesses[i] == 25) {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new CannonChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            } else {
                ChessColor color = ChessColor.BLACK;
                squareComponent = new SoldierChessComponent(new ChessboardPoint(i % 8, i / 8), calculatePoint(i % 8, i / 8), color, clickController, CHESS_SIZE);
            }
            squareComponent.setVisible(true);
            putChessOnBoard(squareComponent);
        }
    }

    public void Restart() {
        this.initAllChessOnBoard();
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                squareComponents[i][j].setReversal(false);
                squareComponents[i][j].repaint();
            }
        }
        for (int i = 0; i < 7; i++) {
            ChessGameFrame.RedAteChess.get(i).setText("");
            ChessGameFrame.BlackAteChess.get(i).setText("");
            ChessGameFrame.redAmount.get(i).setText("");
            ChessGameFrame.blackAmount.get(i).setText("");
            ChessGameFrame.redAmount.get(i).setText("");
            ChessGameFrame.blackAmount.get(i).setText("");
            ChessGameFrame.Score.setText(GameController.BlackScores + "        " + GameController.RedScores);
            GameController.BlackAteChesses[i] = 0;
            GameController.RedAteChesses[i] = 0;
            GameController.RedScores = 0;
            GameController.BlackScores = 0;
        }
        for (JLabel label : ChessGameFrame.BlackAteChess)
            label.setVisible(false);
        for (JLabel label : ChessGameFrame.RedAteChess)
            label.setVisible(false);
        currentColor = ChessColor.BLACK;
        ProcessData.MoveProcess.clear();
        GameController.Time = 30;
        GameController.GameIsOver = false;
        ChessGameFrame.getStatusLabel().setText(String.format("%s's TURN", "BLACK"));
    }

    //翻开所有棋子
    public void OpenAllChess() {
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                if (!squareComponents[i][j].isReversal() && !(squareComponents[i][j] instanceof EmptySlotComponent)) {
                    squareComponents[i][j].setReversedByOpenAll(true);
                    squareComponents[i][j].setReversal(true);
                    squareComponents[i][j].repaint();
                }
            }
        }
        ProcessData.MoveProcess.add("OpenAll");
    }

    public void RetractChess() {
        if (ProcessData.MoveProcess.size() == 0) {
            JOptionPane.showConfirmDialog(null, "无法继续悔棋！", "提醒", JOptionPane.DEFAULT_OPTION);
        } else {
            String[] CurrentProcess = (ProcessData.MoveProcess.get(ProcessData.MoveProcess.size() - 1)).split(" ");

            if (CurrentProcess[0].equals("Move") || CurrentProcess[0].equals("Eat")) {
                int x1 = Integer.parseInt(CurrentProcess[2]), y1 = Integer.parseInt(CurrentProcess[3]);
                int x2 = Integer.parseInt(CurrentProcess[4]), y2 = Integer.parseInt(CurrentProcess[5]);
                swapChessComponents(squareComponents[x2][y2], squareComponents[x1][y1]);
                remove(squareComponents[x2][y2]);
                squareComponents[x2][y2] = ProcessData.ateChesses.get(ProcessData.ateChesses.size() - 1);
                add(squareComponents[x2][y2]);
                squareComponents[x1][y1].repaint();
                ProcessData.MoveProcess.remove(ProcessData.MoveProcess.size() - 1);
                ProcessData.ateChesses.remove(ProcessData.ateChesses.size() - 1);

                //还原计分板
                if (squareComponents[x2][y2].getChessColor().getColor() == Color.RED) {
                    GameController.BlackScores -= squareComponents[x2][y2].getScore();
                    ChessGameFrame.Score.setText(GameController.BlackScores + "        " + GameController.RedScores);
                    GameController.RedAteChesses[squareComponents[x2][y2].getRank()]--;
                } else if (squareComponents[x2][y2].getChessColor().getColor() == Color.BLACK) {
                    GameController.RedScores -= squareComponents[x2][y2].getScore();
                    ChessGameFrame.Score.setText(GameController.BlackScores + "        " + GameController.RedScores);
                    GameController.BlackAteChesses[squareComponents[x2][y2].getRank()]--;
                }
                for (int i = 0; i < 7; i++) {
                    if (GameController.RedAteChesses[i] == 0) {
                        ChessGameFrame.redAmount.get(i).setText("");
//                            ChessGameFrame.RedAteChess.get(i).setText("");
                        ChessGameFrame.RedAteChess.get(i).setVisible(false);
                    } else if (GameController.RedAteChesses[i] == 1) {
                        ChessGameFrame.redAmount.get(i).setText("");
                    } else if (GameController.RedAteChesses[i] >= 2)
                        ChessGameFrame.redAmount.get(i).setText("*" + GameController.RedAteChesses[i]);
//                            ChessGameFrame.RedAteChess.get(i).setText(GameController.RedChessName[i] + "*" + GameController.RedAteChesses[i]);
                    if (GameController.BlackAteChesses[i] == 0) {
                        ChessGameFrame.blackAmount.get(i).setText("");
                        ChessGameFrame.BlackAteChess.get(i).setVisible(false);
//                            ChessGameFrame.BlackAteChess.get(i).setText("");
                    } else if (GameController.BlackAteChesses[i] == 1) {
                        ChessGameFrame.blackAmount.get(i).setText("");
                        ChessGameFrame.BlackAteChess.get(i).repaint();
                    } else if (GameController.BlackAteChesses[i] >= 2) {
                        ChessGameFrame.blackAmount.get(i).setText("*" + GameController.BlackAteChesses[i]);
                        ChessGameFrame.BlackAteChess.get(i).setText(GameController.BlackChessName[i] + "*" + GameController.BlackAteChesses[i]);
                    }
                }
            } else if (CurrentProcess[0].equals("Reversal")) {
                int x = Integer.parseInt(CurrentProcess[2]), y = Integer.parseInt(CurrentProcess[3]);
                squareComponents[x][y].setReversal(false);
                squareComponents[x][y].repaint();
                ProcessData.MoveProcess.remove(ProcessData.MoveProcess.size() - 1);
            } else if (CurrentProcess[0].equals("OpenAll")) {
                for (int i = 0; i < ROW_SIZE; i++)
                    for (int j = 0; j < COL_SIZE; j++)
                        if (squareComponents[i][j].isReversedByOpenAll()) {
                            squareComponents[i][j].setReversedByOpenAll(false);
                            squareComponents[i][j].setReversal(false);
                            squareComponents[i][j].repaint();
                        }
                ProcessData.MoveProcess.remove(ProcessData.MoveProcess.size() - 1);
            }

            //交换行棋权
            if (!CurrentProcess[0].equals("OpenAll"))
                clickController.swapPlayer();
        }
    }

    /**
     * 处理 翻开步骤 对应信息
     * [类型][Current Color]
     * [x][y][选中棋子isReversal]
     */
    public void ProcessingSaveInfo_Reversal(SquareComponent squareComponent) {
        String NowProcess = "Reversal " + Chessboard.currentColor + " "
                + squareComponent.getChessboardPoint().getX() + " " + squareComponent.getChessboardPoint().getY() + " " + squareComponent.isReversal();
        ProcessData.SaveInfo_Reverse(NowProcess);
    }

    public void SaveChessBoardInfo(int[] chessboard) {
        ProcessData.SaveInfo_OriginChessBoard(chessboard);
    }

    /**
     * 绘制棋盘格子
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * 将棋盘上行列坐标映射成Swing组件的Point
     *
     * @param row 棋盘上的行
     * @param col 棋盘上的列
     * @return
     */
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + 3, row * CHESS_SIZE + 3);
    }

    /**
     * 通过GameController调用该方法
     *
     * @param chessData
     */
    public void loadGame(List<String> chessData) throws InterruptedException {
        Restart();
        this.chessData = chessData;
        chessData.forEach(System.out::println);


        //检查棋盘是否是8*4
        if (chessData.get(0).split(" ").length != 32) {
            JOptionPane.showConfirmDialog(null, "加载游戏失败,棋盘错误\n请检查文档内容后重试\n错误编码： 102", "警告", JOptionPane.DEFAULT_OPTION);
            return;
        }

        //重绘棋局
        int[] generate = new int[32];
        int n = 0;
        for (String str : chessData.get(0).split(" "))
            generate[n++] = Integer.parseInt(str);

        //检查棋子是否合法
        n = 0;
        for (int i : generate) {
            ProcessData.OriginChessBoard[n++] = i;
            if (i < 10 || (i > 16 && i < 20) || i > 26) {
                JOptionPane.showConfirmDialog(null, "加载游戏失败,棋子不合法\n请检查棋子类型后重试\n错误编码： 103", "警告", JOptionPane.DEFAULT_OPTION);
                return;
            }
        }

        SetChesses(generate);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 4; j++)
                squareComponents[i][j].repaint();

        //模拟棋局
        /**
         * 吃子 [类型][Current Color]
         *           选中棋子[x][y]
         *           目标棋子[x][y]
         *           [当前红方得分][当前黑方得分]
         *       移动 [类型][Current Color]
         *           选中棋子[x][y]
         *           目标棋子[x][y]
         *           [当前红方得分][当前黑方得分]
         *       翻开
         *              [类型][Current Color]
         *              [x][y][选中棋子isReversal]
         */

        Thread test = new Thread(() -> {
            GameController.SuspendKeepTime = true;
            GameController.LoadingStatement = true; //表示已经开始模拟棋局
            ChessGameFrame.SpeedButton.setVisible(true);
            ChessGameFrame.SuspendButton.setVisible(true);
            ChessGameFrame.SkipButton.setVisible(true);
            chessData.remove(0);
            for (String info : Chessboard.chessData) {
                System.out.println(info);
                if (info.equals("OpenAll")) OpenAllChess();
                else {
                    String[] str = info.split(" ");

                    boolean istrue = false;
                    for(int i = 0;i<10;i++){
                        if ((""+i).equals(str[2])) {
                            istrue = true;
                            break;
                        }
                    }
                    if(!istrue){
                        JOptionPane.showConfirmDialog(null, "Error\n错误编码:106", "警告", JOptionPane.DEFAULT_OPTION);
                    }

                    //检查行棋方错误
                    if (!str[1].equals(Chessboard.currentColor + "")) {
                        JOptionPane.showConfirmDialog(null, String.format("加载游戏失败,下一行棋方不正确\n当前行棋方：%s  正确行棋方：%s\n请检查行棋方后重试\n错误编码： 104", str[1], currentColor), "警告", JOptionPane.DEFAULT_OPTION);
                        currentColor = ChessColor.BLACK;
                        Restart();
                        return;
                    }

                    if (str[0].equals("Reversal")) {
                        int x = Integer.parseInt(str[2]), y = Integer.parseInt(str[3]);
                        squareComponents[x][y].setReversal(true);
                        squareComponents[x][y].repaint();
                        ProcessingSaveInfo_Reversal(squareComponents[x][y]);
                    } else if (str[0].equals("Eat") || str[0].equals("Move")) {
                        int x1 = Integer.parseInt(str[2]), y1 = Integer.parseInt(str[3]);
                        int x2 = Integer.parseInt(str[4]), y2 = Integer.parseInt(str[5]);

                        //检查行棋步骤错误
                        if (!squareComponents[x1][y1].canMoveTo(squareComponents, squareComponents[x2][y2].getChessboardPoint(), "NeedAddPoints")) {
                            JOptionPane.showConfirmDialog(null, String.format("加载游戏失败,行棋步骤不合法\n%s方%s不可从[%d][%d]移动到[%d][%d]\n请检查行棋步骤后重试\n错误编码： 105", currentColor, squareComponents[x1][y1].getName(), x1, y1, x2, y2), "警告", JOptionPane.DEFAULT_OPTION);
                            currentColor = ChessColor.BLACK;
                            Restart();
                            return;
                        }

                        squareComponents[x1][y1].setSelected(true);
                        squareComponents[x1][y1].repaint();
                        try {
                            Thread.sleep(GameController.LoadingSpeed);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        squareComponents[x1][y1].setSelected(false);

//                        ProcessData.ateChesses.add(squareComponents[x2][y2]);
//                        squareComponents[x2][y2].KeepScore(squareComponents[x2][y2]);

                        swapChessComponents(squareComponents[x1][y1], squareComponents[x2][y2]);
                        squareComponents[x1][y1].repaint();
                        squareComponents[x2][y2].repaint();
                    }

                    clickController.swapPlayer();
                    try {
                        Thread.sleep(GameController.LoadingSpeed);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    if (GameController.SuspendStatement) {
                        try {
                            TimeUnit.HOURS.sleep(1);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                }

            }
            ChessGameFrame.SpeedButton.setVisible(false);
            ChessGameFrame.SuspendButton.setVisible(false);
            ChessGameFrame.SkipButton.setVisible(false);
            GameController.LoadingStatement = false; //表示模拟棋局结束
            GameController.LoadingSpeed = 500;//重置速度
            GameController.SuspendKeepTime = false;
        });
        test.start();

        ChessGameFrame.SuspendButton.addActionListener(e -> {
            if (!GameController.SuspendStatement) {
                GameController.SuspendStatement = true;
                ChessGameFrame.SuspendButton.setText("继续");
            } else {
                test.interrupt();
                GameController.SuspendStatement = false;
                ChessGameFrame.SuspendButton.setText("暂停");
            }
        });


    }
    //G:\计算机\Java\Project\DarkChess_Process\res\out


    //人机
    public void AI_Easy() {
        GameController.AIstatement = true;//表示AI开始下棋了

        //模拟AI思考的过程
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Random random = new Random();

        boolean[][][][] ChessToMove = new boolean[8][4][8][4];  //[x][y][m][n] = true 表示 [x][y]能移动到[m][n]
        boolean[][] ChessCanMove = new boolean[8][4];   //表示[x][y]能进行移动操作

        boolean[][][][] ChessToEat = new boolean[8][4][8][4];   //[x][y][m][n] = true 表示 [x][y]吃[m][n]
        boolean[][] ChessCanEat = new boolean[8][4];    //表示[x][y]能进行吃子操作


        boolean[][] ChessToReverse = new boolean[8][4]; //[x][y] = true 表示 [x][y] 还未翻开，能进行翻开操作


        int NumOfChessToMove = 0;//能移动棋子的数量
        int NumOfChessToReverse = 0;//能翻开（还未翻开）棋子的数量
        int NumOfChessToEat = 0;//能选择吃子的棋子的数量

        for (int x = 0; x < ROW_SIZE; x++)
            for (int y = 0; y < COL_SIZE; y++) {
                //判断能否移动至目标位置
                boolean CanMove = false;
                boolean CanEat = false;
                for (int m = 0; m < ROW_SIZE; m++)
                    for (int n = 0; n < COL_SIZE; n++) {
                        if (squareComponents[x][y].isReversal() && squareComponents[x][y].getChessColor() == currentColor) {
                            if (squareComponents[x][y].getRank() == 5) {
                                if (!(squareComponents[m][n] instanceof EmptySlotComponent)) {
                                    ChessToEat[x][y][m][n] = (squareComponents[m][n].isReversal() && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor() && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"))
                                            || ((!squareComponents[m][n].isReversal()) && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"));
                                    CanEat = ChessToEat[x][y][m][n] || CanEat;
                                }
                            } else if (squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()
                                    && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge")) {
                                if (squareComponents[m][n] instanceof EmptySlotComponent) {
                                    ChessToMove[x][y][m][n] = true;
                                    CanMove = true;
                                } else {
                                    ChessToEat[x][y][m][n] = true;
                                    CanEat = true;
                                }
                            }
                        }
                    }
                if (CanMove) {
                    NumOfChessToMove++;
                    ChessCanMove[x][y] = true;
                }
                if (CanEat) {
                    NumOfChessToEat++;
                    ChessCanEat[x][y] = true;
                }
                //判断能否翻开 若未翻开 则为true
                if (!squareComponents[x][y].isReversal() && !(squareComponents[x][y] instanceof EmptySlotComponent)) {
                    ChessToReverse[x][y] = true;
                    NumOfChessToReverse++;
                }
            }

        int type; //人机下棋的类型选择   type=1表示选择翻开棋子  type=2表示随机移动棋子 type=3表示选择吃子
        //随机数决定人机下棋类型
        if (NumOfChessToEat != 0) type = 3;
        else if (NumOfChessToMove != 0 && NumOfChessToReverse == 0) type = 2;
        else if (NumOfChessToMove == 0 && NumOfChessToReverse != 0) type = 1;
        else type = random.nextInt(2) + 1;

        if (type == 1) {
            int RandomNowChess = random.nextInt(NumOfChessToReverse) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessToReverse[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        squareComponents[x][y].setReversal(true);
                        squareComponents[x][y].repaint();
                        this.ProcessingSaveInfo_Reversal(squareComponents[x][y]);
                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        } else if (type == 2) {
            int RandomNowChess = random.nextInt(NumOfChessToMove) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessCanMove[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        ArrayList<Integer> destination_CanMoveTo = new ArrayList<>();
                        for (int m = 0; m < ROW_SIZE; m++)
                            for (int n = 0; n < COL_SIZE; n++) {
                                if (ChessToMove[x][y][m][n])
                                    destination_CanMoveTo.add(m * 4 + n);
                            }
                        int RandomDestinationChess = random.nextInt(destination_CanMoveTo.size());
                        int m = destination_CanMoveTo.get(RandomDestinationChess) / 4, n = destination_CanMoveTo.get(RandomDestinationChess) % 4;

                        squareComponents[x][y].setSelected(true);
                        squareComponents[x][y].repaint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        squareComponents[x][y].setSelected(false);
                        squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "NeedAddPoints");
                        swapChessComponents(squareComponents[x][y], squareComponents[m][n]);

                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        } else {
            int RandomNowChess = random.nextInt(NumOfChessToEat) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessCanEat[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        ArrayList<Integer> destination_CanEat = new ArrayList<>();
                        for (int m = 0; m < ROW_SIZE; m++)
                            for (int n = 0; n < COL_SIZE; n++) {
                                if (ChessToEat[x][y][m][n])
                                    destination_CanEat.add(m * 4 + n);
                            }

                        int RandomDestinationChess = random.nextInt(destination_CanEat.size());
                        int m = destination_CanEat.get(RandomDestinationChess) / 4, n = destination_CanEat.get(RandomDestinationChess) % 4;

                        squareComponents[x][y].setSelected(true);
                        squareComponents[x][y].repaint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        squareComponents[x][y].setSelected(false);
                        squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "NeedAddPoints");
                        swapChessComponents(squareComponents[x][y], squareComponents[m][n]);

                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        }

        GameController.AIstatement = false;//AI已经下完棋了
    }

    public void AI_Medium() {
        GameController.AIstatement = true;//表示AI开始下棋了

        //模拟AI思考的过程
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Random random = new Random();

        boolean[][][][] ChessToMove = new boolean[8][4][8][4];  //[x][y][m][n] = true 表示 [x][y]能移动到[m][n]
        boolean[][] ChessCanMove = new boolean[8][4];   //表示[x][y]能进行移动操作

        boolean[][][][] ChessToEat = new boolean[8][4][8][4];   //[x][y][m][n] = true 表示 [x][y]吃[m][n]
        boolean[][] ChessCanEat = new boolean[8][4];    //表示[x][y]能进行吃子操作


        boolean[][] ChessToReverse = new boolean[8][4]; //[x][y] = true 表示 [x][y] 还未翻开，能进行翻开操作


        int NumOfChessToMove = 0;//能移动棋子的数量
        int NumOfChessToReverse = 0;//能翻开（还未翻开）棋子的数量
        int NumOfChessToEat = 0;//能选择吃子的棋子的数量

        for (int x = 0; x < ROW_SIZE; x++)
            for (int y = 0; y < COL_SIZE; y++) {
                //判断能否移动至目标位置
                boolean CanMove = false;
                boolean CanEat = false;
                for (int m = 0; m < ROW_SIZE; m++)
                    for (int n = 0; n < COL_SIZE; n++) {
                        if (squareComponents[x][y].isReversal() && squareComponents[x][y].getChessColor() == currentColor) {
                            if (squareComponents[x][y].getRank() == 5) {
                                if (!(squareComponents[m][n] instanceof EmptySlotComponent) && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()) {
                                    ChessToEat[x][y][m][n] = (squareComponents[m][n].isReversal() && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor() && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"))
                                            || ((!squareComponents[m][n].isReversal()) && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"));
                                    CanEat = ChessToEat[x][y][m][n] || CanEat;
                                }
                            } else if (squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()
                                    && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge")) {
                                if (squareComponents[m][n] instanceof EmptySlotComponent) {
                                    ChessToMove[x][y][m][n] = true;
                                    CanMove = true;
                                } else {
                                    ChessToEat[x][y][m][n] = true;
                                    CanEat = true;
                                }
                            }
                        }
                    }
                if (CanMove) {
                    NumOfChessToMove++;
                    ChessCanMove[x][y] = true;
                }
                if (CanEat) {
                    NumOfChessToEat++;
                    ChessCanEat[x][y] = true;
                }
                //判断能否翻开 若未翻开 则为true
                if (squareComponents[x][y].getChessColor() == currentColor && (!squareComponents[x][y].isReversal()) && (!(squareComponents[x][y] instanceof EmptySlotComponent))) {
                    ChessToReverse[x][y] = true;
                    NumOfChessToReverse++;
                }
            }

        int type; //人机下棋的类型选择   type=1表示选择翻开棋子  type=2表示随机移动棋子 type=3表示选择吃子
        //随机数决定人机下棋类型
        if (NumOfChessToEat != 0) type = 3;
        else if (NumOfChessToMove != 0 && NumOfChessToReverse == 0) type = 2;
        else if (NumOfChessToMove == 0 && NumOfChessToReverse != 0) type = 1;
        else type = random.nextInt(2) + 1;

        if (type == 1) {
            int RandomNowChess = random.nextInt(NumOfChessToReverse) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessToReverse[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        squareComponents[x][y].setReversal(true);
                        squareComponents[x][y].repaint();
                        this.ProcessingSaveInfo_Reversal(squareComponents[x][y]);
                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        } else if (type == 2) {
            int RandomNowChess = random.nextInt(NumOfChessToMove) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessCanMove[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        ArrayList<Integer> destination_CanMoveTo = new ArrayList<>();
                        for (int m = 0; m < ROW_SIZE; m++)
                            for (int n = 0; n < COL_SIZE; n++) {
                                if (ChessToMove[x][y][m][n])
                                    destination_CanMoveTo.add(m * 4 + n);
                            }
                        int RandomDestinationChess = random.nextInt(destination_CanMoveTo.size());
                        int m = destination_CanMoveTo.get(RandomDestinationChess) / 4, n = destination_CanMoveTo.get(RandomDestinationChess) % 4;

                        squareComponents[x][y].setSelected(true);
                        squareComponents[x][y].repaint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        squareComponents[x][y].setSelected(false);
                        squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "NeedAddPoints");
                        swapChessComponents(squareComponents[x][y], squareComponents[m][n]);

                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        } else {
            int RandomNowChess = random.nextInt(NumOfChessToEat) + 1;
            for (int x = 0; x < ROW_SIZE; x++) {
                for (int y = 0; y < COL_SIZE; y++) {
                    if (ChessCanEat[x][y]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        ArrayList<Integer> destination_CanEat = new ArrayList<>();
                        for (int m = 0; m < ROW_SIZE; m++)
                            for (int n = 0; n < COL_SIZE; n++) {
                                if (ChessToEat[x][y][m][n])
                                    destination_CanEat.add(m * 4 + n);
                            }

                        int m, n; //最终选择吃的棋子的坐标
                        int theBestChoice = destination_CanEat.get(0);
                        for (int i = 1; i < destination_CanEat.size(); i++) {
                            if (squareComponents[destination_CanEat.get(i) / 4][destination_CanEat.get(i) % 4].getRank() < squareComponents[theBestChoice / 4][theBestChoice % 4].getRank())
                                theBestChoice = destination_CanEat.get(i);
                        }
                        m = theBestChoice / 4;
                        n = theBestChoice % 4;

                        squareComponents[x][y].setSelected(true);
                        squareComponents[x][y].repaint();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        squareComponents[x][y].setSelected(false);
                        squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "NeedAddPoints");
                        swapChessComponents(squareComponents[x][y], squareComponents[m][n]);

                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        }

        GameController.AIstatement = false;//AI已经下完棋了
    }

    public void AI_Difficult() {
        if (GameController.BlackScores >= 60 || GameController.RedScores >= 60) return;

        GameController.AIstatement = true;//表示AI开始下棋了

        //模拟AI思考的过程
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Random random = new Random();

        boolean[][][][] ChessCanGeneralMove = new boolean[8][4][8][4];//包括吃子和移动

        boolean[][][][] ChessToMove = new boolean[8][4][8][4];  //[x][y][m][n] = true 表示 [x][y]能移动到[m][n]
        boolean[][] ChessCanMove = new boolean[8][4];   //表示[x][y]能进行移动操作

        boolean[][][][] ChessToEat = new boolean[8][4][8][4];   //[x][y][m][n] = true 表示 [x][y]吃[m][n]
        boolean[][] ChessCanEat = new boolean[8][4];    //表示[x][y]能进行吃子操作

        boolean[][] ChessToReverse = new boolean[8][4]; //[x][y] = true 表示 [x][y] 还未翻开，能进行翻开操作

        boolean[][][][] ChessToBeAte = new boolean[8][4][8][4]; //[x][y][m][n] = true 表示[x][y]可以被[m][n]
        boolean[][] ChessBeAte = new boolean[8][4]; //表示[x][y] 可以被吃掉


        int NumOfChessToMove = 0;//能移动棋子的数量
        int NumOfChessToReverse = 0;//能翻开（还未翻开）棋子的数量
        int NumOfChessToEat = 0;//能选择吃子的棋子的数量
        int NumOfChessToBeAte = 0;//能被吃的棋子的数量

        for (int x = 0; x < ROW_SIZE; x++)
            for (int y = 0; y < COL_SIZE; y++) {
                boolean CanMove = false;    //判断能否移动至目标位置
                boolean CanEat = false;     //判断能否选择吃子
                boolean CanBeAte = false;   //判断该棋子能否被吃

                for (int m = 0; m < ROW_SIZE; m++)
                    for (int n = 0; n < COL_SIZE; n++) {

                        if (squareComponents[x][y].isReversal() && squareComponents[x][y].getChessColor() == currentColor) {
                            if (squareComponents[x][y].getRank() == 5) {
                                if (!(squareComponents[m][n] instanceof EmptySlotComponent) && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()) {
                                    ChessToEat[x][y][m][n] = (squareComponents[m][n].isReversal() && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor() && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"))
                                            || ((!squareComponents[m][n].isReversal()) && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge"));
                                    ChessCanGeneralMove[x][y][m][n] = ChessToEat[x][y][m][n];
                                    CanEat = ChessToEat[x][y][m][n] || CanEat;
                                }
                            } else if (squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()
                                    && squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "Judge")) {
                                if (squareComponents[m][n] instanceof EmptySlotComponent) {
                                    ChessToMove[x][y][m][n] = true;
                                    CanMove = true;
                                    ChessCanGeneralMove[x][y][m][n] = ChessToMove[x][y][m][n];
                                } else {
                                    ChessToEat[x][y][m][n] = true;
                                    CanEat = true;
                                    ChessCanGeneralMove[x][y][m][n] = ChessToEat[x][y][m][n];
                                }
                            }
                            if (squareComponents[x][y].isReversal() && squareComponents[m][n].isReversal()
                                    && squareComponents[x][y].getChessColor() != squareComponents[m][n].getChessColor()
                                    && squareComponents[m][n].canMoveTo(squareComponents, squareComponents[x][y].getChessboardPoint(), "Judge")) {
                                ChessToBeAte[x][y][m][n] = true;
                                CanBeAte = true;
                            }
                        }
                    }
                if (CanMove) {
                    NumOfChessToMove++;
                    ChessCanMove[x][y] = true;
                }
                if (CanEat) {
                    NumOfChessToEat++;
                    ChessCanEat[x][y] = true;
                }
                if (CanBeAte) {
                    NumOfChessToBeAte++;
                    ChessBeAte[x][y] = true;
                }

            }

        for (int x = 0; x < ROW_SIZE; x++)
            for (int y = 0; y < COL_SIZE; y++) {
//                翻棋时做到不会刚翻开即被吃
                if (squareComponents[x][y].getChessColor() == currentColor && (!squareComponents[x][y].isReversal()) && (!(squareComponents[x][y] instanceof EmptySlotComponent))) {
                    if (x - 1 >= 0 && squareComponents[x - 1][y].isReversal() && CanEat(squareComponents[x - 1][y], squareComponents[x][y]))
                        break;
                    if (x + 1 < ROW_SIZE && squareComponents[x + 1][y].isReversal() && CanEat(squareComponents[x + 1][y], squareComponents[x][y]))
                        break;
                    if (y - 1 >= 0 && squareComponents[x][y - 1].isReversal() && CanEat(squareComponents[x][y - 1], squareComponents[x][y]))
                        break;
                    if (y + 1 < COL_SIZE && squareComponents[x][y + 1].isReversal() && CanEat(squareComponents[x][y + 1], squareComponents[x][y]))
                        break;
                    ChessToReverse[x][y] = true;
                    NumOfChessToReverse++;
                }
            }

        int x = -1, y = -1, m = -1, n = -1; // 最终的选择[x][y]-->[m][n]
        int NowScore = currentColor == ChessColor.RED ? GameController.RedScores : GameController.BlackScores;//目前得分

        int x1 = -1, y1 = -1, m1 = -1, n1 = -1;  //[x][y] 吃[m][n]
        //处理数据 选择最佳吃子
        int maxScoreCanGet = 0;
        int theFormerRank = 7;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                for (int a = 0; a < ROW_SIZE; a++)
                    for (int b = 0; b < COL_SIZE; b++) {
                        if (ChessToEat[i][j][a][b]) {

                            //若吃子即胜利则无需考虑后续
                            if (squareComponents[a][b].getScore() + NowScore >= 60) {
                                maxScoreCanGet = 31;
                                x1 = i;
                                y1 = j;
                                m1 = a;
                                n1 = b;
                                break;
                            }

                            //优先吃将
                            if (squareComponents[a][b].getRank() == 0) {
                                maxScoreCanGet = squareComponents[a][b].getScore();
                                x1 = i;
                                y1 = j;
                                m1 = a;
                                n1 = b;
                                break;
                            }

                            //分别判断吃子后的上下左右能否吃掉该棋子 以及吃子是否有利
                            boolean isProfitable = isMovingProfitable(i, j, a, b);

                            if (squareComponents[i][j].getRank() == 5 && (squareComponents[a][b].getRank() == 1 || squareComponents[a][b].getRank() == 0)) {
                                isProfitable = true;
                            }

                            if (squareComponents[a][b].getScore() >= maxScoreCanGet && squareComponents[a][b].getRank() <= theFormerRank) {
                                if (squareComponents[i][j].getRank() == squareComponents[a][b].getRank()
                                        || (squareComponents[i][j].getRank() != squareComponents[a][b].getRank() && isProfitable)) {
                                    maxScoreCanGet = squareComponents[a][b].getScore();
                                    theFormerRank = squareComponents[a][b].getRank();
                                    x1 = i;
                                    y1 = j;
                                    m1 = a;
                                    n1 = b;
                                }
                            }
                        }
                    }
            }
        }

        int x2 = -1, y2 = -1, m2 = -1, n2 = -1;
        //处理数据，得到最坏被吃
        int maxScoreMayLose = 0;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                for (int a = 0; a < ROW_SIZE; a++)
                    for (int b = 0; b < COL_SIZE; b++) {
                        if (ChessToBeAte[i][j][a][b]) {

                            //保帅或者被吃即失败优先级最高
                            if (squareComponents[i][j].getRank() == 0 || NowScore + squareComponents[i][j].getScore() >= 60) {
                                if (squareComponents[a][b].getRank() == 5) {
                                    if (i == a) {
                                        if (i - 1 >= 0 && (ChessCanGeneralMove[i][j][i - 1][j]) && isMovingProfitable(i, j, i - 1, j)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i - 1;
                                            n2 = j;
                                            maxScoreMayLose = (squareComponents[i][j].getRank() == 0) ? squareComponents[i][j].getScore() : 31;
                                        } else if (i + 1 < ROW_SIZE && (ChessCanGeneralMove[i][j][i + 1][j]) && isMovingProfitable(i, j, i + 1, j)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i + 1;
                                            n2 = j;
                                            maxScoreMayLose = (squareComponents[i][j].getRank() == 0) ? squareComponents[i][j].getScore() : 31;
                                        }
                                    } else {
                                        if (j - 1 >= 0 && (ChessCanGeneralMove[i][j][i][j - 1]) && isMovingProfitable(i, j, i, j - 1)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i;
                                            n2 = j - 1;
                                            maxScoreMayLose = (squareComponents[i][j].getRank() == 0) ? squareComponents[i][j].getScore() : 31;
                                        } else if (j + 1 < COL_SIZE && (ChessCanGeneralMove[i][j][i][j + 1]) && isMovingProfitable(i, j, i, j + 1)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i;
                                            n2 = j + 1;
                                            maxScoreMayLose = (squareComponents[i][j].getRank() == 0) ? squareComponents[i][j].getScore() : 31;
                                        }
                                    }
                                    break;
                                } else {
                                    if (i - 1 >= 0 && (ChessCanGeneralMove[i][j][i - 1][j]) && isMovingProfitable(i, j, i - 1, j)) {
                                        x2 = i;
                                        y2 = j;
                                        m2 = i - 1;
                                        n2 = j;
                                        maxScoreMayLose = squareComponents[i][j].getScore();
                                    } else if (i + 1 < ROW_SIZE && (ChessCanGeneralMove[i][j][i + 1][j]) && isMovingProfitable(i, j, i + 1, j)) {
                                        x2 = i;
                                        y2 = j;
                                        m2 = i + 1;
                                        n2 = j;
                                        maxScoreMayLose = squareComponents[i][j].getScore();
                                    } else if (j - 1 >= 0 && (ChessCanGeneralMove[i][j][i][j - 1]) && isMovingProfitable(i, j, i, j - 1)) {
                                        x2 = i;
                                        y2 = j;
                                        m2 = i;
                                        n2 = j - 1;
                                        maxScoreMayLose = squareComponents[i][j].getScore();
                                    } else if (j + 1 < COL_SIZE && (ChessCanGeneralMove[i][j][i][j + 1]) && isMovingProfitable(i, j, i, j + 1)) {
                                        x2 = i;
                                        y2 = j;
                                        m2 = i;
                                        n2 = j + 1;
                                        maxScoreMayLose = squareComponents[i][j].getScore();
                                    }
                                    break;
                                }
                            }

                            boolean isProfitable = isBeingAteProfitable(i, j, a, b); //判断被换子是否有利

                            if (squareComponents[i][j].getScore() >= maxScoreMayLose) {
                                if (!isProfitable && squareComponents[i][j].getRank() != 5) {
                                    if (squareComponents[a][b].getRank() == 5) {
                                        if (i == a) {
                                            if (i - 1 >= 0 && (ChessCanGeneralMove[i][j][i - 1][j]) && isMovingProfitable(i, j, i - 1, j)) {
                                                x2 = i;
                                                y2 = j;
                                                m2 = i - 1;
                                                n2 = j;
                                                maxScoreMayLose = squareComponents[i][j].getScore();
                                            } else if (i + 1 < ROW_SIZE && (ChessCanGeneralMove[i][j][i + 1][j]) && isMovingProfitable(i, j, i + 1, j)) {
                                                x2 = i;
                                                y2 = j;
                                                m2 = i + 1;
                                                n2 = j;
                                                maxScoreMayLose = squareComponents[i][j].getScore();
                                            }
                                        } else {
                                            if (j - 1 >= 0 && (ChessCanGeneralMove[i][j][i][j - 1]) && isMovingProfitable(i, j, i, j - 1)) {
                                                x2 = i;
                                                y2 = j;
                                                m2 = i;
                                                n2 = j - 1;
                                                maxScoreMayLose = squareComponents[i][j].getScore();
                                            } else if (j + 1 < COL_SIZE && (ChessCanGeneralMove[i][j][i][j + 1]) && isMovingProfitable(i, j, i, j + 1)) {
                                                x2 = i;
                                                y2 = j;
                                                m2 = i;
                                                n2 = j + 1;
                                                maxScoreMayLose = squareComponents[i][j].getScore();
                                            }
                                        }
                                    } else {
                                        if (i - 1 >= 0 && (ChessCanGeneralMove[i][j][i - 1][j]) && isMovingProfitable(i, j, i - 1, j)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i - 1;
                                            n2 = j;
                                            maxScoreMayLose = squareComponents[i][j].getScore();
                                        } else if (i + 1 < ROW_SIZE && (ChessCanGeneralMove[i][j][i + 1][j]) && isMovingProfitable(i, j, i + 1, j)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i + 1;
                                            n2 = j;
                                            maxScoreMayLose = squareComponents[i][j].getScore();
                                        } else if (j - 1 >= 0 && (ChessCanGeneralMove[i][j][i][j - 1]) && isMovingProfitable(i, j, i, j - 1)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i;
                                            n2 = j - 1;
                                            maxScoreMayLose = squareComponents[i][j].getScore();
                                        } else if (j + 1 < COL_SIZE && (ChessCanGeneralMove[i][j][i][j + 1]) && isMovingProfitable(i, j, i, j + 1)) {
                                            x2 = i;
                                            y2 = j;
                                            m2 = i;
                                            n2 = j + 1;
                                            maxScoreMayLose = squareComponents[i][j].getScore();
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }

        //处理数据，得到主动移动至空棋子
        boolean CanChooseToMove = false; //表示所有的移动都是不利的
        int scoreMayGetInNextStep = 0;
        int x3 = -1, y3 = -1, m3 = -1, n3 = -1;
        for (int i = 0; i < ROW_SIZE; i++) {
            for (int j = 0; j < COL_SIZE; j++) {
                for (int a = 0; a < ROW_SIZE; a++)
                    for (int b = 0; b < COL_SIZE; b++) {
                        if (ChessToMove[i][j][a][b] && ((isProtect(i, j, squareComponents[i][j]) == 7) || (isProtect(i, j, squareComponents[i][j]) > isProtect(a, b, squareComponents[i][j])))) {


                            if (squareComponents[i][j].getRank() == 0) {
                                if ((a - 1 >= 0 && squareComponents[a - 1][b].getRank() == 6)
                                        || (a + 1 < ROW_SIZE && squareComponents[a + 1][b].getRank() == 6)
                                        || (b - 1 >= 0 && squareComponents[a][b - 1].getRank() == 6)
                                        || (b + 1 < COL_SIZE && squareComponents[a][b + 1].getRank() == 6))
                                    break;
                            }
                            boolean isProfitable = isMovingProfitable(i, j, a, b);
                            CanChooseToMove = isProfitable;

                            int thisStep = 0;
                            if (a - 1 >= 0 && squareComponents[i][j].getChessColor() != squareComponents[a - 1][b].getChessColor() && squareComponents[a - 1][b].isReversal())
                                thisStep = Math.max(thisStep, squareComponents[a - 1][b].getScore());
                            if (a + 1 < ROW_SIZE && squareComponents[i][j].getChessColor() != squareComponents[a + 1][b].getChessColor() && squareComponents[a + 1][b].isReversal())
                                thisStep = Math.max(thisStep, squareComponents[a + 1][b].getScore());
                            if (b - 1 >= 0 && squareComponents[i][j].getChessColor() != squareComponents[a][b - 1].getChessColor() && squareComponents[a][b - 1].isReversal())
                                thisStep = Math.max(thisStep, squareComponents[a][b - 1].getScore());
                            if (b + 1 < COL_SIZE && squareComponents[i][j].getChessColor() != squareComponents[a][b + 1].getChessColor() && squareComponents[a][b + 1].isReversal())
                                thisStep = Math.max(thisStep, squareComponents[a][b + 1].getScore());
                            if (isProfitable && scoreMayGetInNextStep <= thisStep) {
                                scoreMayGetInNextStep = thisStep;
                                x3 = i;
                                y3 = j;
                                m3 = a;
                                n3 = b;
                            }
                        }
                    }
            }
        }
        if (NumOfChessToMove != 0 && scoreMayGetInNextStep == 0) {
            if (CanChooseToMove) {
                do {
                    int RandomNowChess = random.nextInt(NumOfChessToMove) + 1;
                    for (int i = 0; i < ROW_SIZE; i++) {
                        for (int j = 0; j < COL_SIZE; j++) {
                            if (ChessCanMove[i][j]) RandomNowChess--;
                            if (RandomNowChess == 0) {
                                ArrayList<Integer> destination_CanMoveTo = new ArrayList<>();
                                for (int a = 0; a < ROW_SIZE; a++)
                                    for (int b = 0; b < COL_SIZE; b++) {
                                        if (ChessToMove[i][j][a][b])
                                            destination_CanMoveTo.add(a * 4 + b);
                                    }
                                x3 = i;
                                y3 = j;
                                int RandomDestinationChess = random.nextInt(destination_CanMoveTo.size());
                                m3 = destination_CanMoveTo.get(RandomDestinationChess) / 4;
                                n3 = destination_CanMoveTo.get(RandomDestinationChess) % 4;
                                break;
                            }
                        }
                        if (RandomNowChess == 0) break;
                    }
                } while (!isMovingProfitable(x3, y3, m3, n3));
            }
            if (x3 == -1) {
                int RandomNowChess = random.nextInt(NumOfChessToMove) + 1;
                for (int i = 0; i < ROW_SIZE; i++) {
                    for (int j = 0; j < COL_SIZE; j++) {
                        if (ChessCanMove[i][j]) RandomNowChess--;
                        if (RandomNowChess == 0) {
                            ArrayList<Integer> destination_CanMoveTo = new ArrayList<>();
                            for (int a = 0; a < ROW_SIZE; a++)
                                for (int b = 0; b < COL_SIZE; b++) {
                                    if (ChessToMove[i][j][a][b])
                                        destination_CanMoveTo.add(a * 4 + b);
                                }
                            x3 = i;
                            y3 = j;
                            int RandomDestinationChess = random.nextInt(destination_CanMoveTo.size());
                            m3 = destination_CanMoveTo.get(RandomDestinationChess) / 4;
                            n3 = destination_CanMoveTo.get(RandomDestinationChess) % 4;
                            break;
                        }
                    }
                    if (RandomNowChess == 0) break;
                }
            }
        }

        //翻开棋子
        int xx = -1, yy = -1;
        if (NumOfChessToReverse != 0) {
            int RandomNowChess = random.nextInt(NumOfChessToReverse) + 1;
            for (int i = 0; i < ROW_SIZE; i++) {
                for (int j = 0; j < COL_SIZE; j++) {
                    if (ChessToReverse[i][j]) RandomNowChess--;
                    if (RandomNowChess == 0) {
                        xx = i;
                        yy = j;
                        break;
                    }
                }
                if (RandomNowChess == 0) break;
            }
        }


        int type = 0; //人机下棋的类型选择     type=0表示吃子或者移动  type=1表示选择翻开棋子

        if (scoreMayGetInNextStep == 30 && scoreMayGetInNextStep > maxScoreMayLose && scoreMayGetInNextStep > maxScoreCanGet) {
            x = x3;
            y = y3;
            m = m3;
            n = n3;
        } else if (maxScoreCanGet != 0 && maxScoreMayLose != 0) {
            if (maxScoreCanGet >= maxScoreMayLose) {
                x = x1;
                y = y1;
                m = m1;
                n = n1;
            } else {
                x = x2;
                y = y2;
                m = m2;
                n = n2;


            }
        } else if (maxScoreCanGet != 0) {
            x = x1;
            y = y1;
            m = m1;
            n = n1;
        } else if (maxScoreMayLose != 0) {
            x = x2;
            y = y2;
            m = m2;
            n = n2;
        } else {
            if (scoreMayGetInNextStep != 0) {
                x = x3;
                y = y3;
                m = m3;
                n = n3;
            } else if (NumOfChessToReverse != 0) type = 1;
            else {
                x = x3;
                y = y3;
                m = m3;
                n = n3;
            }
        }

        if (type == 0 && (x != -1 && y != -1 && m != -1 && n != -1)) {
            squareComponents[x][y].setSelected(true);
            squareComponents[x][y].repaint();
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            squareComponents[x][y].setSelected(false);
            squareComponents[x][y].canMoveTo(squareComponents, squareComponents[m][n].getChessboardPoint(), "NeedAddPoints");
            swapChessComponents(squareComponents[x][y], squareComponents[m][n]);
        } else {
            squareComponents[xx][yy].setReversal(true);
            squareComponents[xx][yy].repaint();
            this.ProcessingSaveInfo_Reversal(squareComponents[xx][yy]);
        }

        GameController.AIstatement = false;//AI已经下完棋了
    }


    //被吃是否有利
    private boolean isBeingAteProfitable(int i, int j, int a, int b) {
        boolean isProfitable;
        if (i - 1 >= 0 && squareComponents[i - 1][j].getRank() == 5) isProfitable = false;
        else {
            if (i - 1 >= 0 && squareComponents[a][b].getRank() == 0 && squareComponents[i - 1][j].getRank() == 6)
                squareComponents[i - 1][j].setRank(-1);
            isProfitable = i - 1 >= 0 && squareComponents[i - 1][j].isReversal()
                    && squareComponents[i - 1][j].getChessColor() != squareComponents[a][b].getChessColor()
                    && squareComponents[i - 1][j].getRank() <= squareComponents[a][b].getRank();
            if (i - 1 >= 0 && squareComponents[i - 1][j].getRank() == -1) squareComponents[i - 1][j].setRank(6);
        }
        if (!isProfitable) {
            if (i + 1 < ROW_SIZE && squareComponents[a][b].getRank() == 0 && squareComponents[i + 1][j].getRank() == 6)
                squareComponents[i + 1][j].setRank(-1);
            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].getRank() == 5) isProfitable = false;
            else if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].isReversal()
                    && squareComponents[i + 1][j].getChessColor() != squareComponents[a][b].getChessColor()
                    && squareComponents[i + 1][j].getRank() <= squareComponents[a][b].getRank()) {
                isProfitable = true;
            }
            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].getRank() == -1) squareComponents[i + 1][j].setRank(6);
        }
        if (!isProfitable) {
            if (j - 1 >= 0 && squareComponents[a][b].getRank() == 0 && squareComponents[i][j - 1].getRank() == 6)
                squareComponents[i][j - 1].setRank(-1);
            if (j - 1 >= 0 && squareComponents[i][j - 1].getRank() == 5) isProfitable = false;
            if (j - 1 >= 0 && squareComponents[i][j - 1].isReversal()
                    && squareComponents[i][j - 1].getChessColor() != squareComponents[a][b].getChessColor()
                    && squareComponents[i][j - 1].getRank() <= squareComponents[a][b].getRank()) {
                isProfitable = true;
            }
            if (j - 1 >= 0 && squareComponents[i][j - 1].getRank() == -1) squareComponents[i][j - 1].setRank(6);
        }
        if (!isProfitable) {
            if (j + 1 < COL_SIZE && squareComponents[a][b].getRank() == 0 && squareComponents[i][j + 1].getRank() == 6)
                squareComponents[i][j + 1].setRank(-1);
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].getRank() == 5) isProfitable = false;
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].isReversal()
                    && squareComponents[i][j + 1].getChessColor() != squareComponents[a][b].getChessColor()
                    && squareComponents[i][j + 1].getRank() <= squareComponents[a][b].getRank()) {
                isProfitable = true;
            }
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].getRank() == -1) squareComponents[i][j + 1].setRank(6);
        }
        return isProfitable;
    }

    //吃子或者移动是否有利
    private boolean isMovingProfitable(int i, int j, int a, int b) {
        boolean isProfitable;       //判断换子是否有利

        if (i == a) {
            for (int k = 0; k < ROW_SIZE; k++) {
                if (k != a && squareComponents[k][b].getChessColor() != currentColor && squareComponents[k][b].getRank() == 5) {
                    for (int l = 0; l < ROW_SIZE; l++) {
                        if (i < Math.max(l, k) && i > Math.min(l, k) && squareComponents[l][b].getChessColor() == currentColor) {
                            int num = 0;
                            for (int p = Math.min(l, k) + 1; p < Math.max(l, k); p++) {
                                if (!(squareComponents[p][b] instanceof EmptySlotComponent))
                                    num++;
                            }
                            if (num == 0) return false;
                        }
                    }
                }
            }
        } else {
            for (int k = 0; k < COL_SIZE; k++) {
                if (k != b && squareComponents[a][k].getChessColor() != currentColor && squareComponents[a][k].getRank() == 5) {
                    for (int l = 0; l < COL_SIZE; l++) {
                        if (j < Math.max(l, k) && j > Math.min(l, k) && squareComponents[a][l].getChessColor() == currentColor) {
                            int num = 0;
                            for (int p = Math.min(l, k) + 1; p < Math.max(l, k); p++) {
                                if (!(squareComponents[a][p] instanceof EmptySlotComponent))
                                    num++;
                            }
                            if (num == 0) return false;
                        }
                    }
                }
            }
        }

        if (a - 1 >= 0 && squareComponents[a - 1][b].getRank() == 5) isProfitable = true;
        else {
            if (a - 1 >= 0 && squareComponents[i][j].getRank() == 0 && squareComponents[a - 1][b].getRank() == 6)
                squareComponents[a - 1][b].setRank(-1);
            if (a - 1 >= 0 && squareComponents[i][j].getRank() == 6 && squareComponents[a - 1][b].getRank() == 0)
                squareComponents[i][j].setRank(-1);
            isProfitable = a - 1 < 0 || !squareComponents[a - 1][b].isReversal()
                    || squareComponents[a - 1][b].getChessColor() == squareComponents[i][j].getChessColor()
                    || squareComponents[a - 1][b].getRank() > squareComponents[i][j].getRank();
            if (a - 1 >= 0 && squareComponents[a - 1][b].getRank() == -1) squareComponents[a - 1][b].setRank(6);
            if (a - 1 >= 0 && squareComponents[i][j].getRank() == -1) squareComponents[i][j].setRank(6);
        }

        if (isProfitable) {
            if (a + 1 < ROW_SIZE && squareComponents[i][j].getRank() == 0 && squareComponents[a + 1][b].getRank() == 6)
                squareComponents[a + 1][b].setRank(-1);
            if (a + 1 < ROW_SIZE && squareComponents[i][j].getRank() == 6 && squareComponents[a + 1][b].getRank() == 0)
                squareComponents[i][j].setRank(-1);
            if (a + 1 < ROW_SIZE && squareComponents[a + 1][b].getRank() == 5) isProfitable = true;
            else if (a + 1 < ROW_SIZE && squareComponents[a + 1][b].isReversal()
                    && squareComponents[a + 1][b].getChessColor() != squareComponents[i][j].getChessColor()
                    && squareComponents[a + 1][b].getRank() <= squareComponents[i][j].getRank()) {
                isProfitable = false;
            }
            if (a + 1 < ROW_SIZE && squareComponents[a + 1][b].getRank() == -1) squareComponents[a + 1][b].setRank(6);
            if (a + 1 < ROW_SIZE && squareComponents[i][j].getRank() == -1) squareComponents[i][j].setRank(6);
        }
        if (isProfitable) {
            if (b - 1 >= 0 && squareComponents[i][j].getRank() == 0 && squareComponents[a][b - 1].getRank() == 6)
                squareComponents[a][b - 1].setRank(-1);
            if (b - 1 >= 0 && squareComponents[i][j].getRank() == 6 && squareComponents[a][b - 1].getRank() == 0)
                squareComponents[i][j].setRank(-1);
            if (b - 1 >= 0 && squareComponents[a][b - 1].getRank() == 5) isProfitable = true;
            else if (b - 1 >= 0 && squareComponents[a][b - 1].isReversal()
                    && squareComponents[a][b - 1].getChessColor() != squareComponents[i][j].getChessColor()
                    && squareComponents[a][b - 1].getRank() <= squareComponents[i][j].getRank()) {
                isProfitable = false;
            }
            if (b - 1 >= 0 && squareComponents[i][j].getRank() == -1) squareComponents[i][j].setRank(6);
        }
        if (isProfitable) {
            if (b + 1 < COL_SIZE && squareComponents[i][j].getRank() == 0 && squareComponents[a][b + 1].getRank() == 6)
                squareComponents[a][b + 1].setRank(-1);
            if (b + 1 < COL_SIZE && squareComponents[i][j].getRank() == 6 && squareComponents[a][b + 1].getRank() == 0)
                squareComponents[i][j].setRank(-1);
            if (b + 1 < COL_SIZE && squareComponents[a][b + 1].getRank() == 5) isProfitable = true;
            else if (b + 1 < COL_SIZE && squareComponents[a][b + 1].isReversal()
                    && squareComponents[a][b + 1].getChessColor() != squareComponents[i][j].getChessColor()
                    && squareComponents[a][b + 1].getRank() <= squareComponents[i][j].getRank()) {
                isProfitable = false;
            }
            if (b + 1 < COL_SIZE && squareComponents[a][b + 1].getRank() == -1) squareComponents[a][b + 1].setRank(6);
            if (b + 1 < COL_SIZE && squareComponents[i][j].getRank() == -1) squareComponents[i][j].setRank(6);
        }

//        if (!isProfitable) {
//            for (int x = 0; x < ROW_SIZE; x++)
//                for (int y = 0; y < COL_SIZE; y++) {
//                    if (squareComponents[x][y].getChessColor() == currentColor
//                            && squareComponents[x][y].isReversal()
//                            && squareComponents[x][y] != squareComponents[i][j]
//                            && squareComponents[x][y].getRank() == 5
//                            && (x == a || y == b)) {
//                        int num = 0;
//                        if (x == a && !CanEat(squareComponents[x][y > b ? (b + 1) : (b - 1)], squareComponents[i][j])) {
//                            int min = Math.min(y, b), max = Math.max(y, b);
//                            for (int p = min + 1; p < max; p++)
//                                if (!(squareComponents[x][p] instanceof EmptySlotComponent) && squareComponents[x][p] != squareComponents[i][j])
//                                    num++;
//                        } else if (!CanEat(squareComponents[x > a ? (a + 1) : (a - 1)][y], squareComponents[i][j])) {
//                            int min = Math.min(x, a), max = Math.max(x, a);
//                            for (int q = min + 1; q < max; q++)
//                                if (!(squareComponents[q][y] instanceof EmptySlotComponent) && squareComponents[q][y] != squareComponents[i][j])
//                                    num++;
//                        }
//                        isProfitable = (num == 1);
//                    }
//                }
//        }

        return isProfitable;
    }

    public boolean CanEat(SquareComponent a, SquareComponent b) {
        if (a.getChessColor() == b.getChessColor()) return false;
        if (a.getRank() == 0) {
            return b.getRank() != 6;
        } else if (a.getRank() == 6) {
            return b.getRank() == 0;
        } else if (a.getRank() != 5) {
            return a.getRank() <= b.getRank();
        } else return true;
    }

    public int isProtect(int x, int y, SquareComponent chess) {
        int rank = 7;//被保护棋子的等级
        int i, j;//该棋子周围棋子坐标

        if (chess.getRank() == 5) {
            for (int a = 0; a < ROW_SIZE && squareComponents[a][y] != squareComponents[x][y] && squareComponents[a][y].getChessColor() == currentColor; a++) {
                int num = 0;
                for (int k = Math.min(a, x) + 1; k < Math.max(a, x); k++)
                    if (!(squareComponents[k][y] instanceof EmptySlotComponent))
                        num++;
                if (num == 1) rank = Math.min(squareComponents[a][y].getRank(), rank);
            }
            for (int b = 0; b < COL_SIZE && squareComponents[x][b] != squareComponents[x][y] && squareComponents[x][b].getChessColor() == currentColor; b++) {
                int num = 0;
                for (int k = Math.min(y, b) + 1; k < Math.max(y, b); k++)
                    if (!(squareComponents[x][k] instanceof EmptySlotComponent))
                        num++;
                if (num == 1) rank = Math.min(squareComponents[x][b].getRank(), rank);
            }
        }


        if (x - 1 >= 0 && squareComponents[x - 1][y].isReversal()) {
            i = x - 1;
            j = y;
            if (i - 1 >= 0 && squareComponents[i - 1][j].isReversal() && CanEat(squareComponents[i - 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i - 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
//            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].isReversal() && CanEat(squareComponents[i + 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i + 1][j]))
//                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j - 1 >= 0 && squareComponents[i][j - 1].isReversal() && CanEat(squareComponents[i][j - 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j - 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].isReversal() && CanEat(squareComponents[i][j + 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j + 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
        }
        if (x + 1 < ROW_SIZE && squareComponents[x + 1][y].isReversal()) {
            i = x + 1;
            j = y;
//            if (i - 1 >= 0 && squareComponents[i - 1][j].isReversal() && CanEat(squareComponents[i - 1][j], squareComponents[i][j]) && CanEat(squareComponents[x][y], squareComponents[i - 1][j]))
//                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].isReversal() && CanEat(squareComponents[i + 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i + 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j - 1 >= 0 && squareComponents[i][j - 1].isReversal() && CanEat(squareComponents[i][j - 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j - 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].isReversal() && CanEat(squareComponents[i][j + 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j + 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
        }
        if (y - 1 >= 0 && squareComponents[x][y - 1].isReversal()) {
            i = x;
            j = y - 1;
            if (i - 1 >= 0 && squareComponents[i - 1][j].isReversal() && CanEat(squareComponents[i - 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i - 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].isReversal() && CanEat(squareComponents[i + 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i + 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j - 1 >= 0 && squareComponents[i][j - 1].isReversal() && CanEat(squareComponents[i][j - 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j - 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
//            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].isReversal() && CanEat(squareComponents[i][j + 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j + 1]))
//                rank = Math.min(squareComponents[i][j].getRank(), rank);
        }
        if (y + 1 < COL_SIZE && squareComponents[x][y + 1].isReversal()) {
            i = x;
            j = y + 1;
            if (i - 1 >= 0 && squareComponents[i - 1][j].isReversal() && CanEat(squareComponents[i - 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i - 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (i + 1 < ROW_SIZE && squareComponents[i + 1][j].isReversal() && CanEat(squareComponents[i + 1][j], squareComponents[i][j]) && CanEat(chess, squareComponents[i + 1][j]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
//            if (j - 1 >= 0 && squareComponents[i][j - 1].isReversal() && CanEat(squareComponents[i][j - 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j - 1]))
//                rank = Math.min(squareComponents[i][j].getRank(), rank);
            if (j + 1 < COL_SIZE && squareComponents[i][j + 1].isReversal() && CanEat(squareComponents[i][j + 1], squareComponents[i][j]) && CanEat(chess, squareComponents[i][j + 1]))
                rank = Math.min(squareComponents[i][j].getRank(), rank);
        }
        return rank;
    }
}

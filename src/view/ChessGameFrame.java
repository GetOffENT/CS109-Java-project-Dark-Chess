package view;

import DataBase.ProcessData;
import Listen.BackgroundMusic;
import com.sun.management.GarbageCollectionNotificationInfo;
import controller.GameController;
import model.MyThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示游戏窗体，窗体上包含：
 * 1 Chessboard: 棋盘
 * 2 JLabel:  标签
 * 3 JButton： 按钮
 */
public class ChessGameFrame extends JFrame {

    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;

    private final JFrame InitialWindow;
    private static JLabel statusLabel;
    public static JPanel ScoreKeeper;

    public static JLabel background;
    public static JLabel Score;
    //    public static JLabel BlackScore;
    public static JLabel timekeeperPicture;
    public static JLabel timekeeper;
    public static JButton StartGameButton;
    public static JButton AIButton;
    public static JButton SpeedButton;
    public static JButton SuspendButton;
    public static JButton SkipButton;

    public static ArrayList<JLabel> RedAteChess = new ArrayList<>();
    public static ArrayList<JLabel> BlackAteChess = new ArrayList<>();
    public static ArrayList<String> BlackPicture = new ArrayList<>();
    public static ArrayList<String> RedPicture = new ArrayList<>();
    public static ArrayList<JLabel> redAmount = new ArrayList<>();
    public static ArrayList<JLabel> blackAmount = new ArrayList<>();
    //picture存放图片的路径

    public static void addBlackPicturePath() {
        BlackPicture.add(0, "imgs/黑 将.png");
        BlackPicture.add(1, "imgs/黑 士.png");
        BlackPicture.add(2, "imgs/黑 象.png");
        BlackPicture.add(3, "imgs/黑 車.png");
        BlackPicture.add(4, "imgs/黑 馬.png");
        BlackPicture.add(5, "imgs/黑 砲.png");
        BlackPicture.add(6, "imgs/黑 卒.png");
    }

    public static void addRedPicturePath() {
        RedPicture.add(0, "imgs/红 帥.png");
        RedPicture.add(1, "imgs/红 仕.png");
        RedPicture.add(2, "imgs/红 相.png");
        RedPicture.add(3, "imgs/红 俥.png");
        RedPicture.add(4, "imgs/红 傌.png");
        RedPicture.add(5, "imgs/红 炮.png");
        RedPicture.add(6, "imgs/红 兵.png");
    }

    public ChessGameFrame(int width, int height) {

        InitialWindow = new JFrame("翻棋");
        InitialWindow.setSize(720, 720);
        InitialWindow.setLocationRelativeTo(null);
        InitialWindow.setBackground(Color.WHITE);
        InitialWindow.setLayout(null);
//
//
//        //关闭窗口时保存GUI
//        InitialWindow.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                super.windowClosing(e);
//                try {
//                    ProcessData.SaveGUI_infoToFile();
//                } catch (FileNotFoundException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
        InitialWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        InitialWindow.setResizable(false);

        ImageIcon icon = new ImageIcon(GameController.MainPagePicturePath);
        JLabel initialPicture = new JLabel(icon);
        initialPicture.setBounds(0, 0, 720, 720);
        icon.setImage(icon.getImage().getScaledInstance(720, 720, Image.SCALE_DEFAULT));
        initialPicture.setIcon(icon);

        JButton TwoPlayer = new JButton("双人对战");
        TwoPlayer.setLocation(270, 200);
        TwoPlayer.setSize(180, 60);
        TwoPlayer.setFont(new Font("宋体", Font.BOLD, 30));
        TwoPlayer.setContentAreaFilled(false); //设置按钮透明
        TwoPlayer.setBorderPainted(false);
        TwoPlayer.setFocusPainted(false);
        TwoPlayer.setForeground(new Color(220, 57, 57));

        JButton AI = new JButton("人机对战");
        AI.setLocation(270, 420);
        AI.setSize(180, 60);
        AI.setFont(new Font("宋体", Font.BOLD, 30));
        AI.setContentAreaFilled(false);
        AI.setBorderPainted(false);
        AI.setForeground(new Color(220, 57, 57));

        InitialWindow.add(TwoPlayer);
        InitialWindow.add(AI);
        InitialWindow.add(initialPicture);
        InitialWindow.setVisible(true);

        final JPopupMenu jp = new JPopupMenu();

        JMenuItem item1 = new JMenuItem("切换主页背景");
        JMenuItem item2 = new JMenuItem("切换主页背景音乐");
        item1.addActionListener(e -> {
            Object[] options = {"默认", "背景1（红云）", "背景2（树）", "背景3（梅枝）", "自定义"};
            String result = (String) JOptionPane.showInputDialog(null, "请选择想要切换的游戏背景:\n", "背景", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
            if (result != null) {
                String thePath = null;
                if ("默认".equals(result)) {
                    thePath = "imgs/钟1.jpg";
                } else if ("背景1（红云）".equals(result)) {
                    thePath = "imgs/图片1.png";
                } else if ("背景2（树）".equals(result)) {
                    thePath = "imgs/图片2.png";
                } else if ("背景3（梅枝）".equals(result)) {
                    thePath = "imgs/随便试试.png";
                } else if ("自定义".equals(result)) {
                    JFileChooser jFileChooser = new JFileChooser(".");
                    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg,*.png)", "jpg", "png"));
                    int state = jFileChooser.showOpenDialog(null);
                    if (state == JFileChooser.APPROVE_OPTION) {
                        String path = jFileChooser.getSelectedFile().getPath();
                        ImageIcon imageIcon;
                        try {
                            imageIcon = new ImageIcon(ImageIO.read(new File(path)));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        initialPicture.setIcon(imageIcon);
                        imageIcon.setImage(imageIcon.getImage().getScaledInstance(720, 720, Image.SCALE_DEFAULT));
                        GameController.MainPagePicturePath = path;
                    }
                }
                if (thePath != null) {
                    ImageIcon imageIcon = null;
                    try {
                        imageIcon = new ImageIcon(ImageIO.read(new File(thePath)));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    initialPicture.setIcon(imageIcon);
                    imageIcon.setImage(imageIcon.getImage().getScaledInstance(720, 720, Image.SCALE_DEFAULT));
                    GameController.MainPagePicturePath = thePath;
                }
            }
        });
        item2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    GameController.SuspendKeepTime = true;
                    String originalMusic = BackgroundMusic.MainPageMusicPath;
                    Object[] options = {"弈星", "入世", "自定义"};
                    String result = (String) JOptionPane.showInputDialog(null, "请选择音乐:\n", "更换背景音乐", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
                    GameController.SuspendKeepTime = false;
                    if ("入世".equals(result)) {
                        BackgroundMusic.MainPageMusicPath = "sound/王者荣耀 - 入世.wav";
                    } else if ("弈星".equals(result)) {
                        BackgroundMusic.MainPageMusicPath = "sound/弈星.wav";
                    } else if ("自定义".equals(result)) {
                        JFileChooser jFileChooser = new JFileChooser(".");
                        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        jFileChooser.setFileFilter(new FileNameExtensionFilter("*.wav", "wav"));
                        int state = jFileChooser.showOpenDialog(null);
                        if (state == JFileChooser.APPROVE_OPTION) {
                            BackgroundMusic.MainPageMusicPath = jFileChooser.getSelectedFile().getPath();
                        }
//                        String path = JOptionPane.showInputDialog("请输入自定义音乐的路径(仅支持WAV格式)：");
                    }
                    BackgroundMusic.StopMusic = !originalMusic.equals(BackgroundMusic.MusicPath);
                }
            }
        });
        jp.add(item1);
        jp.add(item2);
        InitialWindow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    jp.show(InitialWindow, e.getX(), e.getY());
                }
            }
        });


        TwoPlayer.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            BackgroundMusic.StopMusic = true;
            GameController.page = 2;
            GameController.TwoPlayerStatement = true;
            AIButton.setText("AI托管");
            ImageIcon icon1 = new ImageIcon("imgs/AI托管.png");
            AIButton.setIcon(icon1);
            AIButton.setSize(200, icon1.getIconHeight());
            AIButton.setContentAreaFilled(false);
            AIButton.setBorderPainted(false);
            AIButton.setOpaque(false);
            AIButton.setFocusPainted(false);
            AIButton.setMargin(new Insets(0, 0, 0, 0));
            GameController.type = 0;
            this.setVisible(true);
            InitialWindow.setVisible(false);
            gameController.RestartGame();
        }));
        AI.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            GameController.TwoPlayerStatement = false;
            AIButton.setText("调整难度");
            ImageIcon icon1 = new ImageIcon("imgs/更换难度.png");
            AIButton.setIcon(icon1);
            AIButton.setSize(200, icon1.getIconHeight());
            AIButton.setContentAreaFilled(false);
            AIButton.setBorderPainted(false);
            AIButton.setOpaque(false);
            AIButton.setFocusPainted(false);
            AIButton.setMargin(new Insets(0, 0, 0, 0));
            Object[] options = {"简单", "中等", "困难"};
            String result = (String) JOptionPane.showInputDialog(null, "请选择人机对战的游戏难度:\n", "难度", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
            if (result != null) {
                BackgroundMusic.StopMusic = true;
                GameController.page = 2;
                if ("简单".equals(result)) {
                    GameController.type = 1;
                    this.setVisible(true);
                    InitialWindow.setVisible(false);
                    gameController.RestartGame();
                } else if ("中等".equals(result)) {
                    GameController.type = 2;
                    this.setVisible(true);
                    InitialWindow.setVisible(false);
                    gameController.RestartGame();
                } else if ("困难".equals(result)) {
                    GameController.type = 3;
                    this.setVisible(true);
                    InitialWindow.setVisible(false);
                    gameController.RestartGame();
                }
            }
        }));


        setTitle("翻棋小游戏");
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;

        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);

//        //关闭窗口时保存GUI
//        this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                super.windowClosing(e);
//                try {
//                    ProcessData.SaveGUI_infoToFile();
//                } catch (FileNotFoundException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);
        setResizable(false);

        addLabel();
        addStartGameButton();
        addTimekeeper();
        addMenubar();
        addChessboard();
        //addHelloButton();
        //addSaveButton();
        //addLoadButton();
        addRetractButton();
        addCheatingModeButton();
        addOpenAllButton();
        addAIButton();
        addRestartButton();
        addReturnButton();
        addSpeedButton();
        addSuspendButton();
        addSkipButton();
        addPanel1();
        addRedAmount();
        addBlackAmount();
        addAteChess();
        addBackground();

        MyThread.Timekeeping = new Thread(() -> {
            while (true) {
                if (GameController.isGameStart && !GameController.GameIsOver && !GameController.SuspendKeepTime) {
                    GameController.Time--;
                }
                ChessGameFrame.timekeeper.setText("" + GameController.Time);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (GameController.Time == 0) {
                    gameController.swap();
                }
            }
        });
        MyThread.Timekeeping.start();
    }


    /**
     * 在游戏窗体中添加棋盘
     */
    private void addBackground() {
        ImageIcon icon = new ImageIcon(GameController.GamePicturePath);
        background = new JLabel(icon);
        background.setBounds(0, 0, 720, 720);
        icon.setImage(icon.getImage().getScaledInstance(720, 720, Image.SCALE_DEFAULT));
        background.setIcon(icon);
        add(background);
    }

    private void addChessboard() {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE / 2, CHESSBOARD_SIZE, this);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
        add(chessboard);
    }

    /**
     * 在游戏窗体中添加标签
     */
    private void addLabel() {
        statusLabel = new JLabel("BLACK's TURN");
        statusLabel.setLocation(WIDTH * 3 / 5 + 30, HEIGHT / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setForeground(new Color(127, 126, 126, 255));
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 25));
        add(statusLabel);
    }

    public static JLabel getStatusLabel() {
        return statusLabel;
    }

    private void addTimekeeper() {
        timekeeperPicture = new JLabel();
        ImageIcon icon = new ImageIcon("imgs/计时图片.png");
        timekeeperPicture.setIcon(icon);
        timekeeperPicture.setBounds(425, 570, 70, 80);
        timekeeper = new JLabel("", JLabel.CENTER);
        timekeeper.setFont(new Font("宋体", Font.BOLD, 30));
        timekeeper.setBounds(442, 587, 38, 60);
        timekeeper.setText("30");
        timekeeper.setBackground(Color.LIGHT_GRAY);
        timekeeper.setForeground(new Color(224, 83, 83));
        add(timekeeper);
        add(timekeeperPicture);
    }

    private void addPanel1() {
        ScoreKeeper = new JPanel();
        ScoreKeeper.setLocation(WIDTH * 3 / 5 + 30, HEIGHT / 10 + 70);
        ScoreKeeper.setSize(200, 100);
        ScoreKeeper.setBackground(new Color(239, 156, 120, 255));
        JLabel keeper = new JLabel("BLACK   vs   RED");
        keeper.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        keeper.setLocation(0, 20);
        keeper.setForeground(new Color(129, 48, 15, 255));
        Score = new JLabel("0        0");
        Score.setLocation(0, 0);
        Score.setFont(new Font("宋体", Font.BOLD, 30));
        Score.setForeground(Color.BLACK);
//        BlackScore = new JLabel("       ");
//        BlackScore.setLocation(0,0);
//        BlackScore.setFont(new Font("宋体", Font.BOLD, 30));
//        BlackScore.setForeground(Color.BLACK);

        ScoreKeeper.add(keeper);
        ScoreKeeper.add(Score);
//        ScoreKeeper.add(BlackScore);

//        JLabel RED
        add(ScoreKeeper);
    }

    /**
     * 在游戏窗体中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addHelloButton() {
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> JOptionPane.showMessageDialog(this, "Hello, world!"));
        button.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 230);
        button.setSize(180, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        add(button);
    }

    private void addSaveButton() {
        JButton button = new JButton("存档");
        button.addActionListener(e -> {

        });
        button.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 230);
        button.setSize(180, 60);
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setBackground(Color.LIGHT_GRAY);
        add(button);
    }


    private void addLoadButton() {
        JButton button = new JButton("读档");
        button.setLocation(WIDTH * 3 / 5, HEIGHT / 10 + 300);
        button.setSize(180, 60);
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this, "Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    public void addStartGameButton() {
        StartGameButton = new JButton();
        StartGameButton.setVisible(true);
        ImageIcon icon = new ImageIcon("imgs/开始游戏.png");
        StartGameButton.setIcon(icon);
        StartGameButton.setSize(250, icon.getIconHeight());
        StartGameButton.setContentAreaFilled(false);
        StartGameButton.setBorderPainted(false);
        StartGameButton.setOpaque(false);
        StartGameButton.setFocusPainted(false);
        StartGameButton.setMargin(new Insets(0, 0, 0, 0));
        StartGameButton.setLocation(100, 280);
//       StartGameButton.setSize(250, 100);
//       StartGameButton.setBackground(Color.LIGHT_GRAY);
//        StartGameButton.setFont(new Font("宋体", Font.BOLD, 50));
        add(StartGameButton);
        StartGameButton.addActionListener(e -> {
            GameController.isGameStart = true;
            StartGameButton.setVisible(false);
        });
    }

    public void addRetractButton() {
        JButton button = new JButton("悔棋");
        button.setLocation(WIDTH * 3 / 5 + 50, HEIGHT / 10 + 200);
//        button.setSize(180, 50);
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        ImageIcon icon = new ImageIcon("imgs/悔棋1.png");
        button.setIcon(icon);
        button.setSize(200, icon.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));

        add(button);
        button.addActionListener(e -> {
            GameController.Retract = true;
            gameController.RetractChesses();
            if (GameController.type != 0) {
                gameController.RetractChesses();
            }
            GameController.Retract = false;
        });
    }
//        ImageIcon icon = new ImageIcon("imgs/悔棋.png");
//        button.setIcon(icon);
//        button.setContentAreaFilled(false);
//        button.setBorderPainted(false);
//        button.setMargin(new Insets(0,0,0,0));


    private void addOpenAllButton() {
        JButton button = new JButton("全部翻开");
        button.setLocation(WIDTH * 3 / 5 + 50, HEIGHT / 10 + 260);//360
//        button.setSize(180, 50);
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(new Color(80, 64, 28));
        ImageIcon icon = new ImageIcon("imgs/全部翻开.png");
        button.setIcon(icon);
        button.setSize(200, icon.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
//        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener((e) -> {
            gameController.OpenAllChessOnBoard();
        });
        add(button);
    }


    private void addCheatingModeButton() {
        JButton button = new JButton("作弊模式");
        button.setLocation(WIDTH * 3 / 5 + 50, HEIGHT / 10 + 320);//280
        button.setSize(180, 50);
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(new Color(80, 64, 28));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        ImageIcon icon = new ImageIcon("imgs/作弊模式.png");
        button.setIcon(icon);
        button.setSize(200, icon.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        add(button);
        button.addActionListener(e -> {
            System.out.println("Click CheatingModeButton");
            GameController.CheatingModeButtonStatement = !GameController.CheatingModeButtonStatement;
            if (GameController.CheatingModeButtonStatement) {
                JOptionPane.showMessageDialog(null, "作弊模式已开启", "提醒", JOptionPane.PLAIN_MESSAGE);
            } else JOptionPane.showMessageDialog(null, "作弊模式已关闭", "提醒", JOptionPane.PLAIN_MESSAGE);
        });
    }

    private void addAIButton() {
        AIButton = new JButton();
        AIButton.setLocation(WIDTH * 3 / 5 + 40, HEIGHT / 10 + 380);//200
        add(AIButton);
        AIButton.addActionListener(e -> {
            if (GameController.TwoPlayerStatement) {
                if (GameController.type == 0) {
                    Object[] options = {"弱", "普通", "强"};
                    String result = (String) JOptionPane.showInputDialog(null, "请选择AI托管的强度:\n", "强度", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
                    if ("弱".equals(result)) {
                        GameController.type = 1;
                    } else if ("普通".equals(result)) {
                        GameController.type = 2;
                    } else if ("强".equals(result)) {
                        GameController.type = 3;
                    }
                    JOptionPane.showMessageDialog(null, "AI托管已开启", "提醒", JOptionPane.PLAIN_MESSAGE);
//                    AIButton.setText("关闭AI托管");
                    ImageIcon icon2 = new ImageIcon("imgs/关闭AI托管.png");
                    AIButton.setIcon(icon2);
                    AIButton.setSize(200, icon2.getIconHeight());
                    AIButton.setContentAreaFilled(false);
                    AIButton.setBorderPainted(false);
                    AIButton.setOpaque(false);
                    AIButton.setMargin(new Insets(0, 0, 0, 0));
                } else {
                    GameController.type = 0;
                    JOptionPane.showMessageDialog(null, "AI托管已关闭", "提醒", JOptionPane.PLAIN_MESSAGE);
                    ImageIcon icon1 = new ImageIcon("imgs/AI托管.png");
                    AIButton.setIcon(icon1);
                    AIButton.setSize(200, icon1.getIconHeight());
                    AIButton.setContentAreaFilled(false);
                    AIButton.setBorderPainted(false);
                    AIButton.setOpaque(false);
                    AIButton.setMargin(new Insets(0, 0, 0, 0));
                    //                    AIButton.setText("AI托管");
                }
            } else {
                Object[] options = {"简单", "中等", "困难"};
                String result = (String) JOptionPane.showInputDialog(null, "请更改人机对战的游戏难度:\n", "更改难度", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
                if ("简单".equals(result)) {
                    GameController.type = 1;
                } else if ("中等".equals(result)) {
                    GameController.type = 2;
                } else if ("困难".equals(result)) {
                    GameController.type = 3;
                }
                JOptionPane.showMessageDialog(null, "人机对战游戏难度已更改", "提醒", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    private void addRestartButton() {
        JButton button = new JButton("重新开始");
        button.setLocation(WIDTH * 3 / 5 + 50, HEIGHT / 10 + 440);//200
        button.setFont(new Font("宋体", Font.BOLD, 20));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(new Color(80, 64, 28));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        ImageIcon icon = new ImageIcon("imgs/重新开始.png");
        button.setIcon(icon);
        button.setSize(200, icon.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        add(button);
        button.addActionListener(e -> {
            System.out.println("Click Restart");
            gameController.RestartGame();
        });
    }

    public void addReturnButton() {

        JButton button = new JButton("返回");
        button.setLocation(WIDTH * 3 / 5 + 50, HEIGHT / 10 + 500);//440
//        button.setSize(180, 50);
//        button.setFont(new Font("宋体", Font.BOLD, 20));
//        button.setForeground(new Color(80, 64, 28));
        ImageIcon icon = new ImageIcon("imgs/返回.png");
        button.setIcon(icon);
        button.setSize(200, icon.getIconHeight());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setMargin(new Insets(0, 0, 0, 0));
//        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(null, "结束游戏并返回主页面", "提醒", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                this.setVisible(false);
                InitialWindow.setVisible(true);
                GameController.CheatingModeButtonStatement = false;
                GameController.isGameStart = false;
                ChessGameFrame.StartGameButton.setVisible(true);
                GameController.Time = 30;
            }
            BackgroundMusic.StopMusic = true;
            GameController.page = 1;
        });
        add(button);
    }

    private void addSpeedButton() {
        SpeedButton = new JButton("倍速加载");
        SpeedButton.setVisible(false);
        SpeedButton.setLocation(WIDTH * 3 / 5 - 20, HEIGHT / 20);
        SpeedButton.setSize(120, 50);
        SpeedButton.setFont(new Font("宋体", Font.BOLD, 20));
        add(SpeedButton);
        SpeedButton.addActionListener(e -> {
//            System.out.println("Click Restart");
            Object[] options = {"0.5X", "1X", "1.25X", "1.5X", "2X"};
            String result = (String) JOptionPane.showInputDialog(null, "请选择棋局加载速度:\n", "更改速度", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
            if (result != null) {
                if ("0.5X".equals(result)) {
                    GameController.LoadingSpeed = 1000;
                } else if ("1X".equals(result)) {
                    GameController.LoadingSpeed = 500;
                } else if ("1.25X".equals(result)) {
                    GameController.LoadingSpeed = 400;
                } else if ("1.5X".equals(result)) {
                    GameController.LoadingSpeed = 333;
                } else if ("2X".equals(result)) {
                    GameController.LoadingSpeed = 250;
                }
            }
        });
    }

    private void addSuspendButton() {
        SuspendButton = new JButton("暂停");
        SuspendButton.setVisible(false);
        SuspendButton.setLocation(WIDTH * 3 / 5 - 20 + 120, HEIGHT / 20);
        SuspendButton.setSize(80, 50);
        SuspendButton.setFont(new Font("宋体", Font.BOLD, 20));
        add(SuspendButton);

    }

    private void addSkipButton() {
        SkipButton = new JButton("跳过");
        SkipButton.setVisible(false);
        SkipButton.setLocation(WIDTH * 3 / 5 - 20 + 120 + 80, HEIGHT / 20);
        SkipButton.setSize(80, 50);
        SkipButton.setFont(new Font("宋体", Font.BOLD, 20));
        add(SkipButton);
        SkipButton.addActionListener(e -> {
            GameController.LoadingSpeed = 0;
        });
    }


    public void addMenubar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("开始");
        file.setFont(new Font("宋体", Font.BOLD, 18));
        JMenuItem Save = new JMenuItem("保存");
        Save.setFont(new Font("宋体", Font.BOLD, 15));
        JMenuItem Load = new JMenuItem("读档");
        Load.setFont(new Font("宋体", Font.BOLD, 15));
        file.add(Save);
        file.add(Load);

        JMenu setting = new JMenu("设置");
        setting.setFont(new Font("宋体", Font.BOLD, 18));

        JMenuItem changeBackground = new JMenuItem("切换背景");
        changeBackground.setFont(new Font("宋体", Font.BOLD, 15));
        JMenuItem ChangeMusic = new JMenuItem("切换背景音乐");
        ChangeMusic.setFont(new Font("宋体", Font.BOLD, 15));
        JMenuItem Volumn = new JMenuItem("总音量(50)");
        Volumn.setFont(new Font("宋体", Font.BOLD, 15));
        JCheckBoxMenuItem isAllMusicOpen = new JCheckBoxMenuItem("总音乐", true);
        isAllMusicOpen.setFont(new Font("宋体", Font.BOLD, 15));
        JCheckBoxMenuItem isMusicOpen = new JCheckBoxMenuItem("背景音乐", true);
        isMusicOpen.setFont(new Font("宋体", Font.BOLD, 15));
        JCheckBoxMenuItem isMovingMusicOpen = new JCheckBoxMenuItem("落子音效", true);
        isMovingMusicOpen.setFont(new Font("宋体", Font.BOLD, 15));
        JCheckBoxMenuItem isSelectMusicOpen = new JCheckBoxMenuItem("选择音效", true);
        isSelectMusicOpen.setFont(new Font("宋体", Font.BOLD, 15));
        JCheckBoxMenuItem isTimeMusicOpen = new JCheckBoxMenuItem("时间警告", true);
        isTimeMusicOpen.setFont(new Font("宋体", Font.BOLD, 15));
        setting.add(changeBackground);
        setting.add(ChangeMusic);
        setting.addSeparator();
        setting.add(Volumn);
        setting.addSeparator();
        setting.add(isAllMusicOpen);
        setting.addSeparator();
        setting.add(isMusicOpen);
        setting.add(isMovingMusicOpen);
        setting.add(isSelectMusicOpen);
        setting.add(isTimeMusicOpen);

        JMenu helping = new JMenu("帮助");
        helping.setFont(new Font("宋体", Font.BOLD, 18));
        JMenuItem Cheating = new JMenuItem("作弊模式介绍");
        Cheating.setFont(new Font("宋体", Font.BOLD, 15));
        JMenuItem Rules = new JMenuItem("翻棋游戏介绍");
        Rules.setFont(new Font("宋体", Font.BOLD, 15));
        helping.add(Rules);
        helping.add(Cheating);


        menuBar.add(file);
        menuBar.add(setting);
        menuBar.add(helping);
        menuBar.setSize(720, 30);
        add(menuBar);
        Save.addActionListener(e -> {
            GameController.SuspendKeepTime = true;
            try {
                System.out.println("Click Save");

                JFileChooser jFileChooser = new JFileChooser(".//res");
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setDialogTitle("保存");
                jFileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));//文件过滤器
                int state = jFileChooser.showOpenDialog(null);
                if (state == JFileChooser.APPROVE_OPTION) {
                    String path = jFileChooser.getSelectedFile().getAbsolutePath();
                    ProcessData.SaveInfoToFile(path);
                }

//                String path = JOptionPane.showInputDialog(this, "保存当前棋局至指定路径");
                GameController.SuspendKeepTime = false;
//                ProcessData.SaveInfoToFile(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Load.addActionListener(e -> {
            GameController.SuspendKeepTime = true;
            System.out.println("Click load");
//            String path = JOptionPane.showInputDialog(this, "读取指定路径中存储的游戏数据并加载游戏");
            JFileChooser jFileChooser = new JFileChooser(".//res");
//            jFileChooser.setDialogTitle("读取指定路径中存储的游戏数据并加载游戏");
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));//文件过滤器
            jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("zip(*.json)", "json"));
            int state = jFileChooser.showOpenDialog(null);
            if (state == JFileChooser.APPROVE_OPTION) {
                String path = jFileChooser.getSelectedFile().getAbsolutePath();
                ChessGameFrame.StartGameButton.setVisible(false);
                GameController.isGameStart = true;
                gameController.loadGameFromFile(path);
            }
            GameController.SuspendKeepTime = false;
        });


        changeBackground.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            GameController.SuspendKeepTime = true;
            Object[] options = {"默认", "背景1（红云）", "背景2（树）", "背景3（梅枝）", "自定义"};
            String result = (String) JOptionPane.showInputDialog(null, "请选择想要切换的游戏背景:\n", "背景", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
            GameController.SuspendKeepTime = false;
            if (result != null) {
                if ("默认".equals(result)) {
                    try {
                        changeBackground("imgs/试试1.jpg");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if ("背景1（红云）".equals(result)) {
                    try {
                        changeBackground("imgs/图片1.png");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if ("背景2（树）".equals(result)) {
                    try {
                        changeBackground("imgs/图片2.png");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if ("背景3（梅枝）".equals(result)) {
                    try {
                        changeBackground("imgs/随便试试.png");
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if ("自定义".equals(result)) {
                    GameController.SuspendKeepTime = true;

                    JFileChooser jFileChooser = new JFileChooser(".");
                    jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    jFileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg,*.png)", "jpg", "png"));
                    int state = jFileChooser.showOpenDialog(null);
                    if (state == JFileChooser.APPROVE_OPTION) {
                        String path = jFileChooser.getSelectedFile().getPath();
                        try {
                            changeBackground(path);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    GameController.SuspendKeepTime = false;
                }
            }
        }));
        ChangeMusic.addActionListener(e -> {
            GameController.SuspendKeepTime = true;
            String originalMusic = BackgroundMusic.MusicPath;
            Object[] options = {"默认", "斗地主", "画心", "阳春白雪", "Start over", "Insomnia(治愈)", "Somewhere", "自定义"};
            String result = (String) JOptionPane.showInputDialog(null, "请选择音乐:\n", "更换背景音乐", JOptionPane.PLAIN_MESSAGE, new ImageIcon("xx.png"), options, "xx");
            GameController.SuspendKeepTime = false;
            if ("默认".equals(result)) {
                BackgroundMusic.MusicPath = "sound/新棋子音乐2.WAV";
            } else if ("斗地主".equals(result)) {
                BackgroundMusic.MusicPath = "sound/斗地主.wav";
            } else if ("画心".equals(result)) {
                BackgroundMusic.MusicPath = "sound/常静 - 画心.wav";
            } else if ("阳春白雪".equals(result)) {
                BackgroundMusic.MusicPath = "sound/群星 - 阳春白雪.wav";
            } else if ("Start over".equals(result)) {
                BackgroundMusic.MusicPath = "sound/林以夏 - Start over（钢琴曲）.wav";
            } else if ("Insomnia(治愈)".equals(result)) {
                BackgroundMusic.MusicPath = "sound/ALisa - Insomnia (钢琴治愈版).wav";
            } else if ("Somewhere".equals(result)) {
                BackgroundMusic.MusicPath = "sound/July (줄라이) - Somewhere (V0).wav";
            } else if ("自定义".equals(result)) {
                GameController.SuspendKeepTime = true;

                JFileChooser jFileChooser = new JFileChooser(".");
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.setFileFilter(new FileNameExtensionFilter("*.wav", "wav"));
                int state = jFileChooser.showOpenDialog(null);
                if (state == JFileChooser.APPROVE_OPTION) {
                    BackgroundMusic.MusicPath = jFileChooser.getSelectedFile().getPath();
                }

//                String path = JOptionPane.showInputDialog(this, "请输入自定义音乐的路径(仅支持WAV格式)：");
                GameController.SuspendKeepTime = false;
//                if (path != null) BackgroundMusic.MusicPath = path;
            }
            BackgroundMusic.StopMusic = !originalMusic.equals(BackgroundMusic.MusicPath);
        });
        Volumn.addActionListener(e -> {
            GameController.SuspendKeepTime = true;
            String volumn = JOptionPane.showInputDialog(this, String.format("当前音量为 %.0f\n请输入您要调节的音量(0-100)：", BackgroundMusic.value * 50));
            GameController.SuspendKeepTime = false;
            BackgroundMusic.value = Double.parseDouble(volumn) / 50;
            Volumn.setText(String.format("总音量(%.0f)", BackgroundMusic.value * 50));
        });
        isAllMusicOpen.addActionListener(e -> {
            BackgroundMusic.OverallMusic = isAllMusicOpen.getState();
            BackgroundMusic.MovingMusic = isAllMusicOpen.getState();
            BackgroundMusic.SelectMusic = isAllMusicOpen.getState();
            BackgroundMusic.TimeMusic = isAllMusicOpen.getState();
            isMusicOpen.setState(isAllMusicOpen.getState());
            isMovingMusicOpen.setState(isAllMusicOpen.getState());
            isSelectMusicOpen.setState(isAllMusicOpen.getState());
            isTimeMusicOpen.setState(isAllMusicOpen.getState());
        });
        isMusicOpen.addActionListener(e -> {
            BackgroundMusic.OverallMusic = isMusicOpen.getState();
        });
        isMovingMusicOpen.addActionListener(e -> {
            BackgroundMusic.MovingMusic = isMovingMusicOpen.getState();
        });
        isSelectMusicOpen.addActionListener(e -> {
            BackgroundMusic.SelectMusic = isSelectMusicOpen.getState();
        });
        isTimeMusicOpen.addActionListener(e -> {
            BackgroundMusic.TimeMusic = isTimeMusicOpen.getState();
        });

        Cheating.addActionListener(e -> {
            GameController.SuspendKeepTime = true;
            JOptionPane.showMessageDialog(null, "作弊模式开启后，鼠标右键长按棋子可看到棋子内容", "关于作弊模式", JOptionPane.PLAIN_MESSAGE);
            GameController.SuspendKeepTime = false;
        });
        Rules.addActionListener(e -> {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe https://baike.baidu.com/item/%E8%B1%A1%E6%A3%8B%E7%BF%BB%E7%BF%BB%E6%A3%8B/8259261");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
    }

    private void changeBackground(String path) throws IOException {
        ImageIcon icon = new ImageIcon(ImageIO.read(new File(path)));
        background.setIcon(icon);
        icon.setImage(icon.getImage().getScaledInstance(720, 720, Image.SCALE_DEFAULT));
        GameController.GamePicturePath = path;
    }

    public void addRedAmount() {
        for (int i = 0; i < 7; i++) {
            JLabel Red = new JLabel();
            Red.setLocation(30, 510 - i * 68);
            Red.setSize(55, 55);
            Red.setForeground(new Color(176, 14, 14));
            Red.setFont(new Font("宋体", Font.BOLD, 20));
            add(Red);
            redAmount.add(Red);
        }
    }

    public void addBlackAmount() {
        for (int i = 0; i < 7; i++) {
            JLabel Black = new JLabel();
            Black.setLocation(400 - 10, 510 - i * 68);
            Black.setSize(55, 55);
            Black.setForeground(new Color(176, 14, 14));
            Black.setFont(new Font("宋体", Font.BOLD, 20));
            add(Black);
            blackAmount.add(Black);
        }
    }

    public void addAteChess() {
        addRedPicturePath();
        addBlackPicturePath();
        for (int i = 0; i < 7; i++) {
            JLabel Red = new JLabel();
            Red.setLocation(5, 530 - i * 68);
            Red.setSize(55, 55);
            Red.setForeground(Color.RED);
            Red.setFont(new Font("宋体", Font.BOLD, 50));
            add(Red);
            RedAteChess.add(Red);
        }
        for (int i = 0; i < 7; i++) {
            JLabel Black = new JLabel();
            Black.setLocation(365, 530 - i * 68);
            Black.setSize(55, 55);
            Black.setForeground(Color.BLACK);
            Black.setFont(new Font("宋体", Font.BOLD, 30));
            add(Black);
            BlackAteChess.add(Black);
        }
    }


}
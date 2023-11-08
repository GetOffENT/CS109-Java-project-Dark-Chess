package chessComponent;

import DataBase.ProcessData;
import controller.ClickController;
import controller.GameController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

public class GeneralChessComponent extends ChessComponent {
    public static final int rank = 0;
    public static final int score = 30;

    public GeneralChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "帥";
        } else {
            name = "將";
        }
        setRank(rank);
        setName(name);
        setScore(score);
    }

    @Override
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination, String type) {
        SquareComponent destinationChess = chessboard[destination.getX()][destination.getY()];
        SquareComponent NowChess = chessboard[getChessboardPoint().getX()][getChessboardPoint().getY()];
        boolean Specific = !(destinationChess.getRank() == 6);//兵的特殊情况
        boolean isAdjacent = getChessboardPoint().getX() == destination.getX() && Math.abs(getChessboardPoint().getY() - destination.getY()) == 1
                || getChessboardPoint().getY() == destination.getY() && Math.abs(getChessboardPoint().getX() - destination.getX()) == 1;
        boolean isRankLegal = destinationChess.getRank() >= NowChess.getRank();
        if ((Specific && isRankLegal && isAdjacent && destinationChess.isReversal) || (destinationChess instanceof EmptySlotComponent && isAdjacent)) {
            if(type.equals("NeedAddPoints")){
                KeepScore(destinationChess);
                ProcessingSaveInfo_EatOrMove(NowChess, destinationChess);
                ProcessData.ateChesses.add(destinationChess);
            }
        }
        return (Specific && isRankLegal && isAdjacent && destinationChess.isReversal) || (destinationChess instanceof EmptySlotComponent && isAdjacent);
    }

}

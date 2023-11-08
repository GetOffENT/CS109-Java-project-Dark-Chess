package chessComponent;

import DataBase.ProcessData;
import controller.ClickController;
import controller.GameController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

public class CannonChessComponent extends ChessComponent {
    public static final int rank = 5;
    public static final int score = 5;

    public CannonChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "炮";
        } else {
            name = "砲";
        }
        setRank(rank);
        setName(name);
        setScore(score);
    }

    @Override
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination, String type) {
        SquareComponent destinationChess = chessboard[destination.getX()][destination.getY()];
        SquareComponent NowChess = chessboard[getChessboardPoint().getX()][getChessboardPoint().getY()];

        if(destinationChess instanceof EmptySlotComponent) return false;

        //判断是否在同一行或者在同一列
        boolean isthsSameRowOrCol = (destination.getX() == getChessboardPoint().getX() && destination.getY() != getChessboardPoint().getY())
                || (destination.getY() == getChessboardPoint().getY() && destination.getX() != getChessboardPoint().getX());
        int num = 0;//炮与目标棋子之间的棋子数
        if(destination.getX() == getChessboardPoint().getX() && destination.getY() != getChessboardPoint().getY()){
            for(int i = Math.min(destination.getY(),getChessboardPoint().getY())+1;i<Math.max(destination.getY(),getChessboardPoint().getY());i++){
                if(!(chessboard[destination.getX()][i] instanceof EmptySlotComponent))
                    num++;
            }
        }else if(destination.getY() == getChessboardPoint().getY() && destination.getX() != getChessboardPoint().getX()){
            for(int i = Math.min(destination.getX(),getChessboardPoint().getX())+1;i<Math.max(destination.getX(),getChessboardPoint().getX());i++){
                if(!(chessboard[i][destination.getY()] instanceof EmptySlotComponent))
                    num++;
            }
        }
        if(isthsSameRowOrCol && (num == 1)) {
            if(type.equals("NeedAddPoints")){
                KeepScore(destinationChess);
                ProcessingSaveInfo_EatOrMove(NowChess, destinationChess);
                ProcessData.ateChesses.add(destinationChess);
            }
        }
        return isthsSameRowOrCol && (num == 1);
    }

}

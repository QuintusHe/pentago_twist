package student_player;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoBoardState.Piece;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    public int opponent_id;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260766420");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */

    public Move chooseMove(PentagoBoardState boardState) {

        ArrayList<PentagoMove> possibleMoves = boardState.getAllLegalMoves() ;
        opponent_id = boardState.getTurnPlayer() == boardState.WHITE ? boardState.BLACK : boardState.WHITE;
        int[] minimaxResult = minimax(boardState , 2 , Integer.MIN_VALUE , Integer.MAX_VALUE , player_id) ;

        //For valid move index, return minmaxMove
        if(minimaxResult[1] <= possibleMoves.size() && minimaxResult[1] >= 0 ){
            Move minimaxMove= possibleMoves.get(minimaxResult[1]);
            return minimaxMove ;
        } else {
            //Else, return random move
            Move rMove = boardState.getRandomMove();
            return rMove ;
        }
    }

    // depth: minimax tree depth
    // return an integer array, result[0] is score, result[1] is indexOfMove
    private int[] minimax (PentagoBoardState boardState , int depth , int alpha , int beta , int playerID){
        int[] result = new int[2] ;
        int score = 0 ;
        int indexOfMove = 0 ;

        //if gameover then return the biggest number
        // when game is not over , we need to evaluate the board
        if (boardState.gameOver()){
            if(boardState.getWinner() == player_id){
                score = 5000 ;
            }else if (boardState.getWinner() == opponent_id){
                score = -5000 ;
            }else{
                score = 0 ;
            }

        } else if (depth == 0){
            //if depth==0 call evaluatingFunction , evaluate current board state
            score = evaluationFunction(playerID , boardState) ;
        }else{

            //else , the following two lines
            ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
            for (int i=0 ; i< moves.size() ; i++){
                //clone boardState
                PentagoBoardState clonedState = (PentagoBoardState) boardState.clone();
                clonedState.processMove(moves.get(i));
                if(playerID == player_id){
                    score = minimax(clonedState , depth - 1 , alpha, beta, opponent_id)[0];
                    if (score > alpha) {
                        alpha = score;
                        indexOfMove=i;
                    }
                } else {
                    score = minimax(clonedState , depth - 1 , alpha, beta, player_id)[0];
                    if (score < beta) {
                        beta = score;
                        indexOfMove=i;
                    }
                }
                if(alpha >= beta){
                    break ;
                }
            }
        }
        result[0] = score ;
        result[1] = indexOfMove ;
        return result ;
    }

    //playerID represent whose turn ; Piece represent whose piece it is
    private int evaluationFunction (int playerID,PentagoBoardState board ){
        Set niceXPoints = new HashSet<Integer>();
        Set niceYPoints = new HashSet<Integer>();
        String[] directions = {"right","downRight","down","downLeft"};
        niceXPoints.add(1);
        niceXPoints.add(4);
        niceYPoints.add(1);
        niceYPoints.add(4);

        int score=0;
        for (int x = 0; x <= 5; x++) {
            for (int y = 0; y <= 5; y++) {
                if (board.getPieceAt(x, y).equals(Piece.BLACK)) {
                    if (niceXPoints.contains(x) && niceYPoints.contains(y)) {
                        score = score + 3;
                    }
                    for (int d = 0;d<directions.length;d++){
                        score = score + explore(x, y, Piece.BLACK, board,directions[d]);
                    }

                } else if (board.getPieceAt(x, y).equals(Piece.WHITE)) {
                    if (niceXPoints.contains(x) && niceYPoints.contains(y)) {
                        score = score + 3;
                    }
                    for (int d = 0;d<directions.length;d++){
                        score = score + explore(x, y, Piece.WHITE, board,directions[d]);
                    }
                }
            }
        }

        if (playerID==player_id){
            return score;
        }else {
            return -score;
        }
    }

    //search if the current move forms a line of two or three or four or five
    private int explore(int x, int y, Piece pieceColor, PentagoBoardState board , String direction) {
        int score = 0;
        if (x < 0 || y < 0 || x > 5 || y > 5) {
            return score;
        }
        if (!board.getPieceAt(x, y).equals(pieceColor)) {
            return score;
        }
        score = score + 1;
        if (board.getPieceAt(x, y).equals(pieceColor)) {
            if(direction == "right"){
                score = score + explore(x + 1, y, pieceColor, board,direction);
            }else if(direction == "downRight"){
                score = score + explore(x + 1, y+1, pieceColor, board,direction);
            }else if(direction == "down"){
                score = score + explore(x, y+1, pieceColor, board,direction);
            }else if(direction == "downLeft"){
                score = score + explore(x - 1, y+1, pieceColor, board,direction);
            }
        }
        return score;
    }
}
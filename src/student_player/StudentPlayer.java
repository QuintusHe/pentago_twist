package student_player;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoBoardState.Piece;
//import pentago_twist.PentagoBoardState.Quadrant;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    public int opponent_id;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("xxxxxxxxx");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
//    public Move chooseMove(PentagoBoardState boardState) {
//        // You probably will make separate functions in MyTools.
//        // For example, maybe you'll need to load some pre-processed best opening
//        // strategies...
//        MyTools.getSomething();
//
//        // Is random the best you can do?
//        Move myMove = boardState.getRandomMove();
//
//        // Return your move to be processed by the server.
//        return myMove;
//    }

    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        ArrayList<PentagoMove> testMoves = boardState.getAllLegalMoves() ;
        opponent_id = boardState.getTurnPlayer() == boardState.WHITE ? boardState.BLACK : boardState.WHITE;

        int[] minimaxResult = minimax(boardState , 2 , Integer.MIN_VALUE , Integer.MAX_VALUE , player_id) ;
        if(minimaxResult[1] <= testMoves.size() && minimaxResult[1] >= 0 ){
            Move minimaxMove= testMoves.get(minimaxResult[1]);
            return minimaxMove ;
        }else{
            Move myMove = boardState.getRandomMove();
            return myMove ;
        }

        // Return your move to be processed by the server.
    }
    // depth: minimax tree depth ; return an integer array ; result[0] is score ; result[1] is indexOfMove
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
            score = evaluationFunction(playerID , boardState) ;
        }else{

            //if depth==0 call evaluatingFunction , evaluate current board state
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
    //transform a quadrant into an integer number
//    private int getQuadrantID (Quadrant q){
//        if (q.equals(Quadrant.TL)){
//            return 0;
//        }
//        if (q.equals(Quadrant.TR)){
//            return 1;
//        }
//        if (q.equals(Quadrant.BL)){
//            return 2;
//        }
//        if (q.equals(Quadrant.BR)){
//            return 3;
//        }
//        return -1;
//    }
//    //transform int back into Quadrant when returning
//    private Quadrant getQuadrant(int id){
//        if (id == 0){
//            return Quadrant.TL;
//        }
//        if (id == 1){
//            return Quadrant.TR;
//        }
//        if (id == 2){
//            return Quadrant.BL;
//        }
//        if (id == 3){
//            return Quadrant.BR;
//        }
//        return null ;
//    }

    //int: score
    //playerID is mandatory (represent whose turn) ; Piece (represent whose piece it is)
    private int evaluationFunction (int playerID,PentagoBoardState board ){
        Set goldXPits = new HashSet<Integer>();
        Set goldYPits = new HashSet<Integer>();
        String[] directions = {"right","downRight","down","downLeft"};
        goldXPits.add(1);
        goldXPits.add(4);
        goldYPits.add(1);
        goldYPits.add(4);
        int score=0;
        for (int x = 0; x <= 5; x++) {
            for (int y = 0; y <= 5; y++) {
                if (board.getPieceAt(x, y).equals(Piece.BLACK)) {
                    if (goldXPits.contains(x) && goldYPits.contains(y)) {
                        score = score + 3;
                    }
                    for (int d = 0;d<directions.length;d++){
                        score = score + explore(x, y, Piece.BLACK, board,directions[d]);
                    }

                } else if (board.getPieceAt(x, y).equals(Piece.WHITE)) {
                    if (goldXPits.contains(x) && goldYPits.contains(y)) {
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
    //todo :add direction
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
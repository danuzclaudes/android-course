package edu.unc.chongrui.assignment1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final ImageButton[][] buttons = new ImageButton[8][8];
    private int[] board;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setHeading();
        setProperties();
        buildBoard();

        registerListeners();
    }

    private void setHeading(){
        // TextView head = new TextView(this);
        // TextView head = (TextView) gl.getChildAt(0);
        // View parent = (View) head.getParent();
        TextView heading = (TextView) this.findViewById(R.id.heading);
        Log.v("Heading", heading.getText().toString());
    }

    private void setProperties(){
        // set each cell/buttons's properties and add into grid layout
        GridLayout gl = (GridLayout) findViewById(R.id.chessboard);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                buttons[i][j] = new ImageButton(this);
                buttons[i][j].setBackground(null);
                buttons[i][j].setId(i * 8 + j);
                buttons[i][j].setPadding(0, 0, 0, 0);
                buttons[i][j].setTag("1");
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(0, 0, 0, 0);
                params.width = 120;
                params.height = 120;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                buttons[i][j].setLayoutParams(params);
                gl.addView(buttons[i][j]);
            }
        }
    }

    private void buildBoard(){
        // build/reset the chessboard
        // reset class members
        this.board = new int[8];
        Arrays.fill(board, -1);
        this.count = 0;
        // re-paint the chessboard
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                // buttons[i][j].setBackgroundColor(Color.rgb(204, 102, 0));
                // buttons[i][j].setBackgroundColor(Color.rgb(255, 204, 153));
                buttons[i][j].setBackgroundResource(
                        (i + j) % 2 == 0 ? R.color.cellEven : R.color.cellOdd
                );
            }
        }
    }

    private void registerListeners(){
        // register action listeners to buttons on board
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                buttons[i][j].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) { onClickBoard(v); }
                });
            }
        }
    }

    public void onClickBoard(View v){
        // store original state of currently clicked button;
        // check if cur cell is placed or valid;
        // once placed, ignore invalid cells while can still replace cur;
        Log.v("Heading", v.getId() + "");
        ImageButton btn = (ImageButton) findViewById(v.getId());
        // int tmp = ((ColorDrawable) btn.getBackground()).getColor();
        int index = btn.getId();
        @DrawableRes int tmp = getColorResource(index);

        /* already placed a Queen, remove it from board */
        if(! v.getTag().equals("1")){
            // set 0 to remove the background resource;
            // set android.R.color.transparent;
            // set android.R.drawable.btn_default;
            btn.setBackgroundResource(tmp);
            v.setTag("1");

            board[this.getRow(index)] = -1;
        }
        else if(valid(this.getRow(index), this.getCol(index))){
            btn.setBackgroundResource(R.drawable.crown);
            v.setTag("2");

            board[this.getRow(index)] = this.getCol(index);
        }
        /* no queen but invalid to place */
        else{
            showMsg("Occupied");
            Log.v("Heading", "Occupied");
        }
    }

    private @DrawableRes int getColorResource(int index){
        // retrieve original color resource of button
        int row = getRow(index);
        int col = getCol(index);
        Log.v("Heading", "index="+index+" row="+row+" col="+col);
        return (row + col) % 2 == 0 ? R.color.cellEven : R.color.cellOdd;
    }

    private int getRow(int index){ return index / 8; }
    private int getCol(int index){
        return index % 8;
    }

    private boolean valid(int row, int col){
        // check if there's a Queen on same row or col or diagonal
        if(board[row] != -1) return false;
        for(int i = 0; i < 8; i++){
            // continue if on same row or current row is not yet placed
            if(i == row || board[i] == -1) continue;
            if(board[i] == col) return false;
            if(Math.abs(row - i) == Math.abs(col - board[i])) return false;
        }
        return true;
    }

    public void onClickGiveUp(View v){
        // BZ: what if no solutions are available under current status?
        List<List<Integer>> res = new ArrayList<>();
        solveQueens(7, res);
        if(! res.isEmpty()) mapBoardToGrids(res.get(0));
        else showMsg("No solutions possible");
        Log.v("Giveup", "count="+count);
    }
    private void solveQueens(int row, List<List<Integer>> res){
        // solve 8-Queens recursively on current row
        if(row < 0) {
            List<Integer> tmp = new ArrayList<>();
            for(int cell : board) tmp.add(cell);
            res.add(tmp);
            this.count++;
            return;
        }
        // BZ: current row already placed, recurse next row;
        else if(board[row] != -1){
            solveQueens(row - 1, res);
            return;
        }
        for(int j = 0; j < 8; j++){
            // invalid col, continue by next col;
            if (! valid(row, j)) continue;
            board[row] = j;
            solveQueens(row - 1, res);
            board[row] = -1;
        }
    }
    private void mapBoardToGrids(List<Integer> board){
        // map current status of board to bg resources on buttons
        for(int i = 0; i < 8; i++){
            int index = i * 8 + board.get(i);
            ImageButton btn = (ImageButton) findViewById(index);
            btn.setBackgroundResource(R.drawable.crown);
            // BZ: this.board may have been covered by later solutions
            // after all Queens are filled, no replacement can occur
            this.board[i] = board.get(i);
        }
        showMsg("Solutions are on the board");
    }

    public void onClickReset(View v){
        buildBoard();
        showMsg("The board has been reset");
    }

    private void showMsg(String s){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}
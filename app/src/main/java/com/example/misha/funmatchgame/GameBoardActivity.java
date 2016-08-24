package com.example.misha.funmatchgame;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteTableLockedException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Misha on 7/29/2016.
 */
public class GameBoardActivity extends Activity {

    //custom views
    TextView timerUp;
    TextView avatar_name;


    //timer action vars
    long countSeconds;
    ImageButton pause_btn;
    boolean isStopped = false;

    //table layout vars
    TableLayout tblGameBoardTable;

    //DBHelper
    DBHelper db;

    //General
    final Timer countUpTimer = new Timer();
    boolean isTimerRunning;
    boolean  isGameStart = false;
    int numberOfRequiredMatches = -1;
    int imgBoard [] [];
    int number_rows;
    int number_columns;
    int clickCounter = 0;
    Drawable cardBack_img;
    ImgCard firstCard;
    ImgCard secondCard;
    private static Object lock = new Object();
    ArrayList<Drawable> arrOfImages;
    UpdateCardsHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_layout);

        db = new DBHelper(GameBoardActivity.this);
        handler = new UpdateCardsHandler();
        /*cardBack_img =  getResources().getDrawable(R.drawable.back_card_minions);*/

        cardBack_img =  getDrawable(R.drawable.back_card_minions);
        timerUp = (TextView)findViewById(R.id.score_bar_timer_up);
        avatar_name = (TextView)findViewById(R.id.score_bar_avatar_info);

        try {
            readFromPreferences(getString(R.string.case_name));
            readFromPreferences(getString(R.string.case_rows));
            readFromPreferences(getString(R.string.case_cols));
        }catch (NullPointerException exc){
            Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }

        lowerMenuINIT();
        createImgArray();
        populateGameBoard(number_rows ,number_columns);
        //startTimer(true);



    }

    public void lowerMenuINIT() {
        pause_btn = (ImageButton)findViewById(R.id.pause_btn);
        try {
            pause_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isStopped) {
                        isStopped = true;
                        pause_btn.setBackgroundResource(R.drawable.play_button3);
                    } else if (isStopped) {
                        isStopped = false;
                        pause_btn.setBackgroundResource(R.drawable.pause_button2);
                    }

                }
            });
        }catch (NullPointerException exc){
            Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
    }

    protected void startTimer(boolean isRunning) {
        isTimerRunning = isRunning;
        if(isTimerRunning) {
            countUpTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    if(!isStopped) {
                        countSeconds++; //increase every sec
                    }
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }, 0, 1000);
        }

        if(!isTimerRunning) {
            this.countUpTimer.cancel();
        }

    }

    //timer handler
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            timerUp.setText(timerUpAsString());
        }
    };

    public void populateGameBoard(int num_rows , int num_columns) {
        // populate buttons
        try {
            numberOfRequiredMatches = (num_columns*num_rows)/2;
        }catch (ArithmeticException ex){
            Toast.makeText(GameBoardActivity.this,R.string.illegal_mathematical_action,Toast.LENGTH_SHORT).show();
        }

        if(num_columns*num_rows >0 ) {
            imgBoard = new int[num_rows][num_columns];
        }

        tblGameBoardTable = (TableLayout) findViewById(R.id.game_board_table);

        for (int rows = 0 ; rows  < num_rows ; rows++ ){
            //create new rows as needed and add it to table

            TableRow tblGameBoardRow = new TableRow(this);
            tblGameBoardRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            try {
                tblGameBoardTable.addView(tblGameBoardRow);
            }catch (NullPointerException ex){
                Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
            }


            for (int cols = 0 ; cols < num_columns ; cols++){

                //create buttonView for each column and add it to the row

                ImageButton imgBtn = new ImageButton(this);
                //all images display same img at the beginning
                imgBtn.setBackground(cardBack_img);
                imgBtn.setId(100*rows+cols);
                imgBtn.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));

                imgBtn.setOnClickListener(new ImgButtonListener());

                try {
                    tblGameBoardRow.addView(imgBtn);
                }catch (NullPointerException ex){
                    Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
                }



            }

            firstCard=null;
            loadImgBoard(num_rows,num_columns);
        }

    }


    public void createImgArray() {
        arrOfImages = new ArrayList<>();
        /*images*/
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard1));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard2));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard3));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard4));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard5));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard6));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard7));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard8));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard9));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard10));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard11));
        arrOfImages.add(this.getDrawable(R.drawable.img_mcard12));
        /*images*/
        shuffleImg();

    }
    public void shuffleImg(){
        long seed = System.nanoTime();
        Collections.shuffle(arrOfImages, new Random(seed));
        long seed2 = System.nanoTime();
        Collections.shuffle(arrOfImages, new Random(seed2));
    }
    class ImgButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            synchronized (lock) {
                if(!isGameStart){
                    isGameStart = true;
                    startTimer(true);
                }
                if(firstCard!=null && secondCard != null){
                    return;
                }
                int id = v.getId();
                int x = id/100;
                int y = id%100;
                turnCard((ImageButton)v,x,y);
            }

        }

        private void turnCard(ImageButton imageButton, int x, int y) {
            try {
                imageButton.setBackground(arrOfImages.get(imgBoard[x][y]));
            }catch (NullPointerException exc){
                Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
            }

            if(firstCard==null){
                firstCard = new ImgCard(imageButton,x,y);
            }
            else {

                if (firstCard.x == x && firstCard.y == y) {
                    return; //the user pressed the same card
                }

                secondCard = new ImgCard(imageButton, x, y);


                TimerTask timerT = new TimerTask() {

                    @Override
                    public void run() {
                        try{
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(GameBoardActivity.this,R.string.failed_to_compare_cards, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(timerT, 700);

            }
        }

    }

    //inner class the cards
    class ImgCard {

        int x;
        int y;
        ImageButton button;

        public ImgCard(ImageButton button, int x,int y) {
            this.x = x;
            this.y=y;
            this.button=button;
        }

        public ImageButton getButton() {
            return button;
        }
    }

    private void loadImgBoard(int rows , int cols){

        int size = rows*cols;

        ArrayList<Integer> list = new ArrayList<>();

        for(int i=0;i<size;i++){
            list.add(new Integer(i));
        }


        Random rnd = new Random();

        if(rows == cols) {
            for (int i = size - 1; i >= 0; i--) {
                int t = 0;

                if (i > 0) {
                    t = rnd.nextInt(i);
                }

                t = list.remove(t).intValue();
                try {
                    imgBoard[i % cols][i / cols] = t % (size / 2);
                }catch (IndexOutOfBoundsException ex){
                    Toast.makeText(GameBoardActivity.this, R.string.failed_to_get_resource, Toast.LENGTH_SHORT).show();
                }


            }
        }else{
            for (int i = size - 1; i >= 0; i--) {
                int t = 0;

                if (i > 0) {
                    t = rnd.nextInt(i);
                }

                t = list.remove(t).intValue();
                try{
                    imgBoard[i % rows][i / rows] = t % (size / 2);
                }catch (IndexOutOfBoundsException ex){
                    Toast.makeText(GameBoardActivity.this, R.string.failed_to_get_resource, Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    class UpdateCardsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }
        public void checkCards(){
            if(imgBoard[secondCard.x][secondCard.y] == imgBoard[firstCard.x][firstCard.y]){
                try {
                    firstCard.getButton().setEnabled(false);
                    secondCard.getButton().setEnabled(false);
                }catch (NullPointerException ex){
                    Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
                }

                numberOfRequiredMatches--;
                if(numberOfRequiredMatches == 0){
                    //end of the game
                    startTimer(false);
                    Date date = new Date();
                    try {
                        db.insertScore(""+avatar_name.getText(),number_rows+"x"+number_columns,countSeconds,date.getTime()+"",clickCounter);
                    }catch (SQLiteTableLockedException exc){
                        Toast.makeText(GameBoardActivity.this,R.string.score_saving_fail,Toast.LENGTH_SHORT).show();
                    }
                    endGameActivityDialog ();
                }
            }
            else {
               /* secondCard.button.setBackgroundDrawable(cardBack_img);
                firstCard.button.setBackgroundDrawable(cardBack_img);*/
                try {
                    secondCard.button.setBackground(cardBack_img);
                    firstCard.button.setBackground(cardBack_img);
                }catch (NullPointerException ex){
                    Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
                }
            }
            firstCard=null;
            secondCard=null;
        }
    }

    public String timerUpAsString(){
        String counterAsText;
        counterAsText = String.format("%d:%02d", countSeconds / 60, countSeconds % 60);
        return counterAsText;
    }
    public void readFromPreferences(String case_s) throws NullPointerException{
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        switch(case_s){
            case "name":
                String defaultNameValue = getResources().getString(R.string.name_key_default);
                avatar_name.setText(sharedPref.getString(getString(R.string.name_key),defaultNameValue));
                break;
            case "rowSize":
                int defaultRowsValue = getResources().getInteger(R.integer.row_key_default);
                number_rows = sharedPref.getInt(getString(R.string.row_key), defaultRowsValue);
                break;
            case "colSize":
                int defaultColsValue = getResources().getInteger(R.integer.col_key_default);
                number_columns = sharedPref.getInt(getString(R.string.col_key), defaultColsValue);
                break;
        }

    }
    public void  endGameActivityDialog (){

        Context context = this;
        View promptView = null;
        AlertDialog.Builder alertDialogBuilder = null;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        try {
            promptView = layoutInflater.inflate(R.layout.end_game_dialog_layout, null);
            alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptView);
        }catch (NullPointerException ex){
            Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }

        try {
            TextView winner_userName = (EditText) promptView.findViewById(R.id.end_game_name);
            TextView winner_time = (EditText) promptView.findViewById(R.id.end_game_time);
            winner_time.setText(timerUpAsString());
            winner_userName.setText("" + avatar_name.getText());
        }catch (NullPointerException ex){
            Toast.makeText(GameBoardActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        {
                            Intent intent = new Intent(GameBoardActivity.this ,MainLobbyActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .setNegativeButton("Replay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(GameBoardActivity.this ,GameBoardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}



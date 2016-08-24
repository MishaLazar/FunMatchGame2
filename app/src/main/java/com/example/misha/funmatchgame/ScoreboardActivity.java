package com.example.misha.funmatchgame;

import android.app.Activity;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Misha on 8/2/2016.
 */
public class ScoreboardActivity extends Activity {

    final String DEFAULT_LVL_STR = "2x2";
    TableLayout gameScoreboard;
    Button back_btn;
    final int NUM_OF_SCORES = 10;
    final int NUM_OF_HEADERS = 3;
    final String [] headers = {"Name" , "Time" , "Game Level"};
    Spinner simpleSpinner;
    ArrayList<Score> scores;
    Boolean isFirst = true;
    Boolean isSpinnerSelected = false;


    //DBHelper
    DBHelper db;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_score_board_activity);

        db = new DBHelper(ScoreboardActivity.this);

        gameScoreboard  =  (TableLayout)findViewById(R.id.scoreboard);


        populateTable();
        selectGameLevel();

        back_btn = (Button)findViewById(R.id.back_toLobby_btn);
        try{
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
        }catch (NullPointerException ex){
            Toast.makeText(ScoreboardActivity.this, R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }





    }
    public void init_headers(){
        //first row is a header
        try {
            TableRow tblScoreboardHeaderRow = new TableRow(this);
            tblScoreboardHeaderRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            gameScoreboard.addView(tblScoreboardHeaderRow);
            TextView header_name = new TextView(ScoreboardActivity.this);
            header_name.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f));
            header_name.setText(headers[0]);

            TextView header_time = new TextView(ScoreboardActivity.this);
            header_time.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f));
            header_time.setText(headers[1]);
            TextView header_level = new TextView(ScoreboardActivity.this);
            header_level.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f));
            header_level.setText(headers[2]);
            tblScoreboardHeaderRow.addView(header_name);
            tblScoreboardHeaderRow.addView(header_time);
            tblScoreboardHeaderRow.addView(header_level);
        }catch (NullPointerException ex){
            Toast.makeText(ScoreboardActivity.this, R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
    }
    public void populateTable(){
        if(!isFirst) {
            String selectedLevel = simpleSpinner.getSelectedItem().toString();
            scores = selectTop10Scores(selectedLevel);
        }else {
            scores = selectTop10Scores(DEFAULT_LVL_STR);
            isFirst = false;
        }
        clearTableContent();
        init_headers();
        for (int rows = 0 ; rows  < NUM_OF_SCORES && rows < scores.size(); rows++ ){
            //create new rows as needed and add it to table

            TableRow tblGameBoardRow = new TableRow(this);
            try {
                tblGameBoardRow.setGravity(Gravity.LEFT);
                tblGameBoardRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        1.0f));
                gameScoreboard.addView(tblGameBoardRow);
                gameScoreboard.setGravity(Gravity.LEFT);

                TextView name = new TextView(ScoreboardActivity.this);
                name.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));
                name.setText(scores.get(rows).name);
                name.setGravity(Gravity.LEFT);
                TextView time = new TextView(ScoreboardActivity.this);
                time.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));
                time.setText(scores.get(rows).ScoreTime);
                time.setGravity(Gravity.LEFT);
                TextView level = new TextView(ScoreboardActivity.this);
                level.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));
                level.setText(scores.get(rows).level);
                level.setGravity(Gravity.LEFT);


                tblGameBoardRow.addView(name);
                tblGameBoardRow.addView(time);
                tblGameBoardRow.addView(level);
            }catch (NullPointerException ex){
                Toast.makeText(ScoreboardActivity.this, R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
            }


        }
    }
    public void clearTableContent(){
        try {
            gameScoreboard.removeAllViews();
        }catch (NullPointerException ex){
            Toast.makeText(ScoreboardActivity.this, R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }

    }
    public ArrayList<Score> selectTop10Scores(String level){
        ArrayList<String> scoreList = null;
        try {
            scoreList = db.getTop10ScoresByLevel(level);
        }catch (SQLiteBindOrColumnIndexOutOfRangeException | SQLiteDatabaseCorruptException ex2){

            Toast.makeText(ScoreboardActivity.this, R.string.score_board_data_fail,Toast.LENGTH_SHORT).show();
        }
        ArrayList<Score> top10Scores = new ArrayList<>();

        try {
            for (int i=0;i<scoreList.size();i++){
                String tempArr [] =  scoreList.get(i).split(":");
                Score score = new Score(tempArr[0],
                        timerUpAsString(Integer.parseInt(tempArr[1])),
                        tempArr[2]);
                top10Scores.add(score);
            }
        }catch (NullPointerException ex){
            Toast.makeText(ScoreboardActivity.this, R.string.score_board_data_fail,Toast.LENGTH_SHORT).show();
        }

        return top10Scores;
    }

    class Score {

        String name;
        String ScoreTime;
        String level;

        public Score(String name, String scoreTime, String level) {
            this.name = name;
            ScoreTime = scoreTime;
            this.level = level;
        }
    }
    public String timerUpAsString(int countSeconds){
        String counterAsText = "00:00";
        if(countSeconds > 0) {
            counterAsText = String.format("%d:%02d", countSeconds / 60, countSeconds % 60);
        }
        return counterAsText;
    }

    public void selectGameLevel (){
        try{
            simpleSpinner = (Spinner)findViewById(R.id.score_level_selector);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(
                    this, R.array.type, R.layout.spinner_item2);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            simpleSpinner.setAdapter(adapter);
        }catch (NullPointerException ex){
            Toast.makeText(ScoreboardActivity.this, R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }

        simpleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(
                    android.widget.AdapterView<?> arg0,
                    View arg1, int pos, long arg3) {
                isSpinnerSelected = true;
                simpleSpinner.setSelection(pos);
                populateTable();

               /* Spinner caseSpinner = ((Spinner) findViewById(R.id.score_level_selector));
                caseSpinner.setSelection(0);*/

 /*               switch (pos) {
                    case 1:
//                        ((Spinner) findViewById(R.id.score_level_selector)).setSelection(1);
                        simpleSpinner.setSelection(pos);
                        populateTable();
                        break;
                    case 2:
//                        ((Spinner) findViewById(R.id.score_level_selector)).setSelection(2);
                        simpleSpinner.setSelection(pos);
                        populateTable();
                        break;
                    case 3:
//                        ((Spinner) findViewById(R.id.score_level_selector)).setSelection(3);
                        simpleSpinner.setSelection(pos);
                        populateTable();
                        break;
                    case 4:
//                        ((Spinner) findViewById(R.id.score_level_selector)).setSelection(4);
                        simpleSpinner.setSelection(pos);
                        populateTable();
                        break;
                    case 5:
//                        ((Spinner) findViewById(R.id.score_level_selector)).setSelection(5);
                        simpleSpinner.setSelection(pos);
                        populateTable();
                        break;
                    default:
                        return;
                }


*/            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

}

package com.example.misha.funmatchgame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Locale;


public class MainLobbyActivity extends AppCompatActivity {

    ///user name
    EditText userName;
    final int USER_NAME_MAX_LENGTH = 15;

    //DBHelper
    DBHelper db;

    //general
    int rowSize = -1;
    int columnSize = -1;

    //lobby buttons
    Button playBtn;
    Button scoreBtn;


    //dialog
    Spinner spinner;
    View promptView;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lobby);

        localeEnforce();

        //db handler
        db = new DBHelper(MainLobbyActivity.this);


        init_Btn();

    }
    public void localeEnforce(){
        Configuration config = new Configuration();
        config.locale = Locale.ENGLISH;
        super.onConfigurationChanged(config);
        Locale.setDefault(config.locale);
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public void  userNameDialog (){


        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainLobbyActivity.this);
        try {
            promptView = layoutInflater.inflate(R.layout.activity_dialog_user_name2, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainLobbyActivity.this);
            alertDialogBuilder.setView(promptView);

            selectGameLevel();

            userName = (EditText) promptView.findViewById(R.id.user_name2);
            userName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(USER_NAME_MAX_LENGTH)});
            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String name = userName.getText().toString() + "";
                            if ((!name.equals("") || userName != null) && (!spinner.getSelectedItem().toString().equals(getString(R.string.level_select_btn)))) {
                                Intent intent = new Intent(MainLobbyActivity.this, GameBoardActivity.class);
                                commitToShared(getString(R.string.case_name));
                                commitToShared(getString(R.string.case_rows));
                                commitToShared(getString(R.string.case_cols));
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainLobbyActivity.this, R.string.name_lvl_msg, Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create an alert dialog
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }catch (NullPointerException ex){
            Toast.makeText(MainLobbyActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
    }


    public void init_Btn(){
        // init all the buttons in the main lobby view
        playBtn = (Button)findViewById(R.id.play_btn);
        scoreBtn = (Button)findViewById(R.id.score_btn);

        ButtonListener buttonListener = new ButtonListener();
        try {
            playBtn.setOnClickListener(buttonListener);
            scoreBtn.setOnClickListener(buttonListener);
        }catch (NullPointerException ex){
            Toast.makeText(MainLobbyActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch(v.getId()){
                case R.id.play_btn:
                    userNameDialog ();
                    break;
                case R.id.score_btn:
                    Intent intent = new Intent(MainLobbyActivity.this, ScoreboardActivity.class);
                    startActivity(intent);
                    break;

            }


        }
    }



    public void selectGameLevel (){

        try {
            spinner = (Spinner) promptView.findViewById(R.id.level_select2);
            ArrayAdapter adapter = ArrayAdapter.createFromResource(
                    this, R.array.type, R.layout.spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(
                        android.widget.AdapterView<?> arg0,
                        View arg1, int pos, long arg3) {


//                ((Spinner) promptView.findViewById(R.id.level_select2)).setSelection(0);
                    spinner.setSelection(pos);
                    switch (pos) {
                        case 1:
//                        ((Spinner)promptView.findViewById(R.id.level_select2)).setSelection(1);
                            rowSize    = 2;
                            columnSize = 2;
                            break;
                        case 2:
//                        ((Spinner) promptView.findViewById(R.id.level_select2)).setSelection(2);
                            rowSize    = 2;
                            columnSize = 3;
                            break;
                        case 3:
//                        ((Spinner) promptView.findViewById(R.id.level_select2)).setSelection(3);
                            rowSize    = 4;
                            columnSize = 3;
                            break;
                        case 4:
                            ((Spinner) promptView.findViewById(R.id.level_select2)).setSelection(4);
                            rowSize    = 4;
                            columnSize = 4;
                            break;
                        case 5:
//                        ((Spinner) promptView.findViewById(R.id.level_select2)).setSelection(5);
                            rowSize    = 6;
                            columnSize = 4;
                            break;
                        default:
                            return;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (NullPointerException ex){
            Toast.makeText(MainLobbyActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }


    }

    public void commitToShared(String case_s){
        Context context = this;
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            switch (case_s) {
                case "name":
                    editor.putString(getString(R.string.name_key), userName.getText().toString());
                    break;
                case "rowSize":
                    editor.putInt(getString(R.string.row_key), rowSize);
                    break;
                case "colSize":
                    editor.putInt(getString(R.string.col_key), columnSize);
                    break;

            }
            editor.commit();
        }catch (NullPointerException ex){
            Toast.makeText(MainLobbyActivity.this,R.string.failed_to_get_resource,Toast.LENGTH_SHORT).show();
        }
    }

}

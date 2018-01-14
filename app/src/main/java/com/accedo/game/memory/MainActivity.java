package com.accedo.game.memory;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accedo.game.memory.database.DatabaseController;
import com.accedo.game.memory.database.EntityUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvCurrentScore)
    TextView tvCurrentScore;

    @BindView(R.id.btn_topscore)
    Button btnHighScore;

    private static final String CARD_VIEW_ID = "ivCard_";

    private static final String IMAGE_NAME = "colour";

    private List<String> card_list;

    private static final int SIZE = 16;

    private List<ImageButton> button_card_list;

    private int[] PICKS;

    private int pickCount;

    private int cardsLeft; //pairs of cards left

    private int score;

    private DatabaseController databaseController;

    private boolean lock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        init();
    }

    private void init() {

        initVariables();
        initViews();
        initCardList();
        initCardViews();

    }

    private void initVariables() {
        databaseController = DatabaseController.getInstance(getApplicationContext());

        score = 0;
        PICKS = new int[2];
        resetPicks();
        pickCount = 0;
        cardsLeft = SIZE / 2;
    }

    private void resetPicks() {
        PICKS[0] = -1;
        PICKS[1] = -1;
    }


    private void initCardList() {
        card_list = new ArrayList<String>(SIZE);

        for (int i = 1; i <= SIZE / 2; i++) {
            card_list.add(IMAGE_NAME + i);
            card_list.add(IMAGE_NAME + i);
        }

        Collections.shuffle(card_list);
    }

    private void initCardViews() {
        button_card_list = new ArrayList<>(16);

        for (int i = 0; i < SIZE; i++) {
            ImageButton btnCard = findViewById(getResources().getIdentifier(CARD_VIEW_ID + i, "id",
                    this.getPackageName()));
            btnCard.setImageResource(R.drawable.card_bg);
            button_card_list.add(btnCard);

            final int index = i;
            btnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnClick(index);

                }
            });
        }

    }

    private synchronized void btnClick(int index) {

        if ((PICKS[0] == index || PICKS[1] == index) || lock) {
            return;
        }

        int id = getResources().getIdentifier(card_list.get(index), "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        button_card_list.get(index).setImageDrawable(drawable);

        PICKS[pickCount ^= 1] = index;

        if (pickCount == 0) {
            new AsyncTask<Void, Void, Void>() {

                int PICK1;
                int PICK2;

                @Override
                protected Void doInBackground(Void... voids) {

                    lock = true;
                    PICK1 = PICKS[0];
                    PICK2 = PICKS[1];


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    resetPicks();

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    if (card_list.get(PICK1).equals(card_list.get(PICK2))) {
                        score += 2;
                        cardsLeft--;
                        setCardBackNull(button_card_list.get(PICK1));
                        setCardBackNull(button_card_list.get(PICK2));
                        removeCardListener(button_card_list.get(PICK1));
                        removeCardListener(button_card_list.get(PICK2));

                    } else {
                        score -= 1;
                        setCardBack(button_card_list.get(PICK1));
                        setCardBack(button_card_list.get(PICK2));
                    }

                    tvCurrentScore.setText(score + "");

                    if (cardsLeft == 0) {
                        showDialogForHighScore();
                    }
                    lock = false;
                }
            }.execute();
        }
    }

    private void initViews() {
        tvCurrentScore.setText(score + "");

        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                startActivity(intent);

            }
        });
    }

    private void setCardBack(ImageButton btnCard) {
        btnCard.setImageResource(R.drawable.card_bg);
    }

    private void setCardBackNull(ImageButton btnCard) {
        btnCard.setImageResource(0);
    }

    private void removeCardListener(ImageButton btnCard) {
        btnCard.setOnClickListener(null);
    }


    private void showDialogUserCurrentRank(final int rank){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle(R.string.current_rank_title);
        builder.setMessage(String.format(getString(R.string.current_rank_msg),score,rank));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                init();
            }
        });
        builder.show();
    }

    private void showDialogForHighScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.newname_dialog, null);

        final EditText name = view.findViewById(R.id.name);

        builder.setView(view)
                .setTitle(R.string.enter_name_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String strName = name.getText().toString();
                        if (TextUtils.isEmpty(strName.trim())) {
                            showDialogForHighScore();
                        } else {
                            new AsyncTask<Void, Void, Integer>() {
                                @Override
                                protected Integer doInBackground(Void... voids) {
                                    databaseController.addUser(name.getText().toString(), score);

                                    List<EntityUser> users = databaseController.getAllUsers();

                                    int rank = getRank(users, strName, score);

                                    return rank;
                                }

                                @Override
                                protected void onPostExecute(Integer rank) {
                                    super.onPostExecute(rank);

                                    showDialogUserCurrentRank(rank);
                                }

                                private int getRank(List<EntityUser> users, String name, int score){
                                    int rank = 0;

                                    for(int i=0; i<users.size(); i++){
                                        EntityUser user = users.get(i);
                                        if(user.name.equals(name) && user.score == score){
                                            rank = i+1;
                                        }
                                    }

                                    return rank;

                                }
                            }.execute();

                        }
                    }
                });
        builder.setCancelable(false);
        builder.show();

    }

}

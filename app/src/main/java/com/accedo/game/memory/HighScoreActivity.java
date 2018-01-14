package com.accedo.game.memory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.accedo.game.memory.adapter.HighScoreAdapter;
import com.accedo.game.memory.database.DatabaseController;
import com.accedo.game.memory.database.EntityUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nathaniel on 14/1/18.
 */

public class HighScoreActivity extends AppCompatActivity {

    private HighScoreAdapter highScoreAdapter;

    DatabaseController databaseController;

    @BindView(R.id.lvScores)
    ListView lvScores;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_highscore);

        ButterKnife.bind(this);

        initToolBar();

        databaseController = DatabaseController.getInstance(getApplicationContext());
        highScoreAdapter = new HighScoreAdapter(this);


        new AsyncTask<Void, Void, List<EntityUser>>() {
            @Override
            protected List<EntityUser> doInBackground(Void... voids) {

                List<EntityUser> userList = databaseController.getHighScoreUser();

                for (int i = 0; i < userList.size(); i++) {
                    EntityUser user = userList.get(i);
                    int rank = i + 0;
                    Log.d("userList[" + i + "]", "rank:[" + rank + "]" + user.name + ":" + user.score);
                }
                return userList;
            }

            @Override
            protected void onPostExecute(List<EntityUser> userList) {
                super.onPostExecute(userList);

                highScoreAdapter.addAll(userList);
                lvScores.setAdapter(highScoreAdapter);
                highScoreAdapter.notifyDataSetChanged();

            }
        }.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

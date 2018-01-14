package com.accedo.game.memory.database;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

/**
 * Created by nathaniel on 14/1/18.
 */

public class DatabaseController {

    private static final String DATABASE_NAME = "USER_DATABASE";

    private static DatabaseController ourInstance;

    private AppDatabase database;

    private UserDao userDao;

    public static DatabaseController getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new DatabaseController(context);
        }
        return ourInstance;
    }

    private DatabaseController(Context context) {
        database = Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME).build();
        userDao = database.userDao();
    }

    public List<EntityUser> getHighScoreUser() {
        return userDao.getHighScoreUsers();
    }

    public List<EntityUser> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void addUser(String name, int score) {
        EntityUser user = new EntityUser();
        user.name = name;
        user.score = score;

        userDao.insert(user);
    }


}

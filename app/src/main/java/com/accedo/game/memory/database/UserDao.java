package com.accedo.game.memory.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by nathaniel on 14/1/18.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM EntityUser ORDER BY score DESC")
    List<EntityUser> getAllUsers();

    @Query("SELECT * FROM EntityUser ORDER BY score DESC LIMIT 10")
    List<EntityUser> getHighScoreUsers();

    @Insert
    void insert(EntityUser entityUser);

    @Delete
    void delete(EntityUser entityUser);
}

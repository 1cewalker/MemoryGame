package com.accedo.game.memory.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by nathaniel on 14/1/18.
 */

@Entity
public class EntityUser {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int score;
}

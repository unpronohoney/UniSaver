package com.unisaver.unisaver;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GradingSystemEntity.class, GradeMappingEntity.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static volatile AppDataBase INSTANCE;

    public abstract GradingSystemDao gradingSystemDao();

    public static AppDataBase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDataBase.class, "grading_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

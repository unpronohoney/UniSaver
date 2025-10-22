package com.unisaver.unisaver;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grading_systems")
public class GradingSystemEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "isSelected")
    public boolean isSelected;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "isDefault")
    public boolean isDefault;
}

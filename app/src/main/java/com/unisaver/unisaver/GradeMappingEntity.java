package com.unisaver.unisaver;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "grade_mappings",
        foreignKeys = @ForeignKey(
                entity = GradingSystemEntity.class,
                parentColumns = "id",
                childColumns = "systemId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("systemId")}
)
public class GradeMappingEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "systemId")
    public int systemId;

    @ColumnInfo(name = "grade")
    public String grade;

    @ColumnInfo(name = "value")
    public double value;

    public GradeMappingEntity(int systemId, String grade, double value) {
        this.systemId = systemId;
        this.grade = grade;
        this.value = value;
    }
}

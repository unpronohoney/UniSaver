package com.unisaver.unisaver;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GradingSystemDao {
    @Query("SELECT * FROM grading_systems")
    List<GradingSystemEntity> getAllSystems();

    @Query("SELECT * FROM grade_mappings WHERE systemId = :systemId")
    List<GradeMappingEntity> getMappingsForSystem(int systemId);

    @Insert
    long insertSystem(GradingSystemEntity system);

    @Insert
    void insertMappings(List<GradeMappingEntity> mappings);

    @Update
    void updateMapping(GradeMappingEntity mapping);

    @Update
    void updateSystems(List<GradingSystemEntity> systems);

    @Delete
    void deleteSystem(GradingSystemEntity system);

    @Delete
    void deleteMapping(GradeMappingEntity mapping);

    @Insert
    void insertMapping(GradeMappingEntity mapping);
}

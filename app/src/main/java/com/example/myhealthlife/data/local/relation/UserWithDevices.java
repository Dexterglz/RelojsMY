package com.example.myhealthlife.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.myhealthlife.data.local.entity.DeviceEntity;
import com.example.myhealthlife.data.local.entity.UserEntity;

import java.util.List;

public class UserWithDevices {

    @Embedded
    public UserEntity user;

    @Relation(
            parentColumn = "id",
            entityColumn = "user_id"
    )
    public List<DeviceEntity> devices;
}


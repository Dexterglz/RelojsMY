package com.example.myhealthlife.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserEntity {
    @PrimaryKey
    @NonNull
    public String idUsuario;

    public String nombre;
    public String primerApellido;
    public String segundoApellido;
    public String correo;
    public String fechaNacimiento;
    public boolean genero;
    public String fotoUrl;
}

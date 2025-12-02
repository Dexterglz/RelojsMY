package com.example.myhealthlife.model;

import com.google.gson.annotations.SerializedName;

public class Usuario {
    @SerializedName("FK_ROL")
    private int rol;

    @SerializedName("FL_CONTRASENA")
    private String contrasena;

    @SerializedName("FL_CORREO")
    private String correo;

    @SerializedName("FL_FECHANACIMIENTO")
    private String fechaNacimiento;

    @SerializedName("FL_GENERO")
    private String genero;

    @SerializedName("FL_NOMBRE")
    private String nombre;

    @SerializedName("FL_PRIMERAPELLIDO")
    private String primerApellido;

    @SerializedName("FL_SEGUNDOAPELLIDO")
    private String segundoApellido;

    @SerializedName("FL_TELEFONO")
    private String telefono;

    @SerializedName("FL_CURP")
    private String curp;

    public Usuario(int FK_ROL, String FL_CONTRASENA, String FL_CORREO, String FL_FECHANACIMIENTO,
                   String FL_GENERO, String FL_NOMBRE, String FL_PRIMERAPELLIDO,
                   String FL_SEGUNDOAPELLIDO, String FL_TELEFONO, String FL_CURP) {
        this.rol = FK_ROL;
        this.contrasena = FL_CONTRASENA;
        this.correo = FL_CORREO;
        this.fechaNacimiento = FL_FECHANACIMIENTO;
        this.genero = FL_GENERO;
        this.nombre = FL_NOMBRE;
        this.primerApellido = FL_PRIMERAPELLIDO;
        this.segundoApellido = FL_SEGUNDOAPELLIDO;
        this.telefono = FL_TELEFONO;
        this.curp = FL_CURP;
    }

    // Getters y setters para todos los campos
}

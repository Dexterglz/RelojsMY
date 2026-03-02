package com.example.myhealthlife.data.local.repository.mapper;

import com.example.myhealthlife.data.local.entity.UserEntity;
import com.example.myhealthlife.io.response.UserApiModel;

public class UserMapper {
        public static UserEntity mapToEntity(UserApiModel apiUser) {

                if (apiUser == null || apiUser.getIdUsuario() == null) {
                throw new IllegalStateException("ID_USUARIO viene null desde la API");
                }

                UserEntity entity = new UserEntity();

                entity.idUsuario = apiUser.getIdUsuario();
                entity.nombre = apiUser.getNombre();
                entity.primerApellido = apiUser.getPrimerApellido();
                entity.segundoApellido = apiUser.getSegundoApellido();
                entity.correo = apiUser.getCorreo();
                entity.fechaNacimiento = apiUser.getFechaNacimiento();
                entity.genero = apiUser.isGenero();
                entity.fotoUrl = apiUser.getFotoUrl();

                return entity;
    }
}


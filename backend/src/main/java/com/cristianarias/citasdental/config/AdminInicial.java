package com.cristianarias.citasdental.config;

import com.cristianarias.citasdental.entity.Rol;
import com.cristianarias.citasdental.entity.Usuario;
import com.cristianarias.citasdental.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Al arrancar, si todavía no existe ningún usuario, se crea el administrador inicial
 * con los datos de ADMIN_EMAIL y ADMIN_PASSWORD del archivo .env.
 * Así no queda ninguna contraseña escrita en el código ni en los scripts SQL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInicial implements ApplicationRunner {

    private static final int LONGITUD_MINIMA = 12;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nombre:Administrador}")
    private String nombre;

    @Value("${app.admin.email:}")
    private String email;

    @Value("${app.admin.password:}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            log.warn("No hay usuarios registrados. Se deben definir ADMIN_EMAIL y ADMIN_PASSWORD en el archivo .env");
            return;
        }
        if (password.length() < LONGITUD_MINIMA) {
            log.warn("No se creó el administrador: ADMIN_PASSWORD debe tener al menos {} caracteres", LONGITUD_MINIMA);
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre.trim());
        admin.setEmail(email.trim().toLowerCase());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);

        log.info("Se creó el usuario administrador inicial: {}", admin.getEmail());
    }
}

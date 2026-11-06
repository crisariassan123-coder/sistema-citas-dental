package com.cristianarias.citasdental.service;

import com.cristianarias.citasdental.dto.LoginRequest;
import com.cristianarias.citasdental.dto.LoginResponse;
import com.cristianarias.citasdental.entity.Usuario;
import com.cristianarias.citasdental.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    // Se usa el mismo mensaje si el correo no existe o si la contraseña es incorrecta,
    // para no revelar qué correos están registrados (enumeración de usuarios)
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String hashDeRelleno;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        // Hash que se compara cuando el correo no existe, para que la respuesta tarde lo mismo
        // y no se pueda deducir por el tiempo si un correo está registrado
        this.hashDeRelleno = passwordEncoder.encode("relleno-para-tiempo-constante");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Optional<Usuario> encontrado = usuarioRepository.findByEmailIgnoreCase(req.email().trim());

        String hash = encontrado.map(Usuario::getPasswordHash).orElse(hashDeRelleno);
        boolean contrasenaCorrecta = passwordEncoder.matches(req.password(), hash);

        Usuario usuario = encontrado
                .filter(u -> contrasenaCorrecta && u.isActivo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS));

        TokenService.TokenGenerado token = tokenService.generar(usuario);
        return new LoginResponse(token.valor(), "Bearer", token.expiraEn(), usuario.getNombre(), usuario.getRol());
    }
}

package com.dedsec.TheDedSec.auth;


import com.dedsec.TheDedSec.bean.Role;
import com.dedsec.TheDedSec.bean.User;
import com.dedsec.TheDedSec.config.JwtService;
import com.dedsec.TheDedSec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutheticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
    User user= User.builder()
                    .userName(request.getUserName())
                            .email(request.getEmail())
                                    .name(request.getFirstName()+" "+request.getLastName())
                                            .password(passwordEncoder.encode(request.getPassword()))
                                                    .role(Role.USER)
                                                            .build();
    repository.save(user);
    String jwtToken=jwtService.generateToken(user);

    return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();

    }

    public AuthenticationResponse  authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );
        User user=repository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Error during fetching from db"));
        String jwtToken=jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}

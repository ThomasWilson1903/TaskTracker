package ru.twilson.tasktracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.twilson.tasktracker.api.AuthenticationApi;
import ru.twilson.tasktracker.model.AuthResponseToken;
import ru.twilson.tasktracker.utils.JwtTokenUtil;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.model.AuthResponse;
import ru.twilson.tasktracker.model.LoginRequest;
import ru.twilson.tasktracker.model.RegisterRequest;
import ru.twilson.tasktracker.service.ConsumerService;
import ru.twilson.tasktracker.utils.ConsumerMapper;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthenticationApi {

    private final ConsumerMapper mapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final ConsumerService consumerService;

    @Override
    public ResponseEntity<AuthResponse> registerUser(RegisterRequest registerRequest) {
        Consumer consumer = consumerService.add(mapper.toConsumer(registerRequest));
        AuthResponse response = new AuthResponse()
                .user(mapper.toUserResponse(consumer))
                .token(jwtTokenUtil.generateToken(consumer.getUsername()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthResponseToken> loginUser(LoginRequest loginRequest) {
        if (consumerService.isExists(loginRequest.getUsername(), loginRequest.getPassword())) {
            return new ResponseEntity<>(new AuthResponseToken()
                    .token(
                            jwtTokenUtil.generateToken(loginRequest.getUsername())
                    ), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}

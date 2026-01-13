package ru.twilson.tasktracker.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import ru.twilson.tasktracker.api.AuthenticationApi;
import ru.twilson.tasktracker.model.*;
import ru.twilson.tasktracker.model.Error;
import ru.twilson.tasktracker.utils.JwtTokenUtil;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.service.ConsumerService;
import ru.twilson.tasktracker.utils.ConsumerMapper;

@Slf4j
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
        consumerService.isEnable(loginRequest.getUsername());
        Consumer consumer = consumerService.findConsumer(loginRequest.getUsername(), loginRequest.getPassword());
        return new ResponseEntity<>(new AuthResponseToken()
                .token(
                        jwtTokenUtil.generateToken(loginRequest.getUsername())
                )
                .user(mapper.toUserResponse(consumer))
                , HttpStatus.OK);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> entityNotFoundExceptionResponse(RuntimeException exception) {
        log.info(exception.getMessage());
        return new ResponseEntity<>(new Error("некорректный запрос", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    //todo #19 Добавить блокировку пользователя и привилегии
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> accessDeniedResponse(AccessDeniedException exception) {
        log.info(exception.getMessage());
        return new ResponseEntity<>(new Error("запрещено", exception.getMessage()), HttpStatus.FORBIDDEN);
    }
    //todo #18 лимит на количество запросов /login
}

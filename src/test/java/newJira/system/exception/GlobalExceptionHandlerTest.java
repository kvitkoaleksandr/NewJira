package newJira.system.exception;

import newJira.system.dto.ErrorDto;
import newJira.system.exception.custom.BadRequestException;
import newJira.system.exception.custom.ForbiddenException;
import newJira.system.exception.custom.NotFoundException;
import newJira.system.exception.custom.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Обработка NotFoundException")
    void handleNotFoundExceptionTest() {
        String message = "Объект не найден";
        NotFoundException exception = new NotFoundException(message);

        ResponseEntity<ErrorDto> response = handler.handleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
        assertNotNull(response.getBody().getId());
    }

    @Test
    @DisplayName("Обработка BadRequestException")
    void handleBadRequestExceptionTest() {
        String message = "Некорректный запрос";
        BadRequestException exception = new BadRequestException(message);

        ResponseEntity<ErrorDto> response = handler.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Обработка UnauthorizedException")
    void handleUnauthorizedExceptionTest() {
        String message = "Пользователь не авторизован";
        UnauthorizedException exception = new UnauthorizedException(message);

        ResponseEntity<ErrorDto> response = handler.handleUnauthorized(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Обработка ForbiddenException")
    void handleForbiddenExceptionTest() {
        String message = "Доступ запрещён";
        ForbiddenException exception = new ForbiddenException(message);

        ResponseEntity<ErrorDto> response = handler.handleForbidden(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    @DisplayName("Обработка непредвиденного исключения")
    void handleGenericExceptionTest() {
        Exception exception = new RuntimeException("Что-то пошло не так");

        ResponseEntity<ErrorDto> response = handler.handleAllUnhandledExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Internal server error"));
    }
}
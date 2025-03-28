package newJira.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import newJira.system.dto.CommentDto;
import newJira.system.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Создание нового комментария")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @Operation(summary = "Получение комментариев по ID задачи")
    @SecurityRequirement(name = "BearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарии успешно получены"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/task/{taskId}")
    public List<CommentDto> getCommentsByTaskId(@PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }
}
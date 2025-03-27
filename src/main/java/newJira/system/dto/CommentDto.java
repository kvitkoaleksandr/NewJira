package newJira.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import newJira.system.entity.AppUser;
import newJira.system.entity.Task;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Task task;
    private AppUser author;
}
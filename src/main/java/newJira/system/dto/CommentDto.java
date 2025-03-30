package newJira.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import newJira.system.entity.AppUser;
import newJira.system.entity.Task;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto implements Serializable {
    private Long id;
    private String text;
    private Long taskId;
    private Long authorId;
}
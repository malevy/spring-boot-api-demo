package net.malevy.hyperdemo.models.dataaccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.malevy.hyperdemo.models.domain.Task;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;

@Entity()
@Table(name = "TASK")
@Data()
@NoArgsConstructor()
@AllArgsConstructor
public class TaskDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String description;

    private String importance;

    private LocalDate due;

    @Column(name = "completedon")
    private LocalDate completedOn;

    private String owner;

    @PrePersist
    void pre() {
        if (StringUtils.isEmpty(this.importance)) {
            this.importance = Task.Importance.NORMAL.toString();
        }
    }

}

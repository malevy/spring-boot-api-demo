package net.malevy.hyperdemo.models.dataaccess;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity(name="TASK")
@Data()
@AllArgsConstructor()
public class TaskDto {

    @Id
    private Integer id;

    private String title;

    private String description;

    private String importance;

    private LocalDate due;

    private LocalDate completedOn;
}

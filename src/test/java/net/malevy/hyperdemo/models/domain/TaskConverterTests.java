package net.malevy.hyperdemo.models.domain;

import java.lang.IllegalArgumentException;
import java.time.LocalDate;

import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class TaskConverterTests {

    @Test()
    public void whenDtoIsMissing_throw() {

        assertThrows(IllegalArgumentException.class, () -> TaskConverter.fromDto(null));
    }

    @Test()
    public void givenGoodDto_ConvertToTask() {
        TaskDto dto = new TaskDto(0, "A", "B",
                Task.Importance.HIGH.toString(),
                LocalDate.of(2017, 11, 4),
                LocalDate.of(2017, 11, 5),
                "jack");

        Task t = TaskConverter.fromDto(dto);
        assertEquals("the id is not correct", dto.getId(), t.getId());
        assertEquals("the title is not correct", dto.getTitle(), t.getTitle());
        assertEquals("the description is not correct", dto.getDescription(), t.getDescription());
        assertEquals("the importance is not correct", Task.Importance.HIGH, t.getImportance());
        assertEquals("the due date is wrong", dto.getDue(), t.getDue());
        assertEquals("the completed date is wrong", dto.getCompletedOn(), t.getCompletedOn());
        assertEquals("the owner is wrong", dto.getOwner(), t.getOwner());
    }

    @Test()
    public void missingImportance_convertedToNormal() {
        TaskDto dto = new TaskDto(0, "A", "B",
                null,
                LocalDate.of(2017, 11, 4),
                LocalDate.of(2017, 11, 5),
                "jack");

        Task t = TaskConverter.fromDto(dto);
        assertEquals("the importance is not correct", Task.Importance.NORMAL, t.getImportance());

    }

    @Test()
    public void givenTaskIsMissing_throw() {

        assertThrows(IllegalArgumentException.class, () -> TaskConverter.toDto(null));
    }

    @Test
    public void whenGivenATask_convertToDto() {
        Task t = new Task(1, "the-title", "the-description",
                Task.Importance.HIGH,
                LocalDate.of(2017, 11, 25),
                LocalDate.of(2017, 11, 24),
                "jack");

        TaskDto dto = TaskConverter.toDto(t);

        assertEquals("the id is wrong", t.getId(), dto.getId());
        assertEquals("the title is wrong", t.getTitle(), dto.getTitle());
        assertEquals("the description is wrong", t.getDescription(), dto.getDescription());
        assertEquals("the importance is wrong", t.getImportance().toString(), dto.getImportance());
        assertEquals("the due date is wrong", t.getDue(), dto.getDue());
        assertEquals("the completed date is wrong", t.getCompletedOn(), dto.getCompletedOn());
        assertEquals("the owner is wrong", t.getOwner(), dto.getOwner());
    }

}

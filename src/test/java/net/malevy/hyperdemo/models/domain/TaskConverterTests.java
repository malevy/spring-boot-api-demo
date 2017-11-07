package net.malevy.hyperdemo.models.domain;

import java.lang.IllegalArgumentException;
import java.time.LocalDate;

import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TaskConverterTests {

    @Test(expected=IllegalArgumentException.class)
    public void whenDtoIsMissing_throw() {
        TaskConverter.fromDto(null);
    }

    @Test()
    public void givenGoodDto_ConvertToTask() {
        TaskDto dto = new TaskDto(0, "A", "B", Task.Importance.HIGH.toString(),
                LocalDate.of(2017, 11, 4), LocalDate.of(2017, 11, 5));

        Task t = TaskConverter.fromDto(dto);
        assertEquals("the id is not correct", dto.getId(), t.getId());
        assertEquals("the title is not correct", dto.getTitle(), t.getTitle());
        assertEquals("the description is not correct", dto.getDescription(), t.getDescription());
        assertEquals("the importance is not correct", Task.Importance.HIGH, t.getImportance());
        assertEquals("the due date is wrong", dto.getDue(), t.getDue());
        assertEquals("the completed date is wrong", dto.getCompletedOn(), t.getCompletedOn());
    }

    @Test()
    public void missingImportance_convertedToNormal() {
        TaskDto dto = new TaskDto(0, "A", "B", null,
                LocalDate.of(2017, 11, 4), LocalDate.of(2017, 11, 5));

        Task t = TaskConverter.fromDto(dto);
        assertEquals("the importance is not correct", Task.Importance.NORMAL, t.getImportance());

    }
}

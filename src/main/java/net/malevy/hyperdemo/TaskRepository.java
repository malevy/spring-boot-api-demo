package net.malevy.hyperdemo;

import net.malevy.hyperdemo.models.dataaccess.TaskDto;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<TaskDto, Integer> {

    Optional<TaskDto> findById(Integer id);
}

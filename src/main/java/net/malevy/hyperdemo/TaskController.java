package net.malevy.hyperdemo;

import net.malevy.hyperdemo.commands.CommandFactory;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private final CommandFactory commandFactory;

    @Autowired
    public TaskController(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getTask(@PathVariable() Integer id, UriComponentsBuilder uriBuilder) {

        WstlMapper mapper = new WstlMapper(uriBuilder);
        GetSingleTaskCommand command = this.commandFactory.getCommand(GetSingleTaskCommand.class);

        Optional<Wstl> wstl = command.execute(id)
                .map(mapper::FromTask);

        return wstl.isPresent()
                ? ok(wstl.get())
                : notFound(id);

    }

    private ResponseEntity<Wstl> ok(Wstl wstl) {
        return new ResponseEntity<>(wstl, HttpStatus.OK);
    }

    private ResponseEntity<HttpProblem> notFound(Integer id) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("Task with id {%s} not found", id))
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return new ResponseEntity<>(problem, HttpStatus.NOT_FOUND);
    }

}

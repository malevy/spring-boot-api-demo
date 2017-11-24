package net.malevy.hyperdemo;

import net.malevy.hyperdemo.commands.CommandDispatcher;
import net.malevy.hyperdemo.commands.GetSingleTaskCommand;
import net.malevy.hyperdemo.commands.NoHandlerException;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private CommandDispatcher dispatcher;

    @Autowired
    public TaskController(CommandDispatcher dispatcher) {

        this.dispatcher = dispatcher;
    }

    @GetMapping(name = "task-gettask", path = "/{id}")
    public ResponseEntity<?> getTask(@PathVariable Integer id, UriComponentsBuilder uriBuilder) {

        WstlMapper mapper = new WstlMapper(uriBuilder);
        GetSingleTaskCommand command = new GetSingleTaskCommand(){{
            setId(id);
        }};

        try {
            Optional<Wstl> wstl = dispatcher.handle(command)
                    .map(mapper::FromTask);

            return wstl.isPresent()
                    ? ok(wstl.get())
                    : notFound(command.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    private ResponseEntity<Wstl> ok(Wstl wstl) {
        return new ResponseEntity<>(wstl, HttpStatus.OK);
    }

    private ResponseEntity<HttpProblem> notFound(Integer id) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("Task with id %s not found", id))
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return new ResponseEntity<>(problem, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<HttpProblem> serverError() {
        HttpProblem problem = HttpProblem.builder()
                .title("Unable to process request. the problem has been reported.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return new ResponseEntity<HttpProblem>(problem, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<HttpProblem> badRequest(String param) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("unable to process request. the required value '%s' was not provided", param))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return new ResponseEntity<>(problem, HttpStatus.BAD_REQUEST);
    }

}

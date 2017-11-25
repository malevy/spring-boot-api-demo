package net.malevy.hyperdemo;

import net.malevy.hyperdemo.commands.*;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Console;
import java.util.Optional;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private CommandDispatcher dispatcher;

    @Autowired
    public TaskController(CommandDispatcher dispatcher) {

        this.dispatcher = dispatcher;
    }

    @ExceptionHandler(NoHandlerException.class)
    ResponseEntity<HttpProblem> handleNoHandlerException(NoHandlerException nhe) {
        //TODO - should be logging this
        return this.serverError();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getTask(@PathVariable Integer id, UriComponentsBuilder uriBuilder) throws NoHandlerException {

        WstlMapper mapper = new WstlMapper(uriBuilder);
        GetSingleTaskCommand command = new GetSingleTaskCommand(){{
            setId(id);
        }};

            Optional<Wstl> wstl = dispatcher.handle(command)
                    .map(mapper::FromTask);

            return wstl.isPresent()
                    ? ok(wstl.get())
                    : notFound(command.getId());
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id) throws NoHandlerException {

        DeleteSingleTaskCommand command = new DeleteSingleTaskCommand(){{
            setId(id);
        }};

        String result = dispatcher.handle(command);

        return ok(result);
    }

    @PostMapping(path = "/{id}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Integer id, UriComponentsBuilder uriBuilder) throws NoHandlerException {

        MarkTaskCompleteCommand cmd = new MarkTaskCompleteCommand() {{
            setId(id);
        }};

        WstlMapper mapper = new WstlMapper(uriBuilder);
        Optional<Wstl> wstl = dispatcher.handle(cmd)
                .map(mapper::FromTask);

        return wstl.isPresent()
                ? ok(wstl.get())
                : notFound(cmd.getId());
    }

    private <T> ResponseEntity<T> ok(T content) {

        return ResponseEntity.ok(content);
    }

    private ResponseEntity<HttpProblem> notFound(Integer id) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("Task with id %s not found", id))
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        ResponseEntity<HttpProblem> response = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);

        return response;
    }

    private ResponseEntity<HttpProblem> serverError() {
        HttpProblem problem = HttpProblem.builder()
                .title("Unable to process request. the problem has been reported.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        ResponseEntity<HttpProblem> response = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);

        return response;
    }

    private ResponseEntity<HttpProblem> badRequest(String param) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("unable to process request. the required value '%s' was not provided", param))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        ResponseEntity<HttpProblem> response = ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(problem);

        return response;
    }
}

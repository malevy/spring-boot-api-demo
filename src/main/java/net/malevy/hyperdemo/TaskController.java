package net.malevy.hyperdemo;

import net.malevy.hyperdemo.commands.*;
import net.malevy.hyperdemo.models.domain.Task;
import net.malevy.hyperdemo.models.viewmodels.TaskInputVM;
import net.malevy.hyperdemo.support.HttpProblem;
import net.malevy.hyperdemo.support.westl.Action;
import net.malevy.hyperdemo.support.westl.Wstl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {

    private final CommandDispatcher dispatcher;

    @Autowired
    public TaskController(CommandDispatcher dispatcher) {

        this.dispatcher = dispatcher;
    }

    @ExceptionHandler(NoHandlerException.class)
    ResponseEntity<HttpProblem> handleNoHandlerException(NoHandlerException nhe) {
        //TODO - should be logging this
        return this.serverError();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<HttpProblem> handleArgumentNotValidException(MethodArgumentNotValidException manv) {

        HttpProblem validationErrors = HttpProblem.builder()
                .title("validation errors")
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        manv.getBindingResult().getFieldErrors()
                .forEach(e -> validationErrors.getAdditional().put(e.getField(), e.getDefaultMessage()));

        return this.badRequest(validationErrors);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getTask(@PathVariable Integer id,
                                     UriComponentsBuilder uriBuilder,
                                     Authentication authN) throws NoHandlerException {

        User user = (User) authN.getPrincipal();

        WstlMapper mapper = new WstlMapper(uriBuilder);
        GetSingleTaskCommand command = new GetSingleTaskCommand(user){{
            setId(id);
        }};

            Optional<Wstl> wstl = dispatcher.handle(command)
                    .map(mapper::fromTask);

            return wstl.isPresent()
                    ? ok(wstl.get())
                    : notFound(command.getId());
    }

    @DeleteMapping(path="/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id,
                                        Authentication authN) throws NoHandlerException {

        User user = (User) authN.getPrincipal();

        DeleteSingleTaskCommand command = new DeleteSingleTaskCommand(user){{
            setId(id);
        }};

        String result = dispatcher.handle(command);

        return ok(result);
    }

    @PostMapping(path = "/{id}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Integer id,
                                          UriComponentsBuilder uriBuilder,
                                          Authentication authN) throws NoHandlerException {

        User user = (User) authN.getPrincipal();

        MarkTaskCompleteCommand cmd = new MarkTaskCompleteCommand(user) {{
            setId(id);
        }};

        WstlMapper mapper = new WstlMapper(uriBuilder);
        Optional<Wstl> wstl = dispatcher.handle(cmd)
                .map(mapper::fromTask);

        return wstl.isPresent()
                ? ok(wstl.get())
                : notFound(cmd.getId());
    }

    @GetMapping()
    public ResponseEntity<?> getTasks(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer pageSize,
            UriComponentsBuilder uriBuilder) throws NoHandlerException {

        GetTasksCommand cmd = new GetTasksCommand(page, pageSize);
        WstlMapper mapper = new WstlMapper(uriBuilder);
        Wstl wstl = mapper.fromPageOfTasks(dispatcher.handle(cmd));

        // if there are no tasks, an empty collection should be returned
        return ok(wstl);
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id,
                                        @Valid @RequestBody TaskInputVM taskInput,
                                        UriComponentsBuilder uriBuilder) throws NoHandlerException {

        UpdateTaskCommand cmd = new UpdateTaskCommand(id, taskInput);

        WstlMapper mapper = new WstlMapper(uriBuilder);
        Optional<Wstl> wstl;
        try {
            wstl = dispatcher.handle(cmd)
                    .map(mapper::fromTask);
        } catch (IllegalArgumentException argsException) {
            return badRequest(argsException.getMessage());
        }

        return wstl.isPresent()
                ? ok(wstl.get())
                : notFound(cmd.getId());

    }

    @PostMapping()
    public ResponseEntity<?> addTask(@Valid @RequestBody TaskInputVM taskInput,
                                     UriComponentsBuilder uriBuilder,
                                     Authentication authN) throws NoHandlerException {

        User user = (User) authN.getPrincipal();

        final AddTaskCommand command = new AddTaskCommand(user, taskInput);
        final WstlMapper mapper = new WstlMapper(uriBuilder);
        try {
            final Task task = dispatcher.handle(command);
            final Wstl wstl = mapper.fromTask(task);

            final URI location = wstl.getSelf()
                    .map(Action::getHref)
                    .orElse(null);

            return created(wstl, location);
        } catch (IllegalArgumentException argsException) {
            return badRequest(argsException.getMessage());
        }

    }

    private <T> ResponseEntity<T> ok(T content) {
        return ResponseEntity.ok(content);
    }

    private <T> ResponseEntity<T> created(T content, URI location) {
        return ResponseEntity.created(location)
                .body(content);
    }

    private ResponseEntity<HttpProblem> notFound(Integer id) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("Task with id %s not found", id))
                .status(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(new MediaType("application", "problem+json"))
                .body(problem);
    }

    private ResponseEntity<HttpProblem> serverError() {
        HttpProblem problem = HttpProblem.builder()
                .title("Unable to process request. the problem has been reported.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(new MediaType("application", "problem+json"))
                .body(problem);
    }

    private ResponseEntity<HttpProblem> badRequest(String message) {

        HttpProblem problem = HttpProblem.builder()
                .title(String.format("unable to process request. %s", message))
                .status(HttpStatus.BAD_REQUEST.value())
                .build();

        return badRequest(problem);
    }

    private ResponseEntity<HttpProblem> badRequest(HttpProblem problem) {

        return ResponseEntity
                .badRequest()
                .contentType(new MediaType("application", "problem+json"))
                .body(problem);
    }
}

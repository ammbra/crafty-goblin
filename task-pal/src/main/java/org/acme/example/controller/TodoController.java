package org.acme.example.controller;

import jakarta.transaction.Transactional;
import org.acme.example.TodoException;
import org.acme.example.repository.TodoRepository;
import org.acme.example.model.Todo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@RestController
@RequestMapping(value = "/api/todo")
public class TodoController {

	private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

	private final TodoRepository repository;

	@Autowired
	public TodoController(TodoRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(method = RequestMethod.OPTIONS)
	public ResponseEntity<String> opt() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<Todo> getAll() {
		try {
			return repository.findAll();
		} catch (Exception e) {
			throw new TodoException("Nothing found", e);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Todo getOne(@PathVariable("id") UUID id) {
		Optional<Todo> entity = repository.findTodoByID(id);
		if (entity.isEmpty()) {
			throw new TodoException("Todo with id of " + id + " does not exist.");
		}
		return entity.get();
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Todo> create(@RequestBody Todo task) {
		try {
			task.setId(UUID.randomUUID());
			Todo entity = repository.save(task);
			return ResponseEntity.status(HttpStatus.CREATED).body(entity);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Todo> update(@PathVariable("id") UUID id, @RequestBody Todo task) {
		try {
			Optional<Todo> entity = repository.findTodoByID(id);
			if (entity.isPresent()) {
				return ResponseEntity.ok(repository.save(task));
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			logger.error("Edit errors: ", e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

	}

	@RequestMapping(method = RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<String> deleteCompleted() {
		repository.deleteAllByCompleted(true);
		return new ResponseEntity<>("All completed todos were deleted", HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteOne(@PathVariable("id") UUID id) {
		try {
			repository.deleteById(id);
			String message = String.format("Entity with id %s was deleted", id);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error occurred during delete: ", e);
			throw new TodoException("Error occurred during delete: ", e);
		}
	}

}

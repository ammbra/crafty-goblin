package org.acme.example;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.*;

import org.acme.example.controller.TodoController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.acme.example.repository.TodoRepository;
import org.acme.example.model.Todo;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TodoController.class)
public class TodoApplicationTest {
	static final String MOCK_CONTENT = "Mock Item";
	final Map<UUID, Todo> repository = new HashMap<>();
	final Todo itemA = new Todo(UUID.randomUUID(), MOCK_CONTENT + "-A", true);
	final Todo itemB = new Todo(UUID.randomUUID(), MOCK_CONTENT + "-B", false);

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TodoRepository todoRepository;

	@BeforeEach
	public void setUp() {
		repository.clear();
		repository.put(itemA.getId(), itemA);
		repository.put(itemB.getId(), itemB);

		given(this.todoRepository.save(any(Todo.class))).willAnswer((InvocationOnMock invocation) -> {
			final Todo item = invocation.getArgument(0);
			repository.put(item.getId(), item);
			return item;
		});

		given(this.todoRepository.findTodoByID(any(UUID.class))).willAnswer((InvocationOnMock invocation) -> {
			final UUID id = invocation.getArgument(0);
			return Optional.of(repository.get(id));
		});

		given(this.todoRepository.findAll()).willAnswer((InvocationOnMock invocation) -> new ArrayList<Todo>(repository.values()));

		willAnswer((InvocationOnMock invocation) -> {
			final UUID id = invocation.getArgument(0);
			if (!repository.containsKey(id)) {
				throw new Exception("Not Found.");
			}
			repository.remove(id);
			return null;
		}).given(this.todoRepository).deleteById(any(UUID.class));
	}


	@Test
	public void canGetAllItems() throws Exception {
		mockMvc.perform(get("/api/todo")).andDo(print()).andExpect(status().isOk()).andExpect(content()
				.json(String.format("[{\"id\":\"%s\"}, {\"id\":\"%s\"}]", itemA.getId(), itemB.getId())));
	}

	@Test
	public void canSaveItem() throws Exception {
		final int size = repository.size();
		final Todo mockItemC = new Todo(null, MOCK_CONTENT + "-C", false);
		String json = STR. """
                { 
                    "title": "\{ mockItemC.getContent() }",
                    "completed": "\{ mockItemC.isCompleted() }"
                }
                """ ;
		mockMvc.perform(post("/api/todo").contentType(MediaType.APPLICATION_JSON_VALUE).content(json))
				.andDo(print()).andExpect(status().isCreated());
		assertEquals(size + 1, repository.size());
	}

	@Test
	public void canDeleteATodoItem() throws Exception {
		final int size = repository.size();
		System.out.println(size);
		mockMvc.perform(delete(String.format("/api/todo/%s", itemA.getId()))).andDo(print())
				.andExpect(status().isOk());
		System.out.println("size is" + repository.size());
		assertEquals(size - 1, repository.size());
		assertFalse(repository.containsKey(itemA.getId()));
	}

	@Test
	public void canUpdateTodoItem() throws Exception {
		String json = STR. """
                { 
                    "id": "\{ itemA.getId() }",
                    "title": "\{ itemA.getContent() }",
                    "completed": "\{ !itemA.isCompleted() }"
                }
                """ ;
		mockMvc.perform(patch(String.format("/api/todo/%s", itemA.getId())).contentType(MediaType.APPLICATION_JSON_VALUE).content(json))
				.andDo(print()).andExpect(status().isOk());
		assertFalse(repository.get(itemA.getId()).isCompleted());
	}

	@Test
	public void canNotDeleteNonExistingItem() throws Exception {
		final int size = repository.size();
		mockMvc.perform(delete(String.format("/api/todo/%s", "Non-Existing-ID"))).andDo(print())
				.andExpect(status().is4xxClientError());
		assertEquals(size, repository.size());
	}

	@AfterEach
	public void tearDown() {
		repository.clear();
	}
}

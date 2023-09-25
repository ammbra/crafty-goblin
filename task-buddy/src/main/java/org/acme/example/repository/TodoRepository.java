package org.acme.example.repository;

import org.acme.example.model.Todo;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

	@Query("SELECT t FROM Todo t WHERE t.id = ?1")
	Optional<Todo> findTodoByID(UUID id);

	void deleteAllByCompleted(boolean completed);
}

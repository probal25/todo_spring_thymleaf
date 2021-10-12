package com.probal.todoapp.repository;

import com.probal.todoapp.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    Long countAllByCompleted(boolean completed);
    List<TodoItem> findAllByCompleted(boolean completed);
}

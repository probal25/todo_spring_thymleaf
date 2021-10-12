package com.probal.todoapp.controller;

import com.probal.todoapp.dto.TodoItemDto;
import com.probal.todoapp.dto.TodoItemFormData;
import com.probal.todoapp.enums.ListFilter;
import com.probal.todoapp.exception.TodoItemNotFountException;
import com.probal.todoapp.model.TodoItem;
import com.probal.todoapp.repository.TodoItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;

    public TodoItemController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping()
    public String index(Model model) {
        addAttributesForIndex(model, ListFilter.ALL);
        return "index";
    }

    @GetMapping("/active")
    public String indexForActive(Model model) {
        addAttributesForIndex(model, ListFilter.ACTIVE);
        return "index";
    }

    @GetMapping("/completed")
    public String indexForCompleted(Model model) {
        addAttributesForIndex(model, ListFilter.COMPLETED);
        return "index";
    }

    public void addAttributesForIndex(Model model, ListFilter filter) {
        model.addAttribute("item", new TodoItemFormData());
        model.addAttribute("filter", filter);
        model.addAttribute("todos", getTodoItems(filter));
        model.addAttribute("totalNumberOfItems", todoItemRepository.count());
        model.addAttribute("numberOfActiveItems", getNumberOfActiveItems());
        model.addAttribute("numberOfCompletedItems", getNumberOfCompletedItems());
    }

    private Long getNumberOfCompletedItems() {
        return todoItemRepository.countAllByCompleted(true);
    }


    @PostMapping
    public String addNewTodoItem(@Valid @ModelAttribute("item") TodoItemFormData formData) {
        todoItemRepository.save(new TodoItem(formData.getTitle(), false));
        return "redirect:/";
    }

    @PutMapping("/{id}/toggle")
    public String toggleSelection(@PathVariable("id") Long id) throws TodoItemNotFountException {
        TodoItem todoItem = todoItemRepository.findById(id).orElseThrow(() -> new TodoItemNotFountException("No Item found with this " + id + ""));
        todoItem.setCompleted(true);
        todoItemRepository.save(todoItem);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable("id") Long id) {
        if (id != null) {
            todoItemRepository.deleteById(id);
        }
        return "redirect:/";
    }
    @DeleteMapping("/clear_completed")
    public String deleteCompletedItems() {
        List<TodoItem> itemList = todoItemRepository.findAllByCompleted(true);
        for (TodoItem item : itemList) {
            todoItemRepository.deleteById(item.getId());
        }
        return "redirect:/";
    }

    @GetMapping("/mark_all_as_completed")
    public String markAllAsCompleted() {
        List<TodoItem> todoItems = todoItemRepository.findAll();
        for (TodoItem todoItem : todoItems) {
            todoItem.setCompleted(true);
            todoItemRepository.save(todoItem);
        }
        return "redirect:/";
    }

    private List<TodoItemDto> getTodoItems(ListFilter filter) {
        switch (filter) {
            case ALL:
                return convertToDto(todoItemRepository.findAll());
            case ACTIVE:
                return convertToDto(todoItemRepository.findAllByCompleted(false));
            case COMPLETED:
                return convertToDto(todoItemRepository.findAllByCompleted(true));
        }
        return null;
    }

    private List<TodoItemDto> convertToDto(List<TodoItem> todoItems) {
        return todoItems
                .stream()
                .map(todoItem ->
                        new TodoItemDto(
                                todoItem.getId(),
                                todoItem.getTitle(),
                                todoItem.isCompleted()))
                .collect(Collectors.toList());
    }

    private Long getNumberOfActiveItems() {
        return todoItemRepository.countAllByCompleted(false);
    }

}

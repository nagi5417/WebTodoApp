package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Task;

public interface TaskService {

	List<Task> findAll(int userId);

	Optional<Task> getTask(int id);

	void insert(Task task);

	void update(Task task);

	void deleteById(int id);

	List<Task> findByType(int typeId);

	List<Task> findByUserId(int userId);

}

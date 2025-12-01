package com.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.taskflow.entity.User;
import com.taskflow.entity.Task;
import com.taskflow.repo.UserRepository;
import com.taskflow.repo.TaskRepository;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class TaskflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskflowApplication.class, args);
	}
	@Bean
	CommandLineRunner run(UserRepository userRepo, TaskRepository taskRepo) {
		return args -> {
			User u = User.builder()
					.name("Alice")
					.email("alice@example.com")
					.password("123456")
					.build();
			userRepo.save(u);

			Task t1 = Task.builder()
					.title("Finish Project")
					.description("Complete TaskFlow backend")
					.priority("HIGH")
					.dueDate(LocalDate.now().plusDays(3))
					.completed(false)
					.user(u)
					.build();

			taskRepo.save(t1);

			List<Task> tasks = taskRepo.findByUser(u);
			tasks.forEach(task -> System.out.println(task.getTitle()));
		};
	}
}

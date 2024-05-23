package course.work.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import course.work.project.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{
	User findByUsername(String username);
}

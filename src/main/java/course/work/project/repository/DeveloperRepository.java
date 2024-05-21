package course.work.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import course.work.project.entity.Developer;

public interface DeveloperRepository extends JpaRepository<Developer,Long> {
	
}

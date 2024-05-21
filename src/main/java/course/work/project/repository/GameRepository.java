package course.work.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import course.work.project.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long>{
	
}

package course.work.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import course.work.project.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long>{
	List<Game> findByDeveloperIdAndGenreId(Long developerId, Long genreId);
	List<Game> findByGenreId(Long genreId);
	List<Game> findByDeveloperId(Long developerId);
}

package course.work.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import course.work.project.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre,Long>{
	
}

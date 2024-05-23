package course.work.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import course.work.project.entity.Genre;
import course.work.project.repository.GenreRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class GenreController {
	@Autowired
	private  GenreRepository repository;
	@GetMapping("/admin/genres")
	public String getGenres(Model model) {
		model.addAttribute("genres", repository.findAll());
		return "genres";
	}
	
	@GetMapping("/admin/genres/new")
	public String newGenre(Model model) {
		model.addAttribute("genre", new Genre());
		return "new_genre";
	}
	
	@PostMapping("/admin/genres")
	public String createGenre(@ModelAttribute Genre genre) {
		repository.save(genre);
		return "redirect:/admin/genres";
	}
	
	@GetMapping("/admin/genres/{id}/edit")
	public String editGenre(@PathVariable Long id, Model model) {
		Genre genre = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Genre id: " + id));
		model.addAttribute("genre", genre);
		return "edit_genre";
	}
	
	@PostMapping("/admin/genres/{id}")
    public String updateGenre(@PathVariable Long id, @ModelAttribute Genre genre) {
        genre.setId(id);
        repository.save(genre);
        return "redirect:/admin/genres";
    }

	@GetMapping("/admin/genres/{id}/delete")
    public String deleteGenre(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin/genres";
    }
}

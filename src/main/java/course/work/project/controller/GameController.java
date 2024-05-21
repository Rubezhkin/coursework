package course.work.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import course.work.project.entity.Developer;
import course.work.project.entity.Game;
import course.work.project.entity.Genre;
import course.work.project.repository.DeveloperRepository;
import course.work.project.repository.GameRepository;
import course.work.project.repository.GenreRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class GameController {
	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private GenreRepository genreRepository;
	@Autowired
	private DeveloperRepository developerRepository;
	
	@GetMapping("/games")
	public String getGames(Model model) {
		model.addAttribute("games", gameRepository.findAll());
		return "games";
	}
	
	@GetMapping("/games/new")
	public String newGame(Model model) {
		model.addAttribute("game", new Game());
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "new_game";
	}
	
	@PostMapping("/games")
	public String createGame(@ModelAttribute Game game) {
		 Developer developer = developerRepository.findById(game.getDeveloper().getId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid developer Id:" + game.getDeveloper().getId()));
    Genre genre = genreRepository.findById(game.getGenre().getId())
        .orElseThrow(() -> new IllegalArgumentException("Invalid genre Id:" + game.getGenre().getId()));
    game.setDeveloper(developer);
    game.setGenre(genre);
    gameRepository.save(game);
		return "redirect:/games";
	}
	
	@GetMapping("/games/{id}/edit")
	public String editGame(@PathVariable Long id, Model model) {
		Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id));
		model.addAttribute("game", game);
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "edit_game";
	}
	
	@PostMapping("/games/{id}")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game game) {
        game.setId(id);
        gameRepository.save(game);
        return "redirect:/games";
    }

	@GetMapping("/games/{id}/delete")
    public String deleteGame(@PathVariable Long id) {
        gameRepository.deleteById(id);
        return "redirect:/games";
    }
}


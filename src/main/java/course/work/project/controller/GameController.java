package course.work.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.tomcat.util.file.ConfigurationSource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

	static private String stringPath = "src\\main\\resources\\static\\files";
	@GetMapping("/games")
	public String getGames(@RequestParam(required = false) Long developerId, @RequestParam(required = false) Long genreId, Model model) {
		Iterable<Game> games = new ArrayList<>();
    	if (developerId != null && genreId != null) {
        	games = gameRepository.findByDeveloperIdAndGenreId(developerId, genreId);
    	} 
		else if (developerId != null) {
        	games = gameRepository.findByDeveloperId(developerId);
    	} 
		else if (genreId != null) {
        	games = gameRepository.findByGenreId(genreId);
    	} 
		else {
        	games = gameRepository.findAll();
    	}
		model.addAttribute("games", games);
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "games";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/games")
	public String getGamesAdmin(@RequestParam(required = false) Long developerId, @RequestParam(required = false) Long genreId, Model model) {
		Iterable<Game> games = new ArrayList<>();
    	if (developerId != null && genreId != null) {
        	games = gameRepository.findByDeveloperIdAndGenreId(developerId, genreId);
    	} 
		else if (developerId != null) {
        	games = gameRepository.findByDeveloperId(developerId);
    	} 
		else if (genreId != null) {
        	games = gameRepository.findByGenreId(genreId);
    	} 
		else {
        	games = gameRepository.findAll();
    	}
		model.addAttribute("games", games);
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "games_admin";
	}

	@GetMapping("/games/{id}")
	public String getGame(@PathVariable Long id, Model model) {
		Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id));
		model.addAttribute("game", game);
		return "game";
	}
	
	@GetMapping("/games/{id}/download")
	public ResponseEntity<InputStreamResource> downloadGame(@PathVariable Long id) throws FileNotFoundException {
    	Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id));
    	String fileName = game.getFileName();
    	File file = new File(stringPath + "\\" + fileName);
    	HttpHeaders headers = new HttpHeaders();
    	headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
    	InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    	return ResponseEntity.ok()
            .headers(headers)
            .contentLength(file.length())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/games/new")
	public String newGame(Model model) {
		model.addAttribute("game", new Game());
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "new_game";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/games")
	public String createGame(@ModelAttribute Game game, @RequestParam("file") MultipartFile file) throws IOException {

		Developer developer = developerRepository.findById(game.getDeveloper().getId())
        	.orElseThrow(() -> new IllegalArgumentException("Invalid developer Id:" + game.getDeveloper().getId()));

    	Genre genre = genreRepository.findById(game.getGenre().getId())
        	.orElseThrow(() -> new IllegalArgumentException("Invalid genre Id:" + game.getGenre().getId()));

    	game.setDeveloper(developer);
    	game.setGenre(genre);

		String fileName = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
		File directory = new File(stringPath);
		if (! directory.exists())
    		directory.mkdir();
    	Path path = Paths.get(stringPath + "\\" + fileName);
    	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    	game.setFileName(fileName);
    	gameRepository.save(game);
		return "redirect:/admin/games";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/games/{id}/edit")
	public String editGame(@PathVariable Long id, Model model) {
		Game game = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id));
		model.addAttribute("game", game);
		model.addAttribute("developers", developerRepository.findAll());
		model.addAttribute("genres", genreRepository.findAll());
		return "edit_game";
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/admin/games/{id}")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game game, @RequestParam("file") MultipartFile file) throws IOException {
        game.setId(id);
		String prevGamePath = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id)).getFileName();

		if (!file.isEmpty()) {
        	String fileName = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
        	Path path = Paths.get(stringPath + "\\" + fileName);
        	Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        	game.setFileName(fileName);
			Path pathPrevGame = Paths.get(stringPath + "\\" + prevGamePath);
			Files.delete(pathPrevGame);
    	} 
		else{
			game.setFileName(prevGamePath);
    	}
        gameRepository.save(game);
        return "redirect:/admin/games";
    }
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/admin/games/{id}/delete")
    public String deleteGame(@PathVariable Long id) throws IOException {
		String fileName = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Game id: " + id)).getFileName();
		Path filePath = Paths.get(stringPath + "\\" + fileName);
        gameRepository.deleteById(id);
		Files.delete(filePath);
        return "redirect:/admin/games";
    }
}


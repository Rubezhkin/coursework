package course.work.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import course.work.project.entity.Developer;
import course.work.project.repository.DeveloperRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class DeveloperController {
	@Autowired
	private DeveloperRepository repository;
	@GetMapping("/developers")
	public String getDevelopers(Model model) {
		model.addAttribute("developers", repository.findAll());
		return "developers";
	}
	
	@GetMapping("/developers/new")
	public String newDeveloper(Model model) {
		model.addAttribute("developer", new Developer());
		return "new_developer";
	}
	
	@PostMapping("/developers")
	public String createDeveloper(@ModelAttribute Developer developer) {
		repository.save(developer);
		return "redirect:/developers";
	}
	
	@GetMapping("/developers/{id}/edit")
	public String editDeveloper(@PathVariable Long id, Model model) {
		Developer developer = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid developer id: " + id));
		model.addAttribute("developer", developer);
		return "edit_developer";
	}
	
	@PostMapping("/developers/{id}")
    public String updateDeveloper(@PathVariable Long id, @ModelAttribute Developer developer) {
        developer.setId(id);
        repository.save(developer);
        return "redirect:/developers";
    }

	@GetMapping("/developers/{id}/delete")
    public String deleteDeveloper(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/developers";
    }
}

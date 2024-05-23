package course.work.project.controller;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import course.work.project.entity.Role;
import course.work.project.entity.User;
import course.work.project.repository.UserRepository;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class RegistrationController {
	@Autowired
	private UserRepository repository;

	@GetMapping("/registration")
	public String registration() {
		return "registration";
	}
	
	@PostMapping("/registration")
	public String addUser(User user, Model model) {
		User userFromDb = repository.findByUsername(user.getUsername());
		if(userFromDb != null){
			model.addAttribute("message", "User exists!");
			return "registration";
		}
		user.setActive(true);
		Set<Role> roles = new LinkedHashSet<>();
		roles.add(Role.USER);
		user.setRoles(roles);
		repository.save(user);
		return "redirect:/login";
	}
	
}

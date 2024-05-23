package course.work.project.controller;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import course.work.project.entity.Genre;
import course.work.project.entity.Role;
import course.work.project.entity.User;
import course.work.project.repository.GenreRepository;
import course.work.project.repository.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
	@Autowired
	private UserRepository repository;

	@GetMapping("/admin/users")
	public String getUsers(Model model) {
		model.addAttribute("users", repository.findAll());
		return "users";
	}

	@GetMapping("/admin/users/{id}/edit")
	public String editUser(@PathVariable Long id, Model model) {
		User user = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid User id: " + id));
		model.addAttribute("user", user);
		return "edit_user";
	}
	
	@PostMapping("/admin/users/{id}")
public String updateUser(@PathVariable Long id, @RequestParam("username") String username, @RequestParam(value = "isAdmin", required = false) boolean isAdmin) {
    User user = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid User id: " + id));
    user.setUsername(username);
	Set<Role> roles = new LinkedHashSet<>();
	roles.add(Role.USER);
    if(isAdmin)
		roles.add(Role.ADMIN);
	
	user.setRoles(roles);
    repository.save(user); // Сохраняем обновленного пользователя
    return "redirect:/admin/users";
}

	@GetMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/admin/users";
    }
}

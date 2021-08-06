package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;

@Controller
public class HomeController {

    ArrayList<Job> jobs = new ArrayList<>();
    static long id = 0;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;



    @RequestMapping("/")
    public String homePage(Model model, Principal principal){
        if (principal != null){
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            model.addAttribute("user", user);
        }
        model.addAttribute("jobs", jobs);
        return "jobHP";
    }

    @GetMapping("/addJob")
    public String addJob(Model model){
        model.addAttribute("job", new Job());
        return "jobForm";
    }

    @PostMapping("/processJob")
    public String processJob(@ModelAttribute Job job,
                             Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        job.setAuthor(user);
        jobs.add(job);
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login?logout=true";
    }


    @RequestMapping("/admin")
    public String admin () {
        return "admin";
    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        return "secure";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result, Model model){
        if (result.hasErrors()){
            user.clearPassword();
            model.addAttribute("user", user);
            return "register";
        }
        else {
            model.addAttribute("user", user);
            model.addAttribute("message", "New user account created");
            user.setEnabled(true);
            userRepository.save(user);

            Role role = new Role(user.getUsername(), "ROLE_USER");
            roleRepository.save(role);
        }
        return "index";
    }

    static void idSetter(Job job){
        id += 1;
        job.setId(id);
    }

}

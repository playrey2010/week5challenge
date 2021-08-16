package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;

@Controller
public class HomeController {
    static String test;
    static String SueTest;
    static ArrayList<Job> jobs = new ArrayList<>();
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
        Job job = new Job();
        idSetter(job);
        model.addAttribute("job", job);
        return "jobForm";
    }

    @PostMapping("/processJob")
    public String processJob(@ModelAttribute Job job,
                             Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        job.setAuthor(user);
        if (!jobExists(job.getId())){
            jobs.add(job);
//            System.out.println("me?");
        } else {
            jobs.remove(findJobById(job.getId()));
            jobs.add(job);
//            System.out.println(job.getTitle());
//            System.out.println(job.getId());
//            for (Job job1: jobs){
//                System.out.println(job1.getTitle());
//                System.out.println(job1.getId());
//            }
        }

        return "redirect:/";
    }

    @GetMapping("/updateJob/{id}")
    public String updateJob(@PathVariable long id, Model model, Principal principal){
        Job job = findJobById(id);
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        // automatically redirects if user does not match author id
        if (job.getAuthor().getId() != user.getId() ){
            return "redirect:/";
        }
        model.addAttribute("job", job);
        return "jobForm";
    }

    @RequestMapping("/viewJob/{id}")
    public String viewJob(@PathVariable long id, Model model){
        Job job = findJobById(id);
        model.addAttribute("job", job);
        return "viewJob";
    }

    @RequestMapping("/deleteJob/{id}")
    public String deleteJob(@PathVariable long id){
        jobs.remove(findJobById(id));
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

    static Job findJobById(long id){
        for (Job job: jobs){
            if (job.getId() == id){
                return job;
            }
        }
        return null;
    }

    static boolean jobExists(long id){
        boolean exists = false;
        for (Job job: jobs){
            if (job.getId() == id){
                exists = true;
                break;
            }
        }
        return exists;
    }

}

package com.edwinvanderwal.filewatcher.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edwinvanderwal.filewatcher.model.Deelnemer;
import com.edwinvanderwal.filewatcher.service.DeelnemerService;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class HomeController {

    private DeelnemerService deelnemerService;

    HomeController(DeelnemerService deelnemerService) {
        this.deelnemerService = deelnemerService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/search")
    public String getFinishers(Model model, @RequestParam String search) {
        List<Deelnemer> deelnemers = deelnemerService.getDeelnemerByStartnummer(search);
        model.addAttribute("results", deelnemers);
        return "search :: results";
    }
    
    
    
}

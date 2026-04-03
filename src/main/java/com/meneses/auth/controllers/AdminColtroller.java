package com.meneses.auth.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminColtroller {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Painel de administração";
    }

}

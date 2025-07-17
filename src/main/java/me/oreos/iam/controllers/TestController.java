package me.oreos.iam.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.Dtos.LoginDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@Tag(name = "Test", description = "Operations related to test in the IAM system")
@RequestMapping(value = "/api/v1/test")
@RestController
public class TestController {

    @GetMapping("/")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PostMapping("path")
    public String postMethodName(@RequestBody LoginDto entity) {
        //TODO: process POST request
        
        return "entity";
    }
    
    

}

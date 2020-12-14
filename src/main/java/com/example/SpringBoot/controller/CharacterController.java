package com.example.SpringBoot.controller;

import com.example.SpringBoot.form.CharacterForm;
import com.example.SpringBoot.model.Character;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Controller
public class CharacterController {

    private RestTemplate restTemplate = new RestTemplate();

    private Character[] characters;

    private final String CHAR_URI = "http://localhost:8081/personnages";


    //Init CharacterList
    private void InitList() {
        ResponseEntity<Character[]> response =
                restTemplate.getForEntity(CHAR_URI, Character[].class);
        characters = response.getBody();;
    }



    // Injectez (inject) via application.properties.
    @Value("${welcome.message}")
    private String message;

    @Value("${error.message}")
    private String errorMessage;

    @RequestMapping(value = { "/", "/index" }, method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("message", message);

        return "index";
    }

    @RequestMapping(value = { "/personList" }, method = RequestMethod.GET)
    public String personList(Model model) {

        InitList();
        model.addAttribute("characters", characters);

        return "personList";
    }


    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {

        CharacterForm characterForm = new CharacterForm();
        model.addAttribute("characterForm", characterForm);

        return "addPerson";
    }

    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.POST)
    public String savePerson(Model model, @ModelAttribute("personForm") CharacterForm characterForm) {

        String name = characterForm.getName();
        String type = characterForm.getType();

        if (name != null && name.length() > 0 && type != null && type.length() > 0) {
            InitList();
            Character newCharacter = new Character(characters.length + 1,name, type);
            ResponseEntity<Character> response = restTemplate.postForEntity(CHAR_URI, newCharacter, Character.class);

            return "redirect:/personList";
        }

        model.addAttribute("errorMessage", errorMessage);
        return "addPerson";
    }


    @GetMapping(value = "/updateForm/{id}")
    public String characterToUpdate(Model model, @PathVariable int id) {

        ResponseEntity<Character> response = restTemplate.getForEntity(CHAR_URI + "/"+ id, Character.class);
        Character characterToUpdate = response.getBody();

        model.addAttribute("character", characterToUpdate);
        return "updateForm";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteCharacter(@PathVariable int id) {

        final String URI = CHAR_URI + "/{id}";
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put("id", id);

        restTemplate.delete(URI, params);

        return "redirect:/personList";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String update(@ModelAttribute("personForm") Character character, @PathVariable int id) {

        restTemplate.put(CHAR_URI + "/" + id, character, Character.class);

        return "redirect:/personList";
    }
}

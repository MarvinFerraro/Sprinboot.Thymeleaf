package com.example.SpringBoot.controller;

import com.example.SpringBoot.form.PersonForm;
import com.example.SpringBoot.model.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PersonController {


    private static List<Person> persons = new ArrayList<Person>();

    static {
        persons.add(new Person(1, "Paul", "Sorcier"));
        persons.add(new Person(2,"Bob", "Guerrier"));
        persons.add(new Person(2,"JeanJaques", "Guerrier"));
    }

    private int counter = 4;

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

        model.addAttribute("persons", persons);

        return "personList";
    }

    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {

        PersonForm personForm = new PersonForm();
        model.addAttribute("personForm", personForm);

        return "addPerson";
    }

    @RequestMapping(value = { "/addPerson" }, method = RequestMethod.POST)
    public String savePerson(Model model, @ModelAttribute("personForm") PersonForm personForm) {

        String name = personForm.getName();
        String type = personForm.getType();

        if (name != null && name.length() > 0 && type != null && type.length() > 0) {
            Person newPerson = new Person(counter++ ,name, type);
            persons.add(newPerson);

            return "redirect:/personList";
        }

        model.addAttribute("errorMessage", errorMessage);
        return "addPerson";
    }
}

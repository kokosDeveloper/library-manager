package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dao.BookDAO;
import org.example.dao.PersonDAO;
import org.example.model.Book;
import org.example.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    @Autowired
    public BookController(BookDAO bookDAO, PersonDAO personDAO) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
    }

    @GetMapping
    public String index(Model model){
        model.addAttribute("books", bookDAO.index());
        return "books/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id,
                       Model model,
                       //для выпадающего списка
                       @ModelAttribute("person") Person person){
        model.addAttribute("book", bookDAO.show(id));
        Optional<Person> bookOwner = bookDAO.getBookOwner(id);
        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else model.addAttribute("people", personDAO.index());
        return "books/show";
    }
    @GetMapping("/new")
    public String newBook(@ModelAttribute("book")Book book){
        return "books/new";
    }
    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";
        bookDAO.save(book);
        return "redirect:/books";
    }
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id,
                       Model model){
        model.addAttribute("book", bookDAO.show(id));
        return "books/edit";
    }
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";
        bookDAO.update(id, book);
        return "redirect:/books";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        bookDAO.delete(id);
        return "redirect:/books";
    }
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id,
                         @ModelAttribute("person") Person selectedPerson){
        bookDAO.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id){
        bookDAO.release(id);
        return "redirect:/books/" + id;
    }
}

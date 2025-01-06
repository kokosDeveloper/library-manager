package org.example.controllers;

import jakarta.validation.Valid;
import org.example.models.Book;
import org.example.models.Person;
import org.example.services.BooksService;
import org.example.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BookController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear){
        if (page == null || booksPerPage == null)
            model.addAttribute("books", booksService.findAll(sortByYear));
        else model.addAttribute("books", booksService.findWithPagination(page, booksPerPage, sortByYear));
        return "books/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id,
                       Model model,
                       //для выпадающего списка
                       @ModelAttribute("person") Person person){
        model.addAttribute("book", booksService.findOne(id));
        Person bookOwner = booksService.getBookOwner(id);
        if (bookOwner != null)
            model.addAttribute("owner", bookOwner);
        else model.addAttribute("people", peopleService.findAll());
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
        booksService.save(book);
        return "redirect:/books";
    }
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id,
                       Model model){
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }
    @PatchMapping("/{id}")
    public String update(@PathVariable("id") int id,
                         @ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors())
            return "books/new";
        booksService.update(id, book);
        return "redirect:/books";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id){
        booksService.delete(id);
        return "redirect:/books";
    }
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id,
                         @ModelAttribute("person") Person selectedPerson){
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id){
        booksService.release(id);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }
    @PostMapping("/search")
    public String makeSearch(Model model,
                             @RequestParam("query") String query){
        model.addAttribute("books", booksService.searchByTitle(query));
        return "books/search";
    }
}

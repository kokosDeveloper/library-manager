package org.example.services;

import org.example.models.Book;
import org.example.models.Person;
import org.example.repositories.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public Book findOne(int id){
        Optional<Book> book = booksRepository.findById(id);
        return book.orElse(null);
    }
    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }
    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

    public List<Book> findAll(boolean sortByYear){
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else return booksRepository.findAll();
    }
    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear){
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    @Transactional
    public void update(int id, Book updatedBook){
        //нужна старая книга чтобы достать владельца
        Book bookToBeUpdated = booksRepository.findById(id).get();
        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner());
        booksRepository.save(updatedBook);
    }
    public Person getBookOwner(int id){
        //не нужен Hibernate.initialize, тк сторона One загружается жадно
        return booksRepository.findById(id).map(Book::getOwner).orElse(null);
    }
    @Transactional
    public void release(int id){
        booksRepository.findById(id).ifPresent(book -> {
            book.setOwner(null);
            book.setTakenAt(null);
        });
    }
    @Transactional
    public void assign(int id, Person selectedPerson){
        booksRepository.findById(id).ifPresent(book -> {
            book.setOwner(selectedPerson);
            book.setTakenAt(new Date());
        });
    }
    public List<Book> searchByTitle(String query){
        return booksRepository.findByTitleStartingWith(query);
    }
}

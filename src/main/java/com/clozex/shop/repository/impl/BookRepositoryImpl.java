package com.clozex.shop.repository.impl;

import com.clozex.shop.exception.DataProcessingException;
import com.clozex.shop.exception.EntityNotFoundException;
import com.clozex.shop.model.Book;
import com.clozex.shop.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    public BookRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can`t save book" + book);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Book ", Book.class)
                    .getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can`t fetch all books");
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery("FROM Book b where b.id = :id ",
                                Book.class)
                        .setParameter("id", id)
                        .getSingleResult());
        } catch (Exception e) {
            throw new EntityNotFoundException("Can`t get book by id: " + id);
        }
    }
}

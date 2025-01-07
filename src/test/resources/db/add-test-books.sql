DELETE FROM books_categories;
DELETE FROM books;
DELETE FROM categories;

INSERT INTO categories (id, name, is_deleted)
VALUES
    (1, 'category1', false),
    (2, 'category2', false),
    (3, 'category3', false);

INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES
    ('1', 'book1', 'author1', 'isbn1', 10.99, 'description1', 'cover_image1', false),
    ('2','book2', 'author2', 'isbn2', 20.99, 'description2', 'cover_image2', false),
    ('3','book3', 'author3', 'isbn3', 30.99, 'description3', 'cover_image3', false),
    ('4','book4', 'author4', 'isbn4', 40.99, 'description4', 'cover_image4', false),
    ('5','book5', 'author5', 'isbn5', 50.99, 'description5', 'cover_image5', false);

INSERT INTO books_categories (book_id, category_id)
VALUES
    (1, 2),
    (2, 1),
    (3, 1),
    (4, 3),
    (5, 1);
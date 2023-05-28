create table if not exists book_rating
(
    id     INT NOT NULL AUTO_INCREMENT,
    book_id INT,
    rating INT,
    review text,
    CONSTRAINT check_rating_range CHECK (rating BETWEEN 0 AND 5),


    PRIMARY KEY (ID)

);
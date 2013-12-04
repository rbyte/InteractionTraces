DROP TABLE sandbox;

CREATE TABLE sandbox (
	ID INT primary key,
	sand INT
);

INSERT INTO sandbox (ID, sand) VALUES
(1,1),
(2,2),
(3,3),
(4,4);


DROP TABLE soilbox;

CREATE TABLE soilbox (
	ID INT primary key,
	soil INT
);

INSERT INTO soilbox (ID, soil) VALUES
(2,2),
(4,4);


DROP TABLE glassbox;

CREATE TABLE glassbox (
	ID INT primary key,
	glass INT
);

INSERT INTO glassbox (ID, glass) VALUES
(4,4),
(5,5);


CREATE TABLE allBox (
	SELECT sandbox.ID, sandbox.sand, soilbox.soil, glassbox.glass FROM sandbox
	INNER JOIN soilbox USING (ID)
	INNER JOIN glassbox USING (ID)
)

CREATE TABLE id_author_isbn_pages_pubDate (
	ID VARCHAR(200) primary key,
	title VARCHAR(200),
	author VARCHAR(200),
	isbn BIGINT,
	pages BIGINT,
	pubDate INT
);


CREATE TABLE id_height_pages_subjects
(idx INT primary key, ID VARCHAR(200), subject VARCHAR(400));


CREATE TABLE id_openLibraryID
(ID VARCHAR(200) primary key, openlibrary_id VARCHAR(15));


CREATE TABLE id_subject_dewy_author
(ID VARCHAR(200) primary key, dewey_decimal_number INT);


CREATE TABLE id_genre_dewey
(idx INT primary key, ID VARCHAR(200), genre VARCHAR(200));


CREATE TABLE id_image_author_pubDate
(ID VARCHAR(200), image VARCHAR(20));

CREATE TABLE id_author_article
(ID VARCHAR(200), article VARCHAR(100));

CREATE TABLE meta_processed
(queryFileDone VARCHAR(100) primary key);

INSERT INTO meta_processed (queryFileDone) VALUES ("id_subject_dewy_author_0.json");

SELECT COUNT(*) FROM Sandbox.meta_processed WHERE queryFileDone="id_subject_dewy_author_0.json";


	
CREATE TABLE id_genre_subject (
	SELECT id_genre.ID, id_genre.genre, id_subject.subject FROM id_genre
	INNER JOIN id_subject USING (ID)
)

CREATE TABLE id_genre_subject_dewey (
	SELECT id_genre_subject.ID, id_genre_subject.genre, id_genre_subject.subject, id_dewey.dewey_decimal_number FROM id_genre_subject
	INNER JOIN id_dewey USING (ID)
)

CREATE TABLE id_genre_subject_dewey_article (
	SELECT id_genre_subject_dewey.ID, id_genre_subject_dewey.genre, id_genre_subject_dewey.subject, id_genre_subject_dewey.dewey_decimal_number, id_article.article FROM id_genre_subject_dewey
	INNER JOIN id_article USING (ID)
)

CREATE TABLE id_genre_subject_dewey_article_title_author_isbn_pages_pubDate (
	SELECT ID, genre, subject, dewey_decimal_number, article, id_title_author_isbn_pages_pubDate.title, id_title_author_isbn_pages_pubDate.author, id_title_author_isbn_pages_pubDate.isbn, id_title_author_isbn_pages_pubDate.pages, id_title_author_isbn_pages_pubDate.pubDate FROM id_genre_subject_dewey_article
	INNER JOIN id_title_author_isbn_pages_pubDate USING (ID)
)

CREATE TABLE id_ALL_expect_image (
	SELECT ID, genre, subject, dewey_decimal_number, article, title, author, isbn, pages, pubDate, id_openLibraryID.openlibrary_id FROM id_genre_subject_dewey_article_title_author_isbn_pages_pubDate
	INNER JOIN id_openLibraryID USING (ID)
)

CREATE TABLE id_ALL_except_image (
	SELECT ID, genre, subject, dewey_decimal_number, article, title, author, isbn, pages, pubDate, id_openLibraryID.openlibrary_id FROM id_genre_subject_dewey_article_title_author_isbn_pages_pubDate
	INNER JOIN id_openLibraryID USING (ID)
)

CREATE TABLE id_ALL_except_openLibraryID (
	SELECT ID, genre, subject, dewey_decimal_number, article, title, author, isbn, pages, pubDate, id_image.image FROM id_genre_subject_dewey_article_title_author_isbn_pages_pubDate
	INNER JOIN id_image USING (ID)
)

CREATE TABLE id_ALL (
	SELECT ID, genre, subject, dewey_decimal_number, article, title, author, isbn, pages, pubDate, image, id_openLibraryID.openlibrary_id FROM id_ALL_except_openLibraryID
	INNER JOIN id_openLibraryID USING (ID)
)



#==================



CREATE TABLE Merge1 (
	SELECT
		id,
		title,
		author,
		isbn,
		pages,
		pubDate,
		#id_openLibraryID.openLibraryID,
		id_subject_dewey.dewey,
		id_subject_dewey.subject,
		id_genre.genre,
		#id_image.image,
		id_article.article
	FROM id_title_author_isbn_pages_pubDate
	
	#INNER JOIN id_openLibraryID USING (id)
	INNER JOIN id_subject_dewey USING (id)
	INNER JOIN id_genre USING (id)
	#INNER JOIN id_image USING (id)
	INNER JOIN id_article USING (id)
)


SELECT COUNT(*) FROM (SELECT * FROM Merge1) AS t1

SHOW COLUMNS FROM Merge1;

SELECT table_name FROM INFORMATION_SCHEMA.TABLES
  WHERE table_name LIKE 'Merge1'

SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'Merge1'

SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Merge1'

SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Merge1'

SELECT t1.id FROM (SELECT * FROM Merge1) AS t1 LIMIT 1,30


CREATE TABLE Merge2 (
	SELECT
		id,
		title,
		author,
		isbn,
		pages,
		pubDate,
		dewey,
		subject,
		genre,
		article,
		id_openLibraryID.openLibraryID
	FROM Merge1
	
	INNER JOIN id_openLibraryID USING (id)
)

ALTER TABLE openLibrary CHANGE COLUMN openlibrary_id openLibraryID varchar(200);

CREATE TABLE Merge3 (
	SELECT
		id,
		Merge2.title,
		Merge2.author,
		isbn,
		Merge2.pages,
		Merge2.pubDate,
		Merge2.dewey
		subject,
		genre,
		article,
		Merge2.openLibraryID,
		openLibrary.coverurl,
		openLibrary.subjects
	FROM Merge2
	
	INNER JOIN openLibrary USING (openLibraryID)
)

find a row where title=otherrow.titel && x!=otherrow.x

SELECT * FROM Sandbox.Merge1 WHERE 

SELECT COUNT(DISTINCT title) FROM Sandbox.Merge1

SELECT title FROM Merge1 GROUP BY title

SELECT * FROM Merge1 GROUP BY title

SELECT title,
	GROUP_CONCAT(DISTINCT pages
		ORDER BY test_score DESC SEPARATOR '-')
	FROM Merge1
	GROUP BY title;

SELECT title,
	GROUP_CONCAT(DISTINCT author SEPARATOR 'URGSBLABLA') AS dings
	FROM Merge1
	GROUP BY title;

http://dev.mysql.com/doc/refman/5.0/en/group-by-functions.html#function_group-concat


DROP TABLE Merge2;
CREATE TABLE Merge2 (
	SELECT title,
	GROUP_CONCAT(DISTINCT author SEPARATOR '::') AS dings
	FROM Merge1
	GROUP BY title
);
SELECT * FROM Merge2 WHERE dings REGEXP '::';

SELECT * FROM Merge2 WHERE title='The Prince'


SELECT * FROM Merge1
GROUP BY title
HAVING COUNT(title) > 5;


DROP TABLE Merge2;
CREATE TABLE Merge2 (
	SELECT title, author,
	GROUP_CONCAT(DISTINCT dewey SEPARATOR '::') AS dings
	FROM Merge1
	GROUP BY title, author
);
SELECT * FROM Merge2 WHERE dings REGEXP '::';



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
DROP TABLE Merge2;
CREATE TABLE Merge2 (
	SELECT *
	FROM Merge1
	GROUP BY title, author
);



CREATE TABLE Merge3 (
	SELECT * FROM Merge2
	INNER JOIN Merge3_coverISBNs USING (isbn)
)




SELECT isbn FROM allGenresLinked WHERE genre = "Adventure"


SELECT COUNT(*)
FROM (SELECT isbn FROM allGenresLinked WHERE genre = "Fiction") AS T1
INNER JOIN (SELECT isbn FROM allGenresLinked WHERE genre = "Adventure") AS T2 USING (isbn)


DROP TABLE allGenresLinkedFiltered;
CREATE TABLE allGenresLinkedFiltered (
	SELECT genre, COUNT(isbn) AS dings
	FROM allGenresLinked
	GROUP BY genre
);


DROP TABLE allGenresLinkedFiltered;
DROP TABLE allGenres;
CREATE TABLE allGenres (
	SELECT genre, COUNT(isbn) AS booksCount
	FROM allGenresLinked
	GROUP BY genre
);

CREATE TABLE allGenresLinkedFiltered2 (
	SELECT genre, booksCount
	FROM allGenresLinkedFiltered
	WHERE booksCount > 4
);

CREATE TABLE allGenresLinkedFiltered3 (
	SELECT genre, isbn FROM allGenresLinked INNER JOIN allGenresLinkedFiltered2 USING (genre)
);




CREATE TABLE allSubjects2 (
	SELECT subject, COUNT(isbn) AS booksCount
	FROM allSubjectsLinked
	GROUP BY subject
);

CREATE TABLE allSubjects3 (
	SELECT subject, booksCount
	FROM allSubjects2
	WHERE booksCount > 4
);



CREATE TABLE allKeywords (
	SELECT * FROM allSubjects
	JOIN
	SELECT * FROM allGenres
);





CREATE TABLE allKeywordsS SELECT subject AS keyword, booksCount FROM allSubjects;



CREATE TABLE Merge4 (
	SELECT
		id,
		title,
		author,
		isbn,
		pages,
		pubDate,
		dewey,
		subject,
		genre,
		article,
		CONCAT(subject, genre) AS keyword
	FROM Merge3
)


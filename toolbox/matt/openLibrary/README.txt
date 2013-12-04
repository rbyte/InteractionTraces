
Download the Editions and Authors File from Open Library.

Create the Database:

CREATE DATABASE `LibraryUTF8` /*!40100 DEFAULT CHARACTER SET utf8 */

Create two tables:

CREATE TABLE `books` (
 `id` varchar(200) NOT NULL,
 `title` varchar(500) NOT NULL,
 `author` varchar(500) NOT NULL,
 `pubyear` int(11) NOT NULL,
 `coverurl` varchar(500) NOT NULL,
 `subjecttime` varchar(500) NOT NULL,
 `pages` int(11) NOT NULL,
 `subjects` text NOT NULL,
 PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `authors` (
 `id` varchar(200) NOT NULL,
 `name` varchar(1000) NOT NULL,
 PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

Add /usr/share/java/mysql.java as an external library.

Adjust the Paths in the Java Files.

Run the AuthorsToDB Script and after that the EditionsToDB Script.

To create a JSON-File from the DB again, run the BackToJson Script.

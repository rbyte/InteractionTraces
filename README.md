InteractionTraces
=================

Calgary Thesis Project 2012

A set up database is required. You may be able to use the predefined one (internet access required).

To setup a new database:

Setup a MySQL server.
In the command shell:
	create database Bookbox;
	use Bookbox;
	source Bookbox.sql;
	GRANT SELECT ON Bookbox.* TO 'Bookbox'@'localhost' IDENTIFIED BY 'rubernecksDelight';

Open params.properties and adjust the database access settings.

Run runnable.jar or run.bat.


How do setup developer mode:

Install JDK.
Install Eclipse.
Import the project folder into Eclipse.
Run matt.ui.PMain, either as an Applet or Java Program.

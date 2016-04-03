CREATE DATABASE IF NOT EXISTS EveBlogs;

USE EveBlogs;

CREATE TABLE tblBlog
(
	PK_Blog SERIAL,
    blogName VARCHAR(100) NOT NULL,
    blogLink VARCHAR(200) NOT NULL UNIQUE,
    feedLink VARCHAR(200) NOT NULL UNIQUE, #the link to the RSS feed of the blog
    author VARCHAR(50),
    # the xml elements in which the information for the blogpost is stored in the RSS feed
    xmlBlogpostName VARCHAR(30) NOT NULL DEFAULT 'title',
    xmlBlogpostLink VARCHAR(30) NOT NULL DEFAULT 'link',
    xmlPublicationDateTime VARCHAR(30) NOT NULL DEFAULT 'pubDate',
    xmlDescription VARCHAR(30) NOT NULL DEFAULT 'description',
    
    PRIMARY KEY(PK_Blog),
    INDEX(blogName),
    INDEX(author),
    INDEX(feedLink),
    INDEX(blogLink)
) ENGINE = InnoDB;

CREATE TABLE tblBlogpost
(
	PK_Blogpost SERIAL,
    FK_Blog BIGINT UNSIGNED,
    blogpostName VARCHAR(100) NOT NULL,
    blogpostLink VARCHAR(200) NOT NULL UNIQUE,
    publicationDateTime DATETIME NOT NULL,
    description VARCHAR(1024),
    PRIMARY KEY(PK_Blogpost),
    FOREIGN KEY(FK_Blog) REFERENCES tblBlog(PK_Blog),
    INDEX(blogpostName),
    INDEX(publicationDateTime),
    INDEX(blogpostLink) #to verify that a specific blogpost does not allready exist
) ENGINE = InnoDB;

DELIMITER $$
CREATE PROCEDURE getLatestBlogposts (IN numberOfPosts INTEGER UNSIGNED)
BEGIN
	SELECT tblBlog.blogName, tblBlog.blogLink, tblBlog.feedLink, tblBlogpost.blogpostName, tblBlogpost.blogpostLink, tblBlogpost.description, tblBlogpost.publicationDateTime
    FROM tblBlogpost INNER JOIN tblBlog ON tblBlog.PK_Blog = tblBlogpost.FK_Blog
	ORDER BY publicationDateTime DESC LIMIT numberOfPosts;
END$$
DELIMITER ;

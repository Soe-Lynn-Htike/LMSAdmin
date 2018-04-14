/**
 * 
 */
package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoanDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.BranchDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookLoan;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Branch;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Publisher;

/**
 * @author Aaron
 *
 */
@RestController
public class AdminService extends BaseController {

	@Autowired
	AuthorDAO adao;

	@Autowired
	BookDAO bookdao;

	@Autowired
	GenreDAO genredao;

	@Autowired
	PublisherDAO publisherdao;

	@Autowired
	BookCopiesDAO bookCopiesdao;

	@Autowired
	BorrowerDAO borrowerdao;

	@Autowired
	BranchDAO branchdao;

	@Autowired
	BookLoanDAO bookloandao;



	@RequestMapping(value="authorObject", method=RequestMethod.GET, produces="application/json" )
	public Author authorObject() throws SQLException {
		return new Author();
	}
	
	// author delete
	@RequestMapping(value = "author/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	@Transactional
	public void deleteAuthor(@PathVariable String Id) throws SQLException {
				Author author = new Author();
		try {
			author = adao.readAuthorsById(Integer.parseInt(Id));
			if (author != null) {
				adao.deleteAuthor(author);
	        }

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
		}
	}
	
	
	// author update
	@RequestMapping(value = "author/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Author updateAuthor(@PathVariable(value = "id") String id,@RequestBody Author author) throws SQLException {
				Author checkauthor = new Author();
		try {
			checkauthor = adao.readAuthorsById(Integer.parseInt(id));
			if (checkauthor == null) {
	           return null;
	        }else {
	        	adao.updateAuthor(author);
				adao.saveAuthorBook(author);
				return author;
	        }
				
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
			return null;
		}
	}
	
	// author create
	@RequestMapping(value = "author", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Author creatAuthor(@RequestBody Author author)
			throws SQLException {
		try {
			Integer authorId = adao.createAuthorWithPK(author);
			author.setAuthorId(authorId);
			adao.saveAuthorBook(author);
			return author;

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
			return null;
		}
	}
	
	
	// get author list
	@RequestMapping(value = "authors", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Author> readAllAuthors() {
		List<Author> authors = new ArrayList<>();
		try {
			authors = adao.readAuthors();
			for (Author a : authors) {
				a.setBooks(bookdao.readBooksByAuthorId(a));
			}
			return authors;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		 
	}
	
	
	// get particular author
	@RequestMapping(value = "author/{Id}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Author readAuthorsById(@PathVariable String Id) {
		Author author = new Author();
		try {
			author = adao.readAuthorsById(Integer.parseInt(Id));
			if (author == null) {
				return null;
			} else {
				author.setBooks(bookdao.readBooksByAuthorId(author));
			}
			return author;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	@RequestMapping(value = "updateAuthorBook", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void updateAuthorBook(@RequestBody Author author) throws SQLException {

		try {
				adao.deleteAuthorBook(author);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
		}
	}
	
	
	// get authors by name
	@RequestMapping(value = "authorsname/{searchname}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Author> searchAuthorsByName(@PathVariable String searchname) {
		List<Author> authors = new ArrayList<>();
		try {
			authors = adao.readAuthorsByName(searchname);
			for (Author a : authors) {
				a.setBooks(bookdao.readBooksByAuthorId(a));
			}
			return authors;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	

	@RequestMapping(value="bookObject", method=RequestMethod.GET, produces="application/json" )
	public Book initBook() throws SQLException {
		return new Book();
	}
	
	
	// get book list
	@RequestMapping(value = "books", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Book> readBooks() throws SQLException {
		List<Book> books = new ArrayList<>();
		try {

			books = bookdao.readBooks("");
			for (Book b : books) {
				b.setAuthors(adao.readAuthorsByBookId(b));
				b.setGenres(genredao.getGenresByBookId(b));
				b.setPublisher(publisherdao.getPublisherbyBookId(b));
				b.setBookcopies(bookCopiesdao.getBookCopiesByBookId(b));
			}
			return books;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	// delete book 
	@RequestMapping(value = "book/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	@Transactional
	public void deleteBook(@PathVariable String Id) throws SQLException {
		Book book = new Book();
		try {
			book = bookdao.getBookByPK(Integer.parseInt(Id));
			if (book != null) {
				bookdao.deleteBook(book);
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	// update book
	@RequestMapping(value = "book/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Book updateBook(@PathVariable String Id, @RequestBody Book book) throws SQLException {
		Book checkbook = new Book();
		try {
			checkbook = bookdao.getBookByPK(Integer.parseInt(Id));
			if (checkbook == null) {
				return null;
			} else {
				bookdao.updateBook(book);
				bookdao.saveBookAuthor(book);
				bookdao.saveBookGenre(book);
				return book;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// create book
	@RequestMapping(value = "book", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Book creatAuthor(@RequestBody Book book, UriComponentsBuilder ucBuilder)
			throws SQLException {
		try {
			Integer bookId = bookdao.createBookWithPK(book);
			book.setBookId(bookId);
			bookdao.saveBookAuthor(book);
			bookdao.saveBookGenre(book);
			return book;

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
			return null;
		}
	}
	

	// read book by title
	@RequestMapping(value = "bookstitle/{searchTitle}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Book> readBooksByTitle(@PathVariable String searchTitle) throws SQLException {
		List<Book> books = new ArrayList<>();
		try {
			books = bookdao.readBooks(searchTitle);
			for (Book book : books) {
				book.setAuthors(adao.readAuthorsByBookId(book));
				book.setGenres(genredao.getGenresByBookId(book));
				book.setPublisher(publisherdao.getPublisherbyBookId(book));
				book.setBookcopies(bookCopiesdao.getBookCopiesByBookId(book));
			}
			return books;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	// read book by id
	@RequestMapping(value = "book/{Id}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Book readBookById(@PathVariable String Id) throws SQLException {
		Book book = new Book();
		try {
			book = bookdao.getBookByPK(Integer.parseInt(Id));
			if(book != null) {
				book.setAuthors(adao.readAuthorsByBookId(book));
				book.setGenres(genredao.getGenresByBookId(book));
				book.setPublisher(publisherdao.getPublisherbyBookId(book));
				book.setBookcopies(bookCopiesdao.getBookCopiesByBookId(book));
			}
			return book;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}
	
	
	
	@RequestMapping(value = "updateBookAuthorGenre", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void updateBookAuthorGenre(@RequestBody Book book) throws SQLException {

		try {
				bookdao.deleteAuthorGenre(book);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
		}
	}

	
	
	@RequestMapping(value="publisherObject", method=RequestMethod.GET, produces="application/json" )
	public Publisher initPublisher() throws SQLException {
		return new Publisher();
	}
	

	// create publisher
	@RequestMapping(value = "publisher", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Publisher updatePublisher(@RequestBody Publisher publisher) throws SQLException {

		try {
				// need to create with PK
				publisherdao.createPublisher(publisher);
				return publisher;
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
	// update publisher
	@RequestMapping(value = "publisher/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Publisher updatePublisher(@PathVariable String id, @RequestBody Publisher publisher)
			throws SQLException {
		Publisher checkpublisher = new Publisher();
		try {
			checkpublisher = publisherdao.getPublisherById(Integer.parseInt(id));
			if (checkpublisher == null) {
				return null;
			} else {
				publisher.setPublisherId(Integer.parseInt(id));
				publisherdao.updatePublisher(publisher);
				return publisher;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	// delete publisher
	@RequestMapping(value = "publisher/{id}", method = RequestMethod.DELETE)
	@Transactional
	public void deletePublisher(@PathVariable String id) throws SQLException {
		Publisher checkpublisher = new Publisher();
		try {
			checkpublisher = publisherdao.getPublisherById(Integer.parseInt(id));
			if (checkpublisher != null) {
				publisherdao.deletePublisher(checkpublisher);
			} 

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
		}
	}
	
	
	// get publisher list
	@RequestMapping(value = "publishers", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Publisher> readPublisher() throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		try {
			publishers = publisherdao.readPublishers("");
			for (Publisher p : publishers) {
				p.setBooks(bookdao.readBooksByPublisherId(p));
			}
			return publishers;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}
	
	@RequestMapping(value = "publishers/withoutbooks", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Publisher> readPublishersWithoutBook() throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		try {
			publishers = publisherdao.readPublishersWithoutBook();
			return publishers;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	// get publisher by name
	@RequestMapping(value = "publishername/{searchPublisher}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Publisher> readPublisher(@PathVariable String searchPublisher) throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		try {
			publishers = publisherdao.readPublishers(searchPublisher);
			for (Publisher p : publishers) {
				p.setBooks(bookdao.readBooksByPublisherId(p));
			}
			return publishers;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	// get certain publisher
	@RequestMapping(value = "publisher/{Id}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Publisher readPublisherById(@PathVariable String Id) throws SQLException {
		Publisher publisher = new Publisher();
		try {
			publisher = publisherdao.getPublisherById(Integer.parseInt(Id));
			if (publisher == null) {
				return null;
			} else {
				publisher.setBooks(bookdao.readBooksByPublisherId(publisher));
				return publisher;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@RequestMapping(value="genreObject", method=RequestMethod.GET, produces="application/json" )
	public Genre initGenre() throws SQLException {
		return new Genre();
	}
	
	
	// create genre
	@RequestMapping(value = "genre", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Genre createGenre(@RequestBody Genre genre, UriComponentsBuilder ucBuilder) throws SQLException {
		try {
				Integer genreId = genredao.createGenreWithPK(genre);
				genre.setGenre_id(genreId);
				genredao.saveGenreBook(genre);
				return genre;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	// update genre
	@RequestMapping(value = "genre/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Genre updateGenre(@PathVariable String id, @RequestBody Genre genre) throws SQLException {
		Genre checkgenre = new Genre();
		try {
			checkgenre = genredao.readGenreById(Integer.parseInt(id));
			if (checkgenre == null) {
				return null;
			} else {
				genredao.updateGenre(genre);
				genredao.saveGenreBook(genre);
				return genre;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}
	
	// delete genre
	@RequestMapping(value = "genre/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	@Transactional
	public void deleteGenre(@PathVariable String id) throws SQLException {
		Genre genre = new Genre();
		try {
			genre = genredao.readGenreById(Integer.parseInt(id));
			if (genre != null) {

				genredao.deleteGenre(genre);

			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	// read Genres
	@Transactional
	@RequestMapping(value = "genres", method = RequestMethod.GET, produces = "application/json")
	public List<Genre> readGenres() throws SQLException {
		List<Genre> genres = new ArrayList<>();
		try {
			genres = genredao.readGenres("");
			for (Genre g : genres) {
				g.setBooks(bookdao.readBooksByGenreId(g));
			}
			return genres;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	// read genre by name
	@RequestMapping(value = "genrenames/{searchGenre}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Genre> readGenreByName(@PathVariable String searchGenre) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		try {
			genres = genredao.readGenres(searchGenre);
			for (Genre g : genres) {
				g.setBooks(bookdao.readBooksByGenreId(g));
			}
			return genres;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	
	// get particular genre
	@RequestMapping(value = "genre/{Id}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Genre readGenreById(@PathVariable String Id) throws SQLException {
		Genre genre = new Genre();
		try {
			genre = genredao.readGenreById(Integer.parseInt(Id));
			if (genre == null) {
				return null;
			} else {
				genre.setBooks(bookdao.readBooksByGenreId(genre));
				return genre;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	@RequestMapping(value = "updateGenreBook", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void updateGenreBook(@RequestBody Genre genre) throws SQLException {
		try {
			genredao.deleteGenreBook(genre);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); // log your stacktrace
			// display a meaningful user
		}
	}
	
	
	
	@RequestMapping(value="borrowerObject", method=RequestMethod.GET, produces="application/json" )
	public Borrower initBorrower() throws SQLException {
		return new Borrower();
	}

	
	// create borrower
	@RequestMapping(value = "borrower/", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Borrower createBorrower(@RequestBody Borrower borrower) throws SQLException {
		try {
			// create with PK
			borrowerdao.createBorrower(borrower);
			return borrower;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	// update borrower
	@RequestMapping(value = "borrower/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Borrower updateBorrower(@PathVariable String id, @RequestBody Borrower borrower) throws SQLException {
		Borrower checkborrower = new Borrower();
		try {

			checkborrower = borrowerdao.readBorrowersByCardNo(Integer.parseInt(id));
			if (checkborrower == null) {
				return null;
			} else {
				borrowerdao.updateBorrower(borrower);
				return borrower;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
	// delete borrower
	@RequestMapping(value = "borrower/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	@Transactional
	public void updateBorrower(@PathVariable String id) throws SQLException {
		Borrower borrower = new Borrower();
		try {
			borrower = borrowerdao.readBorrowersByCardNo(Integer.parseInt(id));
			if (borrower != null) {
				borrowerdao.deleteBorrower(borrower);
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
		}
	}
	
	// read Borrowers
	@RequestMapping(value = "borrowers", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Borrower> readBorrower() throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		try {
			borrowers = borrowerdao.readBorrowers("");
			for (Borrower b : borrowers) {
				b.setBookLoans(bookloandao.getBookLoansByCardNo(b));
			}
			return borrowers;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	// read borrower
	@RequestMapping(value = "borrower/{cardNo}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Borrower readBorrowerByCardNo(@PathVariable String cardNo) throws SQLException {
		Borrower borrower = new Borrower();
		try {
			borrower = borrowerdao.readBorrowersByCardNo(Integer.parseInt(cardNo));
			if (borrower == null) {
				return null;
			} else {
				borrower.setBookLoans(bookloandao.getBookLoansByCardNo(borrower));
				return borrower;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	
	// get borrowers name
	@RequestMapping(value = "borrowersname/{searchName}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Borrower> readBorrowerByName(@PathVariable String searchName) throws SQLException {
		List<Borrower> borrwers = new ArrayList<>();
		try {
			borrwers = borrowerdao.readBorrowers(searchName);
			for (Borrower b : borrwers) {
				b.setBookLoans(bookloandao.getBookLoansByCardNo(b));
			}
			return borrwers;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	@RequestMapping(value="branchObject", method=RequestMethod.GET, produces="application/json" )
	public Branch initBranch() throws SQLException {
		return new Branch();
	}
	
	
	
	// create branch
	@RequestMapping(value = "branch", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public Branch createBranch(@RequestBody Branch branch, UriComponentsBuilder ucBuilder)
			throws SQLException {
		List<Book> books = new ArrayList<>();
		try {

			Integer branchId = branchdao.createBranchWithPK(branch);
			books = bookdao.readBooks("");
			bookCopiesdao.createDefaultBookCopies(branchId, books);
			return branch;

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return null;

		}
	}
	
	// update branch
	@RequestMapping(value = "branch/{id}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public Branch updateBranch(@PathVariable String id, @RequestBody Branch branch) throws SQLException {
		Branch checkbranch = new Branch();
		try {
			checkbranch = branchdao.readBranchById(Integer.parseInt(id));
			if (checkbranch == null) {
				return null;
			} else {
				branchdao.updateBranch(branch);
				return branch;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}
	
	// delete branch
	@RequestMapping(value = "branch/{id}", method = RequestMethod.DELETE, consumes = "application/json")
	@Transactional
	public void deleteBranch(@PathVariable String id) throws SQLException {
		Branch branch = new Branch();
		try {
			branch = branchdao.readBranchById(Integer.parseInt(id));
			if (branch != null) {
				branchdao.deleteBranch(branch);
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// read Branch
	@RequestMapping(value = "branches", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Branch> readBranches() throws SQLException {

		List<Branch> branches = new ArrayList<>();
		try {
			branches = branchdao.readBranches("");
			for (Branch branch : branches) {
				branch.setBookcopies(bookCopiesdao.getBookCopiesByBranch(branch));
			}
			return branches;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	// get branches name
	@RequestMapping(value = "branchesname/{searchBranchName}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Branch> readBranchByName(@PathVariable String searchBranchName) throws SQLException {
		List<Branch> branches = new ArrayList<>();
		try {
			branches = branchdao.readBranches(searchBranchName);
			for (Branch branch : branches) {
				branch.setBookcopies(bookCopiesdao.getBookCopiesByBranch(branch));
			}
			return branches;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	// get particular branch
	@RequestMapping(value = "branch/{id}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public Branch readBranchById(@PathVariable String Id) throws SQLException {
		Branch branch = new Branch();
		try {

			branch = branchdao.readBranchById(Integer.parseInt(Id));
			if (branch == null) {
				return null;
			} else {
				branch.setBookcopies(bookCopiesdao.getBookCopiesByBranch(branch));
				return branch;
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	// Book Loan
	@RequestMapping(value = "updateBookLoan", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void updateBookLoan(@RequestBody BookLoan bookLoan) throws SQLException {

		try {

			if (bookLoan.getBookId() != null && bookLoan.getBranchId() != null && bookLoan.getCardNo() != null
					&& bookLoan.getDateIn() == null) {
				bookloandao.creatBookLoan(bookLoan);
			} else if (bookLoan.getBookId() == null && bookLoan.getBranchId() == null && bookLoan.getCardNo() == null
					&& bookLoan.getDateIn() != null) {
				bookloandao.updateBookLoan(bookLoan);
			} else {
				bookloandao.deleteBookLoan(bookLoan);
			}

		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	@RequestMapping(value = "readBranchByBorrower/{cardNo}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Branch> readBranchByBorrower(@PathVariable String cardNo) throws SQLException {
		List<Branch> branches = new ArrayList<>();
		try {
			branches = branchdao.readBranchByBorrower(Integer.parseInt(cardNo));
			
			return branches;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "readBookByBorrower/{cardNo}", method = RequestMethod.GET, produces = "application/json")
	@Transactional
	public List<Book> readBookByBorrower(@PathVariable String cardNo) throws SQLException {
		List<Book> books = new ArrayList<>();
		try {
			books = bookdao.readBooksByBorrower(Integer.parseInt(cardNo));
			
			return books;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="initBookLoan", method=RequestMethod.GET, produces="application/json" )
	public BookLoan initBookLoan() throws SQLException {
		return new BookLoan();
	}
	
	
	@RequestMapping(value = "getBooksByBranchByCardNo", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public List<Book> getBooksByBranchByCardNo(@RequestBody BookLoan bookLoan) throws SQLException {
		List<Book> books = new ArrayList<>();
		try {
			books = bookdao.readBookByBranchByCardNo(bookLoan);
			return books;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*// override Book Loan Due Date
	@RequestMapping(value = "overrideBookLoanDueDate", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void overrideBookLoanDueDate(@RequestBody BookLoan bookLoan) throws SQLException {

		try {

			bookloandao.overrideDueDate(bookLoan);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();

		}
	}*/
	
	
	// override duedate
	@RequestMapping(value = "duedate/{cardNo}", method = RequestMethod.PUT, consumes = "application/json")
	@Transactional
	public BookLoan overrideBookLoanDueDate(@PathVariable String cardNo, @RequestBody BookLoan bookLoan)
			throws SQLException {
		List<BookLoan> checkbookloan = new ArrayList<>();
		Borrower borrower = new Borrower();
		borrower.setCardNo(Integer.parseInt(cardNo));
		try {
			checkbookloan = bookloandao.getBookLoansByCardNo(borrower);
			if (checkbookloan.isEmpty()) {
				return null;
			} else {
				bookloandao.overrideDueDate(bookLoan);
				return checkbookloan.get(0);
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "updateBookLoanDueDate", method = RequestMethod.POST, consumes = "application/json")
	@Transactional
	public void updateBookLoanDueDate(@RequestBody BookLoan bookLoan) throws SQLException {

		try {
			bookloandao.updateBookLoanDueDate(bookLoan);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

}

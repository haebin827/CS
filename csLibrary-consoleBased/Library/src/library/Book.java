package library;

public class Book {
	private String no;
	private String isbn;
	private String title;
	private String author;
	private String category;
	private int year;
	private String publisher;
	private boolean isRental;
	private boolean isActive;
	
	public Book() {
		no = null;
		title = null;
		author = null;
		isbn = null;
		year = 0;
		publisher = null;
		category = null;
		isRental = false;
		isActive = true;
	}
	
	public Book(String title, String author, String isbn, int year, String publisher, String category, boolean isRental, boolean isActive) {
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.year = year;
		this.publisher = publisher;
		this.category = category;
		this.isRental = isRental;
		this.isActive = isActive;
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean getIsRental() {
		return isRental;
	}

	public void setRental(boolean isRental) {
		this.isRental = isRental;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
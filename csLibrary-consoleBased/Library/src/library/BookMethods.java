package library;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class BookMethods {
	
	public void addBook(Connection con, Book book) throws SQLException {
		String query = "INSERT INTO books (b_isbn, b_title, b_author, b_year, b_publisher, b_isRental, b_isActive, b_category) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try(PreparedStatement statement = con.prepareStatement(query)){
			statement.setString(1, book.getIsbn());
            statement.setString(2, book.getTitle());
            statement.setString(3, book.getAuthor());
            statement.setInt(4, book.getYear());
            statement.setString(5, book.getPublisher());
            statement.setBoolean(6, book.getIsRental());
            statement.setBoolean(7, book.getIsActive());
            statement.setString(8, book.getCategory());
            statement.executeUpdate();
		} catch (SQLException e) {
            System.err.println("SQLException (add book): " + e.getMessage());
        }
	}
	
	//24.6.10
	public void deleteBook(Connection con, Scanner scanner, int no) throws SQLException {
		//24.6.12: If a book has already been rented, it cannot be deleted
		String checkQuery = "SELECT COUNT(*) FROM rentals WHERE b_no = ? AND r_done = 1";
	    try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
	    	checkStmt.setInt(1, no);
	        try (ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next() && rs.getInt(1) > 0) {
	                System.out.println("\nThis book cannot be deleted because it has active rentals.");
	                return;
	            }
	        }
	    }
	    
		String query = "SELECT * FROM books WHERE b_no = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, no);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                	System.out.println("\n[Confirmation]");
                    System.out.println("  Title: " + rs.getString("b_title"));
                    System.out.println("  Author: " + rs.getString("b_author"));
                    System.out.println("  ISBN: " + rs.getString("b_isbn"));
                    System.out.println("  Released Year: " + rs.getInt("b_year"));
                    System.out.println("  Publisher: " + rs.getString("b_publisher"));
                    
                    if(rs.getInt("b_isActive") == 0) {
                    	System.out.print("\nThis book is already deleted.\n");
                    	return;
                    }
                    
                    //24.6.15: Add field to enter reason for deleting books & add record to removed_books
                    //			(If the admin deletes book without reason, m_id of the removed_books table becomes 0)
                    System.out.print("\nWrite a detail reason to delete: ");
                    String reason = scanner.nextLine();
                    
                    System.out.print("\nAre you sure you want to delete it? (Y/N): ");
                    String choice = (scanner.nextLine()).toLowerCase();

                    if (choice.equals("n")) {
                        System.out.println("Request terminated.\n");
                        return;
                    }
                    
                    LocalDate now = LocalDate.now();
                    Date sqlNow = Date.valueOf(now);
                    // m_id(removed books) = 0: deleted by admin
                    //24.6.11: Add a function that adds a record to the removed_books table when an administrator deletes a book
                    String addQuery = "INSERT INTO removed_books (m_id, b_no, rb_reason, rb_removedDate) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt2 = con.prepareStatement(addQuery)) {
                    	stmt2.setString(1, "0");
                        stmt2.setInt(2, rs.getInt("b_no"));
                        //stmt2.setString(3, "DELETED");
                        stmt2.setString(3, reason);
                        stmt2.setDate(4, sqlNow);
                        stmt2.executeUpdate();
                    }
                    
                    String udtQuery = "UPDATE books SET b_isRental = 0, b_isActive = 0 WHERE b_no = ?";
                    try (PreparedStatement stmt3 = con.prepareStatement(udtQuery)) {
                    	stmt3.setInt(1, rs.getInt("b_no"));
                    	stmt3.executeUpdate();
                    } catch (SQLException e) {
                        System.err.println("SQLException (update books): " + e.getMessage());
                    }
                    
                    System.out.println("Book number " + no + " deleted successfully.\n");
                } else {
                	System.out.println("Book number " + no + " does not exist.\n");
                	return;
                }
            }
        }
	}
	
	//24.6.10
	public void bookEdit(Connection con, Scanner scanner, String isbn) throws SQLException {
		//24.6.13: Modify the code by searching based on b_no to searching based on b_isbn & retrieve c_name from the categories table
		String query = "SELECT b.*, c.c_name FROM books b " +
                "LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE b.b_isbn = ?";
		
		//24.6.13: commented out
		//String query = "SELECT * FROM books WHERE b_no = ?";
		try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, isbn);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    do {
                    	System.out.println("\n[Confirmation]");
                        System.out.println("  Title: " + rs.getString("b_title"));
                        System.out.println("  Author: " + rs.getString("b_author"));
                        System.out.println("  ISBN: " + rs.getString("b_isbn"));
                        System.out.println("  Released Year: " + rs.getInt("b_year"));
                        System.out.println("  Publisher: " + rs.getString("b_publisher"));
                        System.out.println("  Category: " + rs.getString("c_name"));
                    } while (rs.next());
                } else {
                	System.out.println("Book with ISBN " + isbn + " does not exist.\n");
                	return;
                }
            }
        }
        
        System.out.println("\nSelect field to edit:");
        System.out.println("[1] Title");
        System.out.println("[2] Author");
        System.out.println("[3] ISBN");
        System.out.println("[4] Released Year");
        System.out.println("[5] Publisher");
        System.out.println("[6] Category");
        System.out.print("\n>> ");
        
        int field = scanner.nextInt();
        scanner.nextLine();
        System.out.println();

        String udtQuery = "UPDATE books SET ";
        String newValue = "";

        switch (field) {
            case 1:
                System.out.print("Enter new Title: ");
                newValue = scanner.nextLine();
                udtQuery += "b_title = ? WHERE b_isbn = ?";
                break;
                
            case 2:
            	System.out.print("Enter new Author: ");
                newValue = scanner.nextLine();
                udtQuery += "b_author = ? WHERE b_isbn = ?";
                break;
                
            case 3:
                while(true) {
        	        System.out.print("Enter new ISBN: ");
        	        newValue = scanner.nextLine();
        	        
        	        //24.6.13: Add ISBN constraint
        	        //			(must consist of 9 to 10 digits, the first 9 digits must be numbers and the last 10 digits must be 'x')
        	        if (newValue.length() != 9 && newValue.length() != 10) {
        	            System.out.println("ISBN should be at most 10 characters long. Please try again.");
        	        } else if (!newValue.matches("\\d{9}") && !newValue.matches("\\d{9}[\\dX]")) {
        	            System.out.println("Wrong ISBN format. Please try again.");
        	        } else {
        	            break;
        	        }
                }
                udtQuery += "b_isbn = ? WHERE b_isbn = ?";
                break;
                
            case 4:
            	int newYear = 0;
            	while(true) {
        	        System.out.print("Enter new Released Year: ");
        	        newYear = scanner.nextInt();
        	        scanner.nextLine();
        	        
        	        if(newYear < 1000 || newYear > 9999) {
        	        	System.out.println("Wrong year value. Please try again.");
        	        } else {
        	        	break;
        	        }
                }
            	newValue = String.valueOf(newYear);
                udtQuery += "b_year = ? WHERE b_isbn = ?";
                break;
                
            case 5:
                System.out.print("Enter new Publisher: ");
                newValue = scanner.nextLine();
                udtQuery += "b_publisher = ? WHERE b_isbn = ?";
                break;
                
            case 6:
            	showCategories(con, 1);
                System.out.print("Enter new Category (number): ");
                newValue = scanner.nextLine();
                udtQuery += "b_category = ? WHERE b_isbn = ?";
                break;
                
            default:
                System.out.println("Invalid selection.");
                return;
        }

        try (PreparedStatement statement = con.prepareStatement(udtQuery)) {
            statement.setString(1, newValue);
            statement.setString(2, isbn);
            statement.executeUpdate();
            System.out.println("\nBook information updated successfully!");
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
	
	public void bookInfo(Connection con, Scanner scanner) throws SQLException {
	    while (true) {
	        System.out.println("\nSelect search criteria (0 to exit):");
	        System.out.println("[1] Book Number");
	        System.out.println("[2] Title");
	        System.out.println("[3] Author");
	        System.out.println("[4] ISBN");
	        System.out.println("[5] Publisher");
	        System.out.println("[6] Category");
	        System.out.print("\n>> ");

	        try {
	            int criteria = scanner.nextInt();
	            scanner.nextLine();

	            if (criteria == 0) {
	                System.out.println();
	                break;
	            }

	            //24.6.12: commented out
                //String query = "SELECT * FROM books WHERE ";
                
                //24.6.12: Modify query to output categories
                /*String query = "SELECT b.*, c.c_name FROM books b " +
                        		"LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE ";
                */
                
                //24.6.12: When searching for a book with the same isbn, instead of showing multiple results, only one result and the number of copies are displayed
	            //			When searching for books with the same isbn, all b_nos for each book are displayed.
	            String query = "SELECT b.b_isbn, b.b_title, b.b_author, b.b_year, b.b_publisher, c.c_name, " +
		                        "GROUP_CONCAT(b.b_no) AS book_numbers, COUNT(*) AS copy_count " +
		                        "FROM books b LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE b_isActive = 1 AND ";
                String param = "";

	            switch (criteria) {
		            case 1:
	                    System.out.print("\nEnter Book Number: ");
	                    param = scanner.nextLine();
	                    query += "b_no = ?";
	                    break;
	                    
	                case 2:
	                    System.out.print("\nEnter Title: ");
	                    param = scanner.nextLine();
	                    query += "b_title LIKE ?";
	                    param = "%" + param + "%";
	                    break;
	                    
	                case 3:
	                    System.out.print("\nEnter Author: ");
	                    param = scanner.nextLine();
	                    query += "b_author LIKE ?";
	                    param = "%" + param + "%";
	                    break;
	                    
	                case 4:
	                    System.out.print("\nEnter ISBN: ");
	                    param = scanner.nextLine();
	                    query += "b_isbn = ?";
	                    break;
	                    
	                case 5:
	                    System.out.print("\nEnter Publisher: ");
	                    param = scanner.nextLine();
	                    query += "b_publisher LIKE ?";
	                    param = "%" + param + "%";
	                    break;
	                    
	                case 6:
                    	System.out.println();
                        showCategories(con, 1);
                        System.out.print("Enter Category (number): ");
                        param = scanner.nextLine();
                        query += "b_category = ?";
                        break;
                        
	                default:
	                    System.out.println("Invalid selection. Please try again.");
	            }
	            query += " GROUP BY b.b_isbn, b.b_title, b.b_author, b.b_year, b.b_publisher, c.c_name";

	            int count = 0;
	            try (PreparedStatement statement = con.prepareStatement(query)) {
	                statement.setString(1, param);
	                try (ResultSet rs = statement.executeQuery()) {
	                    if (rs.next()) {
	                        do {
	                            count++;
	                            System.out.println("\n[" + count + "] Title: " + rs.getString("b_title") + " (Book No: " + rs.getString("book_numbers") + ")");
	                            System.out.println("      Author: " + rs.getString("b_author"));
	                            System.out.println("      ISBN: " + rs.getString("b_isbn"));
	                            System.out.println("      Released Year: " + rs.getInt("b_year"));
	                            System.out.println("      Publisher: " + rs.getString("b_publisher"));
	                            System.out.println("      Category: " + rs.getString("c_name"));
	                            System.out.println("      Copies: " + rs.getInt("copy_count"));
	                            
	                            //24.6.15: Create a function that prints the number of rental copies of the searched book
	                            getRentedCopiesCount(con, rs.getString("book_numbers"));
                                
	                        } while (rs.next());
	                    } else {
	                        System.out.println("\nNo books found matching the criteria.\n");
	                    }
	                }
	            }
	        } catch (InputMismatchException e) {
	            System.out.println("Invalid selection. Please try again.");
	            scanner.nextLine(); // Clear buffer
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
	    }
	}
	
	//24.6.15: Create a function to output a list of deleted books (edited)
	public void deletedBookInfo(Connection con, Scanner scanner) throws SQLException {
        String query = "SELECT b.*, c.c_name, rb.rb_reason, rb.rb_removedDate " +
        				"FROM books b " +
        				"LEFT JOIN categories c ON b.b_category = c.c_dcode " +
        				"LEFT JOIN removed_books rb ON b.b_no = rb.b_no " +
        				"WHERE b.b_isActive = 0";
        
        int count = 0;
        try (Statement statement = con.createStatement()) {
            try (ResultSet rs = statement.executeQuery(query)) {
                if (rs.next()) {
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Title: " + rs.getString("b_title") + " (Book No: " + rs.getString("b_no") + ")");
                        System.out.println("      Author: " + rs.getString("b_author"));
                        System.out.println("      ISBN: " + rs.getString("b_isbn"));
                        System.out.println("      Released Year: " + rs.getInt("b_year"));
                        System.out.println("      Publisher: " + rs.getString("b_publisher"));
                        System.out.println("      Category: " + rs.getString("c_name"));
                        System.out.println("    - Reason: " + rs.getString("rb_reason"));
                        System.out.println("    - Removed Date: " + rs.getDate("rb_removedDate"));
                    } while (rs.next());
                } else {
                    System.out.println("\nNo books found matching the criteria.\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    } 

	//24.6.13: Create a function to check how many duplicate books are currently rented
	//			(output the number of rented books + the ID of the user who rented them)
	private void getRentedCopiesCount(Connection con, String bookNumbers) throws SQLException {
        String rentalQuery = "SELECT COUNT(*) AS rented_count, GROUP_CONCAT(m_id) AS members_list " +
                             "FROM rentals " +
                             "WHERE b_no IN (" + bookNumbers + ") AND r_done = 1";
        try (PreparedStatement rentalStmt = con.prepareStatement(rentalQuery);
             ResultSet rentalRs = rentalStmt.executeQuery()) {
            if (rentalRs.next()) {
            	System.out.print("      Rented Copies: " + rentalRs.getInt("rented_count"));
            	if (rentalRs.getString("members_list") != null) {
            		System.out.print(" (" + rentalRs.getString("members_list") + ")");
            	}
            }
            System.out.println();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
	
	private boolean bookExists(Connection con, int no) throws SQLException {
        String query = "SELECT COUNT(*) FROM books WHERE b_no = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, no);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                	return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }
	
	public static void showCategories(Connection con, int select) throws SQLException {
    	String query = "SELECT c_name, c_dcode FROM categories ORDER BY c_dcode ASC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nCategories:");
            while (rs.next()) {
                String name = rs.getString("c_name");
                String dcode = rs.getString("c_dcode");
                if(select == 1) {
	                if(dcode.length() == 2) {
	                	System.out.println("\n- " + name + " (" + dcode + ")");
	                }
	                else {
	                	System.out.println(String.format("		%-25s : %s", name, dcode));
	                }
                } else if(select == 2) {
                	if(dcode.length() == 2) {
	                	System.out.println("- " + name + " : " + dcode);
	                }
                }
            }
            System.out.println();
        } catch (SQLException e) {
            con.rollback();  // Rollback the transaction in case of an error
            throw e;
        }
	}
	
	//24.6.21: Created a function that add a new category
	public static void addCategory(Connection con, Scanner scanner) throws SQLException {
	    System.out.println("\nChoose an option:");
	    System.out.println("[1] Add a new major category");
	    System.out.println("[2] Add a subcategory to an existing category");
	    System.out.print("\n>> ");
	    int select = scanner.nextInt();
	    scanner.nextLine(); // Consume newline left-over

	    // Add a new major category
	    if (select == 1) {
	        System.out.print("\nEnter the name of the major category: ");
	        String majorCategoryName = scanner.nextLine();
	        System.out.println();

	        // Find the next available d_code1
	        String newCode1 = findNextAvailableCode1(con);

	        List<String> subCategoryNames = new ArrayList<>();
	        while (true) {
	            System.out.print("Enter the name of the subcategory (0 to finish): ");
	            String subCategoryName = scanner.nextLine();
	            if (subCategoryName.equals("0")) {
	                break;
	            }
	            subCategoryNames.add(subCategoryName);
	        }

            // Insert the major category
            String addMajorCategory = "INSERT INTO categories (c_name, c_code1, c_code2, c_dcode) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(addMajorCategory)) {
                    stmt.setString(1, majorCategoryName);
                    stmt.setString(2, newCode1);
                    stmt.setString(3, "");
                    stmt.setString(4, newCode1);
                    stmt.executeUpdate();
                
            }
            
            // Insert its subcategories
            String addMinorCategory = "INSERT INTO categories (c_name, c_code1, c_code2, c_dcode) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt2 = con.prepareStatement(addMinorCategory)) {
                int subCategoryCount = 1;
                for (String subCategoryName : subCategoryNames) {
                    String newCode2 = String.format("%02d", subCategoryCount);
                    String newDcode = newCode1 + newCode2;

                    stmt2.setString(1, subCategoryName);
                    stmt2.setString(2, newCode1);
                    stmt2.setString(3, newCode2);
                    stmt2.setString(4, newDcode);
                    stmt2.addBatch();

                    subCategoryCount++;
                }
                stmt2.executeBatch();
                System.out.println("\nMajor category and subcategories added successfully.");
            }
        }
	    
	    //24.6.22: When the admin selects 2 (Add a subcategory to an existing category)
	    else if (select == 2) {
	    	showCategories(con, 2);
	    	System.out.print("\nSelect the major category to enter: ");
	        String majorCategoryNum = scanner.nextLine();
	        System.out.println();
	        
	        List<String> subCategoryNames = new ArrayList<>();
	        while (true) {
	            System.out.print("Enter the name of the subcategory (0 to finish): ");
	            String subCategoryName = scanner.nextLine();
	            if (subCategoryName.equals("0")) {
	                break;
	            }
	            subCategoryNames.add(subCategoryName);
	        }
	        
	        // Insert its subcategories
            String insertMinorCategory = "INSERT INTO categories (c_name, c_code1, c_code2, c_dcode) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(insertMinorCategory)) {
                for (String subCategoryName : subCategoryNames) {
                    String newCode2 = findNextAvailableCode2(con, majorCategoryNum);
                    String newDcode = majorCategoryNum + newCode2;
                    System.out.println(newDcode);

                    stmt.setString(1, subCategoryName);
                    stmt.setString(2, majorCategoryNum);
                    stmt.setString(3, newCode2);
                    stmt.setString(4, newDcode);
                    //stmt.addBatch();
                    stmt.executeUpdate();
                }
                //stmt.executeBatch();
                System.out.println("\nSubcategories added successfully.");
            }
	    }
    }

	//24.6.21: Function to find the next possible code1
    private static String findNextAvailableCode1(Connection con) throws SQLException {
        String query = "SELECT c_code1 FROM categories ORDER BY c_code1";
        HashSet<Integer> code1List = new HashSet<>();

        try (PreparedStatement stmt = con.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String code1 = rs.getString("c_code1");
                if (code1 != null && !code1.isEmpty()) {
                    try {
                        code1List.add(Integer.parseInt(code1));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid c_code1 value: " + code1);
                    }
                }
            }
        }

        int expectedCode1 = 1;
        for (int code1 : code1List) {
            if (code1 != expectedCode1) {
                break;
            }
            expectedCode1++;
        }
        return String.format("%02d", expectedCode1); // Ensure the code is 2 digits
    }
	
	//24.6.15: Function to edit categories
	public static void editCategory(Connection con, Scanner scanner) throws SQLException {
		String dcode = "";
		while(dcode != "0") {
			System.out.print("\nEnter the category code (O to exit): ");
	        dcode = scanner.nextLine();
	
	        if (!isValidCategory(con, dcode)) {
	        	System.out.println("Category code " + dcode + " does not exist.");
	        }
	        else if (dcode == "0"){break;}
		}
		
		if (dcode.length() == 2) {
            // Major category
            editMajorCategory(con, scanner, dcode);
        } else if (dcode.length() == 4) {
        	// Minor category
            editMinorCategory(con, scanner, dcode);
        }
		
	}
	
	//24.6.15: Function to modify major categories
	private static void editMajorCategory(Connection con, Scanner scanner, String dcode) throws SQLException {
        System.out.println("\nMajor category selected. Choose an option:");
        System.out.println("[1] Edit category name");
        System.out.println("[2] Delete category");
        System.out.print("\n>> ");
        int select = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (select) {
            case 1:
            	// Edit category name
                System.out.print("\nEnter the new category name: ");
                String newName = scanner.nextLine();
                
                System.out.print("Are you sure you want to update it? (Y/N): ");
                String choice = (scanner.nextLine()).toLowerCase();

                if (choice.equals("n")) {
                    System.out.println("Request terminated.\n");
                    return;
                } else if (choice.equals("y")) {
                
	                String updateQuery = "UPDATE categories SET c_name = ? WHERE c_dcode = ?";
	                try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
	                    stmt.setString(1, newName);
	                    stmt.setString(2, dcode);
	                    stmt.executeUpdate();
	                    System.out.println("Category name updated successfully.");
	                }
	                break;
                }
            case 2:
            	// Delete the main category and all subcategories associated with it & add a record to removed_books & set b_isActive of all related books to 0
            	deleteMajorCategory(con, scanner, dcode);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
	
	//24.6.15
	private static void deleteMajorCategory(Connection con, Scanner scanner, String dcode) throws SQLException {
		
		System.out.print("\nALL BOOKS related to the category will be deleted.\nAre you sure you want to remove it? (Y/N): ");
        String choice = (scanner.nextLine()).toLowerCase();

        if (choice.equals("n")) {
            System.out.println("Request terminated.\n");
            return;
        } else if (choice.equals("y")) {
        	
        	// Find and store all book numbers associated with the major category
            String selectBooksQuery = "SELECT b_no FROM books WHERE b_category LIKE ?";
            List<Integer> bookNumbers = new ArrayList<>();
            try (PreparedStatement selectStmt = con.prepareStatement(selectBooksQuery)) {
                selectStmt.setString(1, dcode + "%");
                try (ResultSet rs = selectStmt.executeQuery()) {
                    while (rs.next()) {
                        bookNumbers.add(rs.getInt("b_no"));
                    }
                }
            }
            
	        // Delete books associated with major category (삭제된 카테고리는 0으로 업데이트됨)
	        String deleteBooksQuery = "UPDATE books SET b_isActive = 0 WHERE b_category LIKE ?";
	        try (PreparedStatement stmt = con.prepareStatement(deleteBooksQuery)) {
	            stmt.setString(1, dcode + "%");
	            int deletedBooks = stmt.executeUpdate();
	            // Add to deleted table
	            System.out.println("\nDeleted " + deletedBooks + " books associated with category " + dcode + ".");
	        }
	
	        LocalDate now = LocalDate.now();
            Date sqlNow = Date.valueOf(now);
            
	        // Insert removed book records into the removed_books table
	        String insertRemovedBooksQuery = "INSERT INTO removed_books (m_id, b_no, rb_reason, rb_removedDate) VALUES (?, ?, ?, ?)";
	        try (PreparedStatement insertStmt = con.prepareStatement(insertRemovedBooksQuery)) {
	            for (Integer bNo : bookNumbers) {
	                insertStmt.setString(1, "0"); // m_id = 0
	                insertStmt.setInt(2, bNo);
	                insertStmt.setString(3, "CATEGORY DELETION");
	                insertStmt.setDate(4, sqlNow); // Today's date
	                insertStmt.addBatch();
	            }
	            insertStmt.executeBatch();
	        }
	        
	        // Delete all subcategories
	        deleteSubcategories(con, dcode);
	        
	        // Delete the major category itself
	        String deleteCategoryQuery = "DELETE FROM categories WHERE c_dcode = ?";
	        try (PreparedStatement stmt = con.prepareStatement(deleteCategoryQuery)) {
	            stmt.setString(1, dcode);
	            stmt.executeUpdate();
	            System.out.println("Category " + dcode + " and its subcategories deleted successfully.");
	        }
	        
	        //24.6.15: Adjust the dcode of remaining categories
	        //24.6.21: commented out
	        //adjustCategories(con, dcode);
        } else {
        	System.out.println("Invalid selection.\n");
        	return;
        }
    }
	
	//24.6.15: Adjust a dcode of remaining categories
	//24.6.21: commented out
	/*private static void adjustCategories(Connection con, String deletedCode1) throws SQLException {
	    // Get all categories with a higher dcode than the deleted one
	    String selectCategoriesQuery = "SELECT c_code2, c_dcode FROM categories WHERE c_code1 > ? ORDER BY c_dcode ASC";
	    List<String> code2List = new ArrayList<>();
	    String(cd: code2List) {
	    	System.out println(cd);
	    }
	    List<String> dcodeList = new ArrayList<>();
	    try (PreparedStatement selectStmt = con.prepareStatement(selectCategoriesQuery)) {
	        selectStmt.setString(1, deletedCode1);
	        try (ResultSet rs = selectStmt.executeQuery()) {
	            while (rs.next()) {
	            	code2List.add(rs.getString("c_code2"));
	            	dcodeList.add(rs.getString("c_dcode"));
	            }
	        }
	    }
	    
	 // Adjust the dcode of the remaining categories
	    code2List = adjustCode2(code2List);
	    dcodeList = adjustDcode(code2List, dcodeList);
	    
	    
	    //deletedCode1: Deleted main category (code1)
	    //code2List: Code2 for all categories that need to be modified
	    String updateCategoryQuery = "UPDATE categories SET c_code1, c_dcode = ? WHERE c_code1 > ?";
	    try (PreparedStatement updateStmt = con.prepareStatement(updateCategoryQuery)) {
	        for (int i = 0; i<code2List.size(); i++) {
	            updateStmt.setString(1, code2List.get(i));
	            updateStmt.setString(2, dcodeList.get(i));
	            updateStmt.setString(3, deletedCode1);
	            updateStmt.executeUpdate();
	        }
	    }
	}*/
	    
	 //24.6.15: Function to adjust dcode
	//24.6.15: commented out
	    /*private static List<String> adjustCode2(List<String> code2List) {
	    	List<String> newCode2List = new ArrayList<>();
	        for (String code2: code2List) {
	        	String newCode2 = String.format("%02d", Integer.parseInt(code2) - 1);
	        	newCode2List.add(newCode2);
	        }
	        return newCode2List;
	    }
	    
	    private static List<String> adjustDcode(List<String> code2List, List<String> dcodeList) {
	    	List<String> newDcodeList = new ArrayList<>();
	    	for(int i = 0; i<code2List.size(); i++) {
	    		String dcode = dcodeList.get(i);
	            String code2 = code2List.get(i);
	            String newDcode = code2 + dcode.substring(0, 2);
	            newDcodeList.add(newDcode);
	    	}
	    	return newDcodeList;
	    }*/
	
    private static void deleteSubcategories(Connection con, String dcode) throws SQLException {
        // Find all subcategories
        String findSubcategoriesQuery = "DELETE FROM categories WHERE c_dcode LIKE ?";
        try (PreparedStatement stmt = con.prepareStatement(findSubcategoriesQuery)) {
            stmt.setString(1, dcode + "%");
            stmt.executeUpdate();
        }
    }

    //24.6.15: Function that modify subcategories
    private static void editMinorCategory(Connection con, Scanner scanner, String dcode) throws SQLException {
        System.out.println("\nMinor category selected. Choose an option:");
        System.out.println("[1] Edit category name");
        System.out.println("[2] Change major category");
        System.out.println("[3] Delete category");
        System.out.print("\n>> ");
        int select = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (select) {
            case 1:
                System.out.print("\nEnter the new category name: ");
                String newName = scanner.nextLine();
                
                System.out.print("\nAre you sure you want to update? (Y/N): ");
                String choice = (scanner.nextLine()).toLowerCase();

                if (choice.equals("n")) {
                    System.out.println("Request terminated.\n");
                    return;
                } else if (choice.equals("y")) {
                
	                String updateQuery = "UPDATE categories SET c_name = ? WHERE c_dcode = ?";
	                try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
	                    stmt.setString(1, newName);
	                    stmt.setString(2, dcode);
	                    stmt.executeUpdate();
	                    System.out.println("Category name updated successfully.");
	                }
	                break;
                }
            case 2:
                showCategories(con, 2);
                String newCode1 = "";
                while(newCode1 != "0") {
	                System.out.print("Enter the new major category code (O to exit): ");
	                newCode1 = scanner.nextLine();
	                if (newCode1.length() != 2) {
	                    System.out.println("Invalid major category code. Must be 2 characters.");
	                    return;
	                }
        	        if (!isValidCategory(con, newCode1)) {
        	        	System.out.println("Category code " + newCode1 + " does not exist.");
        	        }
        	        else {break;}
        		}
        
                // Find the next available c_code2 under the newCode1
                String nextCode2 = findNextAvailableCode2(con, newCode1);
                String newDcode = newCode1 + nextCode2;

                // Update the b_category in books table
                String updateBooksQuery = "UPDATE books SET b_category = ? WHERE b_category = ?";
                try (PreparedStatement stmt = con.prepareStatement(updateBooksQuery)) {
                    stmt.setString(1, newDcode);
                    stmt.setString(2, dcode);
                    stmt.executeUpdate();
                    System.out.println("Books updated with new category successfully.");
                }

                // Update the category
                String changeMajorQuery = "UPDATE categories SET c_code1 = ?, c_code2 = ?, c_dcode = ? WHERE c_dcode = ?";
                try (PreparedStatement stmt = con.prepareStatement(changeMajorQuery)) {
                    stmt.setString(1, newCode1);
                    stmt.setString(2, nextCode2);
                    stmt.setString(3, newDcode);
                    stmt.setString(4, dcode);
                    stmt.executeUpdate();
                    System.out.println("Major category changed successfully.");
                }
                break;
            case 3:
            	System.out.print("\nAre you sure you want to delete? (Y/N): ");
                String choice2 = (scanner.nextLine()).toLowerCase();

                if (choice2.equals("n")) {
                    System.out.println("Request terminated.\n");
                    return;
                }
                String deleteQuery = "DELETE FROM categories WHERE c_dcode = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
                    stmt.setString(1, dcode);
                    stmt.executeUpdate();
                    System.out.println("Category deleted successfully.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    //24.6.21: Function to find the next possible code2
    private static String findNextAvailableCode2(Connection con, String newCode1) throws SQLException {
        String query = "SELECT c_code2 FROM categories WHERE c_code1 = ? ORDER BY c_code2";
        List<Integer> code2List = new ArrayList<>();
        
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, newCode1);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                	String code2 = rs.getString("c_code2");
                    if (code2 != null && !code2.isEmpty()) {
                        try {
                            code2List.add(Integer.parseInt(code2));
                        } catch (NumberFormatException e) {
                            // Handle the case where c_code2 is not a valid integer
                            System.err.println("Invalid c_code2 value: " + code2);
                        }
                    }
                }
            }
        }
        
        int expectedCode2 = 1;
        for (int code2 : code2List) {
            if (code2 != expectedCode2) {
                break;
            }
            expectedCode2++;
        }
        
        return String.format("%02d", expectedCode2); // Ensure the code is 2 digits
    }
	
  //24.6.21: Check if a category already exists in the table
	private static boolean isValidCategory(Connection con, String dcode) throws SQLException {
        String query = "SELECT COUNT(*) FROM categories WHERE c_dcode = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, dcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
	
	public void usersInfo(Connection con, Scanner scanner) throws SQLException {
	    //String query = "SELECT * FROM members WHERE m_name != 'admin'";
		
		//24.6.13: create new query (same as query in "searchAndDeleteUser")
		String query = "SELECT m.*, " +
                "SUM(CASE WHEN r.r_done = 1 THEN 1 ELSE 0 END) AS currently_rented, " +
                "SUM(CASE WHEN r.r_done = 0 THEN 1 ELSE 0 END) AS total_rented " +
                "FROM members m LEFT JOIN rentals r ON m.m_id = r.m_id WHERE m.m_name != 'admin' GROUP BY m.m_id";

	    try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	         ResultSet rs = stmt.executeQuery(query)) {
	        System.out.println();
	        int count = 0;
	        if (!rs.next()) {
	            System.out.println("\nNo members found");
	            return;
	        }
	        // Move cursor to before first row
	        rs.beforeFirst();
	        while (rs.next()) {
	            count++;
	            System.out.println(String.format("%-16s : %s", "[" + count + "] Name", rs.getString("m_name")));
	            System.out.println(String.format("%-16s : %s", "    ID", rs.getString("m_id")));
	            System.out.println(String.format("%-16s : %s", "    Phone", rs.getString("m_phone")));
	            System.out.println(String.format("%-16s : %s", "    Level", rs.getString("m_level")));
	            System.out.println(String.format("%-16s : %s", "    Point", rs.getString("m_point")));
	            System.out.println(String.format("%-16s : %s", "    Late Return", rs.getString("m_noOfLateReturn")));
	            System.out.println(String.format("%-16s : %s", "    Registered Date", rs.getDate("m_regDate")));
	            System.out.println(String.format("%-16s : %s", "    Currently Rented", rs.getInt("currently_rented")));
                System.out.println(String.format("%-16s : %s", "    Total Rentals", rs.getInt("currently_rented") + rs.getInt("total_rented")));
	            System.out.println();
	        }
	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}
	
	public void bookRental(Connection con, Scanner scanner) throws SQLException {
		LocalDate now = LocalDate.now();
        Date sqlNow = Date.valueOf(now);
        Date futureDate = Date.valueOf(now.plusDays(30));
        //24.6.10
	    //String query = "SELECT * FROM rentalrequests WHERE r_rentalRequest = 1";
        
        //24.6.10
        String query = "SELECT b.b_title, b.b_author, b.b_no, rq.r_id, rq.r_requestDate, rq.m_id, rq.r_rentalRequest, rq.m_id "
                + "FROM rentalrequests rq "
                + "JOIN books b ON rq.b_no = b.b_no "
                + "WHERE rq.r_rentalRequest = 1 "
                + "ORDER BY rq.r_requestDate ASC";
	    
	    try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	         ResultSet rs = stmt.executeQuery(query)) {
	        if (!rs.next()) {
	            System.out.println("\nNo rental requests found.\n");
	            return;
	        }
	        
	        System.out.println("\nRental Requests:\n");
	        int count = 0;
	        do {
	            count++;
	            System.out.println("[" + count + "] Book Number: " + rs.getInt("b_no"));
	            System.out.println("                (" + rs.getString("b_title") + " / Written By " + rs.getString("b_author") + ")");
	            System.out.println("    Member ID: " + rs.getString("m_id"));
	            System.out.println("    Request Date: " + rs.getDate("r_requestDate"));
	            System.out.println();
	        } while (rs.next());

	        int reqNum = 0;
	        boolean validInput = false;
	        
	        while (!validInput) {
	            System.out.print(">> Enter the number of the Rental Request to process: ");
	            reqNum = scanner.nextInt();
	            scanner.nextLine();
	            
	            if (reqNum > 0 && reqNum <= count) {
	                validInput = true;
	            } else {
	                System.out.println("Invalid selection. Please try again.\n");
	            }
	        }

	        System.out.print("\nAre you sure you want to confirm it? (Y/N): ");
	        String choice = (scanner.nextLine()).toLowerCase();

	        if (choice.equals("n")) {
	            System.out.println("Request terminated.\n");
	            return;
	        }
	        
	        rs.absolute(reqNum);

	        int rId = rs.getInt("r_id");

	        String updQuery = "UPDATE rentalrequests SET r_rentalRequest = 0 WHERE r_id = ?";
	        try (PreparedStatement updStmt = con.prepareStatement(updQuery)) {
	            updStmt.setInt(1, rId);
	            updStmt.executeUpdate();
                
                String addQuery = "INSERT INTO rentals (m_id, b_no, r_rentalDate, r_returnDate, r_whenToReturn) VALUES (?, ?, ?, ?, ?)";
        		try(PreparedStatement statement = con.prepareStatement(addQuery)){
        			statement.setString(1, rs.getString("m_id"));
                    statement.setInt(2, rs.getInt("b_no"));
                    statement.setDate(3, sqlNow);
                    statement.setDate(4, null);
                    statement.setDate(5, futureDate);
                    statement.executeUpdate();
        		}
        		
        		String updQuery2 = "UPDATE rentalrequests SET r_rentalRequest = 0 WHERE r_id = ?";
    	        try (PreparedStatement updStmt2 = con.prepareStatement(updQuery2)) {
    	            updStmt2.setInt(1, rId);
    	            updStmt2.executeUpdate();
		        } catch (SQLException e) {
		            System.err.println("SQLException: " + e.getMessage());
		        }
    	        
    	        
    	        //개발:만약에 rentals에 m_id가 몇개 이상 있고, members에서 블랙리스트가 false라면 등급올리기 + 포인트올리기
    	        
        		//24.6.10: Create a query to update the level & points in the member table
    	        /*String levelQuery = "SELECT COUNT(*) "
    	        					+ "FROM rentals r "
    	        					+ "INNER JOIN members m ON r.m_id = m.m_id "
    	        					+ "WHERE m.m_level != 'blacklist' AND (r.r_done = 1 OR r.r_done = 0)";
    	        int noOfBooks = Integer.parseInt(levelQuery);
    	        String updLevQuery = "UPDATE members SET m_level = ";
    	        
    	        if (noOfBooks >= 150) {
    	        	updLevQuery += "PLATINUM ";
    	        } else if (noOfBooks >= 80) {
    	        	updLevQuery += "PLATINUM ";
    	        } else if (noOfBooks >= 40) {
    	        	updLevQuery += "GOLD ";
    	        } else if (noOfBooks >= 15) {
    	        	updLevQuery += "SILVER ";
    	        }
    	        updLevQuery += "AND m_point = ? WHERE m_id = ?";
    	        try (PreparedStatement updLevStmt = con.prepareStatement(updLevQuery)) {
    	        
    	        	// Point = Rented book * 200 (대여중, 리턴. r_done이 -1면 문제가있단소리)
    	        	updLevStmt.setInt(1, noOfBooks * 200);
    	        	updLevStmt.setString(2, rs.getString("m_id"));
    	        	updLevStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("SQLException (update levels): " + e.getMessage());
                }*/
    	        
    	        //24.6.11 : Create a query to update the level
    	        String countQuery = "SELECT COUNT(*) FROM rentals r "
                        + "INNER JOIN members m ON r.m_id = m.m_id "
                        + "WHERE m_isBlacklist = 0 AND (r.r_done = 1 OR r.r_done = 0) AND r.m_id = ?";
    	        
    	        int noOfBooks = 0;
    	        try (PreparedStatement countStmt = con.prepareStatement(countQuery)) {
    	            countStmt.setString(1, rs.getString("m_id"));
    	            try (ResultSet rs2 = countStmt.executeQuery()) {
    	                if (rs2.next()) {
    	                    noOfBooks = rs2.getInt(1);
    	                }
    	            }
    	        }
    	        
    	        String newLevel = null;
    	        if (noOfBooks >= 150) {
    	            newLevel = "PLATINUM";
    	        } else if (noOfBooks >= 80) {
    	            newLevel = "GOLD";
    	        } else if (noOfBooks >= 40) {
    	            newLevel = "SILVER";
    	        }

    	        if (newLevel != null) {
    	            String updLevQuery = "UPDATE members SET m_level = ?, m_point = ? WHERE m_id = ?";
    	            try (PreparedStatement updLevStmt = con.prepareStatement(updLevQuery)) {
    	                updLevStmt.setString(1, newLevel);
    	                updLevStmt.setInt(2, noOfBooks * 200);
    	                updLevStmt.setString(3, rs.getString("m_id"));
    	                updLevStmt.executeUpdate();
    	            }
    	        }
    	        System.out.println("Book rental confirmed successfully.\n");
    	        
		    } catch (SQLException e) {
		        System.err.println("SQLException: " + e.getMessage());
		    }
	    }
	}

	//24.6.10
	public void bookReturn(Connection con, Scanner scanner) throws SQLException {
	    LocalDate now = LocalDate.now();
	    Date sqlNow = Date.valueOf(now);

	    String query = "SELECT b.b_title, b.b_author, b.b_no, rr.r_id, rr.r_requestDate, rr.r_whenToReturn, rr.m_id, rr.r_returnRequest "
	            + "FROM returnrequests rr "
	            + "JOIN books b ON rr.b_no = b.b_no "
	            + "WHERE rr.r_returnRequest = 1 "
	            + "ORDER BY rr.r_requestDate ASC";

	    try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	         ResultSet rs = stmt.executeQuery(query)) {
	        if (!rs.next()) {
	            System.out.println("\nNo return requests found.\n");
	            return;
	        }

	        System.out.println("\nReturn Requests:\n");
	        int count = 0;
	        do {
	            count++;
	            System.out.println("[" + count + "] Book Number: " + rs.getInt("b_no"));
	            System.out.println("                (" + rs.getString("b_title") + " / Written By " + rs.getString("b_author") + ")");
	            System.out.println("    Member ID: " + rs.getString("m_id"));
	            System.out.println("    Request Date: " + rs.getDate("r_requestDate"));
	            System.out.println("    Rental End Date: " + rs.getDate("r_whenToReturn"));
	            System.out.println();
	        } while (rs.next());

	        int reqNum = 0;
	        boolean validInput = false;
	        
	        while (!validInput) {
	            System.out.print(">> Enter the number of the Return Request to process: ");
	            reqNum = scanner.nextInt();
	            scanner.nextLine();
	            
	            if (reqNum > 0 && reqNum <= count) {
	                validInput = true;
	            } else {
	                System.out.println("Invalid selection. Please try again.\n");
	            }
	        }

	        System.out.print("\nAre you sure you want to confirm it? (Y/N): ");
	        String choice = (scanner.nextLine()).toLowerCase();

	        if (choice.equals("n")) {
	            System.out.println("Request terminated.\n");
	            return;
	        }

	        rs.absolute(reqNum);

	        String updQuery = "UPDATE rentals SET r_done = 0, r_isReturnReq = 0, r_returnDate = ? WHERE m_id = ? AND b_no = ? AND r_done = 1";
	        try (PreparedStatement updStmt = con.prepareStatement(updQuery)) {
	            updStmt.setDate(1, sqlNow);
	            updStmt.setString(2, rs.getString("m_id"));
	            updStmt.setInt(3, rs.getInt("b_no"));
	            updStmt.executeUpdate();
	            System.out.println("Book return confirmed successfully.");
	        } catch (SQLException e) {
	            System.err.println("SQLException (update rentals): " + e.getMessage());
	        }
	        
	        String updQuery2 = "UPDATE returnrequests SET r_returnRequest = 0 WHERE r_id = ?";
	        try (PreparedStatement updStmt2 = con.prepareStatement(updQuery2)) {
	            updStmt2.setInt(1, rs.getInt("r_id"));
	            updStmt2.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("SQLException (update rentalrequests): " + e.getMessage());
	        }

	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}

	//24.6.10
	public void bookExt(Connection con, Scanner scanner) throws SQLException {
	    
	    //24.6.11
	    /*String query = "SELECT b.b_title, b.b_author, b.b_no, e.m_id, e.e_requestDate, e.e_id "
			            + "FROM books b "
			            + "INNER JOIN extensionrequests e ON b.b_no = e.b_no "
			            + "WHERE e.e_extensionRequest = 1";*/
	    
	    //24.6.11
		String query = "SELECT b.b_title, b.b_author, b.b_no, e.m_id, e.e_requestDate, e.e_id, r.r_whenToReturn "
		                + "FROM books b "
		                + "INNER JOIN extensionrequests e ON b.b_no = e.b_no "
		                + "INNER JOIN rentals r ON b.b_no = r.b_no "
		                + "WHERE e.e_extensionRequest = 1 AND r.r_done = 1";

	    try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
	         ResultSet rs = stmt.executeQuery(query)) {
	        if (!rs.next()) {
	            System.out.println("\nNo extension requests found.\n");
	            return;
	        }

	        System.out.println("\nExtension Requests:\n");
	        int count = 0;
	        do {
	            count++;
	            System.out.println("[" + count + "] Book Number: " + rs.getInt("b_no"));
	            System.out.println("                (" + rs.getString("b_title") + " / Written By " + rs.getString("b_author") + ")");
	            System.out.println("    Member ID: " + rs.getString("m_id"));
	            System.out.println("    Request Date: " + rs.getDate("e_requestDate"));
	            System.out.println("    Rental End Date: " + rs.getDate("r_whenToReturn"));
	            System.out.println();
	        } while (rs.next());

	        int reqNum = 0;
	        boolean validInput = false;
	        
	        while (!validInput) {
	            System.out.print(">> Enter the number of the Extension Request to process: ");
	            reqNum = scanner.nextInt();
	            scanner.nextLine();
	            
	            if (reqNum > 0 && reqNum <= count) {
	                validInput = true;
	            } else {
	                System.out.println("Invalid selection. Please try again.\n");
	            }
	        }

	        System.out.print("\nAre you sure you want to confirm it? (Y/N): ");
	        String choice = (scanner.nextLine()).toLowerCase();

	        if (choice.equals("n")) {
	            System.out.println("Request terminated.\n");
	            return;
	        }

	        rs.absolute(reqNum);

	        //24.6.11: If the user requests an extension of the book = the return period + 15 days
	        Date currRtnDate = rs.getDate("r_whenToReturn");
	        Date newRtnDate = Date.valueOf(currRtnDate.toLocalDate().plusDays(15));
	        
	        String updQuery = "UPDATE rentals SET r_isExtended = 1, r_whenToReturn = ? WHERE m_id = ? AND b_no = ? AND r_done = 1";
	        try (PreparedStatement updStmt = con.prepareStatement(updQuery)) {
	        	// 24.6.11: Modified to date of existing return period + 15 days
	            updStmt.setDate(1, newRtnDate);
	            updStmt.setString(2, rs.getString("m_id"));
	            updStmt.setInt(3, rs.getInt("b_no"));
	            updStmt.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("SQLException (update rentals): " + e.getMessage());
	        }
	        
	        String updQuery2 = "UPDATE extensionrequests SET e_extensionRequest = 0 WHERE e_id = ?";
	        try (PreparedStatement updStmt2 = con.prepareStatement(updQuery2)) {
	            updStmt2.setInt(1, rs.getInt("e_id"));
	            updStmt2.executeUpdate();
	            System.out.println("Book extension confirmed successfully.");
	        } catch (SQLException e) {
	            System.err.println("SQLException (update rentals): " + e.getMessage());
	        }

	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}
	
	//24.6.12: Function that print blacklist
	public void blacklist(Connection con, Scanner scanner) throws SQLException {
	    String query = "SELECT * FROM members m " +
	                   "JOIN blacklist b ON m.m_id = b.m_id " +
	                   "WHERE m.m_isBlacklist = 1";
	    
	    try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	         ResultSet rs = stmt.executeQuery(query)) {
	        System.out.println();
	        int count = 0;
	        if (!rs.next()) {
	            System.out.println("No blacklist found");
	            return;
	        }
	        // Move cursor to before first row
	        rs.beforeFirst();
	        while (rs.next()) {
	            count++;
	            System.out.println(String.format("%-16s : %s", "[" + count + "] Name", rs.getString("m_name")));
	            System.out.println(String.format("%-16s : %s", "    ID", rs.getString("m_id")));
	            System.out.println(String.format("%-16s : %s", "    Blocked date", rs.getDate("bl_date")));
	            System.out.println(String.format("%-16s : %s", "    Reason", rs.getString("bl_reason")));
	            System.out.println(String.format("%-16s : %s", "    Late Return", rs.getString("m_noOfLateReturn")));
	            System.out.println();
	        }
	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}
	
	//24.6.11: Create a function to delete member information & add m_id record to 'deleted_users' table
	private void deleteUser(Connection con, Scanner scanner, String m_id) throws SQLException {
		//24.6.12: If a user has books borrowed, admin cannot delete a user
    	try {
            // Check if there are any rentals with r_done = 1 for the user
            String checkQuery = "SELECT COUNT(*) FROM rentals WHERE m_id = ? AND r_done = 1";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, m_id);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("\nAccount deletion is not allowed. There are active rentals.\n");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
        	System.err.println("SQLException (Account Delete): " + e.getMessage());
        }
    	
	    System.out.print("\nAre you sure you want to delete the account? (Y/N): ");
	    String choice = (scanner.nextLine()).toLowerCase();
	    
	    if (choice.equals("n")) {
	        System.out.println("Request terminated.");
	        return;
	    } else {
	        String query = "DELETE FROM members WHERE m_id = ?";
	        try (PreparedStatement statement = con.prepareStatement(query)) {
	            statement.setString(1, m_id);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
	        
	        String addQuery = "INSERT INTO deleted_users (m_id) VALUE (?)";
	        try (PreparedStatement stmt = con.prepareStatement(addQuery)) {
	            stmt.setString(1, m_id);
	            stmt.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
	        System.out.println("Account deleted successfully!\n");
	    }
	     
	}
	
	//24.6.13: Function that search a user
	public void searchAndDeleteUser(Connection con, Scanner scanner, int select) throws SQLException {
		while (true) {
            System.out.println("\nSelect search criteria (0 to exit):");
            System.out.println("[1] ID");
            System.out.println("[2] Name");
            System.out.println("[3] Phone");
            System.out.println("[4] Registered Date");
            System.out.print("\n>> ");
            
            try {
                int criteria = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (criteria == 0) {
                	System.out.println();
                    break;
                }

                //24.6.13: Query to find currently rented books & total number of rented books
                String query = "SELECT m.*, " +
	                           "SUM(CASE WHEN r.r_done = 1 THEN 1 ELSE 0 END) AS currently_rented, " +
	                           "SUM(CASE WHEN r.r_done = 0 THEN 1 ELSE 0 END) AS total_rented " +
	                           "FROM members m LEFT JOIN rentals r ON m.m_id = r.m_id WHERE m.m_name != 'admin' AND ";
                if(select == 2){
                	query += "m.m_isBlacklist = 1 AND ";
                }
                String param = "";

                switch (criteria) {
                    case 1:
                        System.out.print("\nEnter ID: ");
                        param = scanner.nextLine();
                        query += "m_id = ?";
                        break;
                    case 2:
                        System.out.print("\nEnter Name: ");
                        param = scanner.nextLine();
                        query += "m_name LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 3:
                        System.out.print("\nEnter Phone: ");
                        param = scanner.nextLine();
                        query += "m_phone LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 4:
                        System.out.print("\nEnter Registered date (yyyy-mm-dd): ");
                        param = scanner.nextLine();
                        query += "m_regDate LIKE ?";
                        param = "%" + param + "%";
                        break;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        break;
                }
                query += " GROUP BY m.m_id";
                
                System.out.println();
                int count = 0;
                try (PreparedStatement statement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    statement.setString(1, param);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            do {
                                count++;
                                System.out.println(String.format("%-16s : %s", "[" + count + "] Name", rs.getString("m_name")));
                	            System.out.println(String.format("%-16s : %s", "    ID", rs.getString("m_id")));
                	            System.out.println(String.format("%-16s : %s", "    Phone", rs.getString("m_phone")));
                	            System.out.println(String.format("%-16s : %s", "    Level", rs.getString("m_level")));
                	            System.out.println(String.format("%-16s : %s", "    Point", rs.getString("m_point")));
                	            System.out.println(String.format("%-16s : %s", "    Late Return", rs.getString("m_noOfLateReturn")));
                	            System.out.println(String.format("%-16s : %s", "    Registered Date", rs.getDate("m_regDate")));
                	            System.out.println(String.format("%-16s : %s", "    Currently Rented", rs.getInt("currently_rented")));
                                System.out.println(String.format("%-16s : %s", "    Total Rentals", rs.getInt("currently_rented") + rs.getInt("total_rented")));
                            } while (rs.next());

                            int reqNum = 0;
        	       	        boolean validInput = false;
        	       	        
        	       	        while (!validInput) {
        	       	        	System.out.print("\n>> Select the user to delete: ");
        	       	            reqNum = scanner.nextInt();
        	       	            scanner.nextLine();
        	       	            
        	       	            if (reqNum > 0 && reqNum <= count) {
        	       	                validInput = true;
        	       	            } else {
        	       	                System.out.println("Invalid selection. Please try again.\n");
        	       	            }
        	       	        }

                           rs.absolute(reqNum);
                            //delete function
                            if(select == 3) {
                    			deleteUser(con, scanner, rs.getString("m_id"));
                    		}
                        } else {
                            System.out.println("No member found matching the criteria.\n");
                        }
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine(); // Clear buffer
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        }
	}
	
	//24.6.13: Function that automatically adds all relevant content when admin enter the ISBN for an existing book
	public void addExistingBook(Connection con, Scanner scanner) throws SQLException {
	    System.out.print("Enter the ISBN: ");
	    String isbn = scanner.nextLine();
	    
	    String query = "SELECT b.*, c.c_name, c.c_dcode FROM books b " +
	                   "LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE b.b_isbn = ?";
	    try (PreparedStatement statement = con.prepareStatement(query)) {
	        statement.setString(1, isbn);
	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                System.out.println("\nConfirmation");
	                System.out.println("    Title: " + rs.getString("b_title"));
	                System.out.println("    Author: " + rs.getString("b_author"));
	                System.out.println("    ISBN: " + rs.getString("b_isbn"));
	                System.out.println("    Released Year: " + rs.getInt("b_year"));
	                System.out.println("    Publisher: " + rs.getString("b_publisher"));
	                System.out.println("    Category: " + rs.getString("c_name"));
	                
	                System.out.print("\nAre you sure you want to add this book? (Y/N): ");
	                String choice = (scanner.nextLine()).toLowerCase();

	                if (choice.equals("n")) {
	                    System.out.println("Request terminated.\n");
	                    return;
	                } else {
	                    String addQuery = "INSERT INTO books (b_isbn, b_title, b_author, b_year, b_publisher, b_category) VALUES (?, ?, ?, ?, ?, ?)";
	                    try (PreparedStatement addStatement = con.prepareStatement(addQuery)) {
	                        addStatement.setString(1, rs.getString("b_isbn"));
	                        addStatement.setString(2, rs.getString("b_title"));
	                        addStatement.setString(3, rs.getString("b_author"));
	                        addStatement.setInt(4, rs.getInt("b_year"));
	                        addStatement.setString(5, rs.getString("b_publisher"));
	                        addStatement.setString(6, rs.getString("c_dcode"));
	                        addStatement.executeUpdate();
	                        System.out.println("Book added successfully.");
	                    }
	                }
	            } else {
	                System.out.println("ISBN \"" + isbn + "\" doesn't exist.");
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
	}

	
	//24.6.13: commented out
	//			(Bundle it into one by including it in the searchUser function. searchUser can search both general users and blacklists)
	/*
	public void searchBlacklist(Connection con, Scanner scanner) throws SQLException {
		while (true) {
            System.out.println("\nSelect search criteria (0 to exit):");
            System.out.println("[1] ID");
            System.out.println("[2] Name");
            System.out.println("[3] Phone");
            System.out.println("[4] Registered Date");
            System.out.print("\n>> ");
            
            try {
                int criteria = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (criteria == 0) {
                	System.out.println();
                    break;
                }

                String query = "SELECT * FROM members WHERE m_isBlacklist = 1 AND ";
                String param = "";

                switch (criteria) {
                    case 1:
                        System.out.print("\nEnter ID: ");
                        param = scanner.nextLine();
                        query += "m_id = ?";
                        break;
                    case 2:
                        System.out.print("\nEnter Name: ");
                        param = scanner.nextLine();
                        query += "m_name LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 3:
                        System.out.print("\nEnter Phone: ");
                        param = scanner.nextLine();
                        query += "m_phone LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 4:
                        System.out.print("\nEnter Registered date (yyyy-mm-dd): ");
                        param = scanner.nextLine();
                        query += "m_regDate LIKE ?";
                        param = "%" + param + "%";
                        break;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        break;
                }

                int count = 0;
                try (PreparedStatement statement = con.prepareStatement(query)) {
                    statement.setString(1, param);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            do {
                                count++;
                                System.out.println(String.format("%-16s : %s", "[" + count + "] Name", rs.getString("m_name")));
                	            System.out.println(String.format("%-16s : %s", "    ID", rs.getString("m_id")));
                	            System.out.println(String.format("%-16s : %s", "    Phone", rs.getString("m_phone")));
                	            System.out.println(String.format("%-16s : %s", "    Level", rs.getString("m_level")));
                	            System.out.println(String.format("%-16s : %s", "    Point", rs.getString("m_point")));
                	            System.out.println(String.format("%-16s : %s", "    Late Return", rs.getString("m_noOfLateReturn")));
                	            System.out.println(String.format("%-16s : %s", "    Registered Date", rs.getDate("m_regDate")));
                            } while (rs.next());
                        } else {
                            System.out.println("\nNo members found matching the criteria.\n");
                        }
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid selection. Please try again.");
                scanner.nextLine(); // Clear buffer
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        }
	}*/
	
}

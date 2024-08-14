package library;

import java.sql.*;
import java.util.*;

public class Admin {
    public static void main(String[] args) {
    	
        Dao dao = new Dao();
        Connection con = dao.connectDB();
        Scanner scanner = new Scanner(System.in);
        int select = 0;

        System.out.println("[Admin ver]\n");
        login(scanner, con);

        while (select != 6) {
        	
        	try {
        		System.out.println("Menu (Admin mode):");
	        	System.out.println("  [1] Book info");
	        	System.out.println("  [2] Manage books");
	        	System.out.println("  [3] See requests");
	        	System.out.println("  [4] Member list");
	        	System.out.println("  [5] Change password");
	        	System.out.println("  [6] Exit");
	        	System.out.print("\n>> ");
	        	
	        	select = scanner.nextInt();
	        	scanner.nextLine();

                switch (select) {
                    case 1:
                    	int select7 = 0;
    	            	
    	            	while (select7 != 3) {
    		            	System.out.println("\nBook info:");
    		            	System.out.println("  [1] Search Book info");
    			        	System.out.println("  [2] Deleted book info");
    		            	System.out.println("  [3] Exit");
    		            	System.out.print("\n>> ");
    		            	
    		            	try {
    			            	select7 = scanner.nextInt();
    			            	scanner.nextLine();
    			            	
    			            	switch (select7) {
    				            	case 1:
    				            		bookInfo(scanner, con);
    			    	            	break;
    				            	case 2:
    				            		deletedBookInfo(scanner, con);
    				            		break;
    				            	case 3:
    				            		System.out.println();
    				            		break;
    				            	default:
    				            		System.out.println("Invalid selection. Please try again.");
    			            	}          
    		            	} catch(InputMismatchException e) {
    	                        System.out.println("Invalid selection. Please try again.");
    	                        scanner.nextLine(); // Clear buffer
    	                    }
    	            	}
    	            	break;

                    case 2:
                    	int select2 = 0;
    	            	
    	            	while (select2 != 4) {
    		            	System.out.println("\nManage books:");
    		            	System.out.println("  [1] Add book");
    			        	System.out.println("  [2] Edit book");
    			        	System.out.println("  [3] Delete book");
    			        	System.out.println("  [4] Categories");
    		            	System.out.println("  [5] Exit");
    		            	System.out.print("\n>> ");
    		            	
    		            	try {
    			            	select2 = scanner.nextInt();
    			            	scanner.nextLine();
    			            	
    			            	switch (select2) {
    				            	case 1:
    				            		addBook(scanner, con);
    				            		break;
    				            	case 2:
    				            		bookEdit(scanner, con);
    				            		break;
    				            	case 3:
    				            		bookDelete(scanner, con);
    				            		break;
    				            	case 4:
    				            		//24.6.15: Add menu for category CRUD
    				            		int select8 = 0;
    			    	            	
    			    	            	while (select8 != 4) {
    			    		            	System.out.println("\nCategories:");
    			    		            	System.out.println("  [1] Category info");
    			    			        	System.out.println("  [2] Add category");
    			    			        	System.out.println("  [3] Edit/Delete category");
    			    		            	System.out.println("  [4] Exit");
    			    		            	System.out.print("\n>> ");
    			    		            	
    			    		            	try {
    			    			            	select8 = scanner.nextInt();
    			    			            	scanner.nextLine();
    			    			            	
    			    			            	switch (select8) {
    			    				            	case 1:
    			    				            		try {
    			    				            			System.out.println();
    			    				        	        	BookMethods.showCategories(con, 1);
    			    				        	        } catch (SQLException e) {
    			    				        	            System.err.println("SQLException: " + e.getMessage());
    			    				        	            return;
    			    				        	        }
    			    				            		break;
    			    				            	case 2:
    			    				            		try {
    			    				        	        	BookMethods.addCategory(con, scanner);
    			    				        	        } catch (SQLException e) {
    			    				        	            System.err.println("SQLException: " + e.getMessage());
    			    				        	            return;
    			    				        	        }
    			    				            		break;
    			    				            	case 3:
    			    				            		try {
    			    				        	        	BookMethods.editCategory(con, scanner);
    			    				        	        } catch (SQLException e) {
    			    				        	            System.err.println("SQLException: " + e.getMessage());
    			    				        	            return;
    			    				        	        }
    			    				            		break;
    			    				            	case 4:
    			    				            		System.out.println();
    			    				            		break;
    			    				            	default:
    			    				            		System.out.println("Invalid selection. Please try again.");
    			    			            	}          
    			    		            	} catch(InputMismatchException e) {
    			    	                        System.out.println("Invalid selection. Please try again.");
    			    	                        scanner.nextLine(); // Clear buffer
    			    	                    }
    			    	            	}
    			    	            	break;
    				            	case 5:
    				            		System.out.println();
    				            		break;
    				            	default:
    				            		System.out.println("Invalid selection. Please try again.");
    			            	}          
    		            	} catch(InputMismatchException e) {
    	                        System.out.println("Invalid selection. Please try again.");
    	                        scanner.nextLine(); // Clear buffer
    	                    }
    	            	}
    	            	break;
    	            	
                    case 3:
                        int select3 = 0;
                        while (select3 != 4) {
                        	System.out.println("\nSee requests:");
    		            	System.out.println("  [1] Rental request list");
    			        	System.out.println("  [2] Return request list");
    			        	System.out.println("  [3] Extension request list");
    		            	System.out.println("  [4] Exit");
    		            	System.out.print("\n>> ");

                            try {
                                select3 = scanner.nextInt();
                                scanner.nextLine();

                                switch (select3) {
                                    case 1:
    				            		bookRental(scanner, con);
    				            		break;
                                    case 2:
    				            		bookReturn(scanner, con);
    				            		break;
                                    case 3:
    				            		bookExt(scanner, con);
    				            		break;
                                    case 4:
                                    	System.out.println();
                                    	break;
                                    default:
                                        System.out.println("Invalid selection. Please try again.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid selection. Please try again.");
                                scanner.nextLine(); // Clear buffer
                            }
                        }
                        break;

                    case 4:
                    	int select4 = 0;
                        while (select4 != 4) {
                        	System.out.println("\nMember list:");
                        	System.out.println("  [1] See members");
    			        	System.out.println("  [2] See blacklist");
                        	System.out.println("  [3] Delete member");
    		            	System.out.println("  [4] Exit");
    		            	System.out.print("\n>> ");

                            try {
                                select4 = scanner.nextInt();
                                scanner.nextLine();

                                switch (select4) {
                                    case 1:
                                    	int select5 = 0;
                                        while (select5 != 3) {
                                        	System.out.println("\nSee members:");
                                        	System.out.println("  [1] See all members");
                    			        	System.out.println("  [2] Search for members");
                    		            	System.out.println("  [3] Exit");
                    		            	System.out.print("\n>> ");

                                            try {
                                                select5 = scanner.nextInt();
                                                scanner.nextLine();

                                                switch (select5) {
                                                    case 1:
                    				            		usersInfo(scanner, con);
                    				            		break;
                                                    case 2:
                                                    	// search members (X blacklist)
                                                    	searchAndDeleteUser(scanner, con, 1);
                    				            		break;
                                                    case 3:
                                                    	System.out.println();
                                                    	break;
                    				            	default:
                    				            		System.out.println("Invalid selection. Please try again.");
                        			            	}          
                        		            	} catch(InputMismatchException e) {
                        	                        System.out.println("Invalid selection. Please try again.");
                        	                        scanner.nextLine(); // Clear buffer
                        		            	}
                                        }
                                        break;
                                    case 2:
                                    	int select6 = 0;
                                        while (select6 != 3) {
                                        	System.out.println("\nSee blacklist:");
                                        	System.out.println("  [1] See all blacklist");
                    			        	System.out.println("  [2] Search for blacklist");
                    		            	System.out.println("  [3] Exit");
                    		            	System.out.print("\n>> ");

                                            try {
                                                select6 = scanner.nextInt();
                                                scanner.nextLine();

                                                switch (select6) {
                                                    case 1:
                    				            		blacklist(scanner, con);
                    				            		break;
                                                    case 2:
                                                    	// search blacklist
                                                    	searchAndDeleteUser(scanner, con, 2);
                    				            		break;
                                                    case 3:
                                                    	System.out.println();
                                                    	break;
                    				            	default:
                    				            		System.out.println("Invalid selection. Please try again.");
                        			            	}          
                        		            	} catch(InputMismatchException e) {
                        	                        System.out.println("Invalid selection. Please try again.");
                        	                        scanner.nextLine(); // Clear buffer
                        		            	}
                                        }
                                        break;
                                    case 3:
                                    	//delete
                                    	searchAndDeleteUser(scanner, con, 3);
                                    	break;
                                    case 4:
                                    	System.out.println();
                                    	break;
    				            	default:
    				            		System.out.println("Invalid selection. Please try again.");
        			            	}          
        		            	} catch(InputMismatchException e) {
        	                        System.out.println("Invalid selection. Please try again.");
        	                        scanner.nextLine(); // Clear buffer
        		            	}
                        }
                        break;
                        
                    case 5:
                        changePw(scanner, con);
                        break;
                    case 6:
                        System.out.println("\nGood Bye!");
                        return;

                    default:
                        System.out.println("\nInvalid selection. Please try again.\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\nInvalid selection. Please try again.\n");
                scanner.nextLine(); // Clear buffer
            }
        }
    }


    private static boolean authenticateAdmin(Connection con, String id, String pw) throws SQLException {
        String query = "SELECT COUNT(*) FROM members WHERE m_id = ? AND m_pw = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, id);
            statement.setString(2, pw);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void updatePw(Connection con, String newPw) {
        String updateQuery = "UPDATE members SET m_pw = ? WHERE m_id = 'admin'";
        try (PreparedStatement statement = con.prepareStatement(updateQuery)) {
            statement.setString(1, newPw);
            statement.executeUpdate();
            System.out.println("\nPassword updated successfully!\n");
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void addBook(Scanner scanner, Connection con) {
    	//24.6.13: Create a submenu to add a method to duplicate the existing information when the ISBN of the book to be added already exists
    	System.out.println("\nAdd book:");
        System.out.println("[1] Add new book");
        System.out.println("[2] Add existing book");
        System.out.println("[3] Exit");
        System.out.print("\n>> ");
        
        int field = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        
        if(field == 1) {
	        System.out.print("\nEnter Title: ");
	        String title = scanner.nextLine();
	
	        System.out.print("Enter Author: ");
	        String author = scanner.nextLine();
	
	        String isbn = "";
	        while(true) {
		        System.out.print("Enter ISBN: ");
		        isbn = scanner.nextLine();
		        
		        //24.6.13: Add ISBN constraint
		        //			(must consist of 9 to 10 digits, the first 9 digits must be numbers and the last 10 digits must be 'x'. ISBN must not exist in the existing table)
		        if (isbn.length() != 9 && isbn.length() != 10) {
		            System.out.println("ISBN should be at most 10 characters long. Please try again.");
		        } else if (!isbn.matches("\\d{9}") && !isbn.matches("\\d{9}[\\dX]")) {
		            System.out.println("Wrong ISBN format. Please try again.");
		        } else if (isExistingIsbn(con, isbn)){
		        	return;
		        } else {
		        	break;
		        }
	        }
	        
	        int year = 0;
	        while(true) {
		        System.out.print("Enter Year: ");
		        year = scanner.nextInt();
		        scanner.nextLine();
		        
		        if(year < 1000 || year > 9999) {
		        	System.out.println("Wrong year value. Please try again.");
		        } else {
		        	break;
		        }
	        }
	
	        System.out.print("Enter Publisher: ");
	        String publisher = scanner.nextLine();
	        
	        try {
	        	BookMethods.showCategories(con, 1);
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	            return;
	        }
	        
	        System.out.print(">> Enter Category (number): ");
	        String category = scanner.nextLine();
	        
	        boolean isRental = false;
	        boolean isActive = true;
	
	        Book book = new Book(title, author, isbn, year, publisher, category, isRental, isActive);
	
	        BookMethods bookMethod = new BookMethods();
	        try {
	            bookMethod.addBook(con, book);
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
        } else {
        	//24.6.13
        	BookMethods bookMethod = new BookMethods();
        	try {
	            bookMethod.addExistingBook(con, scanner);
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
        }
    }
    
    private static void bookEdit(Scanner scanner, Connection con) {
    	//24.6.13: Allow to edit the contents of all books with the same ISBN at once by entering the ISBN instead of b_no
    	System.out.print("\nEnter ISBN to edit: ");
        String isbn = scanner.nextLine();

        BookMethods bookMethod = new BookMethods();
        try {
            //bookMethod.bookEdit(con, scanner, no);
        	bookMethod.bookEdit(con, scanner, isbn);
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void bookInfo(Scanner scanner, Connection con) {    	
    	BookMethods bookMethod = new BookMethods();
        try {
        	bookMethod.bookInfo(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void bookDelete(Scanner scanner, Connection con) {
    	System.out.print("\nEnter Book Number: ");
        int no = scanner.nextInt();
        scanner.nextLine();
        
        BookMethods bookMethod = new BookMethods();
        try {
        	bookMethod.deleteBook(con, scanner, no);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void usersInfo(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.usersInfo(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.13
    private static void searchAndDeleteUser(Scanner scanner, Connection con, int select) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.searchAndDeleteUser(con, scanner, select);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.13
    /*private static void deleteUser(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.deleteUser(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }*/
    
    private static void bookRental(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.bookRental(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.10
    private static void bookReturn(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.bookReturn(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.10
    private static void bookExt(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.bookExt(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.10
    private static void blacklist(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.blacklist(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
  //24.6.15
    private static void deletedBookInfo(Scanner scanner, Connection con) {
    	BookMethods bookMethod = new BookMethods();
    	try {
        	bookMethod.deletedBookInfo(con, scanner);
        } catch(SQLException e) {
        	System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.10
    private static void login(Scanner scanner, Connection con) {
        while (true) {
            System.out.print("Enter ID: ");
            String id = scanner.nextLine();

            System.out.print("Enter PW: ");
            String pw = scanner.nextLine();

            try {
                if (authenticateAdmin(con, id, pw)) {
                	//24.6.12
                    /*if ("0000".equals(pw)) {
                        System.out.println("\nChange the password");
                        String newPw;
                        do {
                            System.out.print("New PW: ");
                            newPw = scanner.nextLine();
                            if ("0000".equals(newPw)) {
                                System.out.println("New password cannot be '0000'. Please enter a different password.");
                            }
                        } while ("0000".equals(newPw));
                        updatePassword(con, id, newPw);
                        System.out.println("\nPassword is changed successfully!");
                    }*/
                	
                	//24.6.12: 기존함수로 단순화
                	if ("0000".equals(pw)) {
                		changePw(scanner, con);
                	}
                    System.out.println("\nAdmin login successfully!\n");
                    break;
                } else {
                    System.out.println("Invalid ID or password. Please try again.\n");
                }
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
                return;
            }
        }
    }
    
    //24.6.12: Administrator's password change function
    //			(cannot set 0000, cannot use the previous password, includes upper/lower case, at least 3 numbers and special characters, 8 characters or more)
    private static void changePw(Scanner scanner, Connection con) {
        String pw;
        String confirm_pw;
        
        while (true) {
            System.out.print("\nEnter new PW: ");
            pw = scanner.nextLine();
            if (isValidPassword(con, pw)) {
                break;
            }
        }
        
        while (true) {
            System.out.print("Confirm PW (0 to go back): ");
            confirm_pw = scanner.nextLine();
            if (confirm_pw.equals("0")) {
                return; // Go back
            } else if (!pw.equals(confirm_pw)) {
                System.out.println("Passwords do not match. Please try again.\n");
            } else {
                break;
            }
        }
        
        updatePw(con, confirm_pw);
    }
    
    //24.6.12: Check if the password is valid
    private static boolean isValidPassword(Connection con, String password) {
    	
    	String currentPassword = getCurrentPw(con);
    	
    	// Check if the password is the same as the admin's password
    	if (password.equals("0000")) {
        	System.out.println("You can't set a password with \"0000\"");
            return false;
        }
    	
    	// Check if the new password is the same as the current password
        if (password.equals(currentPassword)) {
            System.out.println("You can't use the same password as the current one.");
            return false;
        }
    	
        // Check the length of the password
        if (password.length() < 8) {
        	System.out.println("Password must be at least 8 characters long.\n");
            return false;
        }

        // Check if the password meets the condition
        int count = 0;
        if (password.matches(".*[A-Z].*")) count++;
        if (password.matches(".*[a-z].*")) count++;
        if (password.matches(".*\\d.*")) count++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) count++;

        if(count >=3 == false) {
        	System.out.println("Password must include at least three of the following: uppercase letters, lowercase letters, digits, and special characters.\n");
        }
        return count >= 3;
    }

    //24.6.12: 어드민의 현재 비밀번호를 가져옴
    private static String getCurrentPw(Connection con) {
        String query = "SELECT m_pw FROM members WHERE m_id = 'admin'";
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    return rs.getString("m_pw");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return null;
    }
    
    // Check if the ISBN exists
    private static boolean isExistingIsbn(Connection con, String isbn) {  
    	
    	String checkQuery = "SELECT * FROM books WHERE b_isbn = ?";
        try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
            checkStatement.setString(1, isbn);
            try (ResultSet rs = checkStatement.executeQuery()) {
                if (rs.next()) {
                    System.out.println("This book cannot be added. The ISBN already exists.");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return false;
    }
}

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class User {

	/*
	 use library;

	CREATE VIEW rental_books_view AS
	SELECT 
	    r.r_id, 
	    r.m_id, 
	    r.b_no, 
	    r.r_rentalDate, 
	    r.r_whenToReturn, 
	    r.r_returnDate, 
	    r.r_isExtended, 
	    r.r_done,
	    b.b_title, 
	    b.b_author, 
	    b.b_isRental, 
	    b.b_isActive,
	    rr.r_rentalRequest AS rentalRequest,
	    rr.r_requestDate AS rentalRequestDate, 
	    re.r_returnRequest AS returnRequest,
	    re.r_requestDate AS returnRequestDate,
	    re.r_whenToReturn AS whenToReturn,
	    ex.e_extensionRequest AS extRequest,
	    ex.e_requestDate AS extRequestDate
	FROM 
	    rentals r
	INNER JOIN 
	    books b ON r.b_no = b.b_no
	LEFT JOIN 
	    rentalrequests rr ON r.b_no = rr.b_no
	LEFT JOIN 
	    returnrequests re ON r.b_no = re.b_no
	LEFT JOIN
	    extensionrequests ex ON r.b_no = ex.b_no;
	 */
	
	static String userId = "";
	static String userPw = "";
	
	public static void main(String[] args) {
		
		Dao dao = new Dao();
    	Connection con = dao.connectDB();
        Scanner scanner = new Scanner(System.in);
        int select = 0;
        int choice = 0;
        
        System.out.println("Welcome!");
        select = enterMenu(select, scanner, con);
        
        if(select == 3) {
        	return;
        }

        showRemainingDays(con);
        
        while (select != 8) {
        	try {
	        	System.out.println("Menu:");
	        	System.out.println("  [1] Book Search");
	        	System.out.println("  [2] Make a Request (Rental/Return/Extension)");
	        	System.out.println("  [3] Current Status/History");
	        	System.out.println("  [4] Profile Settings");
	        	System.out.println("  [5] Exit");
	        	System.out.print("\n>> ");
	        	select = scanner.nextInt();
	        	scanner.nextLine();
	        	
	        	switch (select) {
	        	
	        	case 1:
	            	bookInfo(scanner, con);
	                break;
	                
	            case 2:
	            	int select2 = 0;
	            	
	            	while (select2 != 4) {
		            	System.out.println("\nMake a Request:");
		            	System.out.println("  [1] Book Rental Request");
			        	System.out.println("  [2] Book Return Request");
			        	System.out.println("  [3] Extension Request");
		            	System.out.println("  [4] Exit");
		            	System.out.print("\n>> ");
		            	
		            	try {
			            	select2 = scanner.nextInt();
			            	scanner.nextLine();
			            	
			            	switch (select2) {
				            	case 1:
				            		rentalRequest(scanner, con);
				            		break;
				            	case 2:
				            		returnRequest(scanner, con);
				                	break;
				            	case 3:
				            		extRequest(scanner, con);
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
	            	
	            case 3:
	            	int select3 = 0;
	            	
	            	while (select3 != 3) {
		            	System.out.println("\nCurrent Status:");
		            	System.out.println("  [1] Rental/Return/Extension Status");
			        	System.out.println("  [2] History");
		            	System.out.println("  [3] Exit");
		            	System.out.print("\n>> ");
		            	
		            	try {
			            	select3 = scanner.nextInt();
			            	scanner.nextLine();
			            	
			            	switch (select3) {
				            	case 1:
				            		rentalStatus(scanner, con);
				                	break;
				            	case 2:
				            		historyStatus(scanner, con);
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
	            	
	            case 4:
	            	int select4 = 0;
	            	
	            	while (select4 != 4) {
		            	System.out.println("\nProfile Settings:");
		            	System.out.println("  [1] My Profile Info");
		            	System.out.println("  [2] Edit Profile");
		            	System.out.println("  [3] Delete the account");
		            	System.out.println("  [4] Exit");
		            	System.out.print("\n>> ");
		            	
		            	try {
			            	select4 = scanner.nextInt();
			            	scanner.nextLine();
			            	
			            	switch (select4) {
				            	case 1:
				            		profileInfo(scanner, con);
				            		break;
				            	case 2:
				            		editProfile(scanner, con);
				            		break;
				            	case 3:
				            		choice = accountDelete(scanner, con);
				                	if(choice == 1) {break;}
				                	else {return;}
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
	
    private static boolean authenticate(Connection con, String id, String pw) {
        String query = "SELECT COUNT(*) FROM members WHERE m_id = ? AND m_pw = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, id);
            statement.setString(2, pw);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return false;
    }
    
    private static int enterMenu(int select, Scanner scanner, Connection con) {
    	while (select != 3) {
    		try {
	        	System.out.println("\n[1] Login");
	        	System.out.println("[2] Register");
	        	System.out.println("[3] Exit");
	        	System.out.print(">> ");
	        	select = scanner.nextInt();
	        	scanner.nextLine();
	        	
	        	switch (select) {
	            case 1:
	            	login(scanner, con);
	            	return 1;
	            	
	            case 2:
	            	register(scanner, con);
	            	break;
	            	
	            case 3:
	            	System.out.println("\nGood Bye!");
	            	return 3;
	            	
	            default:
	                System.out.println("\nInvalid selection. Please try again.");
	        	}
	        	
		    	} catch (InputMismatchException e) {
		            System.out.println("\nInvalid selection. Please try again.");
		            scanner.nextLine(); // Clear buffer
		    	}
    		}
    	return select;
    }
    
    private static void login(Scanner scanner, Connection con) {
    	 while (true) {
             System.out.print("\nEnter ID: ");
             String id = scanner.nextLine();

             System.out.print("Enter PW: ");
             String pw = scanner.nextLine();

             // 24.6.10: Add a constraint
             if ("admin".equals(id)) {
     	        System.out.println("You can't use admin for ID.");
     	        continue;
     	    } else {
                 if (authenticate(con, id, pw)) {
                     userId = id;
                     userPw = pw;
                     
                     // Automatically delete users who are inactive for the first 6 months
                     int isDeleted = deleteUserAfterSixMonths(con);
                     
                     if(isDeleted == 0) {
                    	 System.exit(0);
                     }
                     
                     System.out.println("\nLogin successfully!\n");
                     break;
                 } else {
                     System.out.println("Invalid ID or password. Please try again.");
                 }
     	    }
         }
    }
    
    private static void register(Scanner scanner, Connection con) {
        while (true) {
            System.out.print("\nID: ");
            String id = scanner.nextLine();
            
            // 24.6.10: Add a constraint
            if ("admin".equals(id)) {
     	        System.out.println("You can't use admin for ID.");
     	        continue;
     	    }
            
            // 24.6.11: Check if the ID already in the 'deleted_users' table
            String delIdQuery = "SELECT COUNT(*) FROM deleted_users WHERE m_id = ?";
            try (PreparedStatement delIdStmt = con.prepareStatement(delIdQuery)) {
            	delIdStmt.setString(1, id);
                try (ResultSet rs = delIdStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("You can't use this ID. Please enter a different ID.");
                        continue;
                    }
                }
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
            
            // Check if the ID already exists
            String checkIdQuery = "SELECT COUNT(*) FROM members WHERE m_id = ?";
            try (PreparedStatement checkIdStmt = con.prepareStatement(checkIdQuery)) {
                checkIdStmt.setString(1, id);
                try (ResultSet rs = checkIdStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("ID already exists. Please enter a different ID.");
                        continue;
                    }
                }
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
            
            // Check if the ID is 6 characters long
            if (id.length() != 6) {
                System.out.println("ID must be 6 characters long. Please try again.");
                continue;
            }
            
            // Check if the ID is numeric
            if (!id.matches("\\d+")) {
                System.out.println("ID must be numeric. Please try again.");
                continue;
            }
            
            String pw = "";
            String confirm_pw = "";
            boolean passwordsMatch = false;
            while (!confirm_pw.equals("0") && !passwordsMatch) {
                while (true) {
                    System.out.print("PW: ");
                    pw = scanner.nextLine();
                    if (isValidPassword(con, pw)) {
                        break;
                    }
                }
                while (true) {
                    System.out.print("    Confirm PW (0 to go back): ");
                    confirm_pw = scanner.nextLine();
                    if (confirm_pw.equals("0")) {
                        return; // Go back
                    } else if (!pw.equals(confirm_pw)) {
                        System.out.println("    Passwords do not match. Please try again.\n");
                    } else {
                    	passwordsMatch = true;
                    	System.out.println();
                    	break;
                    }
                }
            }

            System.out.print("Name: ");
            String name = scanner.nextLine();
            
            boolean phoneExists = true;
            String phone = "";
            while (phoneExists) {
                System.out.print("Phone number: ");
                phone = scanner.nextLine();
                
                if (!phone.matches("\\d{10}")) {
                	System.out.println("Phone number must be exactly 10 digits long. Please try again.\n");
                }
                else {
	                // Check if the phone number already exists
	                String checkQuery = "SELECT COUNT(*) FROM members WHERE m_phone = ?";
	                try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
	                    checkStatement.setString(1, phone);
	                    try (ResultSet rs = checkStatement.executeQuery()) {
	                        if (rs.next() && rs.getInt(1) == 0) {
	                            phoneExists = false;
	                        } else {
	                            System.out.println("Phone number already exists. Please enter a different phone number.\n");
	                        }
	                    }
	                } catch (SQLException e) {
	                    System.err.println("SQLException: " + e.getMessage());
	                    return;
	                }
                }
            }
            
            LocalDate now = LocalDate.now();
            Date sqlNow = Date.valueOf(now);
            
            String query = "INSERT INTO members (m_id, m_pw, m_name, m_phone, m_regDate) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = con.prepareStatement(query)) {
                statement.setString(1, id);
                statement.setString(2, pw);
                statement.setString(3, name);
                statement.setString(4, phone);
                statement.setDate(5, sqlNow);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }

            System.out.println("\nRegistered successfully!");
            break;
        }
    }
    
    //24.6.23: Function that automatically delete members who have been inactive for 6 months
    public static int deleteUserAfterSixMonths(Connection con) {
        LocalDate now = LocalDate.now();
        Date regDate = null;

        String getRegDateQuery = "SELECT m_regDate FROM members WHERE m_id = ?";
        try (PreparedStatement getRegDateStmt = con.prepareStatement(getRegDateQuery)) {
            getRegDateStmt.setString(1, userId);
            try (ResultSet rs = getRegDateStmt.executeQuery()) {
                if (rs.next()) {
                    regDate = rs.getDate("m_regDate");
                }
            }
            
            LocalDate afterSixMonths = regDate.toLocalDate().plusMonths(6);
            
            // If today's date is 6 months from the registration,
            if (now.isAfter(afterSixMonths)) {
                String checkRenReqQuery = "SELECT COUNT(*) FROM rentalrequests WHERE m_id = ?";
                String delQuery = "DELETE FROM members WHERE m_id = ?";
                
                try (PreparedStatement checkStmt = con.prepareStatement(checkRenReqQuery);
                     PreparedStatement deleteStmt = con.prepareStatement(delQuery)) {
                    
                    checkStmt.setString(1, userId);
                    try (ResultSet rsCheck = checkStmt.executeQuery()) {
                        if (rsCheck.next() && rsCheck.getInt(1) == 0) { // Delete only if there is no rental request
                            deleteStmt.setString(1, userId);
                            deleteStmt.executeUpdate();
                            System.out.println("\nYour account has been canceled because you have been inactive for 6 months.\nGoodbye!");
                            return 0;
                        }
                    }
                }
            }            
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return 1;
    }

    private static boolean isValidPassword(Connection con, String password) {
    	
    	String currentPassword = getCurrentPw(con);
    	
    	// Check if the new password is the same as the current password
        if (password.equals(currentPassword)) {
            System.out.println("You can't use the same password as the current one.\n");
            return false;
        }
        
        if (password.length() < 8) {
        	System.out.println("Password must be at least 8 characters long\n");
            return false;
        }

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
    
    //24.6.12: Get the user's current password
    private static String getCurrentPw(Connection con) {
        String query = "SELECT m_pw FROM members WHERE m_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
        	stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("m_pw");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        return null;
    }
    
    private static void rentalRequest(Scanner scanner, Connection con) {
    	int select = 0;
    	
    	while (select != 3) {
        	System.out.println("\nRental Request:");
        	System.out.println("  [1] Process Rental Request");
        	System.out.println("  [2] Cancel existing Request");
        	System.out.println("  [3] Exit");
        	System.out.print("\n>> ");
        	
        	try {
            	select = scanner.nextInt();
            	scanner.nextLine();
            	
            	switch (select) {
	            	case 1:
	            		processRenReq(scanner, con);
	            		break;
	            	case 2:
	                	cancelRenReq(scanner, con);
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
    }
    
    private static void returnRequest(Scanner scanner, Connection con) {
    	int select = 0;
    	
    	while (select != 3) {
        	System.out.println("\nReturn Request:");
        	System.out.println("  [1] Process Return Request");
        	System.out.println("  [2] Cancel existing Request");
        	System.out.println("  [3] Exit");
        	System.out.print("\n>> ");
        	
        	try {
            	select = scanner.nextInt();
            	scanner.nextLine();
            	
            	switch (select) {
	            	case 1:
	            		processRtnReq(scanner, con);
	            		break;
	            	case 2:
	            		cancelRtnReq(scanner, con);
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
    }
    
    private static void processRtnReq(Scanner scanner, Connection con) {
        int count = 0;
        
        //24.6.9
        //String query = "SELECT * FROM rental_books_view WHERE r_done = 1 AND m_id = ?";
        
        //24.6.9
        String query = "SELECT b.b_title, b.b_author, r.b_no, r.r_rentalDate, r.r_whenToReturn, r.r_id "
				        + "FROM rentals r "
				        + "JOIN books b ON r.b_no = b.b_no "
				        + "WHERE r.m_id = ? AND r.r_done = 1";
        try (PreparedStatement statement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setString(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nNo rented book found.\n");
                    return;
                }
                System.out.println("\nRental status:");
                count = 0;
                do {
                    count++;
                    System.out.println("\n[" + count + "] Book Title: " + rs.getString("b_title"));
                    System.out.println("    Rental Start Date: " + rs.getDate("r_rentalDate"));
                    System.out.println("    Rental End Date: " + rs.getDate("r_whenToReturn"));
                } while (rs.next());

                int reqNum = 0;
    	        boolean validInput = false;
    	        
    	        while (!validInput) {
    	        	System.out.print("\n>> Enter the number of the Return Request to process: ");
    	            reqNum = scanner.nextInt();
    	            scanner.nextLine();
    	            
    	            if (reqNum > 0 && reqNum <= count) {
    	                validInput = true;
    	            } else {
    	                System.out.println("Invalid selection. Please try again.\n");
    	            }
    	        }

                System.out.print("Are you sure you want to process it? (Y/N): ");
                String choice = (scanner.nextLine()).toLowerCase();

                if (choice.equals("n")) {
                    System.out.println("Request terminated.\n");
                    return;
                }
                
                rs.absolute(reqNum);

                LocalDate now = LocalDate.now();
                Date sqlNow = Date.valueOf(now);

                // Update rentals table
                String updateQuery = "UPDATE rentals SET r_isReturnReq = 1 WHERE r_id = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, rs.getInt("r_id"));
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("SQLException (update rentals): " + e.getMessage());
                }

                // Add return request
                String addQuery = "INSERT INTO returnrequests (r_id, m_id, b_no, r_returnRequest, r_requestDate, r_whenToReturn) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(addQuery)) {
                    stmt.setInt(1, rs.getInt("r_id")); // r_id is provided from rs
                    stmt.setString(2, userId);
                    stmt.setInt(3, rs.getInt("b_no"));
                    stmt.setInt(4, 1);
                    stmt.setDate(5, sqlNow);
                    stmt.setDate(6, rs.getDate("r_whenToReturn"));
                    stmt.executeUpdate();
                    System.out.println("\nReturn request processed successfully.\n");
                } catch (SQLException e) {
                    System.err.println("SQLException (insert return request): " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    /*
     private static void returnRequest(Scanner scanner, Connection con) throws SQLException {
        int count = 0;
        String query = "SELECT * FROM rental_books_view WHERE r_done = 0 AND m_id = ?";
        try (PreparedStatement statement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setString(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nConfirm Process:");
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Book Title: " + rs.getString("b_title"));
                        System.out.println("    Rental Start Date: " + rs.getDate("r_rentalDate"));
                        System.out.println("    Rental End Date: " + rs.getDate("r_whenToReturn"));
                        System.out.println("    Return Request: " + rs.getDate("r_returnDate"));
                    } while (rs.next());
                }
            }
        }

        String query2 = "SELECT * FROM rental_books_view WHERE r_done = 1 AND m_id = ?";
        try (PreparedStatement statement = con.prepareStatement(query2, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setString(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("\nNo rental book found.\n");
                    return;
                }
                System.out.println("\nRental status:");
                count = 0;
                do {
                    count++;
                    System.out.println("\n[" + count + "] Book Title: " + rs.getString("b_title"));
                    System.out.println("    Book Number: " + rs.getString("b_no"));
                    System.out.println("    Rental Start Date: " + rs.getDate("r_rentalDate"));
                    System.out.println("    Rental End Date: " + rs.getDate("r_whenToReturn"));
                } while (rs.next());

                System.out.print(">> Enter the number of the Return Request to process: ");
                int requestNumber = scanner.nextInt();
                scanner.nextLine();

                rs.absolute(requestNumber);

                LocalDate now = LocalDate.now();
                Date sqlNow = Date.valueOf(now);

                // Update rentals table
                String updateQuery = "UPDATE rentals SET r_done = 0 WHERE r_id = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, rs.getInt("r_id"));
                    updateStmt.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("SQLException (update rentals): " + e.getMessage());
                }

                // Add return request
                String addQuery = "INSERT INTO returnrequests (r_id, m_id, b_no, r_returnRequest, r_requestDate, r_whenToReturn) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = con.prepareStatement(addQuery)) {
                    stmt.setInt(1, rs.getInt("r_id")); // r_id is provided from rs
                    stmt.setString(2, userId);
                    stmt.setInt(3, rs.getInt("b_no"));
                    stmt.setInt(4, 1);
                    stmt.setDate(5, sqlNow);
                    stmt.setDate(6, rs.getDate("r_whenToReturn"));
                    stmt.executeUpdate();
                    System.out.println("\nReturn request processed successfully.\n");
                } catch (SQLException e) {
                    System.err.println("SQLException (insert return request): " + e.getMessage());
                }
            }
        }
    }
     */

    private static void cancelRtnReq(Scanner scanner, Connection con) {
    	String query = "SELECT rr.r_requestDate, b.b_title, b.b_author, b.b_no " +
                "FROM rentalrequests rr " +
                "LEFT JOIN books b ON rr.b_no = b.b_no " +
                "WHERE rr.m_id = ? AND rr.r_rentalRequest = 1";
    	
    	try (PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
            	if(rs.next()) {
                	int count = 0;
                	System.out.println("\nReturn Request in progress:");
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                        System.out.println("    Author: " + rs.getString("b_author"));
                        System.out.println("    Requested Date: " + rs.getDate("r_requestDate"));
                    } while (rs.next());

                    int reqNum = 0;
        	        boolean validInput = false;
        	        
        	        while (!validInput) {
        	        	System.out.print("\n>> Enter the number of the Return Request to cancel: ");
        	            reqNum = scanner.nextInt();
        	            scanner.nextLine();
        	            
        	            if (reqNum > 0 && reqNum <= count) {
        	                validInput = true;
        	            } else {
        	                System.out.println("Invalid selection. Please try again.\n");
        	            }
        	        }

                    rs.absolute(reqNum);
                    
                    System.out.println("\nConfirmation");
                    System.out.println("    Title: " + rs.getString("b_title"));
                    System.out.println("    Author: " + rs.getString("b_author"));

                    int bookId = rs.getInt("b_no");
                    
                    System.out.println();
                    System.out.print("Are you sure you want to cancel? (Y/N): ");
                    String choice = (scanner.nextLine()).toLowerCase();

                    if (choice.equals("n")) {
                        System.out.println("Request terminated.\n");
                        return;
                    } else {
                    	String delQuery = "DELETE FROM returnrequests WHERE b_no = ? AND m_id = ? AND r_returnRequest = 1";
                        try (PreparedStatement stmt2 = con.prepareStatement(delQuery)) {
                        	stmt2.setInt(1, bookId);
                        	stmt2.setString(2, userId);
                        	stmt2.executeUpdate();
                            System.out.println("Request cancelled successfully!\n");
                        } catch (SQLException e) {
                            System.err.println("SQLException: " + e.getMessage());
                        }
                    }
            	} else {
            		System.out.println("\nNo return request found.\n");
            	}
            }
    	} catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.11: Added 'deleted_users' table
    /*
    CREATE TABLE deleted_users (
    d_id INT AUTO_INCREMENT PRIMARY KEY,
    m_id VARCHAR(15) NOT NULL);
	*/
    
    private static int accountDelete(Scanner scanner, Connection con) {
    	//24.6.12: If a user has books borrowed, admin cannot delete a user
    	try {
            // Check if there are any rentals with r_done = 1 for the user
            String checkQuery = "SELECT COUNT(*) FROM rentals WHERE m_id = ? AND r_done = 1";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setString(1, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("\nAccount deletion is not allowed. There are active rentals.");
                        return 1;
                    }
                }
            }
        } catch (SQLException e) {
        	System.err.println("SQLException (Account Delete): " + e.getMessage());
        }
    	
    	System.out.print("\nAre you sure you want to delete the account? (Y/N): ");
    	String choice = (scanner.nextLine()).toLowerCase();
    	
    	if(choice.equals("n")) {
    		System.out.println("Request terminated.");
    		return 1;
    	}
    	else {
	    	String query = "DELETE FROM members WHERE m_id = ?";
	        try (PreparedStatement statement = con.prepareStatement(query)) {
	            statement.setString(1, userId);
	            statement.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
	        
	        //24.6.11: Add a m_id record to 'deleted_users' table
	        String addQuery = "INSERT INTO deleted_users (m_id) VALUE (?)";
	        try(PreparedStatement stmt = con.prepareStatement(addQuery)) {
	        	stmt.setString(1, userId);
	            stmt.executeUpdate();
			} catch (SQLException e) {
	            System.err.println("SQLException: " + e.getMessage());
	        }
	        System.out.println("\nAccount deleted successfully!");
            System.out.println("Good Bye!\n");
    	}
    	return 2;
    }
    
    private static void bookInfo(Scanner scanner, Connection con) {
        while (true) {
            System.out.println("\nSelect search criteria (0 to exit):");
            System.out.println("[1] Title");
            System.out.println("[2] Author");
            System.out.println("[3] ISBN");
            System.out.println("[4] Publisher");
            System.out.println("[5] Category");
            System.out.print("\n>> ");
            
            try {
                int criteria = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (criteria == 0) {
                	System.out.println();
                    break;
                }

                //24.6.12: commented out
                //String query = "SELECT * FROM books WHERE ";
                
                //24.6.12: Modify query to output categories / 24.6.13: commented out
                //String query = "SELECT b.*, c.c_name FROM books b " +
                //        		"LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE ";
                
                //24.6.12: When searching for a book with the same isbn, instead of showing multiple results, only one result and the number of copies are displayed
	            //			When searching for books with the same isbn, all b_nos for each book are displayed.
                String query = "SELECT b.b_isbn, b.b_title, b.b_author, b.b_year, b.b_publisher, c.c_name, " +
		                        "GROUP_CONCAT(b.b_no) AS book_numbers, COUNT(*) AS copy_count " +
		                        "FROM books b LEFT JOIN categories c ON b.b_category = c.c_dcode WHERE ";
                String param = "";

                switch (criteria) {
                    case 1:
                        System.out.print("\nEnter Title: ");
                        param = scanner.nextLine();
                        query += "b_title LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 2:
                        System.out.print("\nEnter Author: ");
                        param = scanner.nextLine();
                        query += "b_author LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 3:
                        System.out.print("\nEnter ISBN: ");
                        param = scanner.nextLine();
                        query += "b_isbn = ?";
                        break;
                    case 4:
                        System.out.print("\nEnter Publisher: ");
                        param = scanner.nextLine();
                        query += "b_publisher LIKE ?";
                        param = "%" + param + "%";
                        break;
                    case 5:
                    	System.out.println();
                        showCategories(con);
                        System.out.print("\nEnter Category (number): ");
                        param = scanner.nextLine();
                        query += "b_category = ?";
                        break;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        break;
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
                                
                                //24.6.13: Prints if the current book is rented
	                            //24.6.15: commented out
                                // Check if the book is rented
                                /*if (isBookRented(con, rs.getInt("b_no"))) {
                                    System.out.println("      Status: Currently Rented");
                                } else {
                                    System.out.println("      Status: Available");
                                }*/
	                            
	                            //24.6.15: Prints how many books have been rented using the book_numbers value.
	                            isBookRented(con, rs.getString("book_numbers"), rs.getInt("copy_count"));
	                            
                                
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
    
    //24.6.13: Function to check whether the current book is rented
    //24.6.15: Modified to print the number of copies of books currently available for rental
    private static void isBookRented(Connection con, String bookNumbers, int copyCount) throws SQLException {
        String query = "SELECT COUNT(*) AS rented_count FROM rentals WHERE b_no IN (" + bookNumbers + ") AND r_done = 1";
        try (PreparedStatement rentalStmt = con.prepareStatement(query);
                ResultSet rentalRs = rentalStmt.executeQuery()) {
               if (rentalRs.next()) {
               		System.out.print("      Available Copies: " + (copyCount - rentalRs.getInt("rented_count")));
               }
               System.out.println();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
  //24.6.13: Function showing categories
    private static void showCategories(Connection con) throws SQLException {
    	String query = "SELECT c_name, c_dcode FROM categories ORDER BY c_code1 ASC";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Categories:");
            while (rs.next()) {
                String name = rs.getString("c_name");
                String dcode = rs.getString("c_dcode");
                if(dcode.length() == 2) {
                	System.out.println("\n- " + name);
                }
                else {
                	System.out.println(String.format("		%-25s : %s", name, dcode));
                }
            }
            System.out.println();
        } catch (SQLException e) {
            con.rollback();  // Rollback the transaction in case of an error
            throw e;
        }
	}
    
    private static void extRequest(Scanner scanner, Connection con) {
    	int select = 0;
    	
    	while (select != 3) {
        	System.out.println("\nExtension Request:");
        	System.out.println("  [1] Process Extension Request");
        	System.out.println("  [2] Cancel existing Request");
        	System.out.println("  [3] Exit");
        	System.out.print("\n>> ");
        	
        	try {
            	select = scanner.nextInt();
            	scanner.nextLine();
            	
            	switch (select) {
	            	case 1:
	            		processExtReq(scanner, con);
	            		break;
	            	case 2:
	            		cancelExtReq(scanner, con);
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
    }
    
    private static void processExtReq(Scanner scanner, Connection con) {
    	//String query = "SELECT * FROM rental_books_view WHERE r_isExtended = 0";
    	
    	// If m_id is the same and r_isExtended is 0
    	String query = "SELECT r.r_rentalDate, r.r_whenToReturn, b.b_title, b.b_author, r.b_no " +
                "FROM rentals r " +
                "LEFT JOIN books b ON r.b_no = b.b_no " +
                "WHERE r.m_id = ? AND r.r_isExtended = 0 AND r_done = 1";
    	 //try (Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    	 //        ResultSet rs = stmt.executeQuery(query)) {
    	try (PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
    		stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {	
               int count = 0;
               if (rs.next()) {
            	   System.out.println("\nCurrent Rental Status:");
                   do {
                	   count++;
                       System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                       System.out.println("    Author: " + rs.getString("b_author"));
                       System.out.println("    Rental Start Date: " + rs.getString("r_rentalDate"));
                       System.out.println("    Rental End Date: " + rs.getString("r_whenToReturn"));
                   } while (rs.next());
                   
                   int reqNum = 0;
	       	        boolean validInput = false;
	       	        
	       	        while (!validInput) {
	       	        	System.out.print("\n>> Enter the number of the Extension Request to process: ");
	       	            reqNum = scanner.nextInt();
	       	            scanner.nextLine();
	       	            
	       	            if (reqNum > 0 && reqNum <= count) {
	       	                validInput = true;
	       	            } else {
	       	                System.out.println("Invalid selection. Please try again.\n");
	       	            }
	       	        }

                   rs.absolute(reqNum);
                   
                   System.out.print("\nAre you sure you want to request for extension? (Y/N): ");
                   String choice = (scanner.nextLine()).toLowerCase();

                   if (choice.equals("n")) {
                       System.out.println("Request terminated.\n");
                       return;
                   }

                   LocalDate now = LocalDate.now();
                   Date sqlNow = Date.valueOf(now);

                   // Add return request
                   String addQuery = "INSERT INTO extensionrequests (m_id, b_no, e_extensionRequest, e_requestDate) VALUES (?, ?, ?, ?)";
                   try (PreparedStatement stmt2 = con.prepareStatement(addQuery)) {
                       stmt2.setString(1, userId);
                       stmt2.setInt(2, rs.getInt("b_no"));
                       stmt2.setInt(3, 1);
                       stmt2.setDate(4, sqlNow);
                       stmt2.executeUpdate();
                       System.out.println("Extension request processed successfully.\n");
                   } catch (SQLException e) {
                       System.err.println("SQLException (insert extension request): " + e.getMessage());
                   }
               } else {
                   System.out.println("There are no books available for extension.\n");
               }
           } catch (SQLException e) {
        	   System.err.println("SQLException: " + e.getMessage());
           }
    	} catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void cancelExtReq(Scanner scanner, Connection con) {
    	String query = "SELECT e.e_requestDate, b.b_title, b.b_author, b.b_no " +
                "FROM extensionrequests e " +
                "LEFT JOIN books b ON e.b_no = b.b_no " +
                "WHERE e.m_id = ? AND e.e_extensionRequest = 1";
    	
    	try (PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
            	if(rs.next()) {
                	int count = 0;
                	System.out.println("\nExtension Request in progress:");
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                        System.out.println("    Author: " + rs.getString("b_author"));
                        System.out.println("    Requested Date: " + rs.getDate("r_requestDate"));
                    } while (rs.next());

                    
                    int reqNum = 0;
        	        boolean validInput = false;
        	        
        	        while (!validInput) {
        	        	System.out.print("\n>> Enter the number of the Extension Request to cancel: ");
        	            reqNum = scanner.nextInt();
        	            scanner.nextLine();
        	            
        	            if (reqNum > 0 && reqNum <= count) {
        	                validInput = true;
        	            } else {
        	                System.out.println("Invalid selection. Please try again.\n");
        	            }
        	        }

                    rs.absolute(reqNum);
                    
                    System.out.println("\nConfirmation");
                    System.out.println("    Title: " + rs.getString("b_title"));
                    System.out.println("    Author: " + rs.getString("b_author"));

                    int bookId = rs.getInt("b_no");
                    
                    System.out.println();
                    System.out.print("Are you sure you want to cancel? (Y/N): ");
                    String choice = (scanner.nextLine()).toLowerCase();

                    if (choice.equals("n")) {
                        System.out.println("Request terminated.\n");
                        return;
                    } else {
                    	String delQuery = "DELETE FROM extensionrequests WHERE b_no = ? AND m_id = ? AND e_extensionRequest = 1";
                        try (PreparedStatement stmt2 = con.prepareStatement(delQuery)) {
                        	stmt2.setInt(1, bookId);
                        	stmt2.setString(2, userId);
                        	stmt2.executeUpdate();
                            System.out.println("Request cancelled successfully!\n");
                        } catch (SQLException e) {
                            System.err.println("SQLException: " + e.getMessage());
                        }
                    }
            	} else {
            		System.out.println("\nNo extension request found.\n");
            	}
            }
    	} catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    private static void editProfile(Scanner scanner, Connection con) {
    	int field = -1;
    	while(field != 0) {
	    	System.out.println("\nSelect field to edit (0 to exit):");
	        System.out.println("[1] Name");
	        System.out.println("[2] Password");
	        System.out.println("[3] Phone number");
	        System.out.print("\n>> ");
	        
	        try {
	        field = scanner.nextInt();
	        scanner.nextLine();
	
	        if(field == 0) {
	        	break;
	        }
	        
	        String query = "UPDATE members SET ";
	        String newValue = "";
	
	        switch (field) {
	            case 1:
	                System.out.print("\nEnter new Name: ");
	                newValue = scanner.nextLine();
	                query += "m_name = ? WHERE m_id = ?";
	                break;
	            case 2:
	            	// 24.6.7
	            	boolean pwExists = true;
	            	while(pwExists) {
		            	System.out.print("\nEnter the previous password: ");
		            	String prePw = scanner.nextLine();
		            	
		            	String check = "SELECT m_pw FROM members WHERE m_id = ?";
		            	try (PreparedStatement statement = con.prepareStatement(check)) {
		                    statement.setString(1, userId);
		                    try (ResultSet rs = statement.executeQuery()) {
		                    	if(rs.next() && prePw.equals(rs.getString("m_pw"))) {
		                    		pwExists = false;
		                    	} else {
		                    		System.out.print("Password is different. Try it again.\n");
		                    		}
		                    }
		                } catch (SQLException e) {
		                    System.err.println("SQLException: " + e.getMessage());
		                }
	            	}

			        String pw = "";
		            boolean passwordsMatch = false;
		            while (!passwordsMatch) {
		            	while (true) {
			                while (true) {
			                    System.out.print("New password: ");
			                    pw = scanner.nextLine();
			                    if (isValidPassword(con, pw)) {
			                        break;
			                    }
			                }
			                while (true) {
			                    System.out.print("    Confirm PW (0 to go back): ");
			                    newValue = scanner.nextLine();
			                    if ("0".equals(newValue)) {
			                    	System.out.println();
			                        break; // Go back
			                    } else if (!pw.equals(newValue)) {
			                        System.out.println("    Passwords do not match. Please try again.\n");
			                    } else {
			                        passwordsMatch = true;
			                        break;
			                    }
			                }
			                if (passwordsMatch == true) {break;}
		            	}
		            }
		            query += "m_pw = ? WHERE m_id = ?";
	                break;

	            case 3:
	            	// 24.6.7
	            	boolean phoneExists = true;
	            	
	            	while(phoneExists) {
		            	System.out.print("\nEnter the previous phone number: ");
		            	String prePhone = scanner.nextLine();
		            	
		            	String check = "SELECT m_phone FROM members WHERE m_id = ?";
		            	try (PreparedStatement statement = con.prepareStatement(check)) {
		                    statement.setString(1, userId);
		                    try (ResultSet rs = statement.executeQuery()) {
		                    	if(rs.next() && prePhone.equals(rs.getString("m_phone"))) {
		                    		phoneExists = false;
		                    	} else {
		                    		System.out.print("Password is different. Try it again.\n");
		                    		}
		                    }
		                } catch (SQLException e) {
		                    System.err.println("SQLException: " + e.getMessage());
		                }
	            	}
	            	
	            	phoneExists = true;
	                while (phoneExists) {
	                    System.out.print("New phone number: ");
	                    newValue = scanner.nextLine();
	                    
	                    if (!newValue.matches("\\d{10}")) {
	                    	System.out.println("Phone number must be exactly 10 digits long. Please try again.\n");
	                    }
	                    else {
	    	                // Check if the phone number already exists
	    	                String checkQuery = "SELECT COUNT(*) FROM members WHERE m_phone = ?";
	    	                try (PreparedStatement checkStatement = con.prepareStatement(checkQuery)) {
	    	                    checkStatement.setString(1, newValue);
	    	                    try (ResultSet rs = checkStatement.executeQuery()) {
	    	                        if (rs.next() && rs.getInt(1) == 0) {
	    	                            phoneExists = false;
	    	                        } else {
	    	                            System.out.println("You cannot set a same phone number. Please enter a different phone number.\n");
	    	                        }
	    	                    }
	    	                } catch (SQLException e) {
	    	                    System.err.println("SQLException: " + e.getMessage());
	    	                    return;
	    	                }
	                    }
	                }
	                query += "m_phone = ? WHERE m_id = ?";
	                break;
	            default:
	                System.out.println("Invalid selection.");
	                return;
	        }
	        
	        // Update profile
	        try (PreparedStatement statement = con.prepareStatement(query)) {
	            statement.setString(1, newValue);
	            statement.setString(2, userId);
	            statement.executeUpdate();
	            System.out.println("\nProfile updated successfully!");
	        } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid selection. Please try again.");
            scanner.nextLine(); // Clear buffer
        }
    }
}
    
    private static void historyStatus(Scanner scanner, Connection con) {
        // 24.6.10
        // String query = "SELECT * FROM rental_books_view WHERE returnRequest = ?";

        String query = "SELECT b.b_title, b.b_author, r.r_rentalDate, r.r_returnDate, r.r_whenToReturn, r.r_isExtended, r.r_done "
                     + "FROM rentals r "
                     + "JOIN books b ON r.b_no = b.b_no "
                     + "WHERE r.m_id = ? AND r.r_done = 0";
        int count = 0;

        try {
            try (PreparedStatement statement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                statement.setString(1, userId);
                try (ResultSet rs = statement.executeQuery()) {
                    System.out.println("\nMy History:");
                    if (!rs.next()) {
                        System.out.println("No records found.");
                    } else {
                        do {
                            count++;
                            System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                            System.out.println("    Author: " + rs.getString("b_author"));
                            System.out.println("    Rental Start Date: " + rs.getDate("r_rentalDate"));
                            System.out.print("    Rental End Date: " + rs.getDate("r_whenToReturn") + " (Extended: ");
                            if (rs.getBoolean("r_isExtended")) {
                                System.out.println("Yes)");
                            } else {
                                System.out.println("No)");
                            }
                            System.out.println("    Return Date: " + rs.getDate("r_returnDate"));
                        } while (rs.next());
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) { // SQLException 처리
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    
    private static void rentalStatus(Scanner scanner, Connection con) {
        // 24/6/9
        /*String query = "SELECT b.b_title, b.b_author, r.r_rentalDate, r.r_returnDate, r.r_whenToReturn, r.r_isExtended, rq.r_rentalRequest "
                       + "FROM rentalrequests rq "
                       + "JOIN rentals r ON rq.b_no = r.b_no "
                       + "JOIN books b ON rq.b_no = b.b_no "
                       + "WHERE rq.m_id = ? AND rq.r_rentalRequest = ?";*/

        String[] queries = {
            // Currently rented books
    		"SELECT b.b_title, b.b_author, r.r_rentalDate, r.r_returnDate, r.r_whenToReturn, r.r_isExtended, r.r_done, rr.r_requestDate, rr.r_returnRequest, e.e_requestDate, e.e_extensionRequest "
            + "FROM rentals r "
            + "JOIN books b ON r.b_no = b.b_no "
            + "LEFT JOIN returnrequests rr ON r.b_no = rr.b_no "
            + "LEFT JOIN extensionrequests e ON r.b_no = e.b_no "
            + "WHERE r.m_id = ? AND r.r_done = 1",

            // Rental request in progress
            "SELECT b.b_title, b.b_author, rq.r_requestDate "
            + "FROM rentalrequests rq "
            + "JOIN books b ON rq.b_no = b.b_no "
            + "WHERE rq.m_id = ? AND rq.r_rentalRequest = 1"
        };

        String[] messages = {
            "\nCurrently rented books:",
            "\nRental request in progress:"
        };

        for (int i = 0; i < 2; i++) {
            int count = 0;
            
            try (PreparedStatement statement = con.prepareStatement(queries[i], ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                statement.setString(1, userId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println(messages[i] + "\nNo records found.");
                        continue;
                    }
                    System.out.println(messages[i]);
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                        System.out.println("    Author: " + rs.getString("b_author"));
                        if (i == 0) {
                            System.out.println("    Rental Start Date: " + rs.getDate("r_rentalDate"));
                            System.out.print("    Rental End Date: " + rs.getDate("r_whenToReturn") + " (Extended: ");
                            if(rs.getBoolean("r_isExtended")) {
                                System.out.println("Yes)");
                            } else {
                                System.out.println("No)");
                            }
                            System.out.print("    Return request: ");
                            if(rs.getBoolean("r_returnRequest")) {
                                System.out.println("Yes (On " + rs.getDate("r_requestDate") + ")");
                            } else {
                                System.out.println("No");
                            }
                            System.out.print("    Extension request: ");
                            if(rs.getBoolean("e_extensionRequest")) {
                                System.out.println("Yes (On " + rs.getDate("e_requestDate") + ")");
                            } else {
                                System.out.println("No");
                            }
                        } else {
                            System.out.println("    Request Date: " + rs.getDate("r_requestDate"));
                        }
                    } while (rs.next());
                }
            } catch (SQLException e) { // SQLException 처리
                System.err.println("SQLException: " + e.getMessage());
            }
        }
        System.out.println();
    }
    
    private static void profileInfo(Scanner scanner, Connection con) {
    	String query = "SELECT * FROM members WHERE m_id = ?";
    	try (PreparedStatement statement = con.prepareStatement(query)) {
	        statement.setString(1, userId);
	        try (ResultSet rs = statement.executeQuery()) {
	        	if (!rs.next()) { // Check if there's a result
	                System.out.println("\nNo profile information found.\n");
	                return;
	            }
                System.out.println("\nName: " + rs.getString("m_name"));
                System.out.println("ID: " + rs.getString("m_id"));
                System.out.println("Phone Number: " + rs.getString("m_phone"));
                System.out.println("\nLevel: " + rs.getString("m_level"));
                System.out.println("Current Point: " + rs.getInt("m_point"));
                System.out.println("Late Return: " + rs.getString("m_NoOfLateReturn")); //나중에 6개월 지나면 하나씩 지워주는걸로
	        }
	    } catch (SQLException e) {
	        System.err.println("SQLException: " + e.getMessage());
	    }
    }
    
    private static void processRenReq(Scanner scanner, Connection con) {
        String condition = "SELECT COUNT(*) FROM rentals WHERE m_id = ? AND r_done != -1";
        try (PreparedStatement statement = con.prepareStatement(condition)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int rentalCount = resultSet.getInt(1);
                    if (rentalCount >= 3) {
                        System.out.println("\nYou cannot rent more than 3 books.\n");
                        return;
                    } else {
                        LocalDate now = LocalDate.now();
                        Date sqlNow = Date.valueOf(now);

                        int criteria = -1;
                        while (criteria != 0) {
                            System.out.println("\nSelect search criteria (0 to go back):");
                            System.out.println("[1] Title");
                            System.out.println("[2] ISBN");
                            System.out.println("[3] Author");
                            System.out.println("[4] Publisher");
                            System.out.print("\n>> ");
                            try {
                                criteria = scanner.nextInt();
                                scanner.nextLine();
                                System.out.println();

                                String query = "SELECT * FROM books WHERE ";
                                String param = "";

                                switch (criteria) {
                                    case 1:
                                        System.out.print("Enter Title: ");
                                        param = scanner.nextLine();
                                        query += "b_title LIKE ?";
                                        param = "%" + param + "%";
                                        break;
                                    case 2:
                                        System.out.print("Enter ISBN: ");
                                        param = scanner.nextLine();
                                        query += "b_isbn = ?";
                                        break;
                                    case 3:
                                        System.out.print("Enter Author: ");
                                        param = scanner.nextLine();
                                        query += "b_author LIKE ?";
                                        param = "%" + param + "%";
                                        break;
                                    case 4:
                                        System.out.print("Enter Publisher: ");
                                        param = scanner.nextLine();
                                        query += "b_publisher LIKE ?";
                                        param = "%" + param + "%";
                                        break;
                                    case 0:
                                        return;
                                    default:
                                        System.out.println("Invalid selection. Please try again.");
                                        continue;
                                }

                                try (PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                                    stmt.setString(1, param);
                                    try (ResultSet rs = stmt.executeQuery()) {
                                        if (rs.next()) {
                                            int count = 0;
                                            do {
                                                count++;
                                                System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                                                System.out.println("      Author: " + rs.getString("b_author"));
                                                System.out.println("      ISBN: " + rs.getString("b_isbn"));
                                                System.out.println("      Released Year: " + rs.getInt("b_year"));
                                                System.out.println("      Publisher: " + rs.getString("b_publisher"));
                                            } while (rs.next());

                                            int reqNum = 0;
                                	        boolean validInput = false;
                                	        
                                	        while (!validInput) {
                                	        	System.out.print("\n>> Enter the number of the Return Request to process: ");
                                	            reqNum = scanner.nextInt();
                                	            scanner.nextLine();
                                	            
                                	            if (reqNum > 0 && reqNum <= count) {
                                	                validInput = true;
                                	            } else {
                                	                System.out.println("Invalid selection. Please try again.\n");
                                	            }
                                	        }

                                            rs.absolute(reqNum);

                                            System.out.println("\nConfirmation");
                                            System.out.println("    Title: " + rs.getString("b_title"));
                                            System.out.println("    Author: " + rs.getString("b_author"));
                                            System.out.println("    ISBN: " + rs.getString("b_isbn"));
                                            System.out.println("    Released Year: " + rs.getInt("b_year"));
                                            System.out.println("    Publisher: " + rs.getString("b_publisher"));

                                            int bookId = rs.getInt("b_no");
                                            System.out.println();
                                            System.out.print("Are you sure you want to rent? (Y/N): ");
                                            String choice = (scanner.nextLine()).toLowerCase();

                                            if (choice.equals("n")) {
                                                System.out.println("Request terminated.\n");
                                                return;
                                            } else {
                                                String query2 = "SELECT rq.r_rentalRequest, r.r_done " +
                                                        "FROM rentalrequests rq " +
                                                        "LEFT JOIN rentals r ON rq.m_id = r.m_id AND rq.b_no = r.b_no " +
                                                        "WHERE rq.m_id = ? AND rq.b_no = ?";

                                                try (PreparedStatement stmt2 = con.prepareStatement(query2)) {
                                                    stmt2.setString(1, userId);
                                                    stmt2.setInt(2, bookId);
                                                    try (ResultSet rs2 = stmt2.executeQuery()) {
                                                        if (rs2.next()) {
                                                            boolean rentalRequested = rs2.getBoolean("r_rentalRequest");
                                                            boolean isDone = rs2.getBoolean("r_done");

                                                            if (rentalRequested) {
                                                                System.out.println("This book is already requested for rental.\n");
                                                                return;
                                                            } else if (isDone) {
                                                                System.out.println("This book is already rented out.\n");
                                                                return;
                                                            }
                                                        }
                                                    }
                                                }

                                                String query3 = "INSERT INTO rentalrequests (m_id, b_no, r_requestDate) VALUES (?, ?, ?)";
                                                try (PreparedStatement stmt3 = con.prepareStatement(query3)) {
                                                    stmt3.setString(1, userId);
                                                    stmt3.setInt(2, bookId);
                                                    stmt3.setDate(3, sqlNow);
                                                    stmt3.executeUpdate();
                                                }
                                                System.out.println("Rental request processed successfully.\n");
                                            }
                                        } else {
                                            System.out.println("No books found matching the criteria.");
                                        }
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid selection. Please try again.");
                                scanner.nextLine(); // Clear buffer
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    
    private static void cancelRenReq(Scanner scanner, Connection con) {
    	String query = "SELECT rq.r_requestDate, b.b_title, b.b_author, b.b_no " +
                "FROM rentalrequests rq " +
                "LEFT JOIN books b ON rq.b_no = b.b_no " +
                "WHERE rq.m_id = ? AND rq.r_rentalRequest = 1";
    	
    	try (PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
            	if(rs.next()) {
                	int count = 0;
                	System.out.println("\nRental Request in progress:");
                    do {
                        count++;
                        System.out.println("\n[" + count + "] Title: " + rs.getString("b_title"));
                        System.out.println("    Author: " + rs.getString("b_author"));
                        System.out.println("    Requested Date: " + rs.getDate("r_requestDate"));
                    } while (rs.next());

                    int reqNum = 0;
        	        boolean validInput = false;
        	        
        	        while (!validInput) {
        	        	System.out.print("\n>> Enter the number of the Return Request to cancel: ");
        	            reqNum = scanner.nextInt();
        	            scanner.nextLine();
        	            
        	            if (reqNum > 0 && reqNum <= count) {
        	                validInput = true;
        	            } else {
        	                System.out.println("Invalid selection. Please try again.\n");
        	            }
        	        }

                    rs.absolute(reqNum);
                    
                    System.out.println("\nConfirmation");
                    System.out.println("    Title: " + rs.getString("b_title"));
                    System.out.println("    Author: " + rs.getString("b_author"));

                    int bookId = rs.getInt("b_no");
                    
                    System.out.println();
                    System.out.print("Are you sure you want to cancel? (Y/N): ");
                    String choice = (scanner.nextLine()).toLowerCase();

                    if (choice.equals("n")) {
                        System.out.println("Request terminated.\n");
                        return;
                    } else {
                    	String delQuery = "DELETE FROM rentalrequests WHERE b_no = ? AND m_id = ? AND r_rentalRequest = 1";
                        try (PreparedStatement stmt2 = con.prepareStatement(delQuery)) {
                        	stmt2.setInt(1, bookId);
                        	stmt2.setString(2, userId);
                        	stmt2.executeUpdate();
                            System.out.println("Request cancelled successfully!\n");
                        } catch (SQLException e) {
                            System.err.println("SQLException: " + e.getMessage());
                        }
                    }
            	} else {
            		System.out.println("\nNo rental request found.\n");
            	}
            }
    	} catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    //24.6.12: Function that displays the name of the currently rented book + the remaining due date for return whenever the user logs in.
    private static void showRemainingDays(Connection con) {
        String query = "SELECT r.r_whenToReturn, b.b_title " +
                       "FROM rentals r " +
                       "JOIN books b ON r.b_no = b.b_no " +
                       "WHERE r.m_id = ? AND r.r_done = 1";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                LocalDate currentDate = LocalDate.now();
                System.out.println();
                while (rs.next()) {
                    LocalDate whenToReturn = rs.getDate("r_whenToReturn").toLocalDate();
                    long daysRemaining = ChronoUnit.DAYS.between(currentDate, whenToReturn);
                    System.out.println("[Book title \"" + rs.getString("b_title") + "\" should be returned in " + daysRemaining + " days!]\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
}

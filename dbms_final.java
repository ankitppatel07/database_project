import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;



public class dbms_final {

	// BufferedReader for taking input from keyboard
	static BufferedReader  readKeyBoard;
	static BufferedReader  readKeyBoard1;
	static String get_classid = null;
	static Connection conn; // creating a connection with the oracle server
	//Prepare to call stored procedure:
	static CallableStatement cs = null;
	static String get_dept_code;
	static int get_course_no;
	static String get_Bno;
	
	
   public static void main (String args []) throws SQLException 
   {
	try
	{
		InputStreamReader isr2 = new InputStreamReader(System.in);
		BufferedReader br2 = new BufferedReader(isr2);
		   
		//taking credentials from user
		System.out.println("\nEnter the Username: ");
		String username = br2.readLine();
		   
		System.out.println("\nEnter the Password: ");
		String password = br2.readLine();
		
		//Connecting to Oracle server. 
		OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:acad111");
 		conn = ds.getConnection(username, password);
        	
 		System.out.println("\n Connection Established....\n");
 		
 		options_menu();
        	//close the result set, statement, and the connection
        	cs.close();
        	conn.close();
   	}
   	catch (SQLException ex)  //exception thrown by oracle
  	{
	 	System.out.println ("\n" + "SQLException caught" + "\n" + ex.getMessage());
   	}
   	catch (Exception e) //exception thrown by java
   	{
		System.out.println ("\n" + "other Exception caught" + "\n" + e.getMessage());
   	}
   }

   
   public static void options_menu() throws IOException, SQLException
   {
	   while(true) //loop until user chooses to exit
	   {
			//selection menu for choice
		   System.out.println("\n\n\n");
		   System.out.println("List of Options:\n");
		   System.out.println("+------------------------------------+\n");
		   System.out.println("\n" + "1.Show Tables" + "\n");
		   System.out.println("2.Find the TA of a class" + "\n");
		   System.out.println("3.Find all Prerequisites" + "\n");
		   System.out.println("4.Enroll a student" + "\n");
		   System.out.println("5.Drop Student Enrollment" + "\n");
		   System.out.println("6.Delete Student " + "\n");
		   System.out.println("7.Exit" + "\n\n");
		   System.out.println("+------------------------------------+\n\n");
		   System.out.println("Select an option:");
		  
		  //Reading the choice from user	
		   InputStreamReader isr1 = new InputStreamReader(System.in);
		   BufferedReader br1 = new BufferedReader(isr1);
		   
		   int i1 = Integer.parseInt(br1.readLine());
		   
		   switch(i1)
		   {
		   case 1 :
			   show_tables();//call to function that has procedure 2 code
			   break;
		   
		   case 2 :
			   find_class_ta();//call to function that has procedure 3 code
			   break;
		   
		   case 3 :
			   return_all_prereq();//call to function that has procedure 4 code
			   break;
			   
		   case 4 :
			   enroll_stud();//call to function that has procedure 5 code
			   break;
			   
		   case 5 :
			   drop_stud_enroll();//call to function that has procedure 6 code
			   break;
			   
		   case 6 :
			   delete_stud();//call to function that has procedure 7 code
			   break;
			   
		   case 7 :
			   System.exit(0);//exit condition
			   
		   default :
			   System.out.println("Enter correct option!!!");
			   break;
		   }
	   }
	   
	   
   }
   
	   
	   	   // function to show all tables in the system
	   public static void show_tables() throws IOException, SQLException
	   {
		   while(true)
		   {
			   System.out.println("\n\n\n");
			   System.out.println("List of Option:\n");
			   System.out.println("+------------------------------------+\n");
			   System.out.println("1.Show Student Table" + "\n");
			   System.out.println("2.Show TAS Table" + "\n");
			   System.out.println("3.Show Courses Table" + "\n");
			   System.out.println("4.Show Classes Table" + "\n");
			   System.out.println("5.Show Enrollments Table" + "\n");
			   System.out.println("6.Show Prerequisites Table" + "\n");
			   System.out.println("7.Show Logs Table" + "\n");
			   System.out.println("8.Return to main menu" + "\n");
			   System.out.println("9.Exit" + "\n\n");
			   System.out.println("+------------------------------------+\n\n");
			   System.out.println("Select an option:");
			   
			   InputStreamReader isr2 = new InputStreamReader(System.in);
			   BufferedReader br2 = new BufferedReader(isr2);
			   
			   int i2 = Integer.parseInt(br2.readLine());
			   
			   switch(i2)
			   {
			   case 1 :
				   show_student_table();//call to procedure show_students from this function
				   break;
			   
			   case 2 :
				   show_tas_table();//call to procedure show_tas from this function
				   break;
				   
			   case 3 :
				   show_courses_table();//call to procedure show_courses from this function
				   break;
				   
			   case 4 :
				   show_classes_table();//call to procedure show_classes from this function
				   break;
				   
			   case 5 :
				   show_enrollments_table();//call to procedure show_enrollments from this funtion
				   break;
				   
			   case 6 :
				   show_prerequisites_table();//call to procedure show_prerequisites from this funtion
				   break;
				   
			   case 7 :
				   show_logs_table();//call to procedure show_logs from this function
				   break;
				   
			   case 8 :
				   return;
				  			   
			   case 9 :
				   System.exit(0);
				   
			   default :
				   System.out.println("Enter correct option!!!");
				   break;
			   }
		   }
	   }
	   
	   public static void show_student_table() throws IOException, SQLException
	   {
			//calling procedure show_students from package dbms_proj2_package
			cs = conn.prepareCall("begin dbms_proj2_package.show_students(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{			       
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				   System.out.println("\n\n\n");
				System.out.println("B#" + "\t\t" + "First Name" + "\t" + "Last Name" + "\t" + "Status" 
						+ "\t" + "GPA" + "\t" + "Email" + "\t\t" + "BDate" + "\t\t" + "Dept Name" + "\t");
				
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getString("B#") + "\t\t" + rs.getString("first_name") + "\t\t" 
			        	+ rs.getString("last_name") + "\t\t" + rs.getString("status")+ "\t" + rs.getDouble("gpa") 
			        	+ "\t" + rs.getString("email") + "\t" + rs.getDate("bdate") + "\t" + rs.getString("deptname")); 
			        } 
			    System.out.println("+---------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
				      
	   }
	   
	   
	   
	   
	   public static void show_tas_table() throws IOException, SQLException
	   {
		   //calling show_tas procedure from package dbms_proj2_package
		   cs = conn.prepareCall("begin dbms_proj2_package.show_tas(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{				
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("B#" + "\t\t" + "TA Level" + "\t" + "Office" + "\t");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
   	  
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getString("B#") + "\t\t" + rs.getString("ta_level") + "\t\t" 
			        	+ rs.getString("office") + "\t"); 
			        } 
			    System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
	   }
	   
	   
	   public static void show_courses_table() throws IOException, SQLException
	   {
			//calling procedure show_courses from package dbms_proj2_package
		   cs = conn.prepareCall("begin dbms_proj2_package.show_courses(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("Dept Code" + "\t" + "Course no." + "\t" + "Title" + "\t");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
   	  
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getString("dept_code") + "\t\t" + rs.getInt("course#") + "\t\t" 
			        	+ rs.getString("title") + "\t"); 
			        }
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
	   }
	   
	   
	   
	   
	   public static void show_classes_table() throws IOException, SQLException
	   {

			cs = conn.prepareCall("begin dbms_proj2_package.show_classes(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("ClassID" + "\t" + "Dept Code" + "\t" + "Course#" + "\t" + "Sect#" 
						+ "\t" + "Year" + "\t" + "Semester" + "\t" + "Limit" + "\t" + "Class size" 
						+ "\t\t" + "Room" + "\t" + "TA_B#" );
				System.out.println("+---------------------------------------------------------------------------------------------------------+\n");
   	  
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getString("classid") + "\t" + rs.getString("dept_code") + "\t\t" 
			        	+ rs.getInt("course#") + "\t" + rs.getInt("sect#")+ "\t" + rs.getInt("year") 
			        	+ "\t" + rs.getString("semester") + "\t\t" + rs.getInt("limit") 
			        	+ "\t\t" + rs.getInt("class_size") + "\t\t" + rs.getString("room") + "\t   " + rs.getString("ta_B#") + "\t"); 
			        }  
					System.out.println("+---------------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
				      
	   }
	   
	   public static void show_enrollments_table() throws IOException, SQLException
	   {
		   cs = conn.prepareCall("begin dbms_proj2_package.show_enrollments(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("B#" + "\t" + "ClassID" + "\t\t" + "Letter Grade" + "\t");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
   	  
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getString("B#") + "\t" + rs.getString("classid") + "\t\t" 
			        	+ rs.getString("lgrade") + "\t"); 
			        }  
					System.out.println("+---------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
				      
	   }
	   
	   public static void show_prerequisites_table() throws IOException, SQLException
	   {
		   cs = conn.prepareCall("begin dbms_proj2_package.show_prerequisites(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("Dept Code" + "\t" + "Course no." + "\t" + "Prerequisite Dept Code" + "\t" 
						+ "Prerequisite Course no." + "\t");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
    	  
			        while (rs.next()) 
			        {
			        	System.out.println(rs.getString("dept_code") + "\t\t" + rs.getInt("course#") + "\t\t\t" 
			        	+ rs.getString("pre_dept_code") + "\t\t\t" + rs.getInt("pre_course#") + "\t"); 
			        }  
					System.out.println("+---------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
	   }
	   
	   public static void show_logs_table() throws IOException, SQLException
	   {
		   cs = conn.prepareCall("begin dbms_proj2_package.show_logs(?, ?); end;");

			//setting the parameters
			cs.registerOutParameter(1, java.sql.Types.VARCHAR);
			cs.registerOutParameter(2, OracleTypes.CURSOR);

			// execute and retrieve the result set
		       	cs.execute();
				
			ResultSet rs = null;
		        try
			{
				//storing result in rs that the cursor fetched
			       rs = ((OracleCallableStatement)cs).getCursor(2);
			}
			catch(Exception ex)
			{
				//getString method is used for getting the output message
				String output_message = ((OracleCallableStatement)cs).getString(1);
				System.out.println(output_message);
			}

			if(rs != null)
			{
				System.out.println("\n\n\n");
				System.out.println("Log no." + "\t" + "DB User Name" + "\t" + "Operation Time" + "\t" 
						+ "Table Name" + "\t" + "Operation" + "\t" + "Key Value" + "\t");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
    	  
			        while (rs.next()) //printing from the resultSet
			        {
			        	System.out.println(rs.getInt("log#") + "\t" + rs.getString("op_name") + "\t" 
			        	+ rs.getDate("op_time") + "\t" + rs.getString("table_name") + "\t" 
			        			+ rs.getString("operation") + "\t\t" + rs.getString("key_value") + "\t"); 
			        }  
					System.out.println("+---------------------------------------------------------------------------------------------------+\n");

			}
			else
			{
			        //System.out.println("No rows are fetched.");
			}

			if(rs != null)
			      rs.close();
	   }
	   
   
   public static void find_class_ta() throws IOException, SQLException
   {
	readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
	System.out.println("Enter the ClassID: ");	
	get_classid = readKeyBoard.readLine();

	cs = conn.prepareCall("begin dbms_proj2_package.find_ta(?, ?, ?); end;");

	//setting the parameters
	cs.setString(1, get_classid);
	cs.registerOutParameter(2, java.sql.Types.VARCHAR);
	cs.registerOutParameter(3, OracleTypes.CURSOR);

	// execute and retrieve the result set
       	cs.execute();
		
	ResultSet rs = null;
        try
	{
		//storing result in rs that the cursor fetched
	       rs = ((OracleCallableStatement)cs).getCursor(3);
	}
	catch(Exception ex)
	{
		//getString method is used for getting the output message
		String output_message = ((OracleCallableStatement)cs).getString(2);
		System.out.println(output_message);
	}

	if(rs != null)
	{
		System.out.println("\n\n");
		System.out.println("TA_B#" + "\t" + "First Name" + "\t" + "Last Name" + "\t");
		System.out.println("+---------------------------------------------------------------------------------------------------+\n");
   	  
	        while (rs.next()) //printing from the resultSet
		{
	        	System.out.println(rs.getString("ta_B#") + "\t\t" + rs.getString("first_name") + "\t\t" + rs.getString("last_name") + "\t"); 
	        }  
			System.out.println("+---------------------------------------------------------------------------------------------------+\n");

	}
	else
	{
	        //System.out.println("No rows are fetched.");
	}

	if(rs != null)
	      rs.close();
		      
   }


   
   public static void return_all_prereq() throws IOException, SQLException
   {
	   readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the Department Code: ");	
		get_dept_code = readKeyBoard.readLine();
		
		System.out.println("Enter the Course no: ");	
		get_course_no = Integer.parseInt(readKeyBoard.readLine());
		
		cs = conn.prepareCall("begin dbms_proj2_package.return_all_prereq(?, ?, ?); end;");

		//setting the parameters
		cs.setString(1, get_dept_code);
		cs.setInt(2, get_course_no);
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);

		// execute and retrieve the result set
	       	cs.execute();
		//getString method is used for getting the output message	
	       	String output_message = ((OracleCallableStatement)cs).getString(3);
			
			if(output_message == null)
			{
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
				System.out.println("\n\nSuccessfully retrieved the prerequisites.\n");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			}
			else
			{
				System.out.println(output_message);
			}

		
		      cs.close();
   }
   
   
   
   
   
   public static void enroll_stud() throws IOException, SQLException
   {
	   readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
	   System.out.println("Enter the B#: ");	
		get_Bno = readKeyBoard.readLine();
	   
	   System.out.println("Enter the ClassID: ");	
		get_classid = readKeyBoard.readLine();

		cs = conn.prepareCall("begin dbms_proj2_package.enroll_stud(?, ?, ?); end;");

		//setting the parameters
		cs.setString(1, get_Bno);
		cs.setString(2, get_classid);
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);
		
		// execute and retrieve the result set
	       	cs.execute();
			
			//getString method is used for getting the output message
			String output_message = ((OracleCallableStatement)cs).getString(3);
			
			if(output_message == null)
			{
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
				System.out.println("\n\nThe student is enrolled.\n");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			}
			else
			{
				System.out.println(output_message);
			}

		
		      cs.close();
			      
   }
   
   
   
   public static void drop_stud_enroll() throws IOException, SQLException
   {
	   readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
	   System.out.println("Enter the B#: ");	
		get_Bno = readKeyBoard.readLine();
	   
	   System.out.println("Enter the ClassID: ");	
		get_classid = readKeyBoard.readLine();

		cs = conn.prepareCall("begin dbms_proj2_package.drop_stud_enrollment(?, ?, ?); end;");

		//setting the parameters
		cs.setString(1, get_Bno);
		cs.setString(2, get_classid);
		cs.registerOutParameter(3, java.sql.Types.VARCHAR);

		// execute and retrieve the result set
	       	cs.execute();
		//getString method is used for getting the output message			
	       	String output_message = ((OracleCallableStatement)cs).getString(3);
			
			if(output_message == null)
			{
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
				System.out.println("\n\nThe student enrollment is dropped\n.");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			}
			else
			{
				System.out.println(output_message);
			}

		    cs.close();
	   
   }
   
  
   public static void delete_stud() throws IOException, SQLException
   {
	   readKeyBoard = new BufferedReader(new InputStreamReader(System.in));
	   System.out.println("Enter the B#: ");	
		get_Bno = readKeyBoard.readLine();

		cs = conn.prepareCall("begin dbms_proj2_package.delete_stud(?, ?); end;");

		//setting the parameters
		cs.setString(1, get_Bno);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		
		// execute and retrieve the result set
	       	cs.execute();
		//getString method is used for getting the output message			
	       	String output_message = ((OracleCallableStatement)cs).getString(2);
			
			if(output_message == null)
			{
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
				System.out.println("\n\nThe student record is deleted\n.");
				System.out.println("+---------------------------------------------------------------------------------------------------+\n");
			}
			else
			{
				System.out.println(output_message);
			}

		    cs.close();
	   
   }
   
   

}



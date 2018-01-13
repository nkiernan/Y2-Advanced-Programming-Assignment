import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * The {@link Controller} class is used to test various CRUD methods belonging to the {@link EmployeeDAO} class.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class Controller
{
	/**
	 * The main method accesses the empdb.sqlite database via an instance of the {@link EmployeeDAO} class, and tests
	 * the various CRUD methods of the {@link EmployeeDAO} class using a test instance of the {@link Employee} class.
	 * @param args Program arguments which are not used in this case.
	 * @throws IOException If an IO operation fails.
	 */
	public static void main(String[] args) throws IOException
	{
		EmployeeDAO dao = new EmployeeDAO(); //Create access to database
		Image image = ImageIO.read(new File("res/noImage.jpg")); //Placeholder "blank" image for newly created employee records
		
		//Create an employee record to test CRUD methods:
		Employee testEmployee = new Employee();
		testEmployee.setId("1004");
		testEmployee.setName("John Doe");
		testEmployee.setGender('M');
		testEmployee.setDob("30/10/1980");				
		testEmployee.setAddress("London");
		testEmployee.setPostcode("WC1 9QQ");
		testEmployee.setNatInscNo("MM112358U");
		testEmployee.setTitle("Office Manager");
		testEmployee.setStartDate("20/10/2006");
		testEmployee.setSalary("30000");		
		testEmployee.setEmail("john@mail.co.uk");
		testEmployee.setImage(image);

		try
		{
			//Testing insertEmployee() creation method:
			System.out.println("Test employee inserted: " + dao.insertEmployee(testEmployee) + "\n");
			
			//Testing selectAllEmployees() retrieval method:
			System.out.println("All employees:");
			ArrayList<Employee> allEmployees = dao.selectAllEmployees();
			int size = allEmployees.size();			
			for (int i = 0; i < size; i++)
			{
				System.out.println("Employee " + allEmployees.get(i).getId() +  ": " +  allEmployees.get(i).toString());
			}
			
			//Testing selectedEmployeeById() retrieval method:
			System.out.println("\nDetails for selected employee " + testEmployee.getId() +  ": "  + dao.selectEmployeeById("1004"));
			
			//Change all testEmployee details to test update method:
			testEmployee.setName("Jane Doe");
			testEmployee.setGender('F');
			testEmployee.setDob("03/01/1989");				
			testEmployee.setAddress("Birmingham");
			testEmployee.setPostcode("BB1 2FF");
			testEmployee.setNatInscNo("BH654321M");
			testEmployee.setTitle("Manager");
			testEmployee.setStartDate("02/01/2016");
			testEmployee.setSalary("35000");		
			testEmployee.setEmail("jane@mail.co.uk");
			System.out.println("\nTest employee updated: " + dao.updateEmployee(testEmployee));
			System.out.println("Updated details for employee " + testEmployee.getId() +  ": " + dao.selectEmployeeById("1004"));
			
			//Testing deleteEmployeeById() deletion method:
			System.out.println("\nTest employee deleted: " + dao.deleteEmployeeById("1004"));
			
			//Testing generateSearchQuery() retrieval method:
			System.out.println("\nDisplay all employees with salary of 45000:");
			ArrayList<Employee> searchQuery = dao.generateSearchQuery("Salary", "45000");
			size = searchQuery.size();		
			for (int i = 0; i < size; i++)
			{
				System.out.println("Employee " + allEmployees.get(i).getId() +  ": " + searchQuery.get(i).toString());
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}

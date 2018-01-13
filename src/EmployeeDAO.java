import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * {@link EmployeeDAO} provides a connection to the empdb.sqlite database along with
 * CRUD methods in order to perform various operations upon the database records.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class EmployeeDAO
{
	private Connection c = null;
	private Statement s = null;
	private ResultSet r = null;

	/**
	 * An empty constructor for the {@link EmployeeDAO} class.
	 */
	public EmployeeDAO(){}

	/**
	 * A method used to connect to the database when performing CRUD methods.
	 * @return A connection to the database.
	 */
	public Statement getConnection()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
		}
		try
		{
			String dbURL = "jdbc:sqlite:res/empdb.sqlite";
			c = DriverManager.getConnection(dbURL);
			s = c.createStatement();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		return s;
	}

	/**
	 * A method which closes connection to the database upon completion of CRUD methods.
	 * @throws SQLException If the connection fails to close.
	 */
	public void closeConnection() throws SQLException
	{
		if (c != null)
		{
			c.close();
		}
	}

	/**
	 * A method which selects every employee in the database and stores them in an ArrayList.
	 * If used with {@link Controller}, all employees and their details may be printed to the console.
	 * If used with {@link MainForm}, all employees and their details may be displayed and cycled through
	 * individually.
	 * @return An ArrayList of all employees in the database.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public ArrayList<Employee> selectAllEmployees() throws SQLException, IOException
	{	
		String query = "SELECT * From employees;";
		ArrayList<Employee> allEmployees = new ArrayList<Employee>();
		try
		{
			getConnection();			
			r = s.executeQuery(query);			
			while (r.next())
			{
				Employee employee = getEmployeeFromDatabase(r);
				allEmployees.add(employee);
			}
			return allEmployees;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			closeConnection();
		}
	}

	/**
	 * A method which selects employees having an attribute value which matches the specified search
	 * value and stores them in an ArrayList. Used in a similar fashion to {@link #selectAllEmployees}.
	 * @param column Specifies which database column to perform search in.
	 * @param searchTerm Specifies which value to search database for.
	 * @return An ArrayList of employees returned by user's search query.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public ArrayList<Employee> generateSearchQuery(String column, String searchTerm) throws SQLException, IOException
	{
		String query = "SELECT * FROM Employees WHERE " + column + "= '" + searchTerm + "';";
		ArrayList<Employee> searchQuery = new ArrayList<Employee>();
		try
		{
			getConnection();
			r = s.executeQuery(query);
			while (r.next())
			{
				Employee employee = getEmployeeFromDatabase(r);
				searchQuery.add(employee);
			}
			return searchQuery;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			closeConnection();
		}
	}

	/**
	 * A method using a SQL retrieval statement in order to create an
	 * instance of an {@link Employee} using their ID.
	 * @param id The ID number of the desired employee record.
	 * @return The selected employee record as an instance of {@link Employee}.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public Employee selectEmployeeById(String id) throws SQLException, IOException
	{
		String query = "SELECT * FROM Employees WHERE ID = '" + id + "';";

		try
		{
			getConnection();
			r = s.executeQuery(query);
			Employee employee = getEmployeeFromDatabase(r);
			return employee;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
		finally
		{
			closeConnection();
		}
	}

	/**
	 * A method which takes in an {@link Employee} object and adds it to the database using a SQL statement.
	 * @param employee The {@link Employee} instance of the desired employee record.
	 * @return A boolean corresponding to the success of the creation method.
	 * @throws SQLException If the SQL query fails to generate.
	 */
	public boolean insertEmployee(Employee employee) throws SQLException
	{
		String query = "INSERT INTO employees (Name, Gender, DOB, Address, Postcode, NIN, JobTitle, StartDate, Salary, Email)"
				     + "VALUES (" + employee.toString() + ");";

		try
		{
			getConnection();
			s.executeUpdate(query);
			return true;
		}
		catch (SQLException e)
		{
			return false;
		}		
		finally
		{
			closeConnection();
		}		
	}

	/**
	 * A method using a SQL deletion statement in order to delete an employee
	 * record from the database using their ID.
	 * @param id The ID number of the desired employee record.
	 * @return A boolean corresponding to the success of the deletion method.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public boolean deleteEmployeeById(String id) throws SQLException, IOException
	{
		String query = "DELETE FROM employees WHERE ID = '" + id + "';";

		try
		{
			if (selectEmployeeById(id).getId() == null)
			{
				return false;
			}
			else
			{
				getConnection();
				s.executeUpdate(query);
				return true;
			}
		}
		catch (SQLException e)
		{
			return false;
		}		
		finally
		{
			closeConnection();
		}
	}

	/**
	 * A method which takes in an {@link Employee} object and updates it's record in the database using a SQL statement.
	 * Used when not updating an employee's image.
	 * @param employee The {@link Employee} instance of the desired employee record.
	 * @return A boolean corresponding to the success of the update method.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public boolean updateEmployee(Employee employee) throws SQLException, IOException
	{
		getConnection();
		String query = "UPDATE employees SET Name = ?, Gender = ?, DOB = ?, Address = ?, Postcode = ?, NIN = ?, JobTitle = ?, StartDate = ?, Salary = ?, Email = ? WHERE ID = ?";
		PreparedStatement s = c.prepareStatement(query);
		s.setString(1, employee.getName());
		s.setString(2, String.valueOf(employee.getGender()));
		s.setString(3, employee.getDob());
		s.setString(4, employee.getAddress());
		s.setString(5, employee.getPostcode());
		s.setString(6, employee.getNatInscNo());
		s.setString(7, employee.getTitle());
		s.setString(8, employee.getStartDate());
		s.setString(9, employee.getSalary());
		s.setString(10, employee.getEmail());
		s.setString(11, employee.getId());
		
		int updated = s.executeUpdate();
		if (updated > 0)
		{
			closeConnection();
			return true;
		}
		else
		{
			closeConnection();
			return false;
		}
	}
	
	/**
	 * A method which takes in an {@link Employee} object and updates it's record in the database using a SQL statement.
	 * Used when updating an employee's image.
	 * @param employee The {@link Employee} instance of the desired employee record.
	 * @return A boolean corresponding to the success of the update method.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public boolean updateEmployeeWithImage(Employee employee) throws SQLException, IOException
	{
		getConnection();
		String query = "UPDATE employees SET Name = ?, Gender = ?, DOB = ?, Address = ?, Postcode = ?, NIN = ?, JobTitle = ?, StartDate = ?, Salary = ?, Email = ?, Image = ? WHERE ID = ?";
		PreparedStatement s = c.prepareStatement(query);
		s.setString(1, employee.getName());
		s.setString(2, String.valueOf(employee.getGender()));
		s.setString(3, employee.getDob());
		s.setString(4, employee.getAddress());
		s.setString(5, employee.getPostcode());
		s.setString(6, employee.getNatInscNo());
		s.setString(7, employee.getTitle());
		s.setString(8, employee.getStartDate());
		s.setString(9, employee.getSalary());
		s.setString(10, employee.getEmail());
		s.setBytes(11, imageToDatabase(employee.getImage()));
		s.setString(12, employee.getId());
		
		int updated = s.executeUpdate();
		if (updated > 0)
		{
			closeConnection();
			return true;
		}
		else
		{
			closeConnection();
			return false;
		}
	}

	/**
	 * A method which makes converting an employee record into an instance of {@link Employee} more convenient.
	 * Used by {@link #selectEmployeeById}, {@link #selectAllEmployees} and {@link #generateSearchQuery}.
	 * @param r The ResultSet generated by the SQL query.
	 * @return The selected employee record as an instance of {@link Employee}.
	 * @throws IOException If an IO operation fails.
	 */
	public Employee getEmployeeFromDatabase(ResultSet r) throws IOException
	{
		Employee employee = new Employee();
		try
		{
			employee.setId(r.getString("ID"));
			employee.setName(r.getString("Name"));
			employee.setGender(r.getString("Gender").charAt(0));
			employee.setDob(r.getString("DOB"));				
			employee.setAddress(r.getString("Address"));
			employee.setPostcode(r.getString("Postcode"));
			employee.setNatInscNo(r.getString("NIN"));
			employee.setTitle(r.getString("JobTitle"));
			employee.setStartDate(r.getString("StartDate"));
			employee.setSalary(r.getString("Salary"));		
			employee.setEmail(r.getString("Email"));
			if (r.getBytes("Image") != null)
			{
				employee.setImage(Toolkit.getDefaultToolkit().createImage(r.getBytes("Image")));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return employee;
	}
	
	/**
	 * A method which converts an employee's image in bytes and stores it in the database.
	 * @param image Image of the employee to be stored in the database.
	 * @return An array of bytes representing the employee's image.
	 */
	private byte[] imageToDatabase(Image image)
	{
		ByteArrayOutputStream bytes = null;
		BufferedImage temp = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = temp.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		try
		{
			bytes = new ByteArrayOutputStream();
			ImageIO.write(temp, "png", bytes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				bytes.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return bytes.toByteArray();
	}
}

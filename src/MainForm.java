import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The {@link MainForm} displays an interface providing various features
 * to the user wishing to access the database.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class MainForm extends JFrame
{	
	private static EmployeeDAO dao = new EmployeeDAO(); //Create access to the database
	private static ArrayList<Employee> searchQuery; //Store employees retrieved using search feature
	private static int searchQueryIndex; //Current employee to display from search query
	private boolean updateImage = false; //Decides which DAO update method is used
	private String[] days = new String[31];	
	private String[] months = new String[12];
	private String[] years = new String[150];
	private Employee noEmployee = new Employee(); //Placeholder/empty instance of employee for when one isn't being displayed
	private static Employee selectedEmployee; //Current employee to display in GUI
	private static Image noImage; //Placeholder image for when employee has no image

	//Menu components
	private JMenuBar menubar;
	private JMenu fileMenu, recordMenu;
	private JMenuItem uploadPictureItem, exitItem, displayAllItem, searchItem, deleteItem;

	//GUI components
	private static JLabel empInfo, empId, empName, empGender, empDob, empAddress, empPostCode, empSalary, empNiNum, empEmail, empStartDate, empJobTitle, empImage;
	private JButton enter, clear, back, next;
	private static JTextField nameField, addressField, postCodeField, salaryField, niNumField, emailField, jobTitleField;
	private static JRadioButton male, female;
	private static JComboBox dobDay, dobMonth, dobYear, startDay, startMonth, startYear;

	/**
	 * A constructor for the {@link MainForm} which displays it's GUI components as specified by it's GridBagConstraints.
	 * It helps the user interact with the database in various ways.
	 * @throws SQLException If the SQL query fails to generate.
	 * @throws IOException If an IO operation fails.
	 */
	public MainForm() throws SQLException, IOException
	{
		//GUI initialisation
		super("Employee Record System Assignment");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		noImage = ImageIO.read(new File("res/noImage.jpg"));	
		setupNoEmployee();		

		//Populate combo boxes
		for (int i = 0; i < days.length; i++)
		{
			String d = Integer.toString(i + 1);			
			if ((i + 1) < 10)
			{
				d = 0 + d; //Concatenate a zero to day if less than ten to make it double digits
			}
			days[i] = d;					
		}
		for (int i = 0; i < months.length; i++)
		{
			String m = Integer.toString(i + 1);
			if ((i + 1) < 10)
			{
				m = 0 + m; //Concatenate a zero to month if less than ten to make it double digits
			}
			months[i] = m;
		}
		for (int i = 0; i < years.length; i++)
		{
			years[i] = Integer.toString(1900 + i + 1);
		}

		//Set menu up along with menu actions
		menubar = new JMenuBar();
		this.setJMenuBar(menubar);

		//Upload a new picture from the user's file system
		uploadPictureItem = new JMenuItem("Upload Picture");
		uploadPictureItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//Inform the user to select an employee first
				if (selectedEmployee == noEmployee)
				{
					JOptionPane.showMessageDialog(null, "Please select an employee to update image", "Error", JOptionPane.ERROR_MESSAGE);
				}
				//Only perform image upload if an employee is selected
				else if (selectedEmployee != noEmployee)
				{
					uploadImage();
				}
			}
		});

		//Close the window
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		//Display all employees in database so user can cycle through them
		displayAllItem = new JMenuItem("Display All");
		displayAllItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				displayAllEmployees();
			}			
		});

		//Initiate the search feature
		searchItem = new JMenuItem("Search Employee(s)");
		searchItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				search();
			}
		});

		//Delete an employee from the database
		deleteItem = new JMenuItem("Delete Employee");
		deleteItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//Handling user attempting to delete non-existent employee
				if (selectedEmployee == noEmployee)
				{
					JOptionPane.showMessageDialog(null, "Please select an employee to delete first", "Error", JOptionPane.ERROR_MESSAGE);
				}
				//Delete existent employee
				else if (selectedEmployee != noEmployee)
				{
					deleteSelectedEmployee();
				}
			}
		});

		//File menu
		fileMenu = new JMenu("File");
		menubar.add(fileMenu);
		fileMenu.add(uploadPictureItem);
		fileMenu.add(exitItem);

		//Record menu
		recordMenu = new JMenu("Record");
		menubar.add(recordMenu);
		recordMenu.add(displayAllItem);
		recordMenu.add(searchItem);
		recordMenu.add(deleteItem);

		//Add GUI components
		//All columns: "Title" label area
		empInfo = new JLabel("Employee Information");
		empInfo.setFont(new Font("Arial", Font.ITALIC, 18));
		empInfo.setForeground(new Color(0x0000ff));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 10;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.CENTER;		
		c.insets = new Insets(4, 4, 4, 4);
		this.getContentPane().add(empInfo, c);

		//Column 3: Employee image
		empImage = new JLabel();
		c.gridx = 6;
		c.gridy = 2;
		c.gridwidth = 4;
		c.gridheight = 7;	
		this.getContentPane().add(empImage, c);		

		//Column 2: Non label/text field components
		//Male gender button
		male = new JRadioButton("Male");
		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.getContentPane().add(male, c);
		male.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				female.setSelected(false); //Deselect female if male is selected
			}
		});

		//Female gender button
		female = new JRadioButton("Female");
		c.gridx++;
		this.getContentPane().add(female, c);
		female.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				male.setSelected(false); //Deselect male if female is selected
			}
		});

		//Date of birth boxes
		//DOB day box
		dobDay = new JComboBox(days);
		c.gridx = 3;
		c.gridy = 3;		
		this.getContentPane().add(dobDay, c);		

		//DOB month box
		dobMonth = new JComboBox(months);
		c.gridx++;
		this.getContentPane().add(dobMonth, c);	

		//DOB year box
		dobYear = new JComboBox(years);
		c.gridx++;
		this.getContentPane().add(dobYear, c);

		//Start date boxes
		//Start day box
		startDay = new JComboBox(days);
		c.gridx = 3;
		c.gridy = 9;
		c.gridwidth = 1;
		this.getContentPane().add(startDay, c);

		//Start month box
		startMonth = new JComboBox(months);
		c.gridx++;
		this.getContentPane().add(startMonth, c);

		//Start year box
		startYear = new JComboBox(years);
		c.gridx++;
		this.getContentPane().add(startYear, c);

		//Column 1: labels and enter button			
		empName = new JLabel("Name:");
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.ipadx = 120;
		this.getContentPane().add(empName, c);

		empGender = new JLabel("Gender:");
		c.gridy++;
		this.getContentPane().add(empGender, c);

		empDob = new JLabel("Date of Birth:");
		c.gridy++;
		this.getContentPane().add(empDob, c);

		empAddress = new JLabel("Address:");	
		c.gridy++;
		this.getContentPane().add(empAddress, c);

		empPostCode = new JLabel("Post Code:");	
		c.gridy++;
		this.getContentPane().add(empPostCode, c);

		empSalary = new JLabel("Salary:");
		c.gridy++;
		this.getContentPane().add(empSalary, c);

		empNiNum = new JLabel("NI Number:");
		c.gridy++;
		this.getContentPane().add(empNiNum, c);

		empEmail = new JLabel("Email:");
		c.gridy++;
		this.getContentPane().add(empEmail, c);

		empStartDate = new JLabel("Start Date:");
		c.gridy++;
		this.getContentPane().add(empStartDate, c);

		empJobTitle = new JLabel("Job Title:");
		c.gridy++;
		this.getContentPane().add(empJobTitle, c);

		//Enter button which adds new employee or updates existing employee
		//under different circumstances
		enter = new JButton("Enter");		
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		this.getContentPane().add(enter, c);
		enter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				//Using enter button to create new employee when no employee selected
				if (selectedEmployee == noEmployee)
				{				
					try
					{
						addNewEmployee();
					}
					catch (SQLException ex)
					{
						ex.printStackTrace();
					}
				}
				//Using enter button to update existing employee which is currently selected
				else if (getSelectedEmployee() != noEmployee)
				{					
					updateSelectedEmployee();
				}
			}
		});

		//Column 2: text fields and clear button
		nameField = new JTextField();
		c.gridx = 3;
		c.gridy = 1;		
		c.anchor = GridBagConstraints.LINE_START;
		this.getContentPane().add(nameField, c);		

		addressField = new JTextField();
		c.gridy = 4;
		this.getContentPane().add(addressField, c);

		postCodeField = new JTextField();
		c.gridy++;
		this.getContentPane().add(postCodeField, c);

		salaryField = new JTextField();
		c.gridy++;
		this.getContentPane().add(salaryField, c);	

		niNumField = new JTextField();
		c.gridy++;
		this.getContentPane().add(niNumField, c);	

		emailField = new JTextField();
		c.gridy++;
		this.getContentPane().add(emailField, c);		

		jobTitleField = new JTextField();
		c.gridx = 3;
		c.gridy = 10;
		this.getContentPane().add(jobTitleField, c);	

		//Clear button which reset GUI to it's initial appearance
		//and discards any changes
		clear = new JButton("Clear");
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		this.getContentPane().add(clear, c);
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				clearMainForm();
			}
		});

		//Column 3: ID label and back/next buttons
		empId = new JLabel();
		c.gridx = 6;
		c.gridy = 1;		
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.LINE_START;
		this.getContentPane().add(empId, c);

		//Back button which cycles backwards through current search query
		back = new JButton("Back");
		c.gridy = 9;
		c.gridwidth = 2;
		c.ipadx = 50;
		c.ipady = -5;
		this.getContentPane().add(back, c);
		back.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (searchQuery != null)
				{
					previousEmployee();
				}
			}
		});

		//Next button which cycles forwards through current search query
		next = new JButton("Next");
		c.gridx = 8;
		this.getContentPane().add(next, c);
		next.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (searchQuery != null)
				{
					nextEmployee();
				}
			}
		});

		//After all GUI components are added, load noEmployee
		loadSelectedEmployee();
	}

	//Button/Menu methods
	/**
	 * A method which allows the user to upload an image from their file system
	 * and store in the database as an {@link Employee}'s image. It only accepts
	 * image type files (.jpeg, .jpg and .png) and displays an error message to
	 * the user if they attempt to upload an incorrect file type.
	 */
	public void uploadImage()
	{
		FileDialog uploadPicture = new FileDialog(MainForm.this, "Upload Employee Image", FileDialog.LOAD);
		uploadPicture.setFilenameFilter(new FilenameFilter()
		{
			//Check that file type is correct
			public boolean accept(File image, String name)
			{
				String[] validFileTypes = {"jpeg", "jpg", "png"};
				for (String type : validFileTypes)
				{
					if(name.endsWith("." + type))
					{
						return true;
					}
				}
				return false;
			}
		});
		uploadPicture.setVisible(true); //Display file dialog
		String filepath = uploadPicture.getDirectory(); //Get file path for image
		String image = uploadPicture.getFile(); //Get file name of image
		if (filepath != null && image != null)
		{
			try
			{
				selectedEmployee.setImage(ImageIO.read(new File(filepath + image))); //Set employee's image to selected image file
				ImageIcon icon = new ImageIcon(selectedEmployee.getImage().getScaledInstance(215, 255, Image.SCALE_SMOOTH)); //Fit image in GUI
				empImage.setIcon(icon); //Display appropriately sized image in GUI
				updateImage = true; //Specifies which DAO update method to use
				JOptionPane.showMessageDialog(null, "Press Enter to save image or Clear to undo");
			}
			catch(NullPointerException | IOException e)
			{
				//In event of incorrect file type
				JOptionPane.showMessageDialog(null, "Image upload failed, please select image files only", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}		
	}

	/**
	 * A method which sets up a blank instance of {@link Employee} for use when one isn't being display in GUI.
	 * Only used once at GUI initialisation.
	 */
	public void setupNoEmployee()
	{
		noEmployee.setId("");
		noEmployee.setName("");
		noEmployee.setDob("01/01/1901");
		noEmployee.setAddress("");
		noEmployee.setPostcode("");
		noEmployee.setSalary("");
		noEmployee.setNatInscNo("");
		noEmployee.setEmail("");
		noEmployee.setStartDate("01/01/1901");
		noEmployee.setTitle("");
		selectedEmployee = noEmployee;
	}

	/**
	 * A method which changes all GUI components to display the attribute values
	 * reflecting the currently selected {@link Employee}.
	 */
	public static void loadSelectedEmployee()
	{
		empId.setText("Employee ID: " + selectedEmployee.getId());
		nameField.setText(selectedEmployee.getName());

		if (selectedEmployee.getGender() == 'M')
		{
			male.setSelected(true);
			female.setSelected(false);
		}
		else if (selectedEmployee.getGender() == 'F')
		{
			male.setSelected(false);
			female.setSelected(true);
		}
		else
		{
			male.setSelected(false);
			female.setSelected(false);
		}

		dobDay.setSelectedItem(selectedEmployee.getDob().split("/")[0]);
		dobMonth.setSelectedItem(selectedEmployee.getDob().split("/")[1]);
		dobYear.setSelectedItem(selectedEmployee.getDob().split("/")[2]);

		addressField.setText(selectedEmployee.getAddress());
		postCodeField.setText(selectedEmployee.getPostcode());
		salaryField.setText(selectedEmployee.getSalary());
		niNumField.setText(selectedEmployee.getNatInscNo());
		emailField.setText(selectedEmployee.getEmail());

		startDay.setSelectedItem(selectedEmployee.getStartDate().split("/")[0]);
		startMonth.setSelectedItem(selectedEmployee.getStartDate().split("/")[1]);
		startYear.setSelectedItem(selectedEmployee.getStartDate().split("/")[2]);

		jobTitleField.setText(selectedEmployee.getTitle());

		//Display the employee's image if there is one in database
		if (selectedEmployee.getImage() != null)
		{
			ImageIcon icon = new ImageIcon(selectedEmployee.getImage().getScaledInstance(215, 255, Image.SCALE_SMOOTH));
			empImage.setIcon(icon);
		}
		//Display placeholder "noImage" if there is no image for employee in database
		else if (selectedEmployee.getImage() == null)
		{
			ImageIcon icon = new ImageIcon(noImage.getScaledInstance(215, 255, Image.SCALE_SMOOTH));
			empImage.setIcon(icon);
		}
	}

	/**
	 * A method which adds a new {@link Employee} record into the database, taking in the values entered
	 * into the GUI components by the user. It uses the {@link #checkErrors} method to validate user input.
	 * @throws SQLException If the SQL query fails to generate.
	 */
	public void addNewEmployee() throws SQLException
	{
		//Check that the user is happy which their input
		int n = JOptionPane.showConfirmDialog(null, "Add entered details as new employee?", "Add new employee?", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			try
			{
				Employee newEmployee = new Employee(); //Temporary instance of employee to add to database
				String errors = checkErrors();
				if(errors != null)
				{
					//Display input error to user if they have made any
					JOptionPane.showMessageDialog(null, "Please check for errors in:\n" + errors, "Error", JOptionPane.ERROR_MESSAGE);
				}					
				else
				{
					//Store user input into temporary employee instance
					newEmployee.setName(nameField.getText());

					if (male.isSelected() == true)
					{
						newEmployee.setGender('M');
					}
					else if (female.isSelected() == true)
					{
						newEmployee.setGender('F');
					}
					newEmployee.setDob(dobDay.getSelectedItem().toString() + "/" + dobMonth.getSelectedItem().toString() + "/" + dobYear.getSelectedItem().toString());
					newEmployee.setAddress(addressField.getText());
					newEmployee.setPostcode(postCodeField.getText().toUpperCase());
					newEmployee.setSalary(salaryField.getText());
					newEmployee.setNatInscNo(niNumField.getText().toUpperCase());
					newEmployee.setEmail(emailField.getText());
					newEmployee.setStartDate(startDay.getSelectedItem().toString() + "/" + startMonth.getSelectedItem().toString() + "/" + startYear.getSelectedItem().toString());
					newEmployee.setTitle(jobTitleField.getText());
					dao.insertEmployee(newEmployee); //Add new employee to database
					JOptionPane.showMessageDialog(null, "Employee added to database"); //Notify user of success					
					//Return GUI to initial appearance
					selectedEmployee = noEmployee;
					loadSelectedEmployee();
				}
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	/**
	 * A method which updates an existing {@link Employee} record in the database, taking in the values entered
	 * into the GUI components by the user. It uses the {@link #checkErrors} method to validate user input.
	 */
	public void updateSelectedEmployee()
	{
		//Check that the user is happy which their input
		int n = JOptionPane.showConfirmDialog(null, "Update selected employee with entered details?", "Update selected employee?", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			try
			{
				String errors = checkErrors();
				if(errors != null)
				{
					//Display input error to user if they have made any
					JOptionPane.showMessageDialog(null, "Please check for errors in:\n" + errors, "Error", JOptionPane.ERROR_MESSAGE);
				}					
				else
				{
					//Update employee's attributes according to user input
					selectedEmployee.setName(nameField.getText());

					if (male.isSelected() == true)
					{
						selectedEmployee.setGender('M');
					}
					else if (female.isSelected() == true)
					{
						selectedEmployee.setGender('F');
					}
					selectedEmployee.setDob(dobDay.getSelectedItem().toString() + "/" + dobMonth.getSelectedItem().toString() + "/" + dobYear.getSelectedItem().toString());
					selectedEmployee.setAddress(addressField.getText());
					selectedEmployee.setPostcode(postCodeField.getText().toUpperCase());
					selectedEmployee.setSalary(salaryField.getText());
					selectedEmployee.setNatInscNo(niNumField.getText().toUpperCase());
					selectedEmployee.setEmail(emailField.getText());
					selectedEmployee.setStartDate(startDay.getSelectedItem().toString() + "/" + startMonth.getSelectedItem().toString() + "/" + startYear.getSelectedItem().toString());
					selectedEmployee.setTitle(jobTitleField.getText());
					
					//Update employee's record in database
					if (updateImage == true)
					{
						dao.updateEmployeeWithImage(selectedEmployee);
					}
					else if (updateImage == false)
					{
						dao.updateEmployee(selectedEmployee);
					}
					
					JOptionPane.showMessageDialog(null, "Employee updated in database"); //Notify user of success				
									
					if (updateImage == true)
					{
						updateImage = false;
					}
					//Return GUI to initial appearance
					selectedEmployee = noEmployee;
					loadSelectedEmployee();
				}
			}
			catch (SQLException | IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * A method which clears user input from the GUI along with their search query (if any),
	 * reseting the GUI to the initial appearance.
	 */
	public void clearMainForm()
	{
		//Make sure the user wants to discard changes and reset GUI to initial appearance
		int n = JOptionPane.showConfirmDialog(null, "Clear entered details and search query?\n(No changes will be made to database)", "Clear details?", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			selectedEmployee = noEmployee;
			searchQuery = null;
			searchQueryIndex = 0;
			loadSelectedEmployee();
		}
	}

	/**
	 * A method which allows the user to retrieve all employees from the database using
	 * {@link EmployeeDAO#selectAllEmployees}. They are then displayed in the GUI allowing
	 * the user to cycle backwards and forwards through individual records.
	 */
	public void displayAllEmployees()
	{
		try
		{
			searchQuery = dao.selectAllEmployees(); //Get all employees from database
			//Select and display first record in database
			searchQueryIndex = 0;
			selectedEmployee = searchQuery.get(searchQueryIndex);
			loadSelectedEmployee();
			//Notify user of how many records are found
			JOptionPane.showMessageDialog(null, searchQuery.size() + " results found");
		}
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * A method which creates an instance of {@link SearchForm} and specifies the size,
	 * visibility, resizability, location on screen and how to respond when the user
	 * performs the close operation. Allows the user to search the database using
	 * the {@link EmployeeDAO#generateSearchQuery} method.
	 */
	public void search()
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{				
			public void run()
			{	
				SearchForm f = new SearchForm();
				f.setSize(300, 100);
				f.setVisible(true);
				f.setResizable(false);
				f.setLocationRelativeTo(null);
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
	}

	/**
	 * A method which allows the user to delete the selected {@link Employee} from the
	 * database via the GUI. It uses the {@link EmployeeDAO#deleteEmployeeById} method
	 * in order to do so.
	 */
	public void deleteSelectedEmployee()
	{
		//Confirm that the user wishes to delete the selected employee from the database
		int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete selected employee?", "Warning!", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION)
		{
			try
			{
				//Delete the selected employee and return the GUI to it's initial appearance
				dao.deleteEmployeeById(selectedEmployee.getId());
				JOptionPane.showMessageDialog(null, "Employee deleted from database");
				selectedEmployee = noEmployee;
				loadSelectedEmployee();
			}
			catch (SQLException | IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * A method which selects the previous {@link Employee} in a search query
	 * and updates the GUI components to reflect it's attribute values.
	 */
	public void previousEmployee()
	{
		if (getSearchQueryIndex() > 0)
		{
			//Cycle to previous employee in search query
			searchQueryIndex--;
			selectedEmployee = searchQuery.get(searchQueryIndex);
			loadSelectedEmployee();
		}
		else
		{
			//Cycle to end of search query if current employee is first in ArrayList
			searchQueryIndex = (searchQuery.size() - 1);
			selectedEmployee = searchQuery.get(searchQueryIndex);
			loadSelectedEmployee();
		}
	}

	/**
	 * A method which selects the next {@link Employee} in a search query
	 * and updates the GUI components to reflect it's attribute values.
	 */
	public void nextEmployee()
	{
		if (searchQueryIndex < (searchQuery.size() - 1))
		{
			//Cycle to next employee in search query
			searchQueryIndex++;
			selectedEmployee = searchQuery.get(searchQueryIndex);
			loadSelectedEmployee();
		}
		else
		{
			//Cycle to start of search query if current employee is last in ArrayList
			searchQueryIndex = 0;
			selectedEmployee = searchQuery.get(searchQueryIndex);
			loadSelectedEmployee();
		}
	}

	//Error checking methods
	/**
	 * A method which checks all user input and informs them if it is valid or not.
	 * @return A string detailing which user input contains errors if any.
	 */
	public String checkErrors()
	{
		String errors = null;

		//Check name
		if (nameField.getText().isEmpty() || !alphabeticOnly(nameField.getText()))
		{
			errors = "Name";
		}

		//Check gender
		if (male.isSelected() == false && female.isSelected() == false)
		{
			if (errors == null)
			{
				errors = "Gender";
			}
			else
			{
				errors = errors + ", Gender";
			}
		}

		//Check DOB
		if (validDob() == false)
		{
			if (errors == null)
			{
				errors = "DOB";
			}
			else
			{
				errors = errors + ", DOB";
			}
		}

		//Check address
		if (addressField.getText().isEmpty())
		{
			if (errors == null)
			{
				errors = "Address";
			}
			else
			{
				errors = errors + ", Address";
			}
		}

		//Check post code
		if (postCodeField.getText().isEmpty())
		{
			if (errors == null)
			{
				errors = "Post Code";
			}
			else
			{
				errors = errors + ", Post Code";
			}
		}

		//Check salary
		if (salaryField.getText().isEmpty() || !numericOnly(salaryField.getText()))
		{
			if (errors == null)
			{
				errors = "Salary";
			}
			else
			{
				errors = errors + ", Salary";
			}
		}

		//Check NI number
		if (niNumField.getText().isEmpty() || !checkNiNum(niNumField.getText()))
		{
			if (errors == null)
			{
				errors = "NI Number";
			}
			else
			{
				errors = errors + ", NI Number";
			}
		}

		//Check email
		if (emailField.getText().isEmpty() || !checkEmail(emailField.getText()))
		{
			if (errors == null)
			{
				errors = "Email";
			}
			else
			{
				errors = errors + ", Email";
			}
		}

		//Check start date
		if (validStartDate() == false)
		{
			if (errors == null)
			{
				errors = "Start Date";
			}
			else
			{
				errors = errors + ", Start Date";
			}
		}

		//Check job title
		if (jobTitleField.getText().isEmpty())
		{
			if (errors == null)
			{
				errors = "Job Title";
			}
			else
			{
				errors = errors + ", Job Title";
			}
		}

		//Check if employee is old enough to legally work
		if (getEmployeeAge() < 16 || getEmployeeAge() - getEmploymentLength() < 16)
		{
			if (errors == null)
			{
				errors = "Other: Employee must be over 16 at start of employment";
			}
			else
			{
				errors = errors + "\nOther: Employee must be over 16 at start of employment";
			}
		}

		//Check that DOB occurs before start date
		if (compareDobAndStartDate() == false)
		{
			if (errors == null)
			{
				errors = "Other: Employee DOB must occur before Start Date";
			}
			else
			{
				errors = errors + "\nOther: Employee DOB must occur before Start Date";
			}
		}

		return errors;
	}

	/**
	 * A method which takes in a string entered by user and checks each character
	 * to ensure that it is a letter. Used in {@link #checkErrors}.
	 * @param input User input from a GUI text field.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean alphabeticOnly(String input)
	{
		char[] letters = input.toCharArray();
		for (char l : letters)
		{
			if (l == ' ')
			{
				return true; //Allow space characters
			}
			if (!Character.isLetter(l))
			{
				return false; //If non alphabetic characters are found
			}
		}
		return true; //If all characters are letters
	}

	/**
	 * A method which takes in a string entered by user and checks each character
	 * to ensure that it is a number. Used in {@link #checkErrors}.
	 * @param input User input from a GUI text field.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean numericOnly(String input)
	{
		char[] numbers = input.toCharArray();
		for (char n : numbers)
		{
			if (!Character.isDigit(n))
			{
				return false; //If non numerical characters are found
			}
		}
		return true; //If all characters are numbers
	}

	/**
	 * A method which takes in a date of birth entered by user and checks
	 * to ensure that it is a valid date via {@link #checkValidDate}.
	 * Used in {@link #checkErrors}.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean validDob()
	{
		return checkValidDate("dob"); //Check if DOB is a valid date
	}

	/**
	 * A method which takes in a start date entered by user and checks
	 * to ensure that it is a valid date via {@link #checkValidDate}.
	 * Used in {@link #checkErrors}.
	 * @return A boolean value reflecting validity of user input.
	 */	
	public boolean validStartDate()
	{
		return checkValidDate("start"); //Check if start date is a valid date
	}

	/**
	 * A method which verifies if a date input by the user is a valid calendar date,
	 * including checking that the year is a leap year. Used in {@link #checkErrors}.
	 * @param date A date passed in via {@link #validDob} or {@link #validStartDate}.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean checkValidDate(String date)
	{
		int d = 0;
		int m = 0;
		int y = 0;
		if (date == "dob")
		{
			d = Integer.parseInt((String) dobDay.getSelectedItem());
			m = Integer.parseInt((String) dobMonth.getSelectedItem());
			y = Integer.parseInt((String) dobYear.getSelectedItem());
		}
		else if (date == "start")
		{
			d = Integer.parseInt((String) startDay.getSelectedItem());
			m = Integer.parseInt((String) startMonth.getSelectedItem());
			y = Integer.parseInt((String) startYear.getSelectedItem());
		}

		//Check months with less than 31 days
		if (m == 4 || m == 6 || m == 9 || m == 11)
		{
			if (d == 31)
			{
				return false;
			}
		}

		//Check February is less then 30 days
		if (m == 2 && d >= 30)
		{
			return false;
		}

		//Check February for a leap year
		if (m == 2 && d == 29)
		{
			if (y % 4 != 0)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * A method that makes use of {@link #alphabeticOnly} and {@link #numericOnly} in
	 * order to check the validity of a national insurance number entered by the user.
	 * It also check that the input is nine characters in length. Used in {@link #checkErrors}.
	 * @param niNum User input from the national insurance number text field.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean checkNiNum(String niNum)
	{	
		niNum = niNumField.getText();
		
		//Check NI is nine characters long
		if (niNum.length() != 9)
		{
			return false;
		}

		//Check first, second and ninth digits for letters
		String niLetters = niNum.substring(0, 1);
		niLetters = niLetters + niNum.substring(8);
		if (alphabeticOnly(niLetters) == false)
		{
			return false;
		}

		//Check third to eighth digits for numbers
		String niNumbers = niNum.substring(2, 7);
		if (numericOnly(niNumbers) == false)
		{
			return false;
		}

		return true;
	}

	/**
	 * A method that uses a regular expression in order to confirm that
	 * the user input is a valid email address. Used in {@link #checkErrors}.
	 * @param email User input from the email text field.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean checkEmail(String email)
	{
		//Check that the input resembles a valid email address
		String emailRegex = "^(.+)@(.+)$";
		Pattern emailPattern = Pattern.compile(emailRegex);
		Matcher emailMatch = emailPattern.matcher(email);
		
		if (emailMatch.matches() == false)
		{
			return false;
		}
		return true;
	}

	/**
	 * A method which compares the {@link Employee}'s date of birth to the current
	 * date in the user's system and works out the age. Used in {@link #checkErrors}
	 * to verify that the employee can legally work. Works in a similar fashion to
	 * {@link #getEmploymentLength}.
	 * @return The age of the employee.
	 */
	public int getEmployeeAge()
	{
		Date today = Calendar.getInstance().getTime(); //Get the current date from the system
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = sdf.format(today);
		int years = Integer.valueOf(currentDate.split("/")[2]) - Integer.parseInt((String) dobYear.getSelectedItem());
		int months = Integer.valueOf(currentDate.split("/")[1]) - Integer.parseInt((String) dobMonth.getSelectedItem());
		int days = Integer.valueOf(currentDate.split("/")[0]) - Integer.parseInt((String) dobDay.getSelectedItem());		
		int age = years;	

		if (months < 0)
		{
			age--;
		}
		else if (months == 0 && days < 0)
		{
			age--;
		}
		else if (months == 0)
		{
			age++;
		}		

		return age;
	}

	/**
	 * A method which compares the {@link Employee}'s start date to the current
	 * date in the user's system and works out the employment length.
	 * Used in {@link #checkErrors}. Works in a similar fashion to {@link #getEmployeeAge}.
	 * @return The employment length of the employee.
	 */
	public int getEmploymentLength()
	{
		Date today = Calendar.getInstance().getTime(); //Get the current date from the system
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = sdf.format(today);
		int years = Integer.valueOf(currentDate.split("/")[2]) - Integer.parseInt((String) startYear.getSelectedItem());
		int months = Integer.valueOf(currentDate.split("/")[1]) - Integer.parseInt((String) startMonth.getSelectedItem());
		int days = Integer.valueOf(currentDate.split("/")[0]) - Integer.parseInt((String) startDay.getSelectedItem());		
		int service = years;	

		if (months < 0)
		{
			service--;
		}
		else if (months == 0 && days < 0)
		{
			service--;
		}
		else if (months == 0)
		{
			service++;
		}		

		return service;
	}

	/**
	 * A method which checks the difference between an {@link Employee}'s birth year
	 * and start year. It is used in {@link #checkErrors} to ensure that the DOB occurs
	 * before the start year.
	 * @return A boolean value reflecting validity of user input.
	 */
	public boolean compareDobAndStartDate()
	{
		int years = Integer.parseInt((String) dobYear.getSelectedItem()) - Integer.parseInt((String) startYear.getSelectedItem());		
		if (years > 0)
		{
			return false;
		}	
		return true;
	}

	//Getters and Setters to aid SearchForm
	
	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @param searchFormQuery An ArrayList containing the user's search query results.
	 */
	public static void setSearchQuery(ArrayList<Employee> searchFormQuery)
	{
		searchQuery = searchFormQuery;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @return An ArrayList containing the user's search query results.
	 */
	public static ArrayList<Employee> getSearchQuery()
	{
		return searchQuery;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @return A number to set the first record to be displayed in the GUI.
	 */
	public static int getSearchQueryIndex()
	{
		return searchQueryIndex;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @param n A number to set the first record to be displayed in the GUI.
	 */
	public static void setSearchQueryIndex(int n)
	{
		searchQueryIndex = n;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @return A connection to the database.
	 */
	public static EmployeeDAO getDao()
	{
		return dao;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @return A record to be displayed in the GUI.
	 */
	public static Employee getSelectedEmployee()
	{
		return selectedEmployee;
	}

	/**
	 * A method used by {@link SearchForm} to display user search results in the {@link MainForm}.
	 * @param employee A record to be displayed in the GUI.
	 */
	public static void setSelectedEmployee(Employee employee)
	{
		MainForm.selectedEmployee = employee;
	}
}
import java.awt.Image;

/**
 * {@link Employee} is a base class containing attributes typically belonging to an organisation's employee.
 * It is the subclass of the {@link Person} class.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class Employee extends Person
{
	private String id;
	private String salary;
	private String startDate;
	private String title;
	private String email;
	private Image image;

	/**
	 * An empty constructor for the {@link Employee} class. It creates an instance of {@link Employee}
	 * with no values, which must be set using it's own setter methods.
	 */
	public Employee(){}

	/**
	 * A method used to get the {@link Employee}'s current email address value.
	 * @return {@link Employee}'s current email address.
	 */
	public String getEmail()
	{
		return this.email;
	}

	/**
	 * A method used to set the {@link Employee}'s email address to a new value.
	 * @param email {@link Employee}'s new email address.
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * A method used to get the {@link Employee}'s current job title value.
	 * @return {@link Employee}'s current job title.
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * A method used to set the {@link Employee}'s job title to a new value.
	 * @param title {@link Employee}'s new job title.
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * A method used to get the {@link Employee}'s current ID value.
	 * @return {@link Employee}'s current ID value.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * A method used to set the {@link Employee}'s ID to a new value.
	 * @param id {@link Employee}'s new ID.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * A method used to get the {@link Employee}'s current salary value.
	 * @return {@link Employee}'s current salary value.
	 */
	public String getSalary()
	{
		return this.salary;
	}

	/**
	 * A method used to set the {@link Employee}'s salary to a new value.
	 * @param salary {@link Employee}'s new salary.
	 */
	public void setSalary(String salary)
	{
		this.salary = salary;
	}

	/**
	 * A method used to get the {@link Employee}'s current start date value.
	 * @return {@link Employee}'s current start date value.
	 */
	public String getStartDate()
	{
		return this.startDate;
	}

	/**
	 * A method used to set the {@link Employee}'s start date to a new value.
	 * @param startDate {@link Employee}'s new start date.
	 */
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * A method used to get the {@link Employee}'s current display image.
	 * @return {@link Employee}'s current display image.
	 */
	public Image getImage()
	{
		return this.image;
	}

	/**
	 * A method used to set the {@link Employee}'s display image to a new image.
	 * @param image {@link Employee}'s new display image.
	 */
	public void setImage(Image image)
	{			
		this.image = image;
	}

	/**
	 * A method used with {@link EmployeeDAO} when using it's {@link EmployeeDAO#insertEmployee} method.
	 * @return All values of {@link Employee}'s attributes (including those inherited from {@link Person}) as a string.
	 */
	public String toString()
	{
		return "'" + super.toString() + "', '" + this.getTitle() + "', '" + this.getStartDate() + "', '" + this.getSalary() + "', '" + this.getEmail() + "'";
	}
}

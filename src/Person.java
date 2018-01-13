/**
 * {@link Person} is a base class containing attributes typically belonging to a person.
 * It is the superclass of the {@link Employee} class.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class Person
{	
	private String name;
	private char gender;
	private String natInscNo;
	private String dob;
	private String address;
	private String postcode;
	
	/**
	 * An empty constructor for the {@link Person} class. It creates an instance of {@link Person}
	 * with no values, which must be set using it's own setter methods.
	 */
	public Person(){}
	
	/**
	 * A method used to get the {@link Person}'s current name value.
	 * @return {@link Person}'s current name.
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * A method used to set the {@link Person}'s name to a new value.
	 * @param name {@link Person}'s new name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * A method used to get the {@link Person}'s current gender value.
	 * @return {@link Person}'s current gender.
	 */
	public char getGender()
	{
		return this.gender;
	}
	
	/**
	 * A method used to set the {@link Person}'s gender to a new value.
	 * @param gender {@link Person}'s new gender.
	 */
	public void setGender(char gender)
	{
		this.gender = gender;
	}
	
	/**
	 * A method used to get the {@link Person}'s current national insurance number value.
	 * @return {@link Person}'s current national insurance number.
	 */
	public String getNatInscNo()
	{
		return this.natInscNo;
	}
	
	/**
	 * A method used to set the {@link Person}'s national insurance number to a new value.
	 * @param natInscNo {@link Person}'s new national insurance number.
	 */
	public void setNatInscNo(String natInscNo)
	{
		this.natInscNo = natInscNo;
	}
	
	/**
	 * A method used to get the {@link Person}'s current date of birth value.
	 * @return {@link Person}'s current date of birth.
	 */
	public String getDob()
	{
		return this.dob;
	}
	
	/**
	 * A method used to set the {@link Person}'s date of birth to a new value.
	 * @param dob {@link Person}'s new date of birth.
	 */
	public void setDob(String dob)
	{
		this.dob = dob;
	}
	
	/**
	 * A method used to get the {@link Person}'s current address value.
	 * @return {@link Person}'s current address.
	 */
	public String getAddress()
	{
		return this.address;
	}
	
	/**
	 * A method used to set the {@link Person}'s address to a new value.
	 * @param address {@link Person}'s new address.
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/**
	 * A method used to get the {@link Person}'s current post code value.
	 * @return {@link Person}'s current post code.
	 */
	public String getPostcode()
	{
		return this.postcode;
	}
	
	/**
	 * A method used to set the {@link Person}'s post code to a new value.
	 * @param postcode {@link Person}'s new post code.
	 */
	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}
	
	/**
	 * A method used with {@link EmployeeDAO} when using it's {@link EmployeeDAO#insertEmployee} method.
	 * @return All values of {@link Person}'s attributes as a string.
	 */
	public String toString()
	{
		return this.getName() + "', '" + this.getGender() + "', '" + this.getDob() + "', '" + this.getAddress() + "', '" + this.getPostcode() + "', '" + this.getNatInscNo();
	}	
}

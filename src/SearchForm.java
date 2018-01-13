import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;

/**
 * The {@link SearchForm} is used to help the user retrieve record(s) that they specifically want to see.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class SearchForm extends JFrame
{
	private String[] columns = {"ID", "Name", "Gender", "DOB", "Address", "Postcode", "Salary", "NIN", "Email", "JobTitle", "StartDate"};
	private JLabel searchLabel, inLabel;
	private JTextField searchBox;
	private JComboBox columnBox;
	private JButton search, cancel;
	
	/**
	 * A constructor for the {@link SearchForm} which displays it's GUI components as specified by it's GridBagConstraints.
	 * It takes the user's search query and stores the results in {@link MainForm#setSearchQuery(java.util.ArrayList)} for
	 * display in the {@link MainForm}'s GUI.
	 */
	public SearchForm()
	{
		super("Enter Search Query");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		searchLabel = new JLabel("Search for");
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(4, 4, 4, 4);
		this.getContentPane().add(searchLabel, c);
		
		//A text field for user to enter their query value
		searchBox = new JTextField();
		c.gridx++;
		this.getContentPane().add(searchBox, c);
		
		inLabel = new JLabel("in");
		c.gridx++;
		this.getContentPane().add(inLabel, c);
		
		//A combo box with options corresponding to the database columns
		columnBox = new JComboBox(columns);
		c.gridx++;
		this.getContentPane().add(columnBox, c);
		
		//Search button
		search = new JButton("Search");
		c.gridx = 1;
		c.gridy++;
		this.getContentPane().add(search, c);
		search.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(searchBox.getText() != null || searchBox.getText() != "")
				{
					try
					{
						MainForm.setSearchQuery(MainForm.getDao().generateSearchQuery(columnBox.getSelectedItem().toString(), searchBox.getText()));
						if (MainForm.getSearchQuery().size() == 0)
						{
							JOptionPane.showMessageDialog(null, "No results found");
						}
						else
						{
							MainForm.setSearchQueryIndex(0);
							MainForm.setSelectedEmployee(MainForm.getSearchQuery().get(MainForm.getSearchQueryIndex()));
							MainForm.loadSelectedEmployee();
							JOptionPane.showMessageDialog(null, "Results found: " +  MainForm.getSearchQuery().size());
							SearchForm.this.dispose();
						}
					}
					catch (SQLException | IOException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		
		//Cancel button
		cancel = new JButton("Cancel");
		c.gridx = 3;
		this.getContentPane().add(cancel, c);
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SearchForm.this.dispose();
			}
		});
	}
}

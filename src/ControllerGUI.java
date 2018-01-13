import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFrame;

/**
 * The {@link ControllerGUI} class is used to display the {@link MainForm}'s GUI in order for the
 * user to interact with and use the database.
 * 
 * @author Nathan Kiernan 15088410
 * @version 1.0
 */
public class ControllerGUI
{
	/**
	 * The main method runs an instance of {@link MainForm} and sets it's size, visibility, resizability,
	 * location on screen and how to respond when the user performs the close operation.
	 * 
	 * @param args Program arguments which are not used in this case.
	 */
	public static void main(String[] args)
	{
		//initiate the creation of the GUI on the event dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				MainForm f;
				try
				{
					f = new MainForm();
					f.setSize(750, 500);
					f.setVisible(true);
					f.setResizable(false);
					f.setLocationRelativeTo(null);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
				catch (SQLException | IOException e)
				{
					e.printStackTrace();
				}				
			}
		});
	}
}
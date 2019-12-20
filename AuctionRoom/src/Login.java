import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.jini.space.JavaSpace;

public class Login extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldItemUsernameID;
	private JTextField textFieldPassword;
	
	private JavaSpace space;
	
	private static final long TWO_MINUTES = 2 * 1000 * 60;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the dialog.
	 */
	public Login() {
		space = SpaceUtils.getSpace();
		if (space == null){
			System.err.println("Failed to find the javaspace");
			System.exit(1);
		}
		init();
	}

	private void init() {
			setBounds(100, 100, 572, 257);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(null);
			
			JLabel lblTitle = new JLabel("Login");
			lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
			lblTitle.setBounds(259, 12, 53, 28);
			contentPanel.add(lblTitle);
			{
				JLabel lblUserID = new JLabel("Username");
				lblUserID.setBounds(82, 58, 66, 15);
				contentPanel.add(lblUserID);
			}
			{
				JLabel lblPassword = new JLabel("Password");
				lblPassword.setBounds(82, 108, 94, 15);
				contentPanel.add(lblPassword);
			}
			
			textFieldItemUsernameID = new JTextField();
			textFieldItemUsernameID.setBounds(166, 52, 365, 28);
			contentPanel.add(textFieldItemUsernameID);
			textFieldItemUsernameID.setColumns(10);
			
			textFieldPassword = new JPasswordField();
			textFieldPassword.setColumns(10);
			textFieldPassword.setBounds(166, 102, 365, 28);
			contentPanel.add(textFieldPassword);
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							login();
						}
					});
					{
						JButton btnRegister = new JButton("Register");
						btnRegister.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								dispose();
								RegisterPage registerPage = new RegisterPage();
								registerPage.setVisible(true);
							}
						});
						buttonPane.add(btnRegister);
					}
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
					
					
				}
			}
	}

	public void login(){

		try{

			String username = textFieldItemUsernameID.getText();
			String password = hash(textFieldPassword.getText());

			User userTemplate = new User();
			userTemplate.username = username;
			User readUser = (User)space.readIfExists(userTemplate, null, 2000);

			if (readUser == null){
				JOptionPane.showMessageDialog(null, "User does not exist");
			}

			if(readUser.password.equals(password)){
				dispose();
				AuctionRoom auctionRoom = new AuctionRoom(username);
				auctionRoom.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "Please enter correct password");
			}
		} catch (Exception ex){

			ex.printStackTrace();
		}


	}

	public String hash(String password){
		try {

			// Reference: https://www.geeksforgeeks.org/md5-hash-in-java/

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			//  of an input digest() return array of byte
			byte[] messageDigest = md.digest(password.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
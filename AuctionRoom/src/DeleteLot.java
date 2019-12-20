import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class DeleteLot extends JDialog {

    //initialising variables

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldBuyPrice;
    private JTextField textFieldUsername;

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private JavaSpace space;
    TransactionManager mgr;

    // transaction manager, and finding the java space
    public DeleteLot() {
        // set up the security manager
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        mgr = SpaceUtils.getManager();
        if (mgr == null) {
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }

        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        init();


    }

    // creating the GUI
    private void init() {
        setBounds(100, 100, 572, 257);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Delete Item");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitle.setBounds(239, 12, 94, 28);
        contentPanel.add(lblTitle);
        {
            JLabel lblBuyPrice = new JLabel("Item ID");
            lblBuyPrice.setBounds(82, 58, 66, 15);
            contentPanel.add(lblBuyPrice);
        }
        {
            JLabel lblUsername = new JLabel("Username");
            lblUsername.setBounds(82, 155, 133, 15);
            contentPanel.add(lblUsername);
        }

        textFieldBuyPrice = new JTextField();
        textFieldBuyPrice.setBounds(166, 52, 365, 28);
        contentPanel.add(textFieldBuyPrice);
        textFieldBuyPrice.setColumns(10);

        textFieldUsername = new JTextField();
        textFieldUsername.setColumns(10);
        textFieldUsername.setBounds(166, 149, 365, 28);
        contentPanel.add(textFieldUsername);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        //code for deleting item
                        deleteLot();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }

    }

    // Function to delete the lot out of the space (can only delete your own lots)
    private void deleteLot() {

        Integer itemID = Integer.parseInt(textFieldBuyPrice.getText());
        try {
            ItemLot itemTemplate = new ItemLot();
            itemTemplate.itemID = itemID;
            ItemLot nextJob = (ItemLot) space.takeIfExists(itemTemplate, null, TWO_SECONDS);
            nextJob.isPurchased = false;
            nextJob.isDeleted = true;
            space.write(nextJob, null, Lease.FOREVER);

            JOptionPane.showMessageDialog(DeleteLot.this, "Item has been Deleted!");

            dispose();

        } catch (Exception e) {
            System.out.println("Failed to read or write to space " + e);
            System.exit(1);
        }
    }


}

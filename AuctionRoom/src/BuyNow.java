import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class BuyNow extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldBuyPrice;
    private JTextField textFieldUsername;
    // initialise variables
    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private JavaSpace space;
    TransactionManager mgr;

    // adding the transaction, finding the java space
    public BuyNow() {
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

    //initialising GUI
    private void init() {
        setBounds(100, 100, 572, 257);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Buy Now");
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
        textFieldUsername.setEditable(false);
        textFieldUsername.setBounds(166, 149, 365, 28);
        contentPanel.add(textFieldUsername);
        textFieldUsername.setText(AuctionServer.currentLoggedInUser);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        //code for deleting item
                        buy();
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

    // Function to buy the lots from the auction
    private void buy() {
        // Now try to take the object back out of the space, modify it, and write it back again.
        // All of this IS part of one single transaction, so it all happens or it all rolls back and never happens
        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(mgr, TWO_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            Integer itemID = Integer.parseInt(textFieldBuyPrice.getText());
            try {
                ItemLot itemTemplate = new ItemLot();
                itemTemplate.itemID = itemID;
                ItemLot nextJob = (ItemLot) space.takeIfExists(itemTemplate, txn, 100);
                nextJob.isPurchased = true;
                itemTemplate.isDeleted = false;
                nextJob.itemBuyer = AuctionServer.currentLoggedInUser;
                space.write(nextJob, txn, Lease.FOREVER);
                JOptionPane.showMessageDialog(BuyNow.this, "Item has been Bought.");

                dispose();


            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }
            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }
}

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class PlaceBid extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldItemID;
    private JTextField textFieldBidPrice;
    private JTextField textFieldUsername;

    private JavaSpace space;

    private static final long TWO_MINUTES = 2 * 1000 * 60;
    private static final long TWO_SECONDS = 2 * 100 * 60;

    /**
     * Launch the application.
     */

    /**
     * Create the dialog.
     */
    public PlaceBid() {
        space = SpaceUtils.getSpace();
        if (space == null) {
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

        JLabel lblTitle = new JLabel("Place Bid");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitle.setBounds(239, 12, 79, 28);
        contentPanel.add(lblTitle);
        {
            JLabel lblItemID = new JLabel("Item ID");
            lblItemID.setBounds(82, 58, 66, 15);
            contentPanel.add(lblItemID);
        }
        {
            JLabel lblBidPrice = new JLabel("Bid Price");
            lblBidPrice.setBounds(82, 108, 94, 15);
            contentPanel.add(lblBidPrice);
        }
        {
            JLabel lblUsername = new JLabel("Username");
            lblUsername.setBounds(82, 155, 133, 15);
            contentPanel.add(lblUsername);
        }

        textFieldItemID = new JTextField();
        textFieldItemID.setBounds(166, 52, 365, 28);
        contentPanel.add(textFieldItemID);
        textFieldItemID.setColumns(10);

        textFieldBidPrice = new JTextField();
        textFieldBidPrice.setColumns(10);
        textFieldBidPrice.setBounds(166, 102, 365, 28);
        contentPanel.add(textFieldBidPrice);

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
                    public void actionPerformed(ActionEvent evt) {
                        if (textFieldBidPrice.getText().length() == 0) {
                            JOptionPane.showMessageDialog(null, "Please enter a Bid Price!");
                        } else {
                            placeBid();
                        }
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

    private void placeBid() {
        try {
            String strItemID = textFieldItemID.getText();
            String strBidValue = textFieldBidPrice.getText();
            Integer item_ID = Integer.valueOf(strItemID);
            Integer bidValue = Integer.valueOf(strBidValue);

            ItemLot itemTemplate = new ItemLot();
            itemTemplate.itemID = item_ID;
            itemTemplate.isPurchased = false;
            itemTemplate.isDeleted = false;
            ItemLot itemObject = (ItemLot) space.read(itemTemplate, null, TWO_SECONDS);
            if (itemObject == null) {
                System.out.println("No items found in the space");
            } else if (bidValue <= itemObject.returnHighestBid()) {
                JOptionPane.showMessageDialog(null,
                        "Bid Amount Must Be Higher Than Current Bid: " + itemObject.returnHighestBid());
            } else {
                ItemLot itemObjectOut = (ItemLot) space.takeIfExists(itemTemplate, null, TWO_MINUTES);

                itemObjectOut.itemBuyer = AuctionServer.currentLoggedInUser;
                itemObjectOut.addBid(bidValue);
                space.write(itemObjectOut, null, Lease.FOREVER);

                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

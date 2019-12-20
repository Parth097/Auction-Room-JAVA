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
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class SellItem extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textFieldID;
    private JTextField textFieldName;
    private JTextField textFieldSellerID;
    private JTextField textFieldBidPrice;
    private JTextField textFieldBuyNow;
    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private JavaSpace space;
    JTextArea textAreaDescription;

    /**
     * Create the dialog.
     */
    public SellItem() {
        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        init();
    }

    private void init() {
        setBounds(100, 100, 572, 532);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblSellItem = new JLabel("Sell Item");
        lblSellItem.setFont(new Font("Dialog", Font.BOLD, 16));
        lblSellItem.setBounds(239, 12, 94, 28);
        contentPanel.add(lblSellItem);
        {
            JLabel lblItemId = new JLabel("Item ID");
            lblItemId.setBounds(82, 58, 66, 15);
            contentPanel.add(lblItemId);
        }
        {
            JLabel lblItemName = new JLabel("Item Name");
            lblItemName.setBounds(82, 108, 94, 15);
            contentPanel.add(lblItemName);
        }
        {
            JLabel lblItemDescription = new JLabel("Description");
            lblItemDescription.setBounds(82, 286, 133, 15);
            contentPanel.add(lblItemDescription);
        }
        {
            JLabel lblSellerName = new JLabel("Seller Name");
            lblSellerName.setBounds(82, 155, 133, 15);
            contentPanel.add(lblSellerName);
        }
        {
            JLabel lblBuyPrice = new JLabel("Buy Now");
            lblBuyPrice.setBounds(82, 249, 133, 15);
            contentPanel.add(lblBuyPrice);
        }
        {
            JLabel lblBidPrice = new JLabel("Bid Price");
            lblBidPrice.setBounds(82, 202, 133, 15);
            contentPanel.add(lblBidPrice);
        }

        textFieldID = new JTextField();
        textFieldID.setBounds(166, 52, 365, 28);
        contentPanel.add(textFieldID);
        textFieldID.setColumns(10);
        textFieldID.setEditable(false);

        textFieldName = new JTextField();
        textFieldName.setColumns(10);
        textFieldName.setBounds(166, 102, 365, 28);
        contentPanel.add(textFieldName);

        textFieldSellerID = new JTextField();
        textFieldSellerID.setColumns(10);
        textFieldSellerID.setBounds(166, 149, 365, 28);
        contentPanel.add(textFieldSellerID);
        textFieldSellerID.setText(AuctionServer.currentLoggedInUser);
        textFieldSellerID.setEditable(false);

        textFieldBidPrice = new JTextField();
        textFieldBidPrice.setColumns(10);
        textFieldBidPrice.setBounds(166, 196, 365, 28);
        contentPanel.add(textFieldBidPrice);

        textFieldBuyNow = new JTextField();
        textFieldBuyNow.setColumns(10);
        textFieldBuyNow.setBounds(166, 243, 365, 28);
        contentPanel.add(textFieldBuyNow);
        {
            textAreaDescription = new JTextArea();
            textAreaDescription.setBounds(166, 286, 355, 149);
            contentPanel.add(textAreaDescription);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        //code for adding item
                        addLot();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    private void addLot() {
        try {
            PSLotStatus qsTemplate = new PSLotStatus();
            PSLotStatus qStatus = (PSLotStatus) space.take(qsTemplate, null, TWO_SECONDS);

            // if there is no QueueStatus object in the space print an error
            if (qStatus == null) {
                System.out.println("No " + qsTemplate.getClass().getName() + " object found.  Has 'StartPrintQueue' been run?");
                System.exit(1);
            }

            // create the new QueueItem, write it to the space, and update the GUI
            int lotNumber = qStatus.nextLot;
            String itemName = textFieldName.getText();
            String itemDescription = textAreaDescription.getText();
            String itemSellerName = AuctionServer.currentLoggedInUser;
            Double itemBuyNow = Double.parseDouble(textFieldBuyNow.getText());
            ArrayList<Integer> bid_value = new ArrayList<>();
            bid_value.add(0);
            PSItemLot newJob = new PSItemLot(lotNumber, itemName, itemSellerName, itemDescription, itemBuyNow, bid_value, false, false, null);
            qStatus.addJob();
            space.write(qStatus, null, Lease.FOREVER);
            space.write(newJob, null, Lease.FOREVER);
            textFieldID.setText("" + lotNumber);

            // update the QueueStatus object by incrementing the counter and write it back to the space


            JOptionPane.showMessageDialog(SellItem.this, "Item has been listed.");

            dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

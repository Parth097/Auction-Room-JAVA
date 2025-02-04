import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.jini.space.JavaSpace;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemDetails extends JDialog {

    private final JPanel contentPanel = new JPanel();

    private JavaSpace space;

    private static final long TWO_MINUTES = 2 * 1000 * 60;

    private JTextArea itemSpecification;


    public ItemDetails(Integer itemID) {
        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        init();
        populateWithItemDetails(itemID);
    }

    // creating the GUI
    private void init() {
        setBounds(100, 100, 572, 257);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        JLabel lblTitle = new JLabel("Item Specification");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTitle.setBounds(210, 12, 151, 28);
        contentPanel.add(lblTitle);

        itemSpecification = new JTextArea();
        itemSpecification.setEditable(false);
        ;
        itemSpecification.setBounds(42, 45, 487, 145);
        contentPanel.add(itemSpecification);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    // creating a function to show all the details of the lots
    private void populateWithItemDetails(Integer itemID) {
        try {
            PSItemLot itemTemplate = new PSItemLot();
            itemTemplate.itemID = itemID;
            PSItemLot itemObject = (PSItemLot) space.readIfExists(itemTemplate, null, TWO_MINUTES);

            if (itemObject == null) {
                System.out.println("No items found in the space");
            } else {
                itemSpecification.append(itemObject.showItemDetails());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

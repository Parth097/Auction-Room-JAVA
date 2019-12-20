import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.export.Exporter;
import net.jini.space.JavaSpace;

import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import javax.swing.JTextField;

public class Buyer extends JFrame implements RemoteEventListener {

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private JavaSpace space;
    private JPanel contentPane;
    private RemoteEventListener theStub;
    JList list;
    DefaultListModel model;
    private Exporter myDefaultExporter;
    // regEx to find the pattern for an ID number
    private static final Pattern p = Pattern.compile("^\\D+(\\d+).*");
    //initializing variables
    private Integer jobID;
    private String userLoggedIn;
    private JTextField txtCurrentUser;


    // Method to run the space, set the current user logged in to the username variable, as well as calling all the important functions

    public Buyer() {
        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        init();
        setVisible(true);
        getItems();
        boughtByCurrentUser();

        //pack();


    }


    // Creating the GUI
    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 761, 469);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel_title = new JPanel();
        contentPane.add(panel_title, BorderLayout.NORTH);

        JLabel lblManageItem = new JLabel("My Bought Items");
        lblManageItem.setFont(new Font("Dialog", Font.BOLD, 24));
        panel_title.add(lblManageItem);

        txtCurrentUser = new JTextField();
        txtCurrentUser.setEditable(false);
        txtCurrentUser.setText("Current User: " + AuctionServer.currentLoggedInUser);
        panel_title.add(txtCurrentUser);
        txtCurrentUser.setColumns(15);

        JPanel panel_buttons = new JPanel();
        contentPane.add(panel_buttons, BorderLayout.SOUTH);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dispose();
                new ManageItems().setVisible(true);
            }
        });
        panel_buttons.add(btnBack);

        JPanel panel_content = new JPanel();
        contentPane.add(panel_content, BorderLayout.CENTER);
        panel_content.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane();
        panel_content.add(scrollPane, BorderLayout.SOUTH);

        model = new DefaultListModel();
        list = new JList(model);

        JScrollPane scrollPaneItems = new JScrollPane(list);
        panel_content.add(scrollPaneItems, BorderLayout.CENTER);

    }
	//Function to show all the items which have been bough by the current user logged in.
    private void boughtByCurrentUser() {
        try {
            PSLotStatus lTemplate = new PSLotStatus();
            PSLotStatus lStatus = (PSLotStatus) space.readIfExists(lTemplate, null, 100);

            int noOfItems = lStatus.nextLot;

            for (int currentItem = 0; currentItem < noOfItems; currentItem++) {

                PSItemLot qiTemplate = new PSItemLot();
                qiTemplate.itemID = currentItem;
                qiTemplate.isPurchased = true;
                qiTemplate.isDeleted = false;
                qiTemplate.itemBuyer = AuctionServer.currentLoggedInUser;
                PSItemLot nextJob = (PSItemLot) space.read(qiTemplate, null, 100);

                if (nextJob == null) {

                } else {
                    // we have a job to process
                    int nextJobNumber = nextJob.itemID;
                    String nextJobName = nextJob.itemName;
                    Double nextJobBuyNow = nextJob.buyNow;
                    Integer bids;
                    String seller = nextJob.itemSeller;
                    if (nextJob.returnHighestBid() == null) {
                        bids = 0;
                    } else {
                        bids = nextJob.returnHighestBid();
                    }
                    model.addElement("Item ID: " + nextJobNumber + " \n Item: " + nextJobName + " \n Seller Name: " + seller);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// get the items from the space with notify to display (rather than manually refreshing)
    private void getItems() {
        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            PSItemLot template = new PSItemLot();
            space.notify(template, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notify(RemoteEvent ev) {
        model.removeAllElements();
        boughtByCurrentUser();

    }

}

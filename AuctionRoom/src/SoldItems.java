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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import javax.swing.JTextField;

public class SoldItems extends JFrame implements RemoteEventListener {

    private static final long TWO_SECONDS = 2 * 1000;  // two thousand milliseconds
    private JavaSpace space;
    private JPanel contentPane;
    private RemoteEventListener theStub;
    JList list;
    DefaultListModel model;
    private Exporter myDefaultExporter;

    private static final Pattern p = Pattern.compile("^\\D+(\\d+).*");

    private Integer jobID;

    private String userLoggedIn;
    private JTextField txtCurrentUser;



    public SoldItems() {
        space = SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        init();
        setVisible(true);
        getItems();
        soldMyItems();

        //pack();


    }

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


    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 761, 469);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel_title = new JPanel();
        contentPane.add(panel_title, BorderLayout.NORTH);

        JLabel lblSoldItems = new JLabel("Sold Items");
        lblSoldItems.setFont(new Font("Dialog", Font.BOLD, 24));
        panel_title.add(lblSoldItems);

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

    private void soldMyItems() {
        try {
            PSLotStatus lTemplate = new PSLotStatus();
            PSLotStatus lStatus = (PSLotStatus) space.readIfExists(lTemplate, null, 100);

            int noOfItems = lStatus.nextLot;

            for (int currentItem = 0; currentItem < noOfItems; currentItem++) {

                PSItemLot qiTemplate = new PSItemLot();
                qiTemplate.itemID = currentItem;
                qiTemplate.isPurchased = true;
                qiTemplate.isDeleted = false;
                qiTemplate.itemSeller = AuctionServer.currentLoggedInUser;
                PSItemLot nextJob = (PSItemLot) space.read(qiTemplate, null, 100);

                if (nextJob == null) {

                } else {
                    // we have a job to process
                    int nextJobNumber = nextJob.itemID;
                    String nextJobName = nextJob.itemName;
                    String itemBuyer = nextJob.itemBuyer;
                    Double nextJobBuyNow = nextJob.buyNow;
                    Integer bids;
                    if (nextJob.returnHighestBid() == null) {
                        bids = 0;
                    } else {
                        bids = nextJob.returnHighestBid();
                    }
                    model.addElement("Job ID: " + nextJobNumber + " \n Item: " + nextJobName + " \n Buy Now: £" + nextJobBuyNow + " \n Highest Bid: " + bids + "\n Buyer: " + itemBuyer);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void notify(RemoteEvent ev) {
        model.removeAllElements();
        soldMyItems();

    }


    private Integer findJobID(String value) {
        Matcher m = p.matcher(value);
        // if an occurrence if a pattern was found in a given string...
        if (m.find()) {
            String x = m.group(1); // first expression from round brackets (Testing)
            jobID = Integer.valueOf(x);
        }
        return jobID;
    }


}

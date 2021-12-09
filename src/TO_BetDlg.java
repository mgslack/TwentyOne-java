import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import au.com.virturo.guiutils.*;
import com.slackandassociates.Utilities;

/** Class defines the dialog used for the getting the 21 bet from the player
 * for the game of 21.  Uses a spinner component created by Kevin Mayer 
 * (kmayer@layer9.com) and a listener class created by Bernard Johnston. <br><br>
 * <b>Changes:</b>
 * <ul>
 * <li> 2002-06-10 - Initial release.
 * <li> 2002-06-16 - Modified constructors.  Added tweaks for applet running.
 * <li> 2005-01-18 - Modified to use 'createTestFrame' in Utilities (for dialog
 * test via 'main' method.
 * </ul>
 * @author Michael G. Slack
 * @author slack@attglobal.net
 * @created 2002-06-10
 * @version 2005-01-18 Version 1.02
*/
public class TO_BetDlg extends JDialog
{
    // statics (maybe move to resource bundle later???)
    private static final String S_BETTIT = "Bet";
    private static final String S_HOWMUCH = "Amount?";
    private static final String S_MINBET = "Minimum Bet: ";
    private static final String S_MAXBET = "   Maximum Bet: ";
    private static final String S_AMTBNK = "Amount in Bank: ";
    private static final String S_INVALID = "Invalid number entered for bet.";
    private static final String S_BADBET1 = "Bet must be between ";
    private static final String S_BADBET2 = " and ";
    private static final String S_TOOBIG = "You don't have that much money.";
    private static final String S_MSGTIT = "Error";

    // window size constants
    private static final int D_DLG_WIDTH = 266;
    private static final int D_DLG_HEIGHT = 166;

    // components used by dialog
    private JPanel dPnl1 = new JPanel();
    private JPanel dPnl2 = new JPanel();
    private JPanel dPnl3 = new JPanel();
    private JLabel dL1 = new JLabel(S_HOWMUCH, SwingConstants.CENTER);
    private JTextField textFld = new JTextField("0", 5);
    private Spinner spinEdit = new Spinner(textFld);
    private JLabel dL2 = new JLabel(S_MINBET, SwingConstants.CENTER);
    private JLabel dL3 = new JLabel(S_AMTBNK, SwingConstants.CENTER);
    private JButton dB1 = new JButton("OK");

    // references
    private ImageIcon mbStopImg = null;
    private int minBet = 0;
    private int maxBet = 0;
    private int iBank = 0;
    private boolean bDoMax = false;
    private int iBet = 0;
    private boolean bApplet = false;

    // ------------------------ Constructors -------------------------

    /** Constructor to create the bet dialog used by the 21 game.
     * @param parent Dialog that dialog is a child of.
     * @param iMinBet An int representing the current minimum bet.
     * @param iMaxBet An int representing the current maximum bet.
     * @param iBankAmt An int representing the current amount of money the
     * player has.
     * @param doMax A boolean indicating to load the 'max' bet into the
     * input initially.
     * @param mbStopImg An icon image for the 'error' dialog used.
    */
    public TO_BetDlg(Dialog parent, int iMinBet, int iMaxBet, int iBankAmt,
                     boolean doMax, ImageIcon mbStopImg)
    {
        this(parent, iMinBet, iMaxBet, iBankAmt, doMax, mbStopImg, false);
    }

    /** Constructor to create the bet dialog used by the 21 game.
     * @param parent Frame that dialog is a child of.
     * @param iMinBet An int representing the current minimum bet.
     * @param iMaxBet An int representing the current maximum bet.
     * @param iBankAmt An int representing the current amount of money the
     * player has.
     * @param doMax A boolean indicating to load the 'max' bet into the
     * input initially.
     * @param mbStopImg An icon image for the 'error' dialog used.
    */
    public TO_BetDlg(Frame parent, int iMinBet, int iMaxBet, int iBankAmt,
                     boolean doMax, ImageIcon mbStopImg)
    {
        this(parent, iMinBet, iMaxBet, iBankAmt, doMax, mbStopImg, false);
    }

    /** Constructor to create the bet dialog used by the 21 game.
     * @param parent Dialog that dialog is a child of.
     * @param iMinBet An int representing the current minimum bet.
     * @param iMaxBet An int representing the current maximum bet.
     * @param iBankAmt An int representing the current amount of money the
     * player has.
     * @param doMax A boolean indicating to load the 'max' bet into the
     * input initially.
     * @param mbStopImg An icon image for the 'error' dialog used.
     * @param bApplet Set to true to indicating dialig is running from an
     * applet.
    */
    public TO_BetDlg(Dialog parent, int iMinBet, int iMaxBet, int iBankAmt,
                     boolean doMax, ImageIcon mbStopImg, boolean bApplet)
    {
        super(parent, S_BETTIT, true);

        // set up things now
        this.minBet = iMinBet;
        this.maxBet = iMaxBet;
        this.iBank = iBankAmt;
        this.mbStopImg = mbStopImg;
        this.bDoMax = doMax;
        this.bApplet = bApplet;

        setupDialog();
        Utilities.centerDialog(parent, this);
    }

    /** Constructor to create the bet dialog used by the 21 game.
     * @param parent Frame that dialog is a child of.
     * @param iMinBet An int representing the current minimum bet.
     * @param iMaxBet An int representing the current maximum bet.
     * @param iBankAmt An int representing the current amount of money the
     * player has.
     * @param doMax A boolean indicating to load the 'max' bet into the
     * input initially.
     * @param mbStopImg An icon image for the 'error' dialog used.
     * @param bApplet Set to true to indicating dialig is running from an
     * applet.
    */
    public TO_BetDlg(Frame parent, int iMinBet, int iMaxBet, int iBankAmt,
                     boolean doMax, ImageIcon mbStopImg, boolean bApplet)
    {
        super(parent, S_BETTIT, true);

        // set up things now
        this.minBet = iMinBet;
        this.maxBet = iMaxBet;
        this.iBank = iBankAmt;
        this.mbStopImg = mbStopImg;
        this.bDoMax = doMax;
        this.bApplet = bApplet;

        setupDialog();
        Utilities.centerDialog(parent, this);
    }

    // ----------------------- Private Methods --------------------------

    private void setupDialog()
    {
        Container cp = getContentPane();
        int mx = maxBet;
        int hh = D_DLG_HEIGHT;

        if (bApplet) hh += 24; // for 'applet window border...
        setSize(D_DLG_WIDTH, hh);
        cp.setLayout(new BorderLayout());
        dPnl1.setLayout(new GridLayout(4, 1));
        dPnl1.add(dL1);
        if (bDoMax) {
            if (mx > iBank) mx = iBank;
            textFld.setText(""+mx);
        }
        else {
            textFld.setText(""+minBet);
        }
        spinEdit.addSpinListener(new IntSpinListener(minBet, mx));
        dPnl2.add(spinEdit);
        dPnl1.add(dPnl2);
        dL2.setText(S_MINBET + minBet + S_MAXBET + maxBet);
        dPnl1.add(dL2);
        dL3.setText(S_AMTBNK + iBank);
        dPnl1.add(dL3);
        cp.add(dPnl1, BorderLayout.CENTER);
        dB1.addActionListener(getBtnAction());
        dPnl3.add(dB1);
        cp.add(dPnl3, BorderLayout.SOUTH);
    }

    private ActionListener getBtnAction()
    {
        return new ActionListener() 
                   {
                       public void actionPerformed(ActionEvent evt) {
                           boolean valid = true;
                           int choice = 0;
                           String errS = "";

                           try {
                               String ss = textFld.getText().trim();
                               if ((ss == null) || ("".equals(ss)))
                                   choice = 0;
                               else
                                   choice = Integer.parseInt(ss);
                               }
                           catch(NumberFormatException n) {
                               valid = false;
                               errS = S_INVALID;
                           }
                           if (valid) { // check value entered then...
                               if (choice < 0) choice = 0;
                               if ((choice < minBet) || (choice > maxBet)) {
                                   valid = false;
                                   errS = S_BADBET1 + minBet + S_BADBET2 + maxBet;
                               }
                               else if (choice > iBank) {
                                   valid = false;
                                   errS = S_TOOBIG;
                               }
                               else {
                                   iBet = choice;
                               }
                           }
                           if (valid) {
                               dispose(); // close the dialog
                           }
                           else {
                               JOptionPane.showMessageDialog(null, errS, S_MSGTIT,
                                                             JOptionPane.ERROR_MESSAGE,
                                                             mbStopImg);
                           }
                       }
                   };
    }

    // ------------------------- Public Methods --------------------------

    /** Method used to return the bet entered into the dialog. */
    public int getBet()
    {
        return iBet;
    }

    /** Testor method. */
    public static void main(String[] args)
    {
        JFrame frame = Utilities.createTestFrame("21 Bet Dlg Test", D_DLG_WIDTH, D_DLG_HEIGHT);
        TwentyOneProps props = new TwentyOneProps(frame); // need for class ref
        Image temp = Utilities.loadImage(props, frame.getToolkit(), "images/", "hand.gif");
        ImageIcon mbStopImg = new ImageIcon(temp);
        int min = (int)(Math.random() * 10);
        int max = ((int)(Math.random() * 10)) + 20;
        int bnk = ((int)(Math.random() * 20)) + 20;

        TO_BetDlg dlg = new TO_BetDlg(frame, min, max, bnk, true, mbStopImg);
        dlg.show();
        System.out.println("Bet is: " + dlg.getBet());
    }
}
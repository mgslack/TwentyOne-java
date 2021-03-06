import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.slackandassociates.*;
import com.slackandassociates.cards.*;
import com.slackandassociates.cards.playingcards.*;
import com.slackandassociates.dialogs.AboutDlg;

/**
 * Class used to run the game of 21. <br><br>
 * <b>Changes:</b>
 * <ul>
 * <li> 2002-06-09 - Initial release.
 * <li> 2002-06-16 - Tweaked to run better as an applet.
 * <li> 2002-06-17 - Added 'setLocationRelativeTo' call if applet to bet dialog.
 * <li> 2002-06-18 - Fixed a bug in 'getScoreOfHand' (with splits).
 * <li> 2002-06-19 - Fixed a bug in the 'finishOffDealer' method.
 * <li> 2002-06-22 - Modified to use 'centerWindowOnScreen' method.
 * <li> 2002-06-23 - Added double-buffering in 'PlayArea' component.
 * <li> 2002-06-25 - Modified to use 'AboutDlg' in slack.jar.
 * <li> 2003-08-16 - Modified to add fonts so labels display without truncation
 *                   under GCD Java 1.4.1.
 * <li> 2005-01-11 - Modified to use cards v2 library.
 * <li> 2005-11-08 - Cleaned up code per 'findbugs' hints.
 * <li> 2005-12-19 - Added 'invokeLater' call to main method.
 * <li> 2006-03-15 - Modified menu 'exit' code (removed System.exit() call).
 * <li> 2007-10-14 - Removed un-needed cast.
 * <li> 2021-12-09 - Removed applet references and slightly increased window height
 *                   (408 to 410).
 * </ul>
 * @author Michael G. Slack
 * @author slack@attglobal.net
 * @version Version 2.1.0
*/
public class TwentyOne
{
    // window size constants
    private static final int W_WIDTH = 500;
    private static final int W_HEIGHT = 410;

    // string statics
    private static final String S_CL_LBL  = "Cards Left: ";
    private static final String S_AIB_LBL = "Amount in Bank: ";
    private static final String S_BET_LBL = "Bet: ";

    // card deck, images and hands
    private CardDeck cards = null; // setup later with correct number of decks
    private PlayingCardImageCache cImgs = new PlayingCardImageCache();
    private CardHand dealerCards = new CardHand(5, false);
    private CardHand playerCards = new CardHand(5, false);

    // window components
    private JFrame frame = null;
    private Container frame_cp = null;
    private JMenuBar mb = new JMenuBar();
    private JMenu m1 = new JMenu("File");
    private JMenuItem mi1 = new JMenuItem("Exit");
    private JMenu m2 = new JMenu("Help");
    private JMenuItem mi2 = new JMenuItem("About");

    // other components
    private ImageIcon mbOKImg = null;
    private ImageIcon mbStopImg = null;
    private JLabel lblCTitle = new JLabel(S_CL_LBL);
    private ImageCanvas cardImg = null;
    private JButton btnPlay = new JButton("Play");
    private JButton btnHit = new JButton("Hit");
    private JButton btnStay = new JButton("Stay");
    private JButton btnDouble = new JButton("Double");
    private JButton btnSplit = new JButton("Split");
    private JButton btnInsurance = new JButton("Insurance");
    private JLabel lblDTitle = new JLabel("Dealers Cards:");
    private PlayArea paDealer = new PlayArea(true);
    private JLabel lblPTitle = new JLabel("Players Cards:");
    private PlayArea paPlayer = new PlayArea(false);
    private JLabel lblMsg = new JLabel(" ");
    private JLabel lblAIB = new JLabel(S_AIB_LBL);
    private JLabel lblBet = new JLabel(S_BET_LBL);

    // property instance
    private TwentyOneProps props = new TwentyOneProps(this);

    // other instances
    private boolean bShowDown = false;
    private boolean bSplitHand = false;
    private boolean bDoubleHand = false;
    private int iCardsLeft = 0;
    private int iAmountWon = 0;
    private int iBet = 0;
    private PlayingCardEnum ceCardBackPic = (PlayingCardEnum) PlayingCardEnum.JC_CARDBACK_VAL1;
    private int iNumOfDecks = CardDeck.JC_ONE_DECK;
    private int iMinimumBet = TwentyOneProps.MIN_BET_AMT;
    private int iMaximumBet = TwentyOneProps.START_MAX;
    private int iInitialBank = TwentyOneProps.START_IBANK;
    private boolean bBet_Max = false;

    // ------------------------ Private Methods --------------------------

    /** Method used to create the frame, updated after removing applet code. */ 
    private void createFrame()
    {
        frame = new JFrame("TwentyOne");
        frame.setIconImage(Utilities.loadImage(this, frame.getToolkit(),
                                               "images/", "twentyone.gif"));
        // set default close operation
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /** Method used to load the reference variables with values from the
     * props object.  Loaded here to allow for changes via a 'settings'
     * dialog.
    */
    private void loadProps()
    {
        ceCardBackPic = (PlayingCardEnum) props.getCardBackImage();
        iNumOfDecks = props.getNumDecks();
        iMinimumBet = props.getMinimumBet();
        iMaximumBet = props.getMaximumBet(iMinimumBet);
        iInitialBank = props.getInitialBank(iMinimumBet);
        bBet_Max = props.getBetMax();
    }

    /** Method used to setup the application menu bar. */
    private void setupMenu()
    {
        mi1.addActionListener(new ActionListener() { // exit
                                      public void actionPerformed(ActionEvent evt) {
                                          // was: System.exit(0);
                                          frame.setVisible(false);
                                          frame.dispose();
                                      }
                                  });
        m1.add(mi1);
        mb.add(m1);
        mi2.addActionListener(new ActionListener() { // about
                                      public void actionPerformed(ActionEvent evt) {
                                          Image iImg = Utilities.loadImage(this,
                                                  frame.getToolkit(), "images/", "21.gif");
                                          new AboutDlg(frame,
                                                       frame.getTitle(),
                                                       props.getVersionInfo(),
                                                       "Michael G. Slack",
                                                       props.getAuthorEmail(),
                                                       iImg).show();
                                      }
                                  });
        m2.add(mi2);
        mb.add(m2);
        frame.setJMenuBar(mb);
    }

    /** Method to create and return the 'card deck' panel. */
    private JPanel createCardDeckPanel()
    {
        JPanel pnlRet = new JPanel();
        Dimension d = new Dimension();
        int w = PlayingCardImageCache.IMAGE_WIDTH;
        int h = PlayingCardImageCache.IMAGE_HEIGHT;
        JPanel imgPnl = new JPanel();
        
        cardImg = new ImageCanvas(cImgs.getCardImage(ceCardBackPic));
        imgPnl.add(cardImg);

        lblCTitle.setFont(new Font("Helvetica", Font.PLAIN, 12));
        iCardsLeft = 52 * iNumOfDecks;
        lblCTitle.setText(S_CL_LBL + iCardsLeft);
        d.setSize(w+22, h+24);

        pnlRet.setPreferredSize(d);
        pnlRet.setMinimumSize(d);
        pnlRet.setMaximumSize(d);
        pnlRet.setLayout(null);
        lblCTitle.setBounds(new Rectangle(1, 4, w+20, 16));
        imgPnl.setBounds(new Rectangle(10, 21, w, h+5));
        pnlRet.add(lblCTitle, null);
        pnlRet.add(imgPnl, null);

        return pnlRet;
    }

    /** Method used to set buttons size so all are the same. */
    private void setButtonSize(JButton b)
    {
        Dimension d = new Dimension(100, 27);

        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.setMinimumSize(d);
    }

    /** Method to set the enabled state of all of the buttons. */
    private void setEnab(boolean b1, boolean b2, boolean b3, boolean b4,
                         boolean b5, boolean b6)
    {
        btnPlay.setEnabled(b1);
        btnHit.setEnabled(b2);
        btnStay.setEnabled(b3);
        btnDouble.setEnabled(b4);
        btnSplit.setEnabled(b5);
        btnInsurance.setEnabled(b6);
    }

    /** Method to setup the button handlers. */
    private void setBtnHandlers()
    {
        btnPlay.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new PlayT().start();
                                         }
                                     });
        btnHit.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new HitT().start();
                                         }
                                     });
        btnStay.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new StayT().start();
                                         }
                                     });
        btnDouble.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new DoubleT().start();
                                         }
                                     });
        btnSplit.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new SplitT().start();
                                         }
                                     });
        btnInsurance.addActionListener(new ActionListener() {
                                         public void actionPerformed(ActionEvent evt) {
                                             new InsuranceT().start();
                                         }
                                     });
    }

    /** Method to create the button panel. */
    private Box createButtonPanel()
    {
        Box v1 = Box.createVerticalBox();

        setButtonSize(btnPlay);
        v1.add(btnPlay);
        setButtonSize(btnHit);
        v1.add(btnHit);
        setButtonSize(btnStay);
        v1.add(btnStay);
        setButtonSize(btnDouble);
        v1.add(btnDouble);
        setButtonSize(btnSplit);
        v1.add(btnSplit);
        setButtonSize(btnInsurance);
        v1.add(btnInsurance);

        setEnab(false, false, false, false, false, false);
        setBtnHandlers();

        return v1;
    }

    /** Method to load and install the west panel of the program. */
    private void loadWestPanel()
    {
        JPanel pnlWest = new JPanel(new GridLayout(2, 1));

        pnlWest.add(createCardDeckPanel());
        pnlWest.add(createButtonPanel());

        frame_cp.add(pnlWest, BorderLayout.WEST);
    }

    /** Method to load and install the center panel of the program. */
    private void loadCenterPanel()
    {
        JPanel pnlCenter = new JPanel(new FlowLayout());

        pnlCenter.add(lblDTitle);
        pnlCenter.add(paDealer);
        pnlCenter.add(lblPTitle);
        pnlCenter.add(paPlayer);
        // pnlCenter.add(lblMsg);

        frame_cp.add(pnlCenter, BorderLayout.CENTER);
    }

    /** Method to load and install the south panel of the program. */
    private void loadSouthPanel()
    {
        JPanel pnlSouth = new JPanel(new FlowLayout());

        iAmountWon = iInitialBank;
        lblAIB.setText(S_AIB_LBL + iAmountWon);
        pnlSouth.add(lblAIB);
        pnlSouth.add(new JLabel("     "));
        lblBet.setText(S_BET_LBL + "0");
        pnlSouth.add(lblBet);

        frame_cp.add(pnlSouth, BorderLayout.SOUTH);
    }

    /** Method copied out of JOptionPane returns parent window of a
     * component.  Using to display bet dialog properly inside of an
     * applet.
    */
    private Window getWindowForComponent(Component parentComponent)
    {
        if (parentComponent == null)
            return JOptionPane.getRootFrame();

        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;

        return getWindowForComponent(parentComponent.getParent());
    }

    /** Method used to display message to user. */
    private void displayMessage(String msg)
    {
        JOptionPane.showMessageDialog(frame, msg, "Information",
                                      JOptionPane.INFORMATION_MESSAGE, mbOKImg);
    }

    // ------------------------- Protected Methods -------------------------

    /** Method used to get the players bet from them. */
    protected boolean getBet()
    {
        TO_BetDlg dlg = null;

        // do we have enough to play?
        if (iAmountWon < iMinimumBet) { // not enough to play - reset?
            int jopRet = JOptionPane.NO_OPTION;

            jopRet = JOptionPane.showConfirmDialog(frame,
                                           "You don't have enough money, reset game?",
                                           "Question",
                                           JOptionPane.YES_NO_OPTION,
                                           JOptionPane.QUESTION_MESSAGE,
                                           mbOKImg);
            if (jopRet == JOptionPane.YES_OPTION) {
                iAmountWon += iInitialBank;
                lblAIB.setText(S_AIB_LBL + iAmountWon);
            }
            else {
                return false;
            }
        }

        // get bet then from player...
        dlg = new TO_BetDlg(frame, iMinimumBet, iMaximumBet,
                            iAmountWon, bBet_Max, mbStopImg);
        dlg.show();
        iBet = dlg.getBet();

        return (iBet != 0);
    }

    /** Method used to start off the hand - initial deal. */
    protected void startHand()
    {
        if (iCardsLeft <= 9) { // reshuffle
            cards.shuffle();
            iCardsLeft = 52 * iNumOfDecks;
        }

        // reset variables
        bSplitHand = false;
        bDoubleHand = false;
        bShowDown = false;
        dealerCards.removeAll();
        playerCards.removeAll();

        // deal the cards
        for (int i = 0; i < 2; i++) {
            dealerCards.add(cards.getNextCard());
            playerCards.add(cards.getNextCard());
            iCardsLeft -= 2;
        }

        // redisplay everything
        lblCTitle.setText(S_CL_LBL + iCardsLeft);
        paDealer.repaint();
        paPlayer.repaint();
    }

    /** Method used to return the points in the hand passed in. */
    protected int getScoreOfHand(CardHand ch)
    {
        int iRet = 0;
        int x = ch.getMaximumCardCount();

        for (int i = 0; i < x; i++) {
            Card c = ch.cardAt(i);
            if (c != CardHand.EMPTY_CARD)
                iRet += c.getCardPointValueFace10();
            else
                break; // no more...
        }

        // check for ace - maybe add 10
        // note, will only check for one (can't have 22...)
        for (int i = 0; i < x; i++) {
            Card c = ch.cardAt(i);

            if (c != CardHand.EMPTY_CARD) {
                int v = c.getCardPointValue();
                if (v == 1) {
                    if ((10 + iRet) <= 21) iRet += 10;
                    break; // checked the one - leave
                }
            }
            else {
                break; // no more cards to check...
            }
        }

        return iRet;
    }

    /** Method to score game and display message about it. */
    protected void scoreIt(int iPts, boolean bPly, boolean bPush,
                           boolean bFiveC, boolean bResetG)
    {
        StringBuffer sMsg = new StringBuffer(100);
        String sWho = "";

        if (!bShowDown) { // show down card...
            bShowDown = true;
            paDealer.repaint();
        }

        // create message for display
        if (bPush) {
            sMsg.append("Push! Nobody wins this hand...");
            iAmountWon += iBet;
            if (bDoubleHand) iAmountWon += iBet; // add back in also
        }
        else {
            if (bPly) { // player won
                sWho = "Player";
                iAmountWon += iBet + iBet; // pay back what was bet + won
                if (bDoubleHand) iAmountWon += iBet + iBet; // and again
            }
            else {
                sWho = "Dealer";
            }
            if (bFiveC) {
                sMsg.append(sWho);
                sMsg.append(" has drawn 5 cards without going over 21, ");
                sMsg.append(sWho);
                sMsg.append(" wins.");
            }
            else {
                sMsg.append("The ");
                sMsg.append(sWho);
                sMsg.append(" has won with ");
                sMsg.append(""+iPts);
                sMsg.append(".");
            }
        }
        displayMessage(sMsg.toString());
        lblAIB.setText(S_AIB_LBL + iAmountWon);

        // reset game...
        if (bResetG) {
            setEnab(true, false, false, false, false, false);
            btnPlay.requestFocus();
            dealerCards.removeAll();
            playerCards.removeAll();
            lblBet.setText(S_BET_LBL + "0");
            paDealer.repaint();
            paPlayer.repaint();
        }
    }

    /** Method used to check hands at start for 21. */
    protected void checkHands()
    {
        int d = getScoreOfHand(dealerCards);
        int p = getScoreOfHand(playerCards);

        if ((d == 21) && (p == 21)) { // push - both have 21
            scoreIt(p, false, true, false, true);
        }
        else {
            if (p == 21) { // player wins...
                scoreIt(p, true, false, false, true);
            }
            else {
                int c = dealerCards.cardAt(1).getCardPointValue();
                if ((d == 21) && (c != 1)) { // dealer won
                    scoreIt(d, false, false, false, true);
                }
                else { // finish setup
                    setEnab(false, true, true, true, false, false);
                    int p1 = playerCards.cardAt(0).getCardPointValue();
                    int p2 = playerCards.cardAt(1).getCardPointValue();
                    if (c == 1) btnInsurance.setEnabled(true);
                    if (p1 == p2) btnSplit.setEnabled(true);
                    btnStay.requestFocus();
                }
            }
        }
    }

    /** Method used to add a card to a hand (dealer or player).  Returns
     * the points of the hand.
    */
    protected int giveCard(CardHand hnd, boolean bPly)
    {
        hnd.add(cards.getNextCard());
        iCardsLeft--;

        // redisplay everything
        lblCTitle.setText(S_CL_LBL + iCardsLeft);
        if (bPly)
            paPlayer.repaint();
        else
            paDealer.repaint();

        return getScoreOfHand(hnd);
    }

    /** Method used to finish off the dealers hand. */
    protected void finishOffDealer()
    {
        int d, p, p2 = 0;

        setEnab(false, false, false, false, false, false); // disable all
        // show down card
        bShowDown = true;
        paDealer.repaint();
        // score hands
        d = getScoreOfHand(dealerCards);
        p = getScoreOfHand(playerCards);
        if (bSplitHand) {
            CardHand ply2 = new CardHand(2, false);
            
            ply2.add(playerCards.cardAt(3));
            ply2.add(playerCards.cardAt(4));
            p2 = getScoreOfHand(ply2);
        }
        while ((d <= 16) && 
               (dealerCards.getCardCount() < TwentyOneProps.MAX_DRAW_CARDS)) {
            d = giveCard(dealerCards, false);
        }
        // now do it
        if (d <= 21) { // who won...
            if (dealerCards.getCardCount() == TwentyOneProps.MAX_DRAW_CARDS) {
                scoreIt(d, false, false, true, true);
            }
            else {
                if (bSplitHand) displayMessage("Scoring first hand of split.");
                if (d == p) {
                    scoreIt(p, false, true, false, !bSplitHand); // push
                }
                else {
                    if (d < p)
                        scoreIt(p, true, false, false, !bSplitHand);
                    else
                        scoreIt(d, false, false, false, !bSplitHand);
                }
                if (bSplitHand) {
                    displayMessage("Scoring second hand of split.");
                    if (d == p2) {
                        scoreIt(p2, false, true, false, true); // push
                    }
                    else {
                        if (d < p2)
                            scoreIt(p2, true, false, false, true);
                        else
                            scoreIt(d, false, false, false, true);
                    }
                }
            }
        }
        else { // player won...
            displayMessage("Dealer has exceeded 21.");
            if (bSplitHand) displayMessage("Scoring first hand of split.");
            scoreIt(p, true, false, false, !bSplitHand);
            if (bSplitHand) { // score second hand...
                displayMessage("Scoring second hand of split.");
                scoreIt(p2, true, false, false, true);
            }
        }
    }

    // --------------------------- Public Methods --------------------------

    /** Method implementing applet initialization routine.  It also
     * manages the setup of the panels, contents and frames.
    */
    public void init()
    {
        // get content pane
        if (frame != null) {
            frame_cp = frame.getContentPane();
            setupMenu();
        }
		/* removed else to get content pane from applet */

        // load changeable references from props
        loadProps();

        // setup intial card deck
        cards = new CardDeck(iNumOfDecks, PlayingCardDeck.PC_DECK, PlayingCard.class);
        cards.shuffle();

        // load dialog icon images
        Image temp = Utilities.loadImage(this, frame.getToolkit(), "images/", "info.gif");
        mbOKImg = new ImageIcon(temp);
        temp = Utilities.loadImage(this, frame.getToolkit(), "images/", "hand.gif");
        mbStopImg = new ImageIcon(temp);

        // add components to content panel
        loadWestPanel();
        loadCenterPanel();
        loadSouthPanel();

        // other stuff (for frame)
        if (frame != null) {
            // prepare to show
            frame.setSize(W_WIDTH, W_HEIGHT);
            Utilities.centerWindowOnScreen(frame, W_WIDTH, W_HEIGHT);
            frame.setResizable(false);
            // show me now
            frame.setVisible(true);
        }

        // start up image load...
        // lblMsg.setText("Loading card images, please wait...");
        new CardLoader().start();
    }

    /** Method used to emulate the process of 'starting' an applet from
     * a 'main' method.
    */
    public void go()
    {
        init();
		/* removed 'start()' call with applet removal */
    }

    /** Method used to run the game as a client application. */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
                                     public void run() {
                                         TwentyOne t21 = new TwentyOne();

                                         t21.createFrame();
                                         t21.go();
                                     }
                                   });
    }

    // ---------------------- Inner Classes -------------------------

    /**
     * Class used to initialize the card images during startup.  Runs as
     * a 'SwingWorker' thread to 'waitfor' the card images until all are
     * loaded.
    */
    class CardLoader extends com.slackandassociates.SwingWorker
    {
        /** Run 'waitfor' routine until all cards images fully loaded.
         * Return a string containing 'success'.
        */
        public Object construct()
        {
            cImgs.waitForImages(frame_cp);
            return "Success";
        }

        /** Update U/I to indicate load is finished. */
        public void finished()
        {
            btnPlay.setEnabled(true);
            // lblMsg.setText("");
        }
    }

    /**
     * Class (component) used to deal with the card playing area.  Basically
     * component sets up a set size playing area and handles the painting of the
     * playing area for the game.  Created as an inner class so that it can take
     * advantage of the 'dealt' (hit) cards to paint with.
    */
    class PlayArea extends javax.swing.JComponent
    {
        /** Playing area size reference. */
        private Dimension dim = new Dimension(367, 106);

        /** Whos cards are these. */
        private boolean dealer;

        // other references
        Image offScrn = null;
        Graphics offGrph = null;

        /** Constructor to create the 21 hand playing area (where the cards are played). */
        public PlayArea(boolean dlr)
        {
            super();

            dealer = dlr;

            // set size
            setPreferredSize(dim);
        }

        /** Method to return the accessibilty context to the caller.  This class
         * returns null.
         * @return A AccessibleContext object for this class.  There is currently no
         * support for this so only a null is returned for this method.
        */
        public javax.accessibility.AccessibleContext getAccessibleContext()
        {
            return null;
        }

        /** Overrode method used to display the playing area on the screen.
         * @param g Graphic instance used to draw with.
        */
        public void paint(Graphics g)
        {
            CardHand playedCards;
            int x = 2, y = 4;

            // create background area
            if (offGrph == null) {
                offScrn = createImage(dim.width, dim.height);
                offGrph = offScrn.getGraphics();
            }

            // set up background and border color
            offGrph.setColor(Color.green.darker());
            offGrph.fillRect(0, 0, dim.width, dim.height);

            // whose hand are we doing...
            if (dealer)
                playedCards = dealerCards;
            else
                playedCards = playerCards;

            // paint the played cards now (if any)
            int count = 5; // need to loop through all of the players cards (split...)
            // only go through those in dealer hand though
            if (dealer) count = playedCards.getCardCount();
            for (int i = 0; i < count; i++) {
                Card card = playedCards.cardAt(i);
                if (card != CardHand.EMPTY_CARD) { // paint it
                    if ((dealer) && (i == 0) && (!bShowDown))
                        offGrph.drawImage(cImgs.getCardImage(ceCardBackPic), x, y, this);
                    else
                        offGrph.drawImage(cImgs.getCardImage((PlayingCard) card), x, y, this);
                }
                x += PlayingCardImageCache.IMAGE_WIDTH + 2;
            }

            // now display drawing...
            g.drawImage(offScrn, 0, 0, this);
        }

        /** Method called during component update.  Overrode to do away with
         * screen flicker/erasure.
         * @param screen Graphic instance used to draw with.
        */
        public void update(Graphics screen)
        {
            paint(screen);
        }
    }

    /** Class used to run the 'play' button event. */
    class PlayT extends Thread
    {
        /** Constructor. */
        public PlayT()
        {
            super();

            setName("play");
        }

        /** Method used to run the 'play' event (button pressed). */
        public void run()
        {
            btnPlay.setEnabled(false);
            if (getBet()) {
                iAmountWon -= iBet;
                lblBet.setText(S_BET_LBL + iBet);
                lblAIB.setText(S_AIB_LBL + iAmountWon);
                startHand();
                try { // pause
                    sleep(250);
                }
                catch (InterruptedException ie) { }
                checkHands();
            }
            else {
                btnPlay.setEnabled(true);
            }
        }
    }

    /** Class used to run the 'hit' button event. */
    class HitT extends Thread
    {
        /** Constructor. */
        public HitT()
        {
            super();

            setName("hit");
        }

        /** Method used to run the 'hit' event (button pressed). */
        public void run()
        {
            int p = 0;

            // disable some of the buttons - no longer available
            btnDouble.setEnabled(false);
            btnInsurance.setEnabled(false);
            btnSplit.setEnabled(false);

            p = giveCard(playerCards, true);
            if ((playerCards.getCardCount() == TwentyOneProps.MAX_DRAW_CARDS) &&
                (p <= 21)) {
                scoreIt(0, true, false, true, true);
            }
            else {
                if (p > 21) {
                    displayMessage("You've exceeded 21.");
                    scoreIt(getScoreOfHand(dealerCards), false, false, false, true);
                }
            }
        }
    }
    
    /** Class used to run the 'stay' button event. */
    class StayT extends Thread
    {
        /** Constructor. */
        public StayT()
        {
            super();

            setName("stay");
        }

        /** Method used to run the 'stay' event (button pressed). */
        public void run()
        {
            finishOffDealer();
        }
    }

    /** Class used to run the 'double' button event. */
    class DoubleT extends Thread
    {
        /** Constructor. */
        public DoubleT()
        {
            super();

            setName("double");
        }

        /** Method used to run the 'double' event (button pressed). */
        public void run()
        {
            btnDouble.setEnabled(false); // double or not, this is done...
            if (iAmountWon < iBet) {
                displayMessage("You don't have enough money to double down with.");
            }
            else {
                int p = 0;

                iAmountWon -= iBet; // subtract it out...
                lblAIB.setText(S_AIB_LBL + iAmountWon);
                p = giveCard(playerCards, true);
                if (p > 21) { // whoops!
                    displayMessage("You've exceeded 21.");
                    scoreIt(getScoreOfHand(dealerCards), false, false, false, true);
                }
                else { // finish off dealer...
                    bDoubleHand = true;
                    finishOffDealer();
                }
            }
        }
    }

    /** Class used to run the 'split' button event. */
    class SplitT extends Thread
    {
        /** Constructor. */
        public SplitT()
        {
            super();

            setName("split");
        }

        /** Method used to run the 'split' event (button pressed). */
        public void run()
        {
            btnSplit.setEnabled(false); // either way, no more splitting...
            if (iAmountWon < iBet) {
                displayMessage("You don't have enough money to split.");
            }
            else {
                iAmountWon -= iBet;
                lblAIB.setText(S_AIB_LBL + iAmountWon);
                // move card 1 to pos 3, get two more...
                playerCards.replace(playerCards.cardAt(1), 3);
                playerCards.replace(cards.getNextCard(), 1);
                playerCards.replace(cards.getNextCard(), 4);
                iCardsLeft -= 2;

                // redisplay everything
                lblCTitle.setText(S_CL_LBL + iCardsLeft);
                paPlayer.repaint();
                try { // pause
                    sleep(300);
                }
                catch (InterruptedException ie) { }
                bSplitHand = true;
                finishOffDealer();
            }
        }
    }

    /** Class used to run the 'stay' button event. */
    class InsuranceT extends Thread
    {
        /** Constructor. */
        public InsuranceT()
        {
            super();

            setName("insurance");
        }

        /** Method used to run the 'insurance' event (button pressed). */
        public void run()
        {
            int insCost = (int)(iBet * 0.25);

            btnInsurance.setEnabled(false); // turn off - either get it or not now...
            if (insCost > iAmountWon) {
                String s = "You don't have enough money for insurance, need " +
                           insCost + ".";
                displayMessage(s);
            }
            else {
                iAmountWon -= insCost;
                lblAIB.setText(S_AIB_LBL + iAmountWon);
                if (dealerCards.cardAt(0).getCardPointValueFace10() == 10) {
                    // dealer has 21 - good call!
                    iAmountWon += iBet; // give bet back - just lost ins cost
                    scoreIt(21, false, false, false, true);
                }
                else { // dealer does not have 21 - continue...
                    displayMessage("Dealer does not have 21.");
                    btnStay.requestFocus();
                }
            }
        }
    }
}
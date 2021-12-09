import java.util.*;
import java.io.*;
import com.slackandassociates.cards.CardDeck;
import com.slackandassociates.cards.CardEnum;
import com.slackandassociates.cards.playingcards.PlayingCardEnum;

/** Class defines the 'settable' properites and settings used by the TwentyOne
 * game along with the defaults.  Properties are stored in a properties file
 * called 'TwentyOne.properties' and should be accessible via the Properties
 * class. <br>
 * There are a few properties that should not be changed.  These are marked
 * in the property file. <br>
 * The following are the properties available for setting:
 * <ul>
 * <li> TwentyOne.CardBackImage=<0 through 7>.  Indexes into a private array of
 * the card back image enumerations.  Default is '2'.
 * <li> TwentyOne.NumDecks=<1 through 7>.  Number of decks to use.  Default is 1.
 * <li> TwentyOne.MinimumBet=<1 through 10000>.  Minimum bet that can be made.
 * Default is 1.
 * <li> TwentyOne.MaximumBet=<minbet through 10000>.  Maximum bet that can be
 * made.  Default is 5.  If a player does not have maxbet money, the most a 
 * player can bet is the amount he has.
 * <li> TwentyOne.InitialBank=<minbet through 100000>.  Initial starting bank
 * for a player.  This is also the amount added to the players current bank
 * if game is reset.
 * <li> TwentyOne.BetMax=<anything>.  Default to maximum (highest) bet from
 * player.  Otherwise, use the minimum bet amount.  To turn off (minimum bet),
 * comment the option out.
 * </ul>
 * <b>Changes:</b>
 * <ul>
 * <li> 2002-06-07 - Initial Release.
 * <li> 2005-01-11 - Updated for cards v2.
 * <li> 2007-10-14 - Updated comments and properties.  Added new card backs.
 * </ul>
 * @author Michael G. Slack
 * @author slack@attglobal.net
 * @version Version 2.01
*/
public class TwentyOneProps
{
    // public statics
    /** Minimum bet amount. Changable via settings. */
    public final static int MIN_BET_AMT    = 1;

    /** Starting maximum bet. Changable via settings. */
    public final static int START_MAX      = 5;

    /** Absolute maximum bet.  Highest that can be set. */
    public final static int MAX_BET_AMT    = 10000;

    /** Starting players bank.  Changable via settings. */
    public final static int START_IBANK    = 100;

    /** Absolute maximum starting players bank.  Highest that can be set. */
    public final static int MAX_IBANK_AMT  = 100000;

    /** Draw (hit) this number of cards, automatic win. */
    public final static int MAX_DRAW_CARDS = 5;

    /** Card backs (represented as an array). Made public so it can be 
     * used by the main program (normally would be private).
     * Note: I know this is bogus....
    */
    public CardEnum[] cardBacks = {PlayingCardEnum.JC_CARDBACK_VAL1,
                                   PlayingCardEnum.JC_CARDBACK_VAL2,
                                   PlayingCardEnum.JC_CARDBACK_VAL3,
                                   PlayingCardEnum.JC_CARDBACK_VAL4,
                                   PlayingCardEnum.JC_CARDBACK_VAL5,
                                   PlayingCardEnum.JC_CARDBACK_VAL6,
                                   PlayingCardEnum.JC_CARDBACK_VAL7,
                                   PlayingCardEnum.JC_CARDBACK_VAL8};

    // private statics
    /** Initial number of decks - if not set. */
    private final static int START_DECKS = CardDeck.JC_ONE_DECK;

    /** Properties prepender (key lead). */
    private static final String S_LEAD = "TwentyOne.";

    /** Properties file name. */
    private static final String S_PROPSNAME = S_LEAD + "properties";

    /** Default value for card back image (as index into array). */
    private static final int I_CARDBACK = 2;

    // private references
    /** Properties object used. */
    private Properties props = null;

    // ------------------------- Constructor -------------------------------

    /** Constructor - readies for property read.
     * @param parent Parent object creating the properties object.
    */
    public TwentyOneProps(Object parent)
    {
        props = new Properties(); // no defaults

        try { // to load properties now
            InputStream in = parent.getClass().getResourceAsStream(S_PROPSNAME);
            props.load(in);
            in.close();
        }
        catch (Exception e) {
            System.err.println("Properties failed to load: " + e);
        }
    }

    // ------------------------- Public Methods -----------------------------

    /** Method to return the version information string to the caller. */
    public String getVersionInfo()
    {
        if (props != null)
            return props.getProperty(S_LEAD + "Version");
        else
            return "";
    }

    /** Method used to return the author email string to the caller. */
    public String getAuthorEmail()
    {
        if (props != null)
            return props.getProperty(S_LEAD + "eMail");
        else
            return "";
    }

    /** Method to return the card back image to use (as a card image enumeration)
     * to the caller.
    */
    public CardEnum getCardBackImage()
    {
        CardEnum ceRet = cardBacks[I_CARDBACK];

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "CardBackImage");
            if ((ss != null) && (!"".equals(ss))) {
                int ii = 0;
                try {
                    ii = Integer.parseInt(ss);
                }
                catch (Exception e) {
                    ii = I_CARDBACK;
                }
                if ((ii < 0) || (ii >= cardBacks.length)) ii = I_CARDBACK;
                ceRet = cardBacks[ii];
            }
        }

        return ceRet;
    }

    /** Method to return the number of decks to use to the caller.
    */
    public int getNumDecks()
    {
        int iRet = START_DECKS;

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "NumDecks");
            if ((ss != null) && (!"".equals(ss))) {
                int ii = 0;
                try {
                    ii = Integer.parseInt(ss);
                }
                catch (Exception e) {
                    ii = START_DECKS;
                }
                if ((ii < CardDeck.JC_ONE_DECK) || (ii > CardDeck.JC_SEVEN_DECK))
                    iRet = START_DECKS;
                else
                    iRet = ii;
            }
        }

        return iRet;
    }

    /** Method to return the minimum bet to the caller.
    */
    public int getMinimumBet()
    {
        int iRet = MIN_BET_AMT;

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "MinimumBet");
            if ((ss != null) && (!"".equals(ss))) {
                int ii = 0;
                try {
                    ii = Integer.parseInt(ss);
                }
                catch (Exception e) {
                    ii = 1;
                }
                if ((ii < MIN_BET_AMT) || (ii > MAX_BET_AMT))
                    iRet = MIN_BET_AMT;
                else
                    iRet = ii;
            }
        }

        return iRet;
    }

    /** Method to return the maximum bet to the caller.
     * Should be called after determining the current minimum
     * bet amount.
    */
    public int getMaximumBet(int minBetAmt)
    {
        int iRet = START_MAX;

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "MaximumBet");
            if ((ss != null) && (!"".equals(ss))) {
                int ii = 0;
                try {
                    ii = Integer.parseInt(ss);
                }
                catch (Exception e) {
                    ii = START_MAX;
                }
                if ((ii < minBetAmt) || (ii > MAX_BET_AMT))
                    iRet = minBetAmt;
                else
                    iRet = ii;
            }
        }

        return iRet;
    }

    /** Method to return the players starting bank to the caller.
     * Should be called after determining the current minimum
     * bet amount.
    */
    public int getInitialBank(int minBetAmt)
    {
        int iRet = START_IBANK;

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "InitialBank");
            if ((ss != null) && (!"".equals(ss))) {
                int ii = 0;
                try {
                    ii = Integer.parseInt(ss);
                }
                catch (Exception e) {
                    ii = START_IBANK;
                }
                if ((ii < minBetAmt) || (ii > MAX_IBANK_AMT))
                    iRet = START_IBANK;
                else
                    iRet = ii;
            }
        }

        return iRet;
    }

    /** Method to return if 'try to be maximum bet' is turned on or
     * off at game start.
    */
    public boolean getBetMax()
    {
        boolean bRet = false;

        if (props != null) {
            String ss = props.getProperty(S_LEAD + "BetMax");
            // assume on if set in property file...
            if ((ss != null) && (!"".equals(ss))) bRet = true;
        }

        return bRet;
    }
}
import javax.swing.JTextField;
import java.awt.Toolkit;
import au.com.virturo.guiutils.*;

/**
 * 
 * @author Bernard Johnston
 * @version 1.0
 *
 * Modified 2001-09-22 M.G.S. - Changed process of catch in 'reset' method.
 *                              Added import for spinner classes.
 *
*/
public class IntSpinListener implements SpinListener
{
  private int minVal = 0;
  private int maxVal = 999;

  public IntSpinListener() { }

  public IntSpinListener(int minInt, int maxInt)
  {
    if( minInt <= maxInt ) {
      minVal = minInt;
      maxVal = maxInt;
    }
  }

  public void spinnerSpunUp(SpinEvent e)
  {
    reset(true, e);
  }

  public void spinnerSpunDown(SpinEvent e)
  {
    reset(false, e);
  }

  private void reset(boolean up, SpinEvent e)
  {
    JTextField tempField = (JTextField)e.getComponent();
    String currText = tempField.getText();
    int choice = minVal;
    boolean valid = true;
    try {
        choice = Integer.parseInt(currText);
    } catch(NumberFormatException n) {
      System.err.println("IntSpinListener.reset(): " + n);
      // n.printStackTrace();
      valid = false;
      // tempField.getToolkit().beep();
    }
    if( valid ) {
        if( choice > maxVal || (choice == maxVal && up) )
          tempField.setText(Integer.toString(maxVal));
        else if( choice < minVal || (choice == minVal && !up) )
          tempField.setText(Integer.toString(minVal));
        else {
          if( up )
            tempField.setText(Integer.toString(++choice));
          else
            tempField.setText(Integer.toString(--choice));
        }
    }
    else
      tempField.setText(Integer.toString(minVal));
  }
}
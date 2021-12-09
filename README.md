# TwentyOne-java
Java version of TwentyOne - uses Java cards library

Recompiled to use to test out the re-compiled version of the Java Cards library.  Added
Ant script and moved source to src folder.  Removed JApplet references (was originally
written as both applet and form application).  Updated some bounding sizes (images were
being cut in the recompiled version) and all assignments to the JApplet container.

Program was originally created back in 2002 and uses a custom SwingWorker implmentation
that was created before Java had one available.  Can provide some the code if needed, is in a
utility jar of a mish-mash of code gathered and bundled in a utility jar back when I was doing
Java almost exclusively.

With the Ant build script, was compiled and built using Java 17 along with latest Ant version.
Should work fine with Java back to Java 1.3.  Original code added as text file to repository
in case want to recompile with Applets again.

From original readme:

TwentyOne is a java version of the card game 21.

Future Enhancements:

--------------------

- Add configuration dialog to allow changing of settings (probably no persistent
  save) during game play.

History:

--------

Version 1.00  2002-06-14 > Completed (finally!!!!)

Version 1.01  2002-06-16 > Tweaked some to run better as an applet.

Version 1.02  2002-06-17 > Added call to 'setLocationRelativeTo' to bet dialog if
                           ran as an applet
                           
Version 1.03  2002-06-18 > Fixed a bug in the 'getScoreOfHand' method.

Version 1.04  2002-06-19 > Fixed a bug in the 'finishOffDealer' method.

Version 1.05  2002-06-22 > Modified to use 'centerWindowOnScreen' method.

Version 1.06  2002-06-23 > Added 'double-buffering' to PlayArea component.

Version 1.07  2002-06-25 > Modified to use 'AboutDlg' from slack.jar.

Version 1.08  2003-08-16 > Modified to set a font in a label so the text is
                           not truncated under GCD Java 1.4.1.
                           
Version 2.00  2005-01-11 > Updated for cards v2.0, added code to 'pre-load'
                           card images ('waitFor') after loading into cache.
                           
Version 2.01  2005-11-08 > Cleaned up some code per hints from 'findbugs'.

Version 2.02  2005-12-19 > Changed main method to use invokeLater to setup
                           swing components and make them visible (start).
                           
Version 2.03  2006-03-15 > Modified menu 'exit' code (removed System.exit() call).
                           Did not compile/test until April.
                           
Version 2.04  2007-10-14 > Updated comments/properties.  Removed un-needed cast.

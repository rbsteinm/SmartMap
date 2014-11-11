package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static Friends
 * 
 * @author jfperren
 */
public class MockDB {

    public static final Friend JULIEN = new Friend(0, "Julien Perrenoud",
        46.2547877, 3.2142577);
    public static final Friend ALAIN = new Friend(1, "Alain Milliet",
        45.2547877, 7.2142577);
    public static final Friend ROBIN = new Friend(2, "Robin Genolet",
        42.2547877, 5.214577);
    public static final Friend MATTHIEU = new Friend(3, "Matthieu Girod",
        39.2547877, 5.214577);
    public static final Friend NICOLAS = new Friend(4, "Nicolas Ritter",
        50.2547877, 2.004577);
    public static final Friend MARION = new Friend(5, "Marion Sbai",
        41.5547877, 2.4255457);
    public static final Friend RAPHAEL = new Friend(6, "Raphael Steinmann",
        39.5547877, 7.4255457);
    public static final Friend GUILLAUME = new Friend(7, "Guillaume Clément",
        43.5547877, 6.4255457);
    public static final Friend SELINE = new Friend(8, "Seline Eeckhout",
        51.2547877, 2.2142577);
    public static final Friend CYRIL = new Friend(9, "Cyril Pulver",
        70.2547877, 14.2142577);
    public static final Friend PIETRO = new Friend(10, "Pietro Ortelli",
        42.2547877, 5.214577);
    public static final Friend CHRISTIE = new Friend(11, "Christie Palmer",
        18.4641834, -69.9585861);
    public static final Friend MARIE = new Friend(12, "Marie Wermeille",
        45.2547877, 45.004577);
    public static final Friend HUGO = new Friend(13, "Hugo Sbai", 47.5547877,
        4.4255457);
    public static final ArrayList<Friend> FRIENDS_LIST = new ArrayList<Friend>(
        Arrays.asList(JULIEN, ALAIN, ROBIN, MATTHIEU, NICOLAS, MARION, RAPHAEL,
            HUGO, GUILLAUME, SELINE, CYRIL, PIETRO, CHRISTIE, MARIE));
}

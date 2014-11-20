package ch.epfl.smartmap.test.activities;

/**
 * Tests the correctness of SettingsActivity.<br>
 * <br>
 * A boolean preference that has a dependency to another boolean preference will
 * have its value set as follows:
 * booleanValue = dependencyIsEnabled ? valueSetByUser : false<br />
 * <br>
 * For example if the checkbox vibrate is checked but the notifications checkbox
 * is unchecked, the value of the vibrate checkbox will be false.
 * 
 * @author SpicyCH
 */
public class SettingsActivityTest {

}

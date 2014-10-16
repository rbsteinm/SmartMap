/**
 * 
 */
package ch.epfl.smartmap.gui;


/**
 * @author jfperren
 *
 */
public interface BottomSliderInterface {
    void show();
    void hide();
    void extend();
    void minimize();
    void getState();
    void display(Object o);
}

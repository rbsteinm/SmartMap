/**
 * 
 */
package ch.epfl.smartmap.gui;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * @author jfperren
 *
 */
public class BottomSlider extends LinearLayout {

    public final static int SPEED = 2;
    public enum State {HIDDEN, DISCRETE, EXTENDED};
    
    private State mState;
    /**
     * @param context
     */
    public BottomSlider(Context context) {
        super(context);
        
        mState = State.HIDDEN;
        this.setVisibility(View.GONE);
    }

    public void setState(State newState){
       assert newState != mState : "setState : Should set to a different state";
       
       Animation animation = null;
       
       switch(newState){
           case HIDDEN:
               this.setVisibility(View.VISIBLE);
               // animation = AnimationUtils.loadAnimation(this.getContext(),
               // R.string.
               // this.startAnimation(animation);
               
               break;
           case DISCRETE:
               break;
           case EXTENDED:
               break;
           default:
               assert false;
               
               
               
           assert (this.getVisibility() == View.GONE && mState == State.HIDDEN)
               || (this.getVisibility() == View.VISIBLE && (mState == State.DISCRETE || mState == State.EXTENDED))
               : "setState : Inconsistency between Visibility and state attributes.";
               
       }
       
       animation.setDuration(SPEED);
       
    }
}

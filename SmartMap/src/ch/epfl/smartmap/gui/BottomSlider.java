package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 * @author jfperren
 *
 */
public class BottomSlider extends RelativeLayout {
    /**
     * Represents the visual state of the Slider
     * 
     * @author jfperren
     */

    public final static int SPEED = 2;
    // Same values as in xml file
    public final static int HIDDEN = 0;
    public final static int DISCRETE = 1;
    public final static int EXTENDED = 2;
    private int mState;
    /**
     * @param context
     */
    public BottomSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
       
        // Try to get the default value of the attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.BottomSlider,
            0, 0);
        
        try {
            mState = a.getInteger(R.styleable.BottomSlider_state, 0);
        } finally {
            a.recycle();
        }
        
        // Set visibility attribute according to state
        if (mState == HIDDEN) {
            this.setVisibility(View.GONE);
        } else {
            this.setVisibility(View.VISIBLE);
        }
    }
    
    public int getState() {
        return mState;
    }
    
    public void setState(int newState) {
        assert newState != mState : "setState : Should set to a different state";
       
        switch(newState){
            case HIDDEN:
                this.setVisibility(View.GONE);
                this.getLayoutParams().height = 0;
                break;
            case DISCRETE:
                this.setVisibility(View.VISIBLE);
                this.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                break;
            case EXTENDED: 
                this.setVisibility(View.VISIBLE);
                this.getLayoutParams().height = LayoutParams.MATCH_PARENT;
                break;
            default:
                assert false;
        }
       
        mState = newState;
        
        assert (this.getVisibility() == View.GONE && mState == HIDDEN)
            || (this.getVisibility() == View.VISIBLE && (mState == DISCRETE || mState == EXTENDED))
            : "setState : Inconsistency between Visibility and state attributes.";
           
        
        
        invalidate();
        requestLayout();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (this.getState() != BottomSlider.DISCRETE) {
                Log.d("Touchevent", "setState discrete");
                this.setState(BottomSlider.DISCRETE);
            } else {
                this.setState(BottomSlider.EXTENDED);
            }
        }
        
        return true;
    }
}

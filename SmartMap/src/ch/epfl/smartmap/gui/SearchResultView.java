/**
 * 
 */
package ch.epfl.smartmap.gui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author jfperren
 *
 */
public abstract class SearchResultView extends LinearLayout {

    /**
     * @param context
     * @param attrs
     */
    
    private Button mMoreInfoButton;
    private RelativeLayout mMainLayout;
    
    public SearchResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.setOrientation(HORIZONTAL);
        this.setWeightSum(10f);
        
        mMoreInfoButton = new Button(context);
        mMoreInfoButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 9f));
        mMoreInfoButton.setText("Infos");
        
        mMainLayout = new RelativeLayout(context);
        mMainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
    }
    
    public abstract void update();
}

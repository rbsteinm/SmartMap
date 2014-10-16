package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author jfperren
 *
 */
public class MainActivity extends Activity {

    private float startY;
    private BottomSlider mBottomSlider;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBottomSlider = (BottomSlider) findViewById(R.id.bottom_slider);
        mBottomSlider.setVisibility(View.VISIBLE);
        
        mBottomSlider.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if (mBottomSlider.getState() != BottomSlider.DISCRETE) {
                    mBottomSlider.setState(BottomSlider.DISCRETE);
                } else {
                    mBottomSlider.setState(BottomSlider.HIDDEN);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN : 
//                startY = event.getY();
//                break;           
//            
//            case MotionEvent.ACTION_UP: 
//                float endY = event.getY();
//                 
//                if (endY < startY) {
//                    Log.d("BottomSlider", "moving up");
//                    mBottomSlider.setState(State.DISCRETE);
//                } else {
//                    Log.d("BottomSlider", "moving down");
//                    mBottomSlider.setState(State.HIDDEN);
//                }
//                break;
//            default:
//                assert false;
//        }
//        
//        return true;
//    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if (mBottomSlider.getState() == BottomSlider.HIDDEN) {
                Log.d("Touchevent", "setState discrete");
                TextView tv = (TextView) findViewById(R.id.textview);
                tv.setText("Test");
                mBottomSlider.setState(BottomSlider.DISCRETE);
            } else {
                mBottomSlider.setState(BottomSlider.HIDDEN);
            }
        }
        
        return true;
    }
}

package ch.epfl.smartmap.gui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.MockDB;

/**
 * @author jfperren
 *
 */
public class SlidingPanel extends FrameLayout {
    private static final String TAG = "INFORMATION_PANEL";

    private static final int EXTEND_DURATION = 400;
    
    private static final int FADE_IN_DELAY = 400;
    private static final int FADE_IN_DURATION = 400;
    private static final int FADE_OUT_DELAY = 0;
    private static final int FADE_OUT_DURATION = 400;

    private enum VisualState {
        CLOSED, OPEN, ANIM_PERFORMED;
        int height;
    }

    private enum FadeState {
        IN(0, 1, FADE_IN_DELAY, FADE_IN_DURATION),
        OUT(1, 0, FADE_OUT_DELAY,
            FADE_OUT_DURATION);

        private float mFromAlpha;
        private float mToAlpha;
        private int mDelay;
        private int mDuration;

        private FadeState(float fromAlpha, float toAlpha, int delay,
            int duration) {
            this.mFromAlpha = fromAlpha;
            this.mToAlpha = toAlpha;
            this.mDelay = delay;
            this.mDuration = duration;
        }
    }

    private InformationViewExtended mExtendedView;

    private VisualState mVisualState;

    private ValueAnimator mCloseAnim;
    private ValueAnimator mOpenAnim;

    public SlidingPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create subviews
        mExtendedView = new InformationViewExtended(context, this);
        this.addView(mExtendedView);
       
        // Layout parameters
        this.setBackgroundResource(R.color.background_blue);
        this.setFocusable(true);
        this.setClickable(true);
        // Initial State should be CLOSED
        mVisualState = VisualState.CLOSED;
        this.setVisibility(GONE);
        VisualState.OPEN.height = -1;
    }
    
    public void initView(){
        // Initialize heights
        FrameLayout parent = (FrameLayout) getParent();
        this.measure(parent.getWidth(), parent.getHeight());
        VisualState.CLOSED.height = 0;
        VisualState.OPEN.height = 1800;
        Log.d(TAG, "HEIGHT : " + this.getMeasuredHeight());
        // Initialize Position Animators
        initializeAnimators();
    }

    /**
     * Show full screen view
     */
    public void open() {
        if (mVisualState == VisualState.CLOSED) {
            if(VisualState.OPEN.height == -1){
                initView();
            }
            // Need to set Views to VISIBLE to avoid anim problems
            this.setVisibility(View.VISIBLE);
            // Start Animations
            mExtendedView.clearAnimation();
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView, FadeState.IN));
            mOpenAnim.start();
        }
    }

    /**
     * Hide panel
     */
    public void close() {
        if (mVisualState == VisualState.OPEN) {
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView,
                FadeState.OUT));
            mCloseAnim.start();
        } 
    }

    public boolean isOpened() {
        return mVisualState == VisualState.OPEN;
    }

    public boolean isClosed() {
        return mVisualState == VisualState.CLOSED;
    }
    
    /**
     * Handle the event onBackPressed
     *
     * @return true if the event is handled and should not go further
     */
    public boolean onBackPressed() {
        switch(mVisualState) {
            case ANIM_PERFORMED:
                Log.d(TAG, "onBackPressed, true anim");
                return true;
            case OPEN:
                Log.d(TAG, "onBackPressed, true ext");
                close();
                return true;
            case CLOSED : 
                Log.d(TAG, "onBackPressed, false");
                return false;
            default : assert false;
        }
        
        return false;
    }

    private void initializeAnimators() {
        // Height animators
        mOpenAnim = createTranslateAnimator(VisualState.CLOSED,
            VisualState.OPEN);
        mCloseAnim = createTranslateAnimator(VisualState.OPEN,
            VisualState.CLOSED);
    }
    
    private ValueAnimator createTranslateAnimator(final VisualState start,
        final VisualState end) {
        ValueAnimator animator = ValueAnimator
            .ofInt(start.height, end.height);
        if (start == VisualState.OPEN || end == VisualState.OPEN) {
            animator.setDuration(EXTEND_DURATION);
        }
        
        final SlidingPanel thisPanel = this;
        Log.d(TAG, "new Animation  : " + start.height + " to " + end.height);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                thisPanel.getLayoutParams().height = value.intValue();
                Log.d(TAG, "int value : " + value.intValue());
                thisPanel.requestLayout();
            }
        });

        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Prevents having multiple Animations at the same time
                thisPanel.mVisualState = VisualState.ANIM_PERFORMED;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                thisPanel.mVisualState = end;
                thisPanel.getLayoutParams().height = end.height;
                thisPanel.requestLayout();
                if (end == VisualState.CLOSED) {
                    thisPanel.clearFocus();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Nothing
            }
        });

        return animator;
    }

    private AlphaAnimation createAlphaAnimation(final View view,
        final FadeState fadeState) {
        final AlphaAnimation anim = new AlphaAnimation(fadeState.mFromAlpha,
            fadeState.mToAlpha);
        anim.setDuration(fadeState.mDuration);
        anim.setStartOffset(fadeState.mDelay);

        final SlidingPanel thisPanel = this;

        anim.setAnimationListener(new AlphaAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG,
                    "Fade Animation Started with duration" + anim.getDuration());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // NOTE : All views disappear with a fade out,
                // so we can resolve Visibility issues here
                if (fadeState == FadeState.OUT) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nothing
            }
        });

        return anim;
    }
}

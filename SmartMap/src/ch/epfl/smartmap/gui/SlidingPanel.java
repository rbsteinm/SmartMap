package ch.epfl.smartmap.gui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import ch.epfl.smartmap.R;

/**
 * Provides a Sliding Panel that slides from the bottom of the app, and
 * fades in component.
 * 
 * @author jfperren
 */
public class SlidingPanel extends FrameLayout {

    private static final String TAG = "INFORMATION_PANEL";

    private static final int EXTEND_DURATION = 400;
    private static final int FADE_IN_DELAY = 400;
    private static final int FADE_IN_DURATION = 400;
    private static final int FADE_OUT_DELAY = 0;
    private static final int FADE_OUT_DURATION = 400;

    private static final int OPEN_HEIGHT = 1800;

    /**
     * Visual State of a SlidingPanel
     * 
     * @author jfperren
     */
    private enum VisualState {
        CLOSED,
        OPEN,
        ANIM_PERFORMED;
        private int height;
    }

    /**
     * Type of FadeAnimation
     * 
     * @author jfperren
     */
    private enum Fade {
        IN(0, 1, FADE_IN_DELAY, FADE_IN_DURATION),
        OUT(1, 0, FADE_OUT_DELAY, FADE_OUT_DURATION);

        private float mFromAlpha;
        private float mToAlpha;
        private int mDelay;
        private int mDuration;

        private Fade(float fromAlpha, float toAlpha, int delay, int duration) {
            this.mFromAlpha = fromAlpha;
            this.mToAlpha = toAlpha;
            this.mDelay = delay;
            this.mDuration = duration;
        }
    }

    private final InformationViewExtended mExtendedView;

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

    /**
     * Initialize Position Animations
     */
    public void initView() {
        // Initialize heights
        FrameLayout parent = (FrameLayout) this.getParent();
        this.measure(parent.getWidth(), parent.getHeight());
        VisualState.CLOSED.height = 0;
        // FIXME : Shouldn't be hardcoded
        VisualState.OPEN.height = OPEN_HEIGHT;
        Log.d(TAG, "HEIGHT : " + this.getMeasuredHeight());
        // Initialize Position Animators
        this.initializeAnimators();
    }

    /**
     * Show full screen view
     */
    public void open() {
        if (mVisualState == VisualState.CLOSED) {
            if (VisualState.OPEN.height == -1) {
                this.initView();
            }
            // Need to set Views to VISIBLE to avoid anim problems
            this.setVisibility(View.VISIBLE);
            // Start Animations
            mExtendedView.clearAnimation();
            mExtendedView.startAnimation(this.createAlphaAnimation(mExtendedView, Fade.IN));
            mOpenAnim.start();
        }
    }

    /**
     * Hide panel
     */
    public void close() {
        if (mVisualState == VisualState.OPEN) {
            mExtendedView.startAnimation(this.createAlphaAnimation(mExtendedView, Fade.OUT));
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
        switch (mVisualState) {
            case ANIM_PERFORMED:
                Log.d(TAG, "onBackPressed, true anim");
                return true;
            case OPEN:
                Log.d(TAG, "onBackPressed, true ext");
                this.close();
                return true;
            case CLOSED:
                Log.d(TAG, "onBackPressed, false");
                return false;
            default:
                assert false;
        }

        return false;
    }

    private void initializeAnimators() {
        // Height animators
        mOpenAnim = this.createTranslateAnimator(VisualState.CLOSED, VisualState.OPEN);
        mCloseAnim = this.createTranslateAnimator(VisualState.OPEN, VisualState.CLOSED);
    }

    private ValueAnimator createTranslateAnimator(final VisualState start, final VisualState end) {
        ValueAnimator animator = ValueAnimator.ofInt(start.height, end.height);
        if ((start == VisualState.OPEN) || (end == VisualState.OPEN)) {
            animator.setDuration(EXTEND_DURATION);
        }

        final SlidingPanel thisPanel = this;
        Log.d(TAG, "new Animation  : " + start.height + " to " + end.height);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
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

    /**
     * Creates a Fade Animation on a View
     * 
     * @param view
     *            View to be fade in/out
     * @param fadeState
     *            Fade
     * @return
     */
    private AlphaAnimation createAlphaAnimation(final View view, final Fade type) {
        final AlphaAnimation anim = new AlphaAnimation(type.mFromAlpha, type.mToAlpha);
        anim.setDuration(type.mDuration);
        anim.setStartOffset(type.mDelay);

        anim.setAnimationListener(new AlphaAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Fade Animation Started with duration" + anim.getDuration());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // NOTE : All views disappear with a fade out,
                // so we can resolve Visibility issues here
                if (type == Fade.OUT) {
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
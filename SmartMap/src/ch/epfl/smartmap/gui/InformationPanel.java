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
public class InformationPanel extends FrameLayout {
    private static final String TAG = "INFORMATION_PANEL";

    private static final int ANYTHING_TO_EXTENDED_DURATION = 400;
    private static final int CLOSED_TO_COLLAPSED_DURATION = 150;
    
    private static final int FADE_IN_DELAY = 400;
    private static final int FADE_IN_DURATION = 400;
    private static final int FADE_OUT_DELAY = 0;
    private static final int FADE_OUT_DURATION = 400;

    private enum VisualState {
        CLOSED, COLLAPSED, EXTENDED, ANIM_PERFORMED;

        private int mHeight;

        private void setHeight(int height) {
            mHeight = height;
        }
    }

    private enum FadeState {
        IN(0, 1, FADE_IN_DELAY, FADE_IN_DURATION), OUT(1, 0, FADE_OUT_DELAY,
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

    private InformationViewCollapsed mCollapsedView;
    private InformationViewExtended mExtendedView;

    private VisualState mVisualState;

    private ValueAnimator mCollapsedToExtendedAnim;
    private ValueAnimator mCollapsedToClosedAnim;
    private ValueAnimator mExtendedToClosedAnim;
    private ValueAnimator mExtendedToCollapsedAnim;
    private ValueAnimator mClosedToCollapsedAnim;
    private ValueAnimator mClosedToExtendedAnim;

    public InformationPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Create subviews
        mCollapsedView = new InformationViewCollapsed(context, this);
        mExtendedView = new InformationViewExtended(context, this);
        this.addView(mCollapsedView);
        this.addView(mExtendedView);
        // Initialize Position Animators
        initializePositions();
        initializeAnimators();
        // Layout parameters
        this.setBackgroundResource(R.color.background_blue);
        this.setFocusable(true);
        this.setClickable(true);
        // Initial State should be CLOSED
        mVisualState = VisualState.CLOSED;
        this.setVisibility(GONE);
        mExtendedView.setVisibility(GONE);
        mCollapsedView.setVisibility(GONE);
    }

    public void displayItem(Displayable item) {
        mCollapsedView.setItem(item);
        // mExtendedView.setItem(item);
    }

    /**
     * Show collapsed view
     */
    public void collapse() {
        // Need to set Views to VISIBLE to avoid anim problems
        this.setVisibility(View.VISIBLE);
        mCollapsedView.setVisibility(View.VISIBLE);
        
        if (mVisualState == VisualState.CLOSED) {
            mCollapsedView.startAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.IN));
            mClosedToCollapsedAnim.start();
        } else if (mVisualState == VisualState.EXTENDED) {
            mCollapsedView.startAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.IN));
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView,
                FadeState.OUT));
            mExtendedToCollapsedAnim.start();
        } else {
            assert false : "Trying to collapse, but already in state COLLAPSED (or unknown state)";
        }
    }

    /**
     * Show full screen view
     */
    public void extend() {
        // Need to set Views to VISIBLE to avoid anim problems
        this.setVisibility(View.VISIBLE);
        mExtendedView.setVisibility(View.VISIBLE);

        if (mVisualState == VisualState.CLOSED) {
            mExtendedView.clearAnimation();
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView, FadeState.IN));
        } else if (mVisualState == VisualState.COLLAPSED) {
            mCollapsedView.clearAnimation();
            mCollapsedView.startAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.OUT));
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView,
                FadeState.IN));
            mCollapsedToExtendedAnim.start();
        } else {
            assert (false) : "Trying to extend, but already in state EXTENDED (or unknown state)";
        }
    }

    /**
     * Hide panel
     */
    public void close() {
        
        if (mVisualState == VisualState.EXTENDED) {
            mExtendedView.startAnimation(createAlphaAnimation(mExtendedView,
                FadeState.OUT));
            mExtendedToClosedAnim.start();
        } else if (mVisualState == VisualState.COLLAPSED) {
            mCollapsedView.startAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.OUT));
            mCollapsedToClosedAnim.start();
        } else {
            assert (false) : "Trying to close, but already in state CLOSED (or unknown state)";
        }
    }

    public boolean isExtended() {
        return mVisualState == VisualState.EXTENDED;
    }

    public boolean isCollapsed() {
        return mVisualState == VisualState.COLLAPSED;
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
            case EXTENDED:
                Log.d(TAG, "onBackPressed, true ext");
                collapse();
                return true;
            case COLLAPSED:
                Log.d(TAG, "onBackPressed, true col");
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
        mClosedToCollapsedAnim = createHeightAnimator(VisualState.CLOSED,
            VisualState.COLLAPSED);
        mClosedToExtendedAnim = createHeightAnimator(VisualState.CLOSED,
            VisualState.EXTENDED);
        mCollapsedToClosedAnim = createHeightAnimator(VisualState.COLLAPSED,
            VisualState.CLOSED);
        mCollapsedToExtendedAnim = createHeightAnimator(VisualState.COLLAPSED,
            VisualState.EXTENDED);
        mExtendedToClosedAnim = createHeightAnimator(VisualState.EXTENDED,
            VisualState.CLOSED);
        mExtendedToCollapsedAnim = createHeightAnimator(VisualState.EXTENDED,
            VisualState.COLLAPSED);
    }

    private ValueAnimator createHeightAnimator(final VisualState start,
        final VisualState end) {
        ValueAnimator animator = ValueAnimator
            .ofInt(start.mHeight, end.mHeight);
        if (start == VisualState.EXTENDED || end == VisualState.EXTENDED) {
            animator.setDuration(ANYTHING_TO_EXTENDED_DURATION);
        } else {
            animator.setDuration(CLOSED_TO_COLLAPSED_DURATION);
        }
        

        final InformationPanel thisPanel = this;
        Log.d(TAG, "new Animation  : " + start.mHeight + " to " + end.mHeight);
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
                thisPanel.getLayoutParams().height = end.mHeight;
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

        final InformationPanel thisPanel = this;

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

                if ((mExtendedView.getVisibility() == View.GONE)
                    && (mCollapsedView.getVisibility() == View.GONE)) {
                    thisPanel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nothing
            }
        });

        return anim;
    }

    private void initializePositions() {
        // Initialize heights
        mExtendedView.measure(this.getWidth(), this.getHeight());
        mCollapsedView.measure(this.getWidth(), this.getHeight());
//        VisualState.EXTENDED.setHeight(mExtendedView.getMeasuredHeight());
        VisualState.EXTENDED.setHeight(1800);
        Log.d(TAG, "init EXT : " + mExtendedView.getMeasuredHeight());
        VisualState.COLLAPSED.setHeight(mCollapsedView.getMeasuredHeight());
        Log.d(TAG, "init COL : " + mCollapsedView.getMeasuredHeight());
        VisualState.CLOSED.setHeight(0);
    }
}

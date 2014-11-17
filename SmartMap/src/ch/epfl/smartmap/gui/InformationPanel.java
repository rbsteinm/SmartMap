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

    private static final int TRANSLATION_EXTENDED_DURATION = 400;
    private static final int FADE_IN_DELAY = 400;
    private static final int FADE_IN_DURATION = 400;
    private static final int FADE_OUT_DELAY = 0;
    private static final int FADE_OUT_DURATION = 400;

    private enum VisualState {
        CLOSED, COLLAPSED, EXTENDED, ANIM_PERFORMED;

        private int mHeight;

        private void setPosition(int height) {
            mHeight = height;
        }
    }

    private enum FadeState {
        IN(0, 1, FADE_IN_DELAY, FADE_IN_DURATION), 
        OUT(1, 0, FADE_OUT_DELAY, FADE_OUT_DURATION);

        private float mStart;
        private float mEnd;
        private int mDelay;
        private int mDuration;

        private FadeState(float start, float end, int delay, int duration) {
            this.mStart = start;
            this.mEnd = end;
            this.mDelay = delay;
            this.mDuration = duration;
        }
    }

    private View mCollapsedView;
    private View mExtendedView;

    private Displayable mCurrentItem;
    private VisualState mVisualState;

    private ValueAnimator mCollapsedToExtendedAnim;
    private ValueAnimator mCollapsedToClosedAnim;
    private ValueAnimator mExtendedToClosedAnim;
    private ValueAnimator mExtendedToCollapsedAnim;
    private ValueAnimator mClosedToCollapsedAnim;
    private ValueAnimator mClosedToExtendedAnim;

    public InformationPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.displayItem(MockDB.ALAIN);

        this.setBackgroundResource(R.color.information_panel_background);
        this.setClipChildren(true);
        this.setFocusable(true);
        this.setClickable(true);

        initializePositions();
        initializeAnimators();
    }

    public void displayItem(Displayable item) {
        // Create the 2 views
        mCollapsedView = new InformationViewCollapsed(getContext(), item, this);
        mExtendedView = new InformationViewExtended(getContext(), item, this);
        // Add them
        this.removeAllViews();
        this.addView(mExtendedView);
        this.addView(mCollapsedView);
    }

    /**
     * Show collapsed view
     */
    public void collapse() {
        this.setVisibility(VISIBLE);
        if (mVisualState == VisualState.CLOSED) {
            mClosedToCollapsedAnim.start();
        } else if (mVisualState == VisualState.EXTENDED) {
            Log.d(TAG, "C to E");
            mCollapsedView.setAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.IN));
            mExtendedView.setAnimation(createAlphaAnimation(mExtendedView,
                FadeState.OUT));
            mExtendedToCollapsedAnim.start();
        } else {
            assert false : "Trying to collapse, but already in state COLLAPSED (or unknown state)";
        }
        this.mVisualState = VisualState.COLLAPSED;
    }

    /**
     * Show full screen view
     */
    public void extend() {
        this.setVisibility(VISIBLE);
        if (mVisualState == VisualState.CLOSED) {
            mClosedToExtendedAnim.start();
        } else if (mVisualState == VisualState.COLLAPSED) {
            mCollapsedView.setAnimation(createAlphaAnimation(mCollapsedView,
                FadeState.OUT));
            mExtendedView.setAnimation(createAlphaAnimation(mExtendedView,
                FadeState.IN));
            mCollapsedToExtendedAnim.start();
        } else {
            assert (false) : "Trying to extend, but already in state EXTENDED (or unknown state)";
        }
        this.mVisualState = VisualState.EXTENDED;
    }

    /**
     * Hide panel
     */
    public void close() {
        if (mVisualState == VisualState.EXTENDED) {
            mExtendedToClosedAnim.start();
        } else if (mVisualState == VisualState.COLLAPSED) {
            mCollapsedToClosedAnim.start();
        } else {
            assert (false) : "Trying to close, but already in state CLOSED (or unknown state)";
        }
        this.setVisibility(INVISIBLE);
        clearFocus();
        this.mVisualState = VisualState.CLOSED;
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
        animator.setDuration(TRANSLATION_EXTENDED_DURATION);

        final InformationPanel thisPanel = this;
        Log.d(TAG, "new Animation  : " + start.mHeight + " to " + end.mHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {

                Integer value = (Integer) animation.getAnimatedValue();
                thisPanel.getLayoutParams().height = value.intValue();
                thisPanel.requestLayout();
            }
        });

        animator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "Translate Animation Started");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                thisPanel.mVisualState = end;
                thisPanel.getLayoutParams().height = end.mHeight;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

        return animator;
    }

    private AlphaAnimation createAlphaAnimation(final View view,
        final FadeState fadeState) {

        final AlphaAnimation anim = new AlphaAnimation(fadeState.mStart,
            fadeState.mEnd);
        anim.setDuration(fadeState.mDuration);
        anim.setStartOffset(fadeState.mDelay);

        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG,
                    "Fade Animation Started with duration" + anim.getDuration());
                if (fadeState == FadeState.IN) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
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

    private void initializePositions() {
        // Initialize heights
        VisualState.EXTENDED.setPosition(1800);
        VisualState.COLLAPSED.setPosition(200);
        VisualState.CLOSED.setPosition(0);
    }
}

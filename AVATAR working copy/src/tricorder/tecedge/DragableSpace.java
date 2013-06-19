package tricorder.tecedge;

import gupta.ashutosh.avatar.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewConfiguration;
import android.widget.Scroller;

public class DragableSpace extends ViewGroup {
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mScrollX = 0;
	private int mCurrentScreen = 0;
	private float mLastMotionX;
	private static final int SNAP_VELOCITY = 1000;
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop = 0;

	public DragableSpace(Context context) {
		super(context);
		mScroller = new Scroller(context);
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		this.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public DragableSpace(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);

		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

		this.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.DragableSpace);
		mCurrentScreen = a.getInteger(R.styleable.DragableSpace_default_screen,
				0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			boolean xMoved = xDiff > mTouchSlop;
			if (xMoved) {
				// Scroll if the user moved far enough along the X axis
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// Release the drag
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			final int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;

			if (deltaX < 0) {
				if (mScrollX > 0) {
					scrollBy(Math.max(-mScrollX, deltaX), 0);
				}
			} else if (deltaX > 0) {
				final int availableToScroll = getChildAt(getChildCount() - 1)
						.getRight() - mScrollX - getWidth();
				if (availableToScroll > 0) {
					scrollBy(Math.min(availableToScroll, deltaX), 0);
				}
			}
			// }
			break;
		case MotionEvent.ACTION_UP:
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();

			if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
				// Fling hard enough to move left
				snapToScreen(mCurrentScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurrentScreen < getChildCount() - 1) {
				// Fling hard enough to move right
				snapToScreen(mCurrentScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// }
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
		}
		mScrollX = this.getScrollX();

		return true;
	}

	private void snapToDestination() {
		final int screenWidth = getWidth();
		final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth;
		snapToScreen(whichScreen);
	}

	public void snapToScreen(int whichScreen) {
		mCurrentScreen = whichScreen;
		final int newX = whichScreen * getWidth();
		final int delta = newX - mScrollX;
		mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	public void setToScreen(int whichScreen) {
		mCurrentScreen = whichScreen;
		final int newX = whichScreen * getWidth();
		mScroller.startScroll(newX, 0, 0, 0, 10);
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurrentScreen * width, 0);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mScrollX = mScroller.getCurrX();
			scrollTo(mScrollX, 0);
			postInvalidate();
		}
	}
}
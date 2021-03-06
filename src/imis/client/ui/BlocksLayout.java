
package imis.client.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.ui.adapters.EventsArrayAdapter;

/**
 * Layout showing presence and absence block.
 */
public class BlocksLayout extends AdapterView<EventsArrayAdapter> {
    private static final String TAG = BlocksLayout.class.getSimpleName();
    private static final int INVALID_INDEX = -1;
    private Rect mRect;

    private EventsArrayAdapter mAdapter;
    private TimeRulerView mRulerView = null;
    private View mNowView = null;
    private final int countOfNonBlocksViews = 2;

    private static final int TOUCH_STATE_RESTING = 0;
    private static final int TOUCH_STATE_CLICK = 1;
    private static final int TOUCH_STATE_LONG_CLICK = 3;
    private int touchState = TOUCH_STATE_RESTING;
    private int touchStartX, touchStartY;

    private Runnable longPressRunnable;


    public BlocksLayout(Context context) {
        this(context, null);
    }

    public BlocksLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlocksLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "BlocksLayout()");

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlocksLayout, defStyle,
                0);

        a.recycle();
    }

    @SuppressWarnings("deprecation")
    private void ensureChildren() {
        if (mRulerView == null) {
            mRulerView = new TimeRulerView(getContext());
            mRulerView.setDrawingCacheEnabled(true);
            mRulerView.setId(Integer.MAX_VALUE);
            mRulerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            addViewInLayout(mRulerView, -1, mRulerView.getLayoutParams());
        }
        if (mNowView == null) {
            mNowView = new View(getContext());
            mNowView.setDrawingCacheEnabled(true);
            mNowView.setId(Integer.MAX_VALUE - 1);
            mNowView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            NinePatchDrawable buttonDrawable = (NinePatchDrawable) getContext().getResources().getDrawable(
                    R.drawable.now_bar);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mNowView.setBackgroundDrawable(buttonDrawable);
            } else {
                mNowView.setBackground(buttonDrawable);
            }
            addViewInLayout(mNowView, -1, mNowView.getLayoutParams());
        }

        if (TimeUtil.belongsNowToDate(mAdapter.getDate())) {
            mNowView.setVisibility(View.VISIBLE);
        } else {
            mNowView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureChildren();

        mRulerView.measure(widthMeasureSpec, heightMeasureSpec);
        if (mNowView != null) {
            mNowView.measure(widthMeasureSpec, heightMeasureSpec);
        }

        final int width = mRulerView.getMeasuredWidth();
        final int height = mRulerView.getMeasuredHeight();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureChildren();

        if (mAdapter != null) {
//            printAllChilds();
            removeViewsInLayout(0, getChildCount() - countOfNonBlocksViews);
//            printAllChilds();
            final int count = mAdapter.getCount();
            for (int i = 0; i < count; i++) {
                final BlockView blockView = (BlockView) mAdapter.getView(i, null, this);
                if (blockView == null) {
                    continue;
                }

                blockView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                blockView.setId(blockView.getArriveId());
                addViewInLayout(blockView, -1, blockView.getLayoutParams(), true);
                bringChildToFront(mRulerView);
                bringChildToFront(mNowView);
            }
        }
        positionItems();
//        printAllChilds();
    }

    private void positionItems() {
        //position rulerview
        final int headerWidth = mRulerView.getHeaderWidth();
        final int columnWidth = getWidth() - headerWidth;
        mRulerView.layout(0, 0, getWidth(), getHeight());

        int top, bottom, left, right;
        //postion blocks
        View block;
        BlockView blockView;
        for (int index = 0; index < getChildCount(); index++) {
            block = getChildAt(index);
            if (block instanceof BlockView) {
                blockView = (BlockView) block;
                top = mRulerView.getTimeVerticalOffset(blockView.getStartTime());
                bottom = mRulerView.getTimeVerticalOffset(blockView.getEndTime());
                left = headerWidth;
                right = left + columnWidth;
                blockView.layout(left, top, right, bottom);
                /*Log.d(TAG, "left: " + left + " top: " + top + " right: " + right + " bottom: " + bottom
                        + " ruler height: " + mRulerView.getHeight());*/
            }
        }

        //position now view
        final long now = TimeUtil.currentDayTimeInLong();
        top = mRulerView.getTimeVerticalOffset(now);
        bottom = top + mNowView.getMeasuredHeight();
        left = 0;
        right = getWidth();
        mNowView.layout(left, top, right, bottom);
    }

    @Override
    public EventsArrayAdapter getAdapter() {
        Log.d(TAG, "getAdapter()");
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        Log.d(TAG, "getSelectedView()");
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void setAdapter(EventsArrayAdapter adapter) {
        Log.d(TAG, "setAdapter()");
        mAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int arg0) {
        Log.d(TAG, "setSelection()");
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getChildCount() == 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(event);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (touchState == TOUCH_STATE_CLICK) {
                    clickChildAt((int) event.getX(), (int) event.getY());
                }
                endTouch();
                break;
            default:
                endTouch();
                break;
        }
        return true;
    }

    private void startTouch(final MotionEvent event) {
        touchStartX = (int) event.getX();
        touchStartY = (int) event.getY();
        // start checking for a long press
        startLongPressCheck();
        touchState = TOUCH_STATE_CLICK;
    }

    private void endTouch() {
        removeCallbacks(longPressRunnable);
        touchState = TOUCH_STATE_RESTING;
    }

    private void startLongPressCheck() {

        if (!isEnabled()) return;
        if (longPressRunnable == null) {

            longPressRunnable = new Runnable() {

                public void run() {

                    if (touchState == TOUCH_STATE_CLICK) {

                        final int index = getContainingChildIndex(touchStartX, touchStartY);

                        if (index != INVALID_INDEX) longClickChild(index);
                    }
                }
            };
        }

        postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout());
    }

    private void longClickChild(final int index) {
        touchState = TOUCH_STATE_LONG_CLICK;
        final View itemView = getChildAt(index);
        final long id = mAdapter.getItemId(index);
        final OnItemLongClickListener listener = getOnItemLongClickListener();
        if (listener != null) {
            listener.onItemLongClick(null, itemView, index, id);
        }
    }

    private void clickChildAt(final int x, final int y) {

        final int index = getContainingChildIndex(x, y);
        if (index != INVALID_INDEX) {
            final View itemView = getChildAt(index);
            final int position = index;
            final long id = Long.valueOf(((BlockView) itemView).getArriveId());// mAdapter.getItemId(position);
            performItemClick(itemView, position, id);
        }
    }

    private int getContainingChildIndex(final int x, final int y) {
        if (mRect == null) {
            mRect = new Rect();
        }
        for (int index = 0; index < getChildCount(); index++) {

            View child = getChildAt(index);
            if (child instanceof BlockView) {
                child.getHitRect(mRect);
                if (mRect.contains(x, y)) {
                    return index;
                }
            }

        }
        return INVALID_INDEX;
    }

    /*private void printAllChilds() {
        for (int index = 0; index < getChildCount(); index++) {
            View child = getChildAt(index);
            //Log.d("BlocksLayout", "child i=" + index + " " + child.toString() + " id: " + child.getId());
        }
    }*/

}

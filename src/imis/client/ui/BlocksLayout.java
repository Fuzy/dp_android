/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package imis.client.ui;

import imis.client.R;
import imis.client.ui.adapter.EventsAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Custom layout that contains and organizes a {@link TimeRulerView} and several
 * instances of {@link BlockView}. Also positions current "now" divider using
 * {@link R.id#blocks_now} view when applicable.
 */
public class BlocksLayout extends AdapterView<EventsAdapter> {
  private static final String TAG = BlocksLayout.class.getSimpleName();

  private EventsAdapter mAdapter;
  private TimeRulerView mRulerView;
  private View mNowView;

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

  private void ensureChildren() {
    // mRulerView = (TimeRulerView) findViewById(R.id.blocks_ruler);
    mRulerView = new TimeRulerView(getContext());
    if (mRulerView == null) {
      throw new IllegalStateException("Must include a R.id.blocks_ruler view.");
    }
    mRulerView.setDrawingCacheEnabled(true);

    // mNowView = findViewById(R.id.blocks_now);
    mNowView = new View(getContext());
    if (mNowView == null) {
      throw new IllegalStateException("Must include a R.id.blocks_now view.");
    }
    mNowView.setDrawingCacheEnabled(true);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    ensureChildren();

    mRulerView.measure(widthMeasureSpec, heightMeasureSpec);
    mNowView.measure(widthMeasureSpec, heightMeasureSpec);

    final int width = mRulerView.getMeasuredWidth();
    final int height = mRulerView.getMeasuredHeight();

    setMeasuredDimension(resolveSize(width, widthMeasureSpec),
        resolveSize(height, heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    Log.d(TAG, "onLayout()");
    ensureChildren();

    LayoutParams params = mRulerView.getLayoutParams();
    if (params == null) {
      params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
    addViewInLayout(mRulerView, -1, params);

    final TimeRulerView rulerView = mRulerView;
    final int headerWidth = rulerView.getHeaderWidth();
    final int columnWidth = getWidth() - headerWidth;

    rulerView.layout(0, 0, getWidth(), getHeight());

    if (mAdapter != null) {

      final int count = mAdapter.getCount();
      Log.d(TAG, "count: " + count);
      for (int i = 0; i < count; i++) {
        final BlockView blockView = (BlockView) mAdapter.getView(i, null, this);
        // if (child.getVisibility() == GONE)
        // continue;

        // final BlockView blockView = (BlockView) child;
        final int top = rulerView.getTimeVerticalOffset(blockView.getStartTime());
        final int bottom = rulerView.getTimeVerticalOffset(blockView.getEndTime());
        final int left = headerWidth;
        final int right = left + columnWidth;

        LayoutParams params3 = blockView.getLayoutParams();
        if (params3 == null) {
          params3 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(blockView, -1, params3, true);
        blockView.layout(left, top, right, bottom);
        // TODO jak malou udalost dokaze jeste vykreslit
      }

    }

    // Align now view to match current time
    final View nowView = mNowView;
    final long now = System.currentTimeMillis();

    final int top = rulerView.getTimeVerticalOffset(now);
    final int bottom = top + nowView.getMeasuredHeight();
    final int left = 0;
    final int right = getWidth();

    LayoutParams params2 = mNowView.getLayoutParams();
    if (params2 == null) {
      params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
    addViewInLayout(mNowView, -1, params2);
    nowView.layout(left, top, right, bottom);

    Log.d(TAG, "onLayout() end");
  }

  public int getPosOflastBlock() {
    return 0; // TODO vrati pozici kam se ma nastavait scroll
  }

  @Override
  public EventsAdapter getAdapter() {
    Log.d(TAG, "getAdapter()");
    return mAdapter;
  }

  @Override
  public View getSelectedView() {
    Log.d(TAG, "getSelectedView()");
    throw new UnsupportedOperationException("Not supported");
  }

  @Override
  public void setAdapter(EventsAdapter adapter) {
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
}

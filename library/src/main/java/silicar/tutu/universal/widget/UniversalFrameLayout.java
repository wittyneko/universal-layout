package silicar.tutu.universal.widget;

/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import silicar.tutu.universal.helper.*;
import silicar.tutu.universal.value.ReferDisplay;
import silicar.tutu.universal.value.UniversalLayoutInfo;

/**
 * Subclass of {@link FrameLayout} that supports percentage based dimensions and
 * margins.
 *
 * You can specify dimension or a margin of child by using attributes with "Universal" suffix. Follow
 * this example:
 *
 * <pre class="prettyprint">
 * &lt;android.support.percent.UniversalFrameLayout
 *         xmlns:android="http://schemas.android.com/apk/res/android"
 *         xmlns:app="http://schemas.android.com/apk/res-auto"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent"&gt
 *     &lt;ImageView
 *         app:layout_widthUniversal="50%"
 *         app:layout_heightUniversal="50%"
 *         app:layout_marginTopExt="25%"
 *         app:layout_marginLeftUniversal="25%"/&gt
 * &lt;/android.support.percent.UniversalFrameLayout/&gt
 * </pre>
 *
 * The attributes that you can use are:
 * <ul>
 *     <li>{@code layout_widthUniversal}
 *     <li>{@code layout_heightUniversal}
 *     <li>{@code layout_marginUniversal}
 *     <li>{@code layout_marginLeftUniversal}
 *     <li>{@code layout_marginTopExt}
 *     <li>{@code layout_marginRightUniversal}
 *     <li>{@code layout_marginBottomUniversal}
 *     <li>{@code layout_marginStartUniversal}
 *     <li>{@code layout_marginEndUniversal}
 *     <li>{@code layout_aspectRatio}
 * </ul>
 *
 * It is not necessary to specify {@code layout_width/height} if you specify {@code
 * layout_widthUniversal.} However, if you want the view to be able to take up more space than what
 * percentage value permits, you can add {@code layout_width/height="wrap_content"}. In that case
 * if the percentage size is too small for the View's content, it will be resized using
 * {@code wrap_content} rule.
 *
 * <p>
 * You can also make one dimension be a fraction of the other by setting only width or height and
 * using {@code layout_aspectRatio} for the second one to be calculated automatically. For
 * example, if you would like to achieve 16:9 aspect ratio, you can write:
 * <pre class="prettyprint">
 *     android:layout_width="300dp"
 *     app:layout_aspectRatio="178%"
 * </pre>
 * This will make the aspect ratio 16:9 (1.78:1) with the width fixed at 300dp and height adjusted
 * accordingly.
 */
public class UniversalFrameLayout extends FrameLayout implements UniversalView  {
    private final UniversalLayoutHelper mHelper = new UniversalLayoutHelper(this);

    public UniversalFrameLayout(Context context) {
        super(context);
    }

    public UniversalFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public UniversalFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        mHelper.init(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs, getAutoDisplay(), getAutoChildStyle());
    }

    @Override
    public ReferDisplay getAutoDisplay() {
        return mHelper.getAutoDisplay();
    }

    @Override
    public void setAutoDisplay(ReferDisplay display) {
        mHelper.setAutoDisplay(display);
    }

    @Override
    public int getAutoChildStyle() {
        return mHelper.getAutoChildStyle();
    }

    @Override
    public void setAutoChildStyle(@StyleRes int style) {
        mHelper.setAutoChildStyle(style);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mHelper.restoreOriginalParams();
    }

    public static class LayoutParams extends FrameLayout.LayoutParams
            implements UniversalLayoutParams {
        private UniversalLayoutInfo mUniversalLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            this(c, attrs, ReferDisplay.getInstance(), 0);
        }

        public LayoutParams(Context c, AttributeSet attrs, ReferDisplay display, int style) {
            super(c, attrs);
            mUniversalLayoutInfo = UniversalLayoutHelper.getUniversalLayoutInfo(c, attrs, display, style);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super((MarginLayoutParams) source);
            gravity = source.gravity;
        }

        public LayoutParams(LayoutParams source) {
            this((FrameLayout.LayoutParams) source);
            mUniversalLayoutInfo = source.mUniversalLayoutInfo;
        }

        @Override
        public UniversalLayoutInfo getUniversalLayoutInfo() {
            if (mUniversalLayoutInfo == null) {
                mUniversalLayoutInfo = new UniversalLayoutInfo();
            }

            return mUniversalLayoutInfo;
        }

        @Override
        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            UniversalLayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }
    }
}

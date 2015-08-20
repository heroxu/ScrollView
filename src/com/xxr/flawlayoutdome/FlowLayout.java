package com.xxr.flawlayoutdome;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

	private List<Line> mLines = new ArrayList<Line>();// 用来记录布局有多少行
	private Line mCurrentLine;// 记录当前是哪一行

	private int mHorizontalSpace = 10;
	private int mVerticalSpace = 10;

	public FlowLayout(Context context) {
		this(context, null);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSpace(int horizontalSpace, int verticalSpace) {
		this.mHorizontalSpace = horizontalSpace;
		this.mVerticalSpace = verticalSpace;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 清空记录
		mLines.clear();
		mCurrentLine = null;

		// 获得layout的宽度
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int childMaxWidth = widthSize - getPaddingLeft() - getPaddingRight();

		int count = getChildCount();
		// 测量孩子
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);

			if (child.getVisibility() == View.GONE) {
				continue;
			}

			// 测量每一个孩子
			measureChild(child, widthMeasureSpec, heightMeasureSpec);

			// 记录孩子到行中
			if (mCurrentLine == null) {
				// 新建一行
				mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);
				// 将这一行记录到list
				mLines.add(mCurrentLine);
			}

			if (mCurrentLine.canAdd(child)) {
				// 可以添加
				mCurrentLine.addView(child);
			} else {
				// 不可以添加
				// 新建一行
				mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);
				// 将这一行记录到list
				mLines.add(mCurrentLine);
				// 将child加到line
				mCurrentLine.addView(child);
			}

		}

		// setMeasuredDimension:设置自己宽度和高度
		// 行的高度的累加
		int heightSize = getPaddingTop() + getPaddingBottom();
		for (int i = 0; i < mLines.size(); i++) {
			Line line = mLines.get(i);
			heightSize += line.mHeight;

			if (i != 0) {
				// 垂直的间隙
				heightSize += mVerticalSpace;
			}
		}
		setMeasuredDimension(widthSize, heightSize);
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// 给孩子布局
		// 让行给孩子布局
		int top = getPaddingTop();
		for (int i = 0; i < mLines.size(); i++) {
			Line line = mLines.get(i);

			// 让行布局
			line.layout(getPaddingLeft(), top);

			top += line.mHeight;
			if (i != mLines.size() - 1) {
				top += mVerticalSpace;
			}
		}

	}

	// 用来记录描述 layout中的行的信息
	class Line {

		// 属性:
		private List<View> mViews = new ArrayList<View>();// 用来记录View的
		// 1.当前的宽度。2.最大宽度
		private int mCurrentWidth;
		private int mHeight;
		private int mMaxWidth;// 最大宽度
		private int mSpace;// 空隙

		// 构造
		public Line(int maxWidth, int space) {
			this.mMaxWidth = maxWidth;
			this.mSpace = space;
		}

		public void layout(int pLeft, int pTop) {

			// 多余的宽度
			int extraWidth = mMaxWidth - mCurrentWidth;
			// 获得平均值
			int avgWidth = (int) (extraWidth * 1f / mViews.size() + 0.5f);

			// 给View布局
			for (int i = 0; i < mViews.size(); i++) {
				View view = mViews.get(i);

				int width = view.getMeasuredWidth();
				int height = view.getMeasuredHeight();

				if (avgWidth > 0) {
					// 指定孩子具体的大小

					view.measure(MeasureSpec.makeMeasureSpec(width + avgWidth,
							MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
							height, MeasureSpec.EXACTLY));

					// 重新获取高度
					width = view.getMeasuredWidth();
					height = view.getMeasuredHeight();
				}

				int extraHeight = mHeight - height;

				int l = pLeft;
				int t = (int) (pTop + extraHeight / 2f + 0.5);
				int r = l + width;
				int b = t + height;

				view.layout(l, t, r, b);

				pLeft += width + mSpace;
			}
		}

		// 方法
		public void addView(View view) {

			int width = view.getMeasuredWidth();
			int height = view.getMeasuredHeight();

			// 当前的宽度和高度
			mHeight = mHeight < height ? height : mHeight;

			if (mViews.size() == 0) {
				if (width > mMaxWidth) {
					mCurrentWidth = mMaxWidth;
				} else {
					mCurrentWidth = width;
				}
			} else {
				mCurrentWidth += mSpace + width;
			}

			// 添加到view中
			mViews.add(view);
		}

		/**
		 * 用来判断是否可以添加
		 * 
		 * @param view
		 * @return
		 */
		public boolean canAdd(View view) {

			int width = view.getMeasuredWidth();

			if (mViews.size() == 0) {
				// 一个没有
				return true;
			} else {
				// 已经有View

				if (mCurrentWidth + mSpace + width > mMaxWidth) {
					// 加不进去
					return false;
				} else {
					return true;
				}
			}
		}
	}
}

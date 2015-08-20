package com.xxr.flawlayoutdome;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

/**
 * 
 * @项目名称: FlawLayoutDome
 * @包名: com.xxr.flawlayoutdome
 * @作者: 许夏荣
 * @创建时间: 2015-8-20 下午8:14:16
 * @描述: TODO
 * 
 * @SVN版本号: $Rev$
 * @修改人: $Author$
 * @修改时间: $Date$
 * @修改的内容: TODO
 * 
 */
public class MyFlowLayout extends ViewGroup
{
	// 记录有多少行
	private List<Line>	mLines				= new ArrayList<MyFlowLayout.Line>();
	// 记录当前行
	private Line		mCurrentLine;
	// 行与行之间的水平间距
	private int			mHorizontalSpace	= 10;
	// 行与行之间的垂直间距
	private int			mVerticalSpace		= 10;

	public MyFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyFlowLayout(Context context) {
		super(context);
	}
	public void setSpace(int horizontalSpace, int verticalSpace) {
		this.mHorizontalSpace = horizontalSpace;
		this.mVerticalSpace = verticalSpace;
	}
	//每个View都是先测量(onMeasure),再布局(onLayout),再绘制(onDraw)的过程
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		mLines.clear();// 清空行,否则会出现显示不正常
		mCurrentLine = null;

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int childMaxWidth = widthSize - getPaddingLeft() - getPaddingRight();
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE)
			{ // 如果孩子可见
				continue;
			}
			// 测量每一个孩子
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			if (mCurrentLine == null)
			{
				mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);
				mLines.add(mCurrentLine);
			}
			if (mCurrentLine.canAdd(child))
			{
				mCurrentLine.addView(child);
			}
			else
			{
				mCurrentLine = new Line(childMaxWidth, mHorizontalSpace);
				mLines.add(mCurrentLine);
				mCurrentLine.addView(child);
			}
		}
		// 获得每个view的高度,需要将padding和间距都考虑进去
		int heightsize = getPaddingBottom() + getPaddingTop();
		for (int j = 0; j < mLines.size(); j++)
		{
			Line line = mLines.get(j);
			heightsize += line.mHeight;
			if (j != 0)// 如果不是最后一个,则需要加上间距
			{
				heightsize += mVerticalSpace;
			}
		}
		// 给自己设置宽和高
		setMeasuredDimension(widthSize, heightsize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int top = getPaddingTop();
		for (int i = 0; i < mLines.size(); i++)
		{
			Line line = mLines.get(i);
			line.layout(getPaddingLeft(), top);
			top += line.mHeight;
			if (i != mLines.size() - 1)
			{
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
//	class Line
//	{
//		private List<View>	mViews	= new ArrayList<View>();	// 用来记录View
//		private int			mCurrentWidth;						// 当前View的宽度
//		private int			mMaxWidth;							// 父控件的最大宽度
//		private int			mHeight;							// 当前View的高度
//		private int			mSpace;							// View与View之间的间距
//
//		public Line(int maxWidth, int space) {
//			this.mMaxWidth = maxWidth;
//			this.mSpace = space;
//		}
//
//		public void addView(View view)
//		{
//		}
//
//		private boolean canAdd(View view)
//		{
//			if (mViews.size() == 0) { return true; }
//			return false;
//		}
//
//		public void layout(int left, int top)
//		{
//
//		}
//	}
}

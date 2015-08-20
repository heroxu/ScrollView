package com.xxr.flawlayoutdome;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flawlayoutdome.R;

public class MainActivity extends Activity
{

	private MyFlowLayout	mFl;

	private String[]	mDatas	= new String[] { "QQ", "视频", "放开那三国", "电子书", "酒店",
								"单机", "小说", "斗地主", "优酷", "网游", "WIFI万能钥匙", "播放器", "捕鱼达人2", "机票",
								"游戏", "熊出没之熊大快跑", "美图秀秀", "浏览器", "单机游戏", "我的世界", "电影电视", "QQ空间",
								"旅游", "免费游戏", "2048", "刀塔传奇", "壁纸", "节奏大师", "锁屏", "装机必备", "天天动听",
								"备份", "网盘", "海淘网", "大众点评", "爱奇艺视频", "腾讯手机管家", "百度地图", "猎豹清理大师",
								"谷歌地图", "hao123上网导航", "京东", "youni有你", "万年历-农历黄历", "支付宝钱包" };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFl = (MyFlowLayout) findViewById(R.id.fl);
		mFl.setPadding(10, 10, 10, 10);
		mFl.setSpace(8, 8);

		// 动态去添加数据

		Random rdm = new Random();
		for (int i = 0; i < mDatas.length; i++)
		{
			final String data = mDatas[i];
			TextView view = new TextView(this);
			view.setText(data);
			view.setBackgroundColor(Color.GRAY);
			view.setTextColor(Color.WHITE);
			view.setPadding(5, 5, 5, 5);
			view.setGravity(Gravity.CENTER);
			view.setTextSize(rdm.nextInt(16) + 10);
			view.setTextSize(15);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{
					Toast.makeText(getApplicationContext(), data,
									Toast.LENGTH_SHORT).show();
				}
			});

			// view.setTextSize(14);

			// 添加到布局中
			mFl.addView(view);
		}

	}
}

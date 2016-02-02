package com.example.tab4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ResultTabActivity extends Activity {
	private String LeafList[] = { "栓皮槭", "茶条槭", "鸡爪槭", "条纹槭", "糖枫", "平滑唐棣",
			"美国栗树", "加拿大紫荆", "美国流苏树", "山茱萸", "榛", "华盛顿山楂", "美洲柿", "杜仲", "美洲榉木",
			"无花果", "银杏", "灰胡桃", "北美枫香树", "北美鹅掌楸", "锐叶木兰", "湖北海棠", "加拿大铁木",
			"大齿杨", "大山樱", "榆桔", "栎树montana", "黄橡树", "水栎", "夏栎", "栎树stellata",
			"黑栎", "柳树", "玉铃花", "暴马丁香", "毛白杨", "茯苓", "白榆", "榆杨梅", "榉树" ,
			"刺毛杜鹃","红枫","华山矶","黄山溲疏","江浙钓樟","始建槭","柿","秀丽槭","羊踯躅",
			"银木","玉兰"};
	private ListView listView;
	public static int[] result = new int[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resultlayout);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int sum = 0;
		for (int i = 0; i < result.length; i++) {
			sum += result[i];
		}
		if (sum == 0) {
			// 给result赋初值
			for (int i = 0; i < result.length; i++) {
				result[i] = i;
			}
		}
//		System.out.println("收到结果为：");
//		for (int i = 0; i < result.length; i++) {
//			System.out.println(result[i]);
//		}
		// 对应条目
		listView = (ListView) findViewById(R.id.listView);
		int resId;
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < result.length; i++) {
			resId = getResources().getIdentifier("pic_" + result[i],
					"drawable", "com.example.tab4");
			// System.out.println("执行次数：" + i + "资源ID：" + resId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pic", resId);
			map.put("plant_name", LeafList[result[i]]);
			list.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(ResultTabActivity.this, list,
				R.layout.adapter, new String[] { "pic", "plant_name" },
				new int[] { R.id.pic, R.id.plant_name });
		listView.setAdapter(adapter);
		//new Thread(new RefreshThread()).start();
	}

//	class RefreshThread implements Runnable {
//		public void run() {
//			while (!Thread.currentThread().isInterrupted()) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
//				// 使用postInvalidate可以直接在线程中更新界面
//				listView.postInvalidate();
//			}
//		}
//	}

}

package com.example.tab4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class BrowseTabActivity extends Activity {
	private String LeafList[] = { "Ë¨Æ¤éÊ", "²èÌõéÊ", "¼¦×¦éÊ", "ÌõÎÆéÊ", "ÌÇ·ã", "Æ½»¬ÌÆé¦",
			"ÃÀ¹úÀõÊ÷", "¼ÓÄÃ´ó×Ï¾£", "ÃÀ¹úÁ÷ËÕÊ÷", "É½ÜïİÇ", "é»", "»ªÊ¢¶ÙÉ½é«", "ÃÀÖŞÊÁ", "¶ÅÖÙ", "ÃÀÖŞé·Ä¾",
			"ÎŞ»¨¹û", "ÒøĞÓ", "»ÒºúÌÒ", "±±ÃÀ·ãÏãÊ÷", "±±ÃÀ¶ìÕÆé±", "ÈñÒ¶Ä¾À¼", "ºş±±º£ÌÄ", "¼ÓÄÃ´óÌúÄ¾",
			"´ó³İÑî", "´óÉ½Ó£", "ÓÜ½Û", "èİÊ÷montana", "»ÆÏğÊ÷", "Ë®èİ", "ÏÄèİ", "èİÊ÷stellata",
			"ºÚèİ", "ÁøÊ÷", "ÓñÁå»¨", "±©Âí¶¡Ïã", "Ã«°×Ñî", "ÜòÜß", "°×ÓÜ", "ÓÜÑîÃ·", "é·Ê÷", "´ÌÃ«¶Å¾é",
			"ºì·ã", "»ªÉ½í¶", "»ÆÉ½äÑÊè", "½­ÕãµöÕÁ", "Ê¼½¨éÊ", "ÊÁ", "ĞãÀöéÊ", "ÑòõÜõî", "ÒøÄ¾", "ÓñÀ¼" };
	private ListView listView;
	private int[] order = new int[51];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browselayout);

		// ¸øresult¸³³õÖµ
		for (int i = 0; i < order.length; i++) {
			order[i] = i;
		}
		// ¶ÔÓ¦ÌõÄ¿
		listView = (ListView) findViewById(R.id.listView);
		int resId;
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < order.length; i++) {
			resId = getResources().getIdentifier("pic_" + order[i], "drawable",
					"com.example.tab4");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pic", resId);
			map.put("plant_name", LeafList[order[i]]);
			list.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(BrowseTabActivity.this, list,
				R.layout.adapter, new String[] { "pic", "plant_name" },
				new int[] { R.id.pic, R.id.plant_name });
		listView.setAdapter(adapter);
	}
}

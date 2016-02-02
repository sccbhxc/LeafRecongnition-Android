package com.example.tab4;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PicTabActivity extends Activity {
	private static final int CAMERA_REQUEST = 1888;
	private static final int ALBUM_REQUEST = 10;
	private static final int EDIT_REQUEST=20;
	private final String ALBUM_TYPE = "image/*";
	private ImageView imageView1;
	private ImageView imageView2;
	private Button cameraButton;
	private Button albumButton;
	private Button sendButton;
	private Button checkButton;
	private Uri uri;
	Uri imageFileUri;
	private final int receiveNum = 8;
	private int[] result = new int[receiveNum];
	private Handler handler;
	Bitmap resultBitmap;
	private static final String TAG = "MainActivity";
	Bitmap srcBitmap;
	static boolean isEidt=false;
	// OpenCV库加载并初始化成功后的回调函数
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
		public void onManagerConnected(int status) {
			// TODO Auto-generated method stub
			switch (status) {
			case BaseLoaderCallback.SUCCESS:
				Log.i(TAG, "成功加载");
				break;
			default:
				super.onManagerConnected(status);
				Log.i(TAG, "加载失败");
				break;
			}

		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// load OpenCV engine and init OpenCV library
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10,
				getApplicationContext(), mLoaderCallback);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piclayout);
		this.imageView1 = (ImageView) this.findViewById(R.id.imageView1);
		this.imageView2 = (ImageView) this.findViewById(R.id.imageView2);
		cameraButton = (Button) this.findViewById(R.id.cameraButton);
		albumButton = (Button) this.findViewById(R.id.albumButton);
		sendButton = (Button) this.findViewById(R.id.sendButton);
		checkButton = (Button) this.findViewById(R.id.checkButton);
		sendButton.setEnabled(false);
		checkButton.setEnabled(false);
		// 打开刷新线程
		new Thread(new RefreshThread()).start();

		// 调用摄像头获取图片
		cameraButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST);
				
			}
		});
		// 调用相册获取图片
		albumButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
				getAlbum.setType(ALBUM_TYPE);
				startActivityForResult(getAlbum, ALBUM_REQUEST);
				
			}
		});

		//识别
		sendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				sendButton.setEnabled(false);
				checkButton.setEnabled(false);
				new ServerThread().start();
			}
		});
		//显示处理后图片
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					// 显示处理结果
					int width = resultBitmap.getWidth();
					int height = resultBitmap.getHeight();
					if (width > height) {
						Matrix m = new Matrix();
						int angle = 90;
						m.setRotate(angle);
						resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0,
								width, height, m, true);
					}
					imageView2.setImageBitmap(resultBitmap);
					// 设置按钮启动顺序
					checkButton.setEnabled(true);
					sendButton.setEnabled(true);
				}
			}

		};
		//确认处理结果，并将结果给"结果"标签
		checkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				for (int i = 0; i < result.length; i++) {
					ResultTabActivity.result[i] = result[i];
				}
			}
		});
		imageView1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					if(bitmap.getWidth()>1000||bitmap.getHeight()>1000){
						bitmap.recycle();
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 4;
						bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
					}
					byte[] imgByte=getBytes(bitmap);
					Intent intent=new Intent(PicTabActivity.this,PreprocessActivity.class);
					intent.putExtra("imgByte", imgByte);
					imgByte=null;
					startActivityForResult(intent, EDIT_REQUEST);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	// 接受intent传回的信息
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		// 摄像头返回结果
		case CAMERA_REQUEST: {
			if(resultCode==RESULT_OK){
				uri = data.getData();
				try {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;// 图片宽高都为原来的二分之一，即图片为原来的四分之一
					Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(uri), null, options);

					int width = bitmap.getWidth();
					int height = bitmap.getHeight();

					if (width > height) {
						Matrix m = new Matrix();
						int angle = 90;
						m.setRotate(angle);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
								m, true);// 从新生成图片
					}
					isEidt=false;
					imageView1.setImageBitmap(bitmap);
					sendButton.setEnabled(true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
		}
		// 相册返回结果
		case ALBUM_REQUEST: {
			if(resultCode==RESULT_OK){
				try {
					uri = data.getData(); // 获得图片的uri
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;// 图片宽高都为原来的二分之一，即图片为原来的四分之一
					Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(uri), null, options);

					int width = bitmap.getWidth();
					int height = bitmap.getHeight();

					if (width > height) {
						Matrix m = new Matrix();
						int angle = 90;
						m.setRotate(angle);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
								m, true);// 从新生成图片
					}
					isEidt=false;
					imageView1.setImageBitmap(bitmap);
					sendButton.setEnabled(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			

		}
		case EDIT_REQUEST:{
			if(resultCode==3){
				byte[] finishByte=data.getByteArrayExtra("finish");
				srcBitmap=getBitmap(finishByte);
				imageView1.setImageBitmap(srcBitmap);
				isEidt=true;
			}
		}
		default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
		}

	}

	class ServerThread extends Thread {
		@Override
		public void run() {
			Mat rgbMat = new Mat();

			ContentResolver resolver = getContentResolver();
			try {
				if (isEidt==false) {
					srcBitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
				}
				//Bitmap srcBitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
				if(srcBitmap.getWidth()>1000){
					srcBitmap.recycle();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					srcBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
				}
				
				resultBitmap = Bitmap.createBitmap(srcBitmap.getWidth(),
						srcBitmap.getHeight(), Config.RGB_565);
				Utils.bitmapToMat(srcBitmap, rgbMat);
				//srcBitmap.recycle();

				Recognize recongnize = new Recognize(rgbMat);
				recongnize.RecongnizePlant();
				Mat cm = recongnize.removeHandleImg;
				Utils.matToBitmap(cm, resultBitmap);

				for (int i = 0; i < receiveNum; i++) {
					result[i] = recongnize.resultIndex[i];
				}

				// 释放内存
				rgbMat.release();
				cm.release();

				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	class RefreshThread implements Runnable {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					Thread.currentThread().interrupt();
//				}
				// 使用postInvalidate可以直接在线程中更新界面
				imageView1.postInvalidate();
				imageView2.postInvalidate();
			}
		}
	}

	public byte[] getBytes(Bitmap bitmap){  
	    //实例化字节数组输出流  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图  
	    return baos.toByteArray();//创建分配字节数组  
	}
	
	public Bitmap getBitmap(byte[] data){  
	      return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图  
	} 
}

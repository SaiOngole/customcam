package com.cuxtomcam.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;


import com.glass.cuxtomcam.CuxtomCamActivity;
import com.glass.cuxtomcam.constants.CuxtomIntent;
import com.glass.cuxtomcam.constants.CuxtomIntent.CAMERA_MODE;
import com.glass.cuxtomcam.constants.CuxtomIntent.FILE_TYPE;

public class MainActivity extends Activity {
	private final int CUXTOM_CAM_REQUEST = 1111;
	private ImageView mImageView;
	private String videoPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView = (ImageView) findViewById(R.id.mImageView);
		startCuxtomCam();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void startCuxtomCam() {
		String folder = Environment.getExternalStorageDirectory() + File.separator 
				+ Environment.DIRECTORY_DCIM + File.separator + "Authenticate";
		//+ File.separator + Environment.DIRECTORY_PICTURES
		//+ File.separator + "CuxtomCam Sample";
		Intent intent = new Intent(getApplicationContext(),
				CuxtomCamActivity.class);
		intent.putExtra(CuxtomIntent.CAMERA_MODE, CAMERA_MODE.VIDEO_MODE);
		intent.putExtra(CuxtomIntent.ENABLE_ZOOM, true);
		intent.putExtra(CuxtomIntent.FILE_NAME, "newvideo");
		intent.putExtra(CuxtomIntent.VIDEO_DURATION, 10);
		intent.putExtra(CuxtomIntent.FOLDER_PATH, folder);
		startActivityForResult(intent, CUXTOM_CAM_REQUEST);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CUXTOM_CAM_REQUEST) {
			if (resultCode == RESULT_OK) {
				String path = data.getStringExtra(CuxtomIntent.FILE_PATH);
				videoPath = path;
				int FIleType = data.getIntExtra(CuxtomIntent.FILE_TYPE,
						FILE_TYPE.VIDEO);

				UploadVideo task = new UploadVideo();
				task.execute(new String[] {videoPath});
				//Insert authentication successful code here
				/*	if (FIleType == FILE_TYPE.PHOTO) {

					BitmapFactory.Options o = new BitmapFactory.Options();
					o.inSampleSize = 4;
					Bitmap bmp = BitmapFactory.decodeFile(path, o);
					mImageView.setImageBitmap(bmp);
				} 
				 else {
					Bitmap bmp = ThumbnailUtils
							.createVideoThumbnail(
									path,
									android.provider.MediaStore.Video.Thumbnails.MINI_KIND);
					mImageView.setImageBitmap(bmp);
				} */

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

// Create a new thread to upload the video
class UploadVideo extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		for (String videoPath:params) {
			Log.d("VideoPath", videoPath);
		try {
			
			HttpClient httpClient= new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://128.238.227.242/upload.php");
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			System.out.println(videoPath);
			if(!videoPath.isEmpty()) {
				FileBody filebodyVideo = new FileBody(new File(videoPath));
				reqEntity.addPart("uploaded_file", filebodyVideo);
			}
			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();
			while((sResponse = reader.readLine())!=null) {
				s = s.append(sResponse);
				String temp = s.toString();
				Log.d("network",temp);
			}
			Log.e("Response: ",s.toString());
			//write an external call to the server to start video processing and get the authentication code
		} catch(Exception e) {
			Log.e(e.getClass().getName(),e.getMessage());

		}
	}
		return null;

	}

}
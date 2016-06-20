/*
 * Copyright (C) 2013 yixia.com
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

package io.vov.vitamio.demo;


import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewDemo extends Activity {

	/**
	 * TODO: Set the path variable to a streaming video URL or a local media file
	 * path.
	 */

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Vitamio.isInitialized(getApplicationContext());
		
		setContentView(R.layout.videoview);

		playfunction();	

	}

	
	void playfunction(){
		 String path = "";
		 
		 path="";
		 path="http://sh189cloud001.oos-sh.ctyunapi.cn/0996ccca-55c4-4530-9057-742667f01046?response-content-type=video/mp4&Expires=1466145161&response-content-disposition=attachment%3Bfilename%3D%22BilibiliJJ.COM-%25E3%2580%2590%25E7%25AB%25A5%25E5%25B9%25B4%25E5%259B%259E%25E5%25BF%2586%25E3%2580%2591%25E7%258C%25AB%25E5%2592%258C%25E8%2580%2581%25E9%25BC%25A0%25EF%25BC%2588TomandJerry%25EF%25BC%2589DVD%25E5%2585%25A8%25E9%259B%2586%25EF%25BC%25881161%25EF%25BC%2589_122%25E3%2580%2581Dicky.Moe.1962.mp4%22&AWSAccessKeyId=fb9c2f0f68c98e38b0a3&Signature=YiMet0T/D9it6h/A1QfHneXVNuc%3D";
		 VideoView mVideoView;
		 EditText mEditText;
		mEditText = (EditText) findViewById(R.id.url);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		if (path == "") {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoViewDemo.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			mVideoView.setVideoPath(path);
			mVideoView.setMediaController(new MediaController(this));
			mVideoView.requestFocus();

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
		}
	}
	
}

package com.common.uitl;

import android.content.Context;
import android.media.AudioManager;

public class MediaTool {

	public static void soundPlay(int id, Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.playSoundEffect(id);
	}

}

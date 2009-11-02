package com.android.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.angle.AngleMainEngine;
import com.android.angle.AngleSprite;
import com.android.angle.AngleSpritesEngine;
import com.android.angle.AngleSurfaceView;

/**
 * In this tutorial we use a Runnable to implement a little game engine
 * 
 * We learn to: -Add a game engine. -Use AgleSprite and how to rotate it
 * -Implements a FPS counter -Make the changes consistent with the time elapsed
 * since last frame
 * 
 * @author Ivan Pajuelo
 * 
 */
public class Tutorial03 extends Activity
{
	private MyGameEngine mGame; // Independent game engine
	private AngleSurfaceView mView;
	private AngleSpritesEngine mSprites;
	private AngleSprite mLogo; // In this sample use AmgleSprite (see below)

	class MyGameEngine implements Runnable // Game engine class
	{
		// FPS Counter
		private int frameCount = 0;
		private long lCTM = 0;

		// -----------

		MyGameEngine()
		{
		}

		// The game engine must be a Runnable to callback his method run before
		// draw every frame
		public void run()
		{
			// Add FPS record to log every 100 frames
			frameCount++;
			if (frameCount >= 100)
			{
				long CTM = System.currentTimeMillis();
				frameCount = 0;
				if (lCTM > 0)
					Log.v("FPS", "" + (100.f / ((CTM - lCTM) / 1000.f)));
				lCTM = CTM;
			}
			// --------------------------------------

			// Rotate the logo at 45� per second
			mLogo.mRotation += 45 * AngleMainEngine.secondsElapsed;
			mLogo.mRotation %= 360;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mView = new AngleSurfaceView(this);
		setContentView(mView);

		mGame = new MyGameEngine(); // Instantiation
		mView.setBeforeDraw(mGame); // Tells view what method must call before
		// draw every frame

		mSprites = new AngleSpritesEngine(10, 0);
		mView.addEngine(mSprites);

		// Use AngleSprite instead of AngleSimpleSprite to rotate it
		mLogo = new AngleSprite(128, 128, R.drawable.anglelogo, 0, 0, 128, 128);
		mLogo.mCenter.set(100, 100);
		mSprites.addSprite(mLogo);
	}

	@Override
	protected void onPause()
	{
		mView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume()
	{
		mView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		mView.onDestroy();
		super.onDestroy();
	}
}
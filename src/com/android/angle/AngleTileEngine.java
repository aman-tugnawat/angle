package com.android.angle;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

/**
 * Tile engine with unique tilemap of maximum 256 tiles
 * 
 * @author Ivan Pajuelo
 * 
 */
public class AngleTileEngine extends AngleAbstractEngine
{
	public int mResourceID; // Resource bitmap
	public int mTextureID; // Texture ID on AngleTextureEngine
	protected int[] mTextureCrop = new int[4]; // Cropping coordinates
	public int mTexturePitch; // Horizontal tiles in texture
	public int mTilesCount; // Number of tiles in texture
	public int mTileWidth; // Tile width in pixels
	public int mTileHeight; // Tile height in pixels
	public byte[] mMap; // Tile map
	public int mMapWidth; // Tile map width in tiles
	public int mMapHeight; // Tile map height in tiles
	public float mLeft; // Left corner c�mera in pixels
	public float mTop; // Top corner c�mera in pixels
	public int mViewWidth; // Camera width in tiles
	public int mViewHeight; // Camera height in tiles
	public float mX; // Position in screen
	public float mY;
	public float mZ;

	/**
	 * 
	 * @param resourceID
	 *           Drawable with tiles
	 * @param texturePitch
	 *           Horizontal tiles columns
	 * @param tilesCount
	 *           Number of tiles
	 * @param tileWidth
	 *           Tile width in pixels
	 * @param tileHeight
	 *           Tile height in pixels
	 * @param mapWidth
	 *           Width of tile map
	 * @param mapHeight
	 *           Height of tilemap
	 */
	public AngleTileEngine(int resourceID, int texturePitch, int tilesCount,
			int tileWidth, int tileHeight, int mapWidth, int mapHeight)
	{
		mResourceID = resourceID;
		mTextureID = -1;
		mTexturePitch = texturePitch;
		mTilesCount = tilesCount;
		mTileWidth = tileWidth;
		mTileHeight = tileHeight;
		mMapWidth = mapWidth;
		mMapHeight = mapHeight;
		mMap = new byte[mMapWidth * mMapHeight];
		mLeft = 0;
		mTop = 0;
		mX = 0;
		mY = 0;
		mZ = 0;
		mTextureCrop[2] = mTileWidth; // Wcr
		mTextureCrop[3] = -mTileHeight; // Hcr
	}

	/**
	 * Load a tile map from a raw stream using mMapWidth and mMapHeight
	 * dimensions
	 * 
	 * @param istream
	 */
	public void loadMap(InputStream istream)
	{
		try
		{
			istream.read(mMap, 0, mMapWidth * mMapHeight);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void drawFrame(GL10 gl)
	{
		if (mTextureID >= 0)
		{
			if (!AngleTextureEngine.hasChanges)
			{
				int offsetX = (int) mLeft % mTileWidth;
				int offsetY = (int) mTop % mTileHeight;
				int W = mViewWidth + ((offsetX > 0) ? 1 : 0);
				int H = mViewHeight + ((offsetY > 0) ? 1 : 0);
				AngleTextureEngine.bindTexture(gl, mTextureID);
				for (int y = 0; y < H; y++)
				{
					for (int x = 0; x < W; x++)
					{
						int mTileIdx = ((y + ((int) mTop / mTileHeight)) * mMapWidth
								+ x + ((int) mLeft / mTileWidth));
						int mTile = 0;
						if ((mTileIdx >= 0) && (mTileIdx < mMapWidth * mMapHeight))
							mTile = mMap[mTileIdx];
						if (mTile < mTilesCount)
						{
							mTextureCrop[0] = (mTile % mTexturePitch) * mTileWidth; // Ucr
							mTextureCrop[1] = (mTile / mTexturePitch) * mTileHeight
									+ mTileHeight; // Vcr

							((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
									GL11Ext.GL_TEXTURE_CROP_RECT_OES, mTextureCrop, 0);

							((GL11Ext) gl).glDrawTexfOES(
									mX - offsetX + x * mTileWidth,
									AngleMainEngine.mHeight
											- (mY - offsetY + y * mTileHeight), mZ,
									mTileWidth, mTileHeight);
						}
					}
				}
			}
		}
	}

	@Override
	public void loadTextures(GL10 gl)
	{
		mTextureID = AngleTextureEngine.createHWTextureFromResource(mResourceID);
		super.loadTextures(gl);
	}
}
package com.greenshadow.thebutton.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class ImageSplitterUtil {

	/**
	 * 分割图片
	 * 
	 * @param bitmap
	 * @param piece
	 *            分割的阶数，例如3阶便分割为3*3=9块
	 * @return
	 */
	public static List<ImagePiece> splitImage(Bitmap bitmap, int piece) {
		List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		int pieceWidth = Math.min(width, height) / piece;

		for (int i = 0; i < piece; i++) {
			for (int j = 0; j < piece; j++) {
				ImagePiece imagePiece = new ImagePiece();
				imagePiece.setIndex(j + i * piece);

				int x = j * pieceWidth;
				int y = i * pieceWidth;

				imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y,
						pieceWidth, pieceWidth));
				imagePieces.add(imagePiece);
			}
		}

		return imagePieces;
	}
}

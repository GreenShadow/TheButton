package com.greenshadow.thebutton.ui;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.view.PuzzleView;
import com.greenshadow.thebutton.view.dialog.DialogTips;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * ƴͼ
 */
public class PuzzleGameActivity extends ActivityBase {

	private PuzzleView puzzleView;
	private TextView mTime;
	private Button puzzleBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puzzle);

		final String name = getIntent().getStringExtra("name");

		puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
		mTime = (TextView) findViewById(R.id.puzzle_time);
		puzzleBack = (Button) findViewById(R.id.puzzle_back);

		puzzleView
				.setOnGameComplitListener(new PuzzleView.GameComplitListener() {
					@Override
					public void success() {
						DialogTips dialog = new DialogTips(
								PuzzleGameActivity.this, "", "��Ϸ��ɣ���Ҫ��" + name
										+ "���Ϊ������", "ȷ��", true, false);
						dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setResult(RESULT_OK);
								PuzzleGameActivity.this.finish();
							}
						});
						dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setResult(RESULT_CANCELED);
								PuzzleGameActivity.this.finish();

							}
						});
						dialog.setCancel(false);
						dialog.show();
					}

					@Override
					public void timechanged(int currentTime) {
						mTime.setText(currentTime + "s");
					}

					@Override
					public void gameover() {
						Dialog dialog = new AlertDialog.Builder(
								PuzzleGameActivity.this)
								.setMessage("��Ϸʧ�ܣ��Ƿ�������")
								.setPositiveButton("ȷ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												puzzleView.restart();
											}
										})
								.setNegativeButton("ȡ��",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												setResult(RESULT_CANCELED);
												PuzzleGameActivity.this
														.finish();
											}
										})//
								.create();
						dialog.show();
					}
				});

		puzzleBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PuzzleGameActivity.this.finish();
			}
		});
	}
}

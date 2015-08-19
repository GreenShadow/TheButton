package com.greenshadow.thebutton.ui;

import com.greenshadow.thebutton.R;
import com.greenshadow.thebutton.adapter.ChoosePuzzleImageAdapter;
import com.greenshadow.thebutton.util.PixelUtil;
import com.greenshadow.thebutton.view.PuzzleView;
import com.greenshadow.thebutton.view.dialog.DialogTips;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 拼图
 */
public class PuzzleGameActivity extends ActivityBase implements OnClickListener {

	private PuzzleView puzzleView;
	private TextView mTime;
	private Button puzzleBack, puzzleRestart;
	private AlertDialog dialog;
	private String name;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_puzzle);

		name = getIntent().getStringExtra("name");

		ChoosePuzzleImageAdapter adapter = new ChoosePuzzleImageAdapter(this,
				PuzzleView.getDatas());
		GridView gridView = (GridView) getLayoutInflater().inflate(
				R.layout.dialog_choose_puzzle_image_grid, null);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				initPuzzle(position);
			}
		});

		puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
		mTime = (TextView) findViewById(R.id.puzzle_time);
		puzzleBack = (Button) findViewById(R.id.puzzle_back);
		puzzleRestart = (Button) findViewById(R.id.puzzle_restart);

		puzzleRestart.setOnClickListener(this);
		puzzleBack.setOnClickListener(this);

		dialog = new AlertDialog.Builder(this) //
				.setTitle("选择拼图图片") //
				.setView(gridView) //
				.setCancelable(false) //
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setResult(RESULT_CANCELED);
						PuzzleGameActivity.this.finish();
					}
				}) //
				.create();
		dialog.show();
		LayoutParams lp = dialog.getWindow().getAttributes();
		dialog.getWindow().setLayout(lp.width, PixelUtil.dp2px(450));
	}

	private void initPuzzle(int position) {
		puzzleView.setSelection(position);
		puzzleView
				.setOnGameComplitListener(new PuzzleView.GameComplitListener() {
					@Override
					public void success() {
						DialogTips dialog = new DialogTips(
								PuzzleGameActivity.this, "", "游戏完成！您要将" + name
										+ "添加为好友吗？", "确定", true, false);
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
								dialog.dismiss();
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
						mTime.setText("游戏结束");
						DialogTips dialog = new DialogTips(
								PuzzleGameActivity.this, "", "游戏失败！是否重来？",
								"确定", true, false);
						dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								puzzleView.restartGame();
							}
						});
						dialog.SetOnCancelListener(new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
						dialog.setCancel(false);
						dialog.show();
					}
				});
		puzzleView.startGame();
		if (dialog != null)
			dialog.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		puzzleView.stopGame();
		mTime.setText("游戏结束");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.puzzle_back:
			setResult(RESULT_CANCELED);
			PuzzleGameActivity.this.finish();
			break;
		case R.id.puzzle_restart:
			puzzleView.restartGame();
			break;
		}
	}
}

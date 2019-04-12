/**
 * 実機NXT用ロボットクラス
 */
public class Result_R8 extends Robot
{
	/** leJOS での起動用 main 関数 */
	static void main(String[] args) { 
		try {
			// 時間計測
			Long time = System.currentTimeMillis();

			// ロボットオブジェクトを生成して実行
			new Result_R8().run();

			time = (System.currentTimeMillis() - time) / 1000;
			System.out.println("Time = "+time.intValue() + "sec");

			// 7秒待ってから停止
			Thread.sleep(7000);
		}catch (InterruptedException e) {
			;
		}
	}

	/**
	 * 実行用関数
	 */
	public void run() throws InterruptedException
	{
		/* 学習した最適政策を表す配列 */
		// 最短道のほう
		// 最高記録 Run: 301.8cm
		int[] q = new int[9];
		q[0] = 23;
		q[1] = 9;
		q[2] = 0;
		q[3] = 3;
		q[4] = 10;
		q[5] = 19;
		q[6] = 8;
		q[7] = 8;
		q[8] = 0;

		while (true) {
			/* 現在の状態を観測 */
			int state = getState();

			/* 行動を選択する */
			int action = q[state];

			/* その状態における最適な行動を実行 */
			move(action);

			// delay()メソッドは、Robot.javaに応じていずれかの機能を実行する
			// 速度調整＆画面描画 (シミュレーター用)
			// ESCAPEボタン押下時に割り込みを発生させる (実機NXT用)
			delay();

			// ゴールに到達すれば終了
			if (isOnGoal())
				break;
		}
	}
	
	/**
	 * 状態を定義する関数
	 */
	public int getState(){
		
		int stateNum = 0;

		// LIGHT_A : 右センサー ||| LIGHT_B : 真ん中センサー ||| LIGHT_C : 左センサー
		if(getColor(LIGHT_A) == WHITE && getColor(LIGHT_B) == WHITE && getColor(LIGHT_C) == WHITE)
			stateNum = 0;
		else if(getColor(LIGHT_A) == WHITE && getColor(LIGHT_B) == WHITE && getColor(LIGHT_C) == BLACK)
			stateNum = 1;
		else if(getColor(LIGHT_A) == WHITE && getColor(LIGHT_B) == BLACK && getColor(LIGHT_C) == WHITE)
			stateNum = 2;
		else if(getColor(LIGHT_A) == WHITE && getColor(LIGHT_B) == BLACK && getColor(LIGHT_C) == BLACK)
			stateNum = 3;
		else if(getColor(LIGHT_A) == BLACK && getColor(LIGHT_B) == WHITE && getColor(LIGHT_C) == WHITE)
			stateNum = 4;
		else if(getColor(LIGHT_A) == BLACK && getColor(LIGHT_B) == WHITE && getColor(LIGHT_C) == BLACK)
			stateNum = 5;
		else if(getColor(LIGHT_A) == BLACK && getColor(LIGHT_B) == BLACK && getColor(LIGHT_C) == WHITE)
			stateNum = 6;
		else if(getColor(LIGHT_A) == BLACK && getColor(LIGHT_B) == BLACK && getColor(LIGHT_C) == BLACK)
			stateNum = 7;
		else if(getColor(LIGHT_B) == BLUE)
			stateNum = 8;

		return stateNum;
	}
	
	/**
	 * ロボットを移動する
	 */
	public void move(int action)
	{
		/* 真っ直ぐ進む */
		if(action == 0) // STRAIGHT
			goStraight(1);
		
		/* action が奇数の場合、左に曲がる */
		/* 角度によって、左に曲がる (eg. 5, 10, 15, ..., 90) */
		// LEFT
		else if(action % 2 != 0)
			turnLeft(((action / 2) + 1) * 5);
		
		/* action が偶数の場合、右に曲がる */
		/* 角度によって、右に曲がる (eg. 5, 10, 15, ..., 90) */
		// RIGHT
		else if(action % 2 == 0)
			turnRight((action / 2) * 5);
		
		// 方向を曲げた後に、ロボットを前に進ませる
		if(action != 0)
			goStraight(1);
	}
	
	/**
	 * 各行動を実行するための条件を設定する関数
	 */
	public void turnLeft(int angle){
		if(getColor(LIGHT_A) == WHITE || getColor(LIGHT_C) == BLACK)
			rotateLeft(angle);
	}
	public void turnRight(int angle){
		if(getColor(LIGHT_A) == BLACK || getColor(LIGHT_C) == WHITE)
			rotateRight(angle);
	}
	public void goStraight(int moveSpeed){
		if(getColor(LIGHT_B) == BLACK || getColor(LIGHT_B) == BLUE)
			forward(moveSpeed);
	}
}

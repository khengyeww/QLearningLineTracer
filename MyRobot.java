/**
 * ロボットクラスの作成例：単純なライントレーサーロボット
 */
public class MyRobot extends Robot
{
	/**
	 * 実行用関数
	 */
	public void run() throws InterruptedException
	{
		// ここをがんばって作る

		// step 1:	Q学習する
		// QLearningのインスタンスを作る	
		int states = 9; // 状態数
		int actions = 37;	// 行動数
		double alpha = 0.5; // 学習率
		double gamma = 0.5; // 割引率

		QLearning ql = new QLearning(states, actions, alpha, gamma);

		int trials = 500; //500 // 強化学習の試行回数
		int steps = 500; //100 // 1試行あたりの最大ステップ数

		// 試行回数だけ繰り返し
		for(int t = 0; t < trials; t++){
			//System.out.println("This is new loop testttttttttttt: " + t);

			/* ロボットを初期位置に戻す */
			init();
			
			// ステップ数だけ繰り返し
			for(int s = 0; s < steps; s++){

				/*--------------- ε-Greedy 法により行動を選択 ---------------*/
				// 現在の状態番号を取得する
				int state = getState();

				// ランダムに行動を選択する確率
				double epsilon = 0.5;
				int action = ql.selectAction(state, epsilon);
				//int action = ql.selectAction(state);

				/*--------------- 選択した行動を実行 (ロボットを移動する) ---------------*/
				moveRobotCar(action);

				/*--------------- 新しい状態を観測＆報酬を得る ---------------*/
				//次の状態番号
				int	after = getState(); // 頑張って取得する
				//System.out.println(after);
				
				// 状態afterにおける報酬
				int reward = 0; // 頑張って取得する

				// Goal に到達したら 1000 報酬を与え、黒い/青い線上にいるなら正の報酬を、白い線上にいるなら負の報酬を与える
				if(isOnGoal())
					reward = 1000;
				else if(after == 8)
					reward = 500;
				else if(getColor(LIGHT_B) == BLACK)
					reward = 150;
				else if(getColor(LIGHT_B) == WHITE)
					reward = -150;

				/*--------------- Q 値を更新 ---------------*/
				// qlインスタンスから呼び出す
				ql.update(state, action, after, reward);

				/*
				// 速度調整＆画面描画
				delay();
				//*/
				
				// ゴールに到達すれば終了
				if (isOnGoal())
					break;

			}
		}

		/* 学習終了で、最後の成果を出す */
		init(); // ロボットを初期位置に戻す
		finalRunTest(ql);

		/* MyRobotForNXT に最適な行動番号を登録するために、学習後の Qテーブルを表示する */
		//ql.showQTable(states);
		
		// Done
		System.out.println("Done!!!!!!!!!!!!!!!!!");
		//System.exit(0);
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
	public void moveRobotCar(int action)
	{
		/* 真っ直ぐ進む */
		// STRAIGHT
		if(action == 0)
			goStraight(1);
		
		/* action が奇数の場合、左に曲がる */
		/* 角度によって、左に曲がる (eg. 5, 10, 15, ..., 90) */
		// LEFT
		else if(action % 2 != 0)
			turnLeft(((action / 2) + 1) * 5);

		// 参考用
//		else if(action == 1) // LEFT
//			turnLeft(5);
//		else if(action == 3) // LEFT
//			turnLeft(10);
//		else if(action == 5) // LEFT
//			turnLeft(15);
//		else if(action == 7) // LEFT
//			turnLeft(20);
//		else if(action == 9) // LEFT
//			turnLeft(25);
//		else if(action == 11) // LEFT
//			turnLeft(30);

		/* action が偶数の場合、右に曲がる */
		/* 角度によって、右に曲がる (eg. 5, 10, 15, ..., 90) */
		// RIGHT
		else if(action % 2 == 0)
			turnRight((action / 2) * 5);
		
		// 参考用
//		else if(action == 2) // RIGHT
//			turnRight(5);
//		else if(action == 4) // RIGHT
//			turnRight(10);
//		else if(action == 6) // RIGHT
//			turnRight(15);
//		else if(action == 8) // RIGHT
//			turnRight(20);
//		else if(action == 10) // RIGHT
//			turnRight(25);
//		else if(action == 12) // RIGHT
//			turnRight(30);
		
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
	
	/**
	 * 学習終了で、最後の成果を出す用関数
	 */
	public void finalRunTest(QLearning ql) throws InterruptedException{

		while(true){
			// 現在の状態番号を取得する
			int state = getState();
			
			// デバッグ用
			//System.out.println("A:" + getColor(LIGHT_A) + " B:" + getColor(LIGHT_B) + " C:" + getColor(LIGHT_C));

			// 次の行動を選択する
			int action = ql.selectAction(state);

			// 選択した行動を実行 (ロボットを移動する)
			moveRobotCar(action);

			// 速度調整＆画面描画
			delay();

			// ゴールに到達すれば終了
			if (isOnGoal())
				break;
		}
		
	}
}

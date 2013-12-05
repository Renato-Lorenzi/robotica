package com.nxtcontroller;

import android.util.Log;

import com.nxtcomunication.NXTTalker;

public class RobotController {

	private NXTTalker t;

	public RobotController(NXTTalker talker) {
		t = talker;
		while (t.getState() != NXTTalker.STATE_CONNECTED) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.e("LALAL", "Aguardando conexão");
		}

	}

	public void left() {
		t.motors((byte) 30, (byte) -30, false, true);
		sleep();
		stop();
	}

	public void right() {
		t.motors((byte) -30, (byte) 30, false, true);
		sleep();
		stop();
	}

	public void ahead() {
		t.motors((byte) 30, (byte) 30, false, true);
		sleep();
		stop();
	}

	public void stop() {
		t.motors((byte) 0, (byte) 0, false, true);
		sleep();
	}

	private void sleep() {
		try {
			Thread.sleep(1250);//
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

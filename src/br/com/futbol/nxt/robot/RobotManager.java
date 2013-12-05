package br.com.futbol.nxt.robot;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import br.com.futbol.nxt.ColorDetector;

import com.nxtcomunication.NXTTalker;
import com.nxtcontroller.RobotController;
import com.nxtcontroller.RobotMove;
import com.nxtcontroller.RobotMoveImpl;

public class RobotManager {
	private ColorDetector mBallDetector = new ColorDetector(new Scalar(
			2.609375, 174.5625, 255.0, 0.0));
	private ColorDetector mRobotDetector = new ColorDetector(new Scalar(0, 255,
			0));

	private ColorDetector trave = new ColorDetector(new Scalar(0, 255, 0));

	private RobotMove robotMove;

	public RobotManager() {
	}

	public Mat go(Mat mRgba) {
		if (mRobotDetector.detect(mRgba) && mBallDetector.detect(mRgba)) {
			Point robotCenter = mRobotDetector.getCenter();
			Point ballCenter = mBallDetector.getCenter();

			if (!equals(robotCenter.x, ballCenter.x)) {
				if (robotCenter.x < ballCenter.x) {
					robotMove.incX();
				} else {
					robotMove.decX();
				}
			} else if (!equals(robotCenter.y, ballCenter.y)) {
				if (robotCenter.y < ballCenter.y) {
					robotMove.incY();
				} else {
					robotMove.decY();
				}
			}
		}
		return mRgba;
	}

	private boolean equals(double a, double b) {
		return Math.abs(a - b) < 50;
	}

	public RobotMove newMove() {
		BluetoothAdapter a = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice dev = null;
		for (BluetoothDevice d : a.getBondedDevices()) {
			if (d.getName().equals("NXT_KIDO")) {
				dev = d;
			}
		}
		if (dev == null) {
			return null;
		}
		NXTTalker t = new NXTTalker();
		try {
			t.connect(dev);
		} catch (Exception e) {
		}

		RobotController c = null;
		try {
			c = new RobotController(t);
		} catch (Exception e) {
		}

		return new RobotMoveImpl(c);

	}
}

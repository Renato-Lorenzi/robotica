package br.com.futbol.nxt;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.colorblobdetect.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

public class BallDetectionActivity extends Activity implements OnTouchListener,
		CvCameraViewListener2 {
	private static final String TAG = "OCVSample::Activity";

	private Mat mRgba;
	private BallDetector mDetector;
	private Scalar CONTOUR_COLOR;

	private CameraBridgeViewBase mOpenCvCameraView;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
				mOpenCvCameraView
						.setOnTouchListener(BallDetectionActivity.this);
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	public BallDetectionActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.color_blob_detection_surface_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mDetector = new BallDetector();
		// vermelho
		mDetector.setHsvColor(new Scalar(2.609375, 174.5625, 255.0, 0.0));
		CONTOUR_COLOR = new Scalar(0, 255, 0, 255);
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// http://docs.opencv.org/doc/tutorials/imgproc/shapedescriptors/bounding_rects_circles/bounding_rects_circles.html
		mRgba = inputFrame.rgba();
		mDetector.process(mRgba);
		List<MatOfPoint> contours = mDetector.getContours();
		// Log.e(TAG, "Contours count: " + contours.size());
		if (contours.size() > 0) {
			MatOfPoint contour = contours.get(0);
			MatOfPoint2f contour2 = new MatOfPoint2f(contour.toArray());

			MatOfPoint2f contourPoly = new MatOfPoint2f();
			Imgproc.approxPolyDP(contour2, contourPoly, 3, true);
			Point center = new Point();
			float[] radius = new float[1];
			Imgproc.minEnclosingCircle(contourPoly, center, radius);

			Core.circle(mRgba, center, (int) radius[0], new Scalar(0, 0, 255),
					2, 8, 0);
			// Log.e(TAG, "x: " + center.x + ", y: " + center.y);

			Log.e(TAG, ((center.x > mRgba.cols() / 2) ? "direita" : "esquerda")
					+ ", x: " + center.x + ", y: " + center.y);
		}

		// Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
		return mRgba;
	}
}

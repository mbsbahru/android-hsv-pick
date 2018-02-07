package mbs.hsvcolorpick;
import java.util.List;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

	public class SeekBarVal extends Activity implements CvCameraViewListener2, OnTouchListener {
	   private static final String TAG = "SeekActivity";
	   private static final int    VIEW_MODE_HSV11   = 0;  
	   private int          mViewMode; 
	   private float nLux;
	   private MisiView  mOpenCvCameraView;
	   private Mat mRgba;
	   private Mat mHSV;
	   public static double Hmin1 = 0;
	   public static double Hmid = 0;
	   public static double Hmax1 = 0;
	   public static double Smin1 = 0;
	   public static double Smid = 0;
	   public static double Smax1 = 0;
	   public static double Vmin1 = 0;
	   public static double Vmid = 0;
	   public static double Vmax1 = 0;
	   public static double Dilate1;
	   public static double Erode1;
	   public static int ExVal = -12;
	   public static int WBidx = 2;
	   public static int ExpVal = 12;
	   public static double HueAv = 0;
	   public static double SatAv = 0;
	   public static double ValAv = 0;
	   public static String valWB = "daylight";
	   private Boolean val = false;
	   private Sensor mOrienta;
	   private SeekBar seekHmin1;
	   private SeekBar seekHmax1;
	   private SeekBar seekSmin1;
	   private SeekBar seekSmax1;
	   private SeekBar seekVmin1;
	   private SeekBar seekVmax1;
	   private SeekBar seekErode;
	   private SeekBar seekDilate;
	   private SeekBar seekEV;
	   private SeekBar seekWB;
	   private boolean mIsDisplayTouched;
	   private SeekAdapter sH;
	   private SeekAdapter sS;
	   private SeekAdapter sV;
	   private SeekAdapter sED;
	   private SeekAdapter sEW;
	   private Mat mSpectrum;
	   private Scalar mBlobColorRgba;
	   private Scalar mBlobColorHsv;
	   private Scalar mBlobColorHsv2;
	   private Size SPECTRUM_SIZE;
	   private BlobDetector mDetector;  
	   protected boolean bIsHPressed;
	   protected boolean bIsSPressed;
	   protected boolean bIsVPressed;
	   protected boolean bIsEDPressed;
	   protected boolean bIsEWPressed;
	   
	   private SensorEventListener mySensorEventListener;
		
	   private Mat mDisplay;
	   private Mat mBiner;
	   private boolean idxBiner =false;
	   
		private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {  
			
			   @Override  
			     public void onManagerConnected(int status) {  
			       switch (status) {  
			         case LoaderCallbackInterface.SUCCESS:  
			         {  
			        	 Log.i(TAG, "OpenCV loaded successfully"); 
			           mOpenCvCameraView.enableView();  
			           mOpenCvCameraView.setOnTouchListener(SeekBarVal.this);
			         } break;  
			         default:
			         {  
			           super.onManagerConnected(status);  
			         } break;  
			       }
			     }
			   };
		private MenuItem mItemExposure;

		public SeekBarVal() {  
				     Log.i(TAG, "Instantiated new " + this.getClass());  
				   }  
			   
		@SuppressWarnings("deprecation")
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	    	 Log.i(TAG, "called onCreate"); 
	    	super.onCreate(savedInstanceState); 
	    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
			setContentView(R.layout.biner);
			
			SensorManager mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		    Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		    mySensorManager.registerListener(LightSensorListener, LightSensor, SensorManager.SENSOR_DELAY_NORMAL);
		    
		    mySensorEventListener = new MySensorEventListener();
		    mOrienta = mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		    mySensorManager.registerListener(mySensorEventListener, mOrienta, SensorManager.SENSOR_DELAY_GAME);
			 
			mOpenCvCameraView = (MisiView) findViewById(R.id.activity_surface_view);
			mOpenCvCameraView.setCameraIndex(0);
			mOpenCvCameraView.setMaxFrameSize(480, 320);
	        mOpenCvCameraView.setCvCameraViewListener(this);
	    }
	    
	    @Override  
	    public boolean onCreateOptionsMenu(Menu menu) {  
	    	Log.i(TAG, "called onCreateOptionsMenu");
	        mItemExposure = menu.add("exp");
	    	return true;  
	    }  
	    
	    @Override  
	    public void onPause()  
	    {  
	      super.onPause();  
	      if (mOpenCvCameraView != null)  
	        mOpenCvCameraView.disableView();  
	    }
	    
	    @Override  
	    public void onResume()  
	    {  
	      super.onResume();  
	      OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);  
	    }
	    
	    @Override
	    protected void onDestroy(){ 
	   	  if (mOpenCvCameraView != null)  
	   	       mOpenCvCameraView.disableView();
	    	     super.onDestroy();
	    }

		@Override
		public void onCameraViewStarted(int width, int height) {
			// TODO Auto-generated method stub
			mRgba = new Mat(height, width, CvType.CV_8UC4);
	    	mHSV = new Mat(height, width, CvType.CV_8UC4);
	    	mDetector = new BlobDetector();
		    sH = new SeekAdapter();
		    sS = new SeekAdapter();
		    sV = new SeekAdapter();
		    sED = new SeekAdapter();
		    sEW = new SeekAdapter();
		    mSpectrum = new Mat();
		    mBlobColorRgba = new Scalar(255);
		    mBlobColorHsv = new Scalar(255);
		    SPECTRUM_SIZE = new Size(mRgba.cols()/1.5, 64);
	    	mDisplay = new Mat();
	    	mBiner = new Mat(height,width,CvType.CV_8UC1);
		}

		@Override
		public void onCameraViewStopped() {
			// TODO Auto-generated method stub
			mRgba.release();
		    mHSV.release();
		    mDisplay.release();
		    mBiner.release();
		}

		@Override
		public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
			// TODO Auto-generated method stub
			final int viewMode = mViewMode;
			mRgba = inputFrame.rgba();
			mOpenCvCameraView.setFocus("manual");
			mOpenCvCameraView.setWhite();
			mOpenCvCameraView.setAntibanding("auto");
			mOpenCvCameraView.setExposureLock(val);
			
			Button bMenu = (Button)findViewById(R.id.tombolMenu);
			bMenu.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					openOptionsMenu();
				}
			});
			
			Rect totScreen = new Rect();
			
			    totScreen.x = 0;
			    totScreen.y = 0;

			    totScreen.width = 480;
			    totScreen.height = 320;

			    Mat touchedRegionRgba2 = mRgba.submat(totScreen);

			    Mat touchedRegionHsv2 = new Mat();
			    Imgproc.cvtColor(touchedRegionRgba2, touchedRegionHsv2, Imgproc.COLOR_RGB2HSV_FULL);
			      
			    
			    mBlobColorHsv2 = Core.sumElems(touchedRegionHsv2);
		    
			    HueAv = mBlobColorHsv2.val[0]/153600;
			    SatAv = mBlobColorHsv2.val[1]/153600;
			    ValAv = mBlobColorHsv2.val[2]/153600;
			    
			if (viewMode==VIEW_MODE_HSV11){
				Button bH = (Button)findViewById(R.id.tombolH);
	    		Button bS = (Button)findViewById(R.id.tombolS);
				Button bV = (Button)findViewById(R.id.tombolV);
				Button bED = (Button)findViewById(R.id.tombolED);
				Button bEW = (Button)findViewById(R.id.tombolEW);
				
				seekHmin1=(SeekBar) findViewById(R.id.min);
				seekHmax1=(SeekBar) findViewById(R.id.max);
				seekSmin1=(SeekBar) findViewById(R.id.min);
				seekSmax1=(SeekBar) findViewById(R.id.max);
				seekVmin1=(SeekBar) findViewById(R.id.min);
				seekVmax1=(SeekBar) findViewById(R.id.max);
				seekErode=(SeekBar) findViewById(R.id.min);
				seekDilate=(SeekBar) findViewById(R.id.max);
				seekEV=(SeekBar) findViewById(R.id.min);
				seekWB=(SeekBar) findViewById(R.id.max);
				
				bH.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sH.setProgress(seekHmin1, seekHmax1, Hmin1, Hmax1, 262);
						sH.seekProgress();
						bIsHPressed = true;
						bIsSPressed = false;
						bIsVPressed = false;
						bIsEDPressed = false;
						bIsEWPressed = false;
						mIsDisplayTouched = false;
					}
				});
				bS.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sS.setProgress(seekSmin1, seekSmax1, Smin1, Smax1, 262);
						sS.seekProgress();
						bIsSPressed = true;
						bIsHPressed = false;
						bIsVPressed = false;
						bIsEDPressed = false;
						bIsEWPressed = false;
						mIsDisplayTouched = false;
					}
				});
				bV.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sV.setProgress(seekVmin1, seekVmax1, Vmin1, Vmax1, 262);
						sV.seekProgress();
						sV.seekBarChange();
						bIsHPressed = false;
						bIsSPressed = false;
						bIsEDPressed = false;
						bIsEWPressed = false;
						bIsVPressed = true;
						mIsDisplayTouched = false;
					}
				});
				bED.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sED.setProgress(seekErode, seekDilate, Erode1, Dilate1, 21);
						sED.seekProgress();
						sED.seekBarChange();
						bIsEDPressed = true;
						bIsHPressed = false;
						bIsSPressed = false;
						bIsVPressed = false;
						bIsEWPressed = false;
						mIsDisplayTouched = false;
						
						if(idxBiner)
							idxBiner = false;
							else
								idxBiner = true;
					}
				});
				
				bEW.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sEW.setProgress(seekEV, seekWB, ExpVal, WBidx, 25);
						sEW.seekProgress();
						sEW.seekBarChange();
						bIsEDPressed = false;
						bIsHPressed = false;
						bIsSPressed = false;
						bIsVPressed = false;
						bIsEWPressed = true;
						mIsDisplayTouched = false;
					}
				});
				
				if(bIsHPressed){
					Hmin1 = sH.getFirstVal();
					Hmax1 = sH.getSecondVal();
					sH.setProgress(seekHmin1, seekHmax1, Hmin1, Hmax1, 262);
					sH.seekProgress();
					sH.seekBarChange();
				}
				
				if(bIsSPressed){
					Smin1 = sS.getFirstVal();
					Smax1 = sS.getSecondVal();
					sS.setProgress(seekSmin1, seekSmax1, Smin1, Smax1, 262);
					sS.seekProgress();
					sS.seekBarChange();
				}
				
				if(bIsVPressed){
					Vmin1 = sV.getFirstVal();
					Vmax1 = sV.getSecondVal();
					sV.setProgress(seekVmin1, seekVmax1, Vmin1, Vmax1, 262);
					sV.seekProgress();
					sV.seekBarChange();
				}
				
				if(bIsEDPressed){
					Erode1 = sED.getFirstVal();
					Dilate1 = sED.getSecondVal();
					sED.setProgress(seekErode, seekDilate, Erode1, Dilate1, 21);
					sED.seekProgress();
					sED.seekBarChange();

					
				}
				
				if(bIsEWPressed){
					ExpVal = sEW.getFirstVal();
					WBidx = sEW.getSecondVal();
					sEW.setProgress(seekEV, seekWB, ExpVal, WBidx, 25);
					sEW.seekProgress();
					sEW.seekBarChange();
				}
				
				if(idxBiner){
			        Scalar hsv_min2 = new Scalar(Hmin1, Smin1, Vmin1, 0);  
			        Scalar hsv_max2 = new Scalar(Hmax1, Smax1, Vmax1, 0);
			        Imgproc.cvtColor(mRgba, mHSV, Imgproc.COLOR_RGB2HSV_FULL,4);    
			        Core.inRange(mHSV, hsv_min2, hsv_max2, mBiner);           
			        Imgproc.erode(mBiner, mBiner, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size (2*Erode1+1,2*Erode1+1), new Point (Erode1,Erode1))); 
			        Imgproc.dilate(mBiner, mBiner, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size (2*Dilate1+1,2*Dilate1+1), new Point (Dilate1,Dilate1)));
			        Imgproc.dilate(mBiner, mBiner, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size (2*Dilate1+1,2*Dilate1+1), new Point (Dilate1,Dilate1)));
			        Imgproc.erode(mBiner, mBiner, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size (2*Erode1+1,2*Erode1+1), new Point (Erode1,Erode1)));
			        mDisplay = mBiner;
				}
				
				else{
					if(mIsDisplayTouched){
						Hmin1 = (int) mDetector.getHmin();
						Hmid  = (int) mDetector.getHmid();
						Hmax1 = (int) mDetector.getHmax();
						Smin1 = (int) mDetector.getSmin();
						Smid  = (int) mDetector.getSmid();
						Smax1 = (int) mDetector.getSmax();
						Vmin1 = (int) mDetector.getVmin();
						Vmid  = (int) mDetector.getVmid();
						Vmax1 = (int) mDetector.getVmax();
						}
					
					
	            mDetector.process(mRgba, new Scalar(Hmin1, Smin1, Vmin1), new Scalar(Hmax1, Smax1, Vmax1), Dilate1, Erode1);
	            List<MatOfPoint> contours = mDetector.getContours();
	            //Imgproc.fillPoly(mRgba, contours, CONTOUR_COLOR);
	            
	            Log.e(TAG, "Contours count: " + contours.size());
	            
	            
	            MatOfPoint approx =  new MatOfPoint();
	            
	            double max = 0;
	            for (int i = 0; i < contours.size(); i++){
	            	MatOfPoint tempContour = contours.get(i);
	            	MatOfPoint2f newMat = new MatOfPoint2f( tempContour.toArray() );
	            	MatOfPoint2f newApprox = new MatOfPoint2f( approx.toArray() );
	                       
	            	Imgproc.approxPolyDP(newMat, newApprox, Imgproc.arcLength(newMat, true)*0.02, true);
	     	   
	     	   if (Imgproc.contourArea(contours.get(i)) > max){
	     		   max = Imgproc.contourArea(contours.get(i));
	     	   }
	           }
	            
		 
		
		        switch(WBidx){
		            case 0	: valWB = "auto"; break;
		            case 1	: valWB = "auto"; break;
		            case 2	: valWB = "daylight"; break;
		            case 3	: valWB = "daylight"; break;
		            case 4	: valWB = "cloudy-daylight"; break;
		            case 5	: valWB = "cloudy-daylight"; break;
		            case 6	: valWB = "twilight"; break;
		            case 7	: valWB = "twilight"; break;
		            case 8	: valWB = "incandescent"; break;
		            case 9	: valWB = "incandescent"; break;
		            case 10	: valWB = "warm-fluorescent"; break;
		            case 11	: valWB = "warm-fluorescent"; break;
		            case 12	: valWB = "fluorescent"; break;
		            case 13	: valWB = "fluorescent"; break;
		            case 14	: valWB = "shade"; break;
		            case 15	: valWB = "shade"; break;
		            case 16	: valWB = "auto"; break;
		            case 17	: valWB = "auto"; break;
		            default : valWB = "daylight"; break;
	            }
	            
	            ExVal = ExpVal-12;
	            mOpenCvCameraView.setExposure(ExVal);

				Vmin1 = Vmid - 50;
				Vmax1 = Vmid + 50;
				Vmax1 = (Vmax1 > 255)? 255 : Vmax1;
				Vmin1 = (Vmin1 < 0)? 0 : Vmin1;
	            
				Smin1 = Smid - 60;
				Smax1 = Smid + 40;
				Smax1 = (Smax1 > 255)? 255 : Smax1;
				Smin1 = (Smin1 < 0)? 0 : Smin1;
				
	            Imgproc.drawContours(mRgba, contours, -1, new Scalar(153, 0, 153, 155), 2);

				
	            Imgproc.putText(mRgba, "valBot:   " + Hmin1 + ",  " + Smin1 + ",  " + Vmin1 + ",  " + Erode1, new Point (8, mRgba.rows() * 0.05), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 255, 0), 1);
				Imgproc.putText(mRgba, "valMax:   " + Hmax1 + ",  " + Smax1 + ",  " + Vmax1 + ",  " + Dilate1, new Point (8, mRgba.rows() * 0.1), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 255, 0), 1);
				
				Imgproc.putText(mRgba, "Exp: " + val, new Point (3, 215), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 0, 255), 1);
				Imgproc.putText(mRgba, "EV: " + ExVal, new Point (80, 215), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 0, 255), 1);
	            Imgproc.putText(mRgba, "WB: " + valWB, new Point (140, 215), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 0, 255), 1);
	    		Imgproc.putText(mRgba, "Area: " + max, new Point (250, 215), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 0, 255), 1);
		        Imgproc.putText(mRgba, "I: " + nLux, new Point ( 350, 215), Core.FONT_HERSHEY_SIMPLEX, 0.4, new Scalar(0, 0, 255), 1);

	            Mat colorLabel = mRgba.submat(2, 34, 446, 478);
	            colorLabel.setTo(mBlobColorRgba);
				mDisplay = mRgba;
				}

			}
	        return mDisplay;	
			 
	}


	public boolean onOptionsItemSelected(MenuItem item) {  
	    	if (item == mItemExposure){
	    	if(val)
				val = false;
				else
					val = true;
	    }
	    return true;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int cols = mRgba.cols();
	    int rows = mRgba.rows();
	    
	    int x = (int)(event.getX() * 0.25);
	    int y = (int)(event.getY() * 0.296296296);
	    
	    Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

	    if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

	    Rect touchedRect = new Rect();

	    touchedRect.x = (x>4) ? x-4 : 0;
	    touchedRect.y = (y>4) ? y-4 : 0;

	    touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
	    touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

	    Mat touchedRegionRgba = mRgba.submat(touchedRect);

	    Mat touchedRegionHsv = new Mat();
	    Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

	    // Calculate average color of touched region
	    int pointCount = touchedRect.width*touchedRect.height;
	    
	    mBlobColorHsv = Core.sumElems(touchedRegionHsv);
	    
	    for (int i = 0; i < mBlobColorHsv.val.length; i++)
	        mBlobColorHsv.val[i] /= pointCount;
	    
	    mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
	    
	    Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
	            ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

	    mDetector.setHsvColor(mBlobColorHsv);
	    
	    Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

	    mDetector.process(mRgba);
	    
		bIsHPressed = false;
		bIsSPressed = false;
		bIsVPressed = false;
		bIsEDPressed = false;
		bIsEWPressed = false;

	    mIsDisplayTouched = true;

	    touchedRegionRgba.release();
	    touchedRegionHsv.release();
		
		return false;
	}


	private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
	    Mat pointMatRgba = new Mat();
	    Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
	    Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

	    return new Scalar(pointMatRgba.get(0, 0));
	}

	private final SensorEventListener LightSensorListener = new SensorEventListener(){

		@Override
		  public void onAccuracyChanged(Sensor sensor, int accuracy) {
		   // TODO Auto-generated method stub
//			  nLuxAcc = accuracy;
		  }

		  @Override
		  public void onSensorChanged(SensorEvent event) {
			  nLux = event.values[0];
		  }
		    
		   };

	float xPitch;
	float yRoll;
	float zAzim;
	public class MySensorEventListener implements SensorEventListener{

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					
			        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
			        {  
			            return;  
			        }  
			        
			        xPitch	= event.values[1];
			        yRoll	= event.values[2];
			        zAzim	= event.values[0];
				}
			        
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
					
				}
				 
			 }
	}
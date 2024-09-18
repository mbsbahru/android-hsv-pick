package mbs.hsvcolorpick;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class SeekAdapter {
	public int firstVal;
	public int midVal;
	public int secondVal;
	public int progRange;
	public SeekBar seekVal1; 
	public SeekBar seekVal2; 


	public void setProgress(SeekBar seekVal1, SeekBar seekVal2, double hmin1, double hmax1, int progRange){
		this.seekVal1 = seekVal1;
		this.seekVal2 = seekVal2;
		this.firstVal = (int) hmin1;
		this.secondVal = (int) hmax1;
		this.progRange = progRange;
	}

	public int getFirstVal() {
		return firstVal;
	}
	
	public int getMidVal() {
		return midVal;
	}


	public int getSecondVal() {
		return secondVal;
	}


	public void seekProgress(){
		seekVal1.setMax(progRange);
		seekVal1.setProgress(firstVal); 
		seekVal2.setMax(progRange);
		seekVal2.setProgress(secondVal); 
		
	}
	public void seekBarChange(){

		seekVal1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override 
			public void onStopTrackingTouch(SeekBar seekBarVal) { 
				// TODO Auto-generated method stub 					
			} 
			@Override 
			public void onStartTrackingTouch(SeekBar seekBarVal) { 
				// TODO Auto-generated method stub 
			} 
			@Override 
			public void onProgressChanged(SeekBar seekBarVal, int progress,boolean fromUser) { 
				// TODO Auto-generated method stub 
				firstVal = progress;
			} 
		});
		seekVal2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override 
			public void onStopTrackingTouch(SeekBar seekBarVal) { 
				// TODO Auto-generated method stub 					
			} 
			@Override 
			public void onStartTrackingTouch(SeekBar seekBarVal) { 
				// TODO Auto-generated method stub 
			} 
			@Override 
			public void onProgressChanged(SeekBar seekBarVal, int progress,boolean fromUser) { 
				// TODO Auto-generated method stub 
				secondVal = progress;
			} 
		});
		
		
	}

}
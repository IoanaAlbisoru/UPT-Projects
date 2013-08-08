package lori.sma;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class AngleMeterActivity extends Activity {
  private SensorManager sensorManager;
  private SensorEventListener accelerometerListener;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.main);
    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    this.initAccelerometer();
  }

  private void initAccelerometer() {
    final AngleDisplay display = new AngleDisplay();
    this.accelerometerListener = new AccelerometerListener(display);
    this.sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
    final Sensor defSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    this.sensorManager.registerListener(this.accelerometerListener, defSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onResume() {
    this.sensorManager.registerListener(this.accelerometerListener,
        this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    super.onResume();
  }

  @Override
  protected void onPause() {
    this.sensorManager.unregisterListener(this.accelerometerListener);
    super.onPause();
  }

  /**
   * Class that encapsulates the behavior of the basic UI for displaying the
   * angle of deviation
   * 
   * @author Lori
   * 
   */
  class AngleDisplay {
    public final TextView displayInclination;
    public final Context applicationContext;
    private final RadioGroup direction;

    public AngleDisplay() {
      this.displayInclination = (TextView) AngleMeterActivity.this.findViewById(R.id.displayAngle);
      this.direction = (RadioGroup) AngleMeterActivity.this.findViewById(R.id.direction);
      this.direction.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        public void onCheckedChanged(final RadioGroup group, final int checkedId) {
          if (checkedId == R.id.horizontal) {
            AngleMeterActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          }
          if (checkedId == R.id.vertical) {
            AngleMeterActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          }
        }
      });
      this.applicationContext = AngleMeterActivity.this.getApplicationContext();
    }

    boolean isHorizontal() {
      return this.direction.getCheckedRadioButtonId() == R.id.horizontal;
    }

    boolean isVertical() {
      return this.direction.getCheckedRadioButtonId() == R.id.vertical;
    }
  }

}
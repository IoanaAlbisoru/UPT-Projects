package lori.sma;

import java.text.DecimalFormat;

import lori.sma.AngleMeterActivity.AngleDisplay;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class AccelerometerListener implements SensorEventListener {

  private static final int PITCH = 1;

  private static final float[] GEOMACNETIC = new float[] { 0, 1, 0 };
  private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("##.#");;

  private final AngleDisplay display;

  public AccelerometerListener(final AngleDisplay display) {
    this.display = display;
  }

  public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
  }

  public void onSensorChanged(final SensorEvent event) {
    final Sensor acceletometer = event.sensor;

    if (acceletometer.getType() != Sensor.TYPE_ACCELEROMETER) {
      return;
    }
    final float[] rotationMatrix = this.calculateRotationMatrix(event.values.clone());
    double angle;
    angle = this.calculateAngle(rotationMatrix);
    final String toDisplay = AccelerometerListener.NUMBER_FORMAT.format(angle);
    this.display.displayInclination.setText(toDisplay);
  }

  /**
   * @param event
   *          the vector representing the gravitational pull.
   * @return the rotation matrix from SensorManager.getRotationMatrix()
   */
  private float[] calculateRotationMatrix(final float[] gravity) {
    final float[] rotationMatrix = new float[9];
    final float[] inclinationMatrix = null;

    final boolean result = SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravity,
        AccelerometerListener.GEOMACNETIC);
    this.assertAndHandleError(!result, Error.ROTATION_MATRIX_ERROR);
    return rotationMatrix;
  }

  /**
   * @param rotationMatrix
   *          the matrix as computed by SensorManager.getRotationMatrix(...);
   * @return the angle in degrees, may be positive of negative
   */
  private double calculateAngle(final float[] rotationMatrix) {
    final float[] orientations = new float[3];
    SensorManager.getOrientation(rotationMatrix, orientations);
    final float angleInRadians = this.adjustAngle(orientations);
    return Math.toDegrees(angleInRadians);
  }

  /**
   * This method adjusts for horizontal/vertical particularities
   * 
   * @param orientation
   *          vector as computed by SensorManager.getOrientation
   * @return
   */
  private float adjustAngle(final float[] orientation) {

    if (this.display.isHorizontal()) {
      final float hor = -1 * orientation[AccelerometerListener.PITCH];
      return hor;
    }

    if (this.display.isVertical()) {
      final float ver = (float) (orientation[AccelerometerListener.PITCH] + Math.toRadians(90));
      return ver;
    }

    return Float.NaN;
  }

  /**
   * Semantically similar to assert, except that it displays an error message
   * 
   * @param isError
   *          true = it means that the assertion is false and we have an error;
   * @param error
   */
  private void assertAndHandleError(final boolean isError, final Error error) {
    if (isError) {
      Toast.makeText(this.display.applicationContext, error.toString(), Toast.LENGTH_SHORT).show();
      return;
    }
  }

}

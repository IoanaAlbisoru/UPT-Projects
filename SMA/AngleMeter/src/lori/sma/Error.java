package lori.sma;

enum Error {
  ROTATION_MATRIX_ERROR {
    @Override
    public String toString() {
      return "Error computing rotation matrix.";
    }
  };

  @Override
  public abstract String toString();

}
package server;

enum ServerStates {
  RUNNING {
    @Override
    public String toString() {
      return "running";
    }
  },
  STOPPED {
    @Override
    public String toString() {
      return "not running";
    }
  },
  MAINTENANCE {
    @Override
    public String toString() {
      return "maintenance";
    }
  };

  @Override
  public abstract String toString();
}
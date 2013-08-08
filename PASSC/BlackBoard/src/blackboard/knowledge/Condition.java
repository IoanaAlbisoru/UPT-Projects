package blackboard.knowledge;

public enum Condition {
  AB {
    @Override
    public String toString() {
      return "AB";
    }
  },
  ABCD {
    @Override
    public String toString() {
      return "ABCD";
    }
  },
  ABE {
    @Override
    public String toString() {
      return "ABE";
    }
  },
  ABCE {
    @Override
    public String toString() {
      return "ABCE";
    }
  },
  BCE {
    @Override
    public String toString() {
      return "BCE";
    }
  },
  ABCDE {
    @Override
    public String toString() {
      return "ABCDE";
    }
  },
  ABCDEF {
    @Override
    public String toString() {
      return "ABCDEF";
    }
  },
  CDE {
    @Override
    public String toString() {
      return "CDE";
    }
  };
}

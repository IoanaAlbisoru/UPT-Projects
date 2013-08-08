package interpreter;

public enum Keywords {
  ENTITY {
    @Override
    public String toString() {
      return "entity";
    }
  },
  EXTENDS {
    @Override
    public String toString() {
      return "extends";
    }
  },
  DEF {
    @Override
    public String toString() {
      return "def";
    }
  },
  ATTRIBUTE {
    @Override
    public String toString() {
      return "attribute";
    }
  },
  ADD_ATTR {
    @Override
    public String toString() {
      return "addAt";
    }

  },
  REM_ATTR {
    @Override
    public String toString() {
      return "remAt";
    }
  },
  ASSIGN {
    @Override
    public String toString() {
      return "=";
    }
  },
  NEW {
    @Override
    public String toString() {
      return "new";
    }
  }

}

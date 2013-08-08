package generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DataClassModel {
  public static class FieldModel {
    private final String type;
    private final String name;

    public FieldModel(final String name, final String type) {
      this.type = type;
      this.name = name;
    }

    @Override
    public String toString() {
      return this.type + " " + this.name + ";";
    }
  }

  private final Map<String, DataClassModel> nestedClasses = new HashMap<String, DataClassModel>();
  private final ArrayList<FieldModel> fields = new ArrayList<FieldModel>();
  private final String name;
  private int maxOC = -1;
  private int minOC = -1;

  public static final int UNBOUNDED = Integer.MAX_VALUE;

  public DataClassModel(final String name) {
    this.name = name;
  }

  public void addField(final String name, final String type) {
    this.fields.add(new FieldModel(name, type));
  }

  public void addNestedClass(final DataClassModel clazz) {
    this.nestedClasses.put(clazz.getName(), clazz);
  }

  public String getName() {
    return this.name;
  }

  public void setMaxOC(final int b) {
    this.maxOC = b;
  }

  public void setMinOC(final int b) {
    this.minOC = b;
  }

  public int getMinBound() {
    return this.minOC;
  }

  public int getMaxBound() {
    return this.maxOC;
  }

  private String addCollection(final DataClassModel nestedClass) {

    final String temp = "";

    if (nestedClass.getMaxBound() == DataClassModel.UNBOUNDED) {
      return String.format("public List<%s> %sList = new ArrayList<%s>();", nestedClass.getName(),
          nestedClass.getName(), nestedClass.getName());
    }

    return temp;
  }

  @Override
  public String toString() {

    String temp = "import java.util.*;\npublic class " + this.name + "{\n";

    if (this.nestedClasses.isEmpty()) {
      temp = "public class " + this.name + "{\n";
    }

    for (final DataClassModel nestedClass : this.nestedClasses.values()) {
      temp += this.addCollection(nestedClass) + "\n" + "static " + nestedClass.toString() + "\n";
    }
    for (final FieldModel field : this.fields) {
      temp += "public " + field.toString() + "\n";
    }
    temp += "}";
    return temp;
  }
}

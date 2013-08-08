package interpreter;

import interpreter.Tokenizer.Pair;
import interpreter.exceptions.BadCommand;
import interpreter.exceptions.DuplicateVariable;
import interpreter.exceptions.InvalidArgumentType;
import interpreter.exceptions.UnrecognizedSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myReflection.AttributeType;
import myReflection.Entity;
import myReflection.Instance;
import myReflection.ObjectModel;
import myReflection.Operation;
import myReflection.exceptions.DuplicateAttributeName;
import myReflection.exceptions.DuplicateEntityException;
import myReflection.exceptions.DuplicateOperation;
import myReflection.exceptions.EntityNotFound;
import myReflection.exceptions.InvalidValueType;
import myReflection.exceptions.NoSuchAttributeException;
import myReflection.expressions.ExprAdd;
import myReflection.expressions.ExprDivide;
import myReflection.expressions.ExprMultiply;
import myReflection.expressions.ExprSubtract;
import myReflection.expressions.Expression;
import myReflection.expressions.NumericExpression;
import myReflection.expressions.VariableExpression;

// import java.util.regex.*;

public class Parser {
  public static Double opResult = null;

  private static final HashMap<Keyword, Tokenizer> tokenizers = new HashMap<Keyword, Tokenizer>();
  static {
    Tokenizer token = new Tokenizer();
    token.addPattern("entity [A-Za-z_]*", Keyword.ENTITY);
    token.addPattern("[A-Za-z_0-9]* [A-Za-z_0-9]*;", Keyword.ATTRIBUTE);
    token.addPattern("def( )*\\w*( )*(\\w*[+\\*-/])*\\w*;", Keyword.DEF);
    Parser.tokenizers.put(Keyword.ENTITY, token);

    token = new Tokenizer();
    token.addPattern("entity [A-Za-z_0-9]* extends [A-Za-z_0-9]*", Keyword.EXTENDS);
    token.addPattern("[A-Za-z_0-9]* [A-Za-z_0-9]*;", Keyword.ATTRIBUTE);
    token.addPattern("def( )*\\w*( )*(\\w*[+\\*-/])*\\w*;", Keyword.DEF);
    Parser.tokenizers.put(Keyword.EXTENDS, token);

    token = new Tokenizer();
    token.addPattern("[A-Za-z0-9]*( )*=( )*new( )*[A-Za-z0-9]*;", Keyword.NEW);
    Parser.tokenizers.put(Keyword.NEW, token);

    token = new Tokenizer();
    String pattern = String.format("[A-Za-z0-9]*\\.%s( )*[A-Za-z0-9]*( )*[A-Za-z]*;", Keyword.ADD_ATTR.toString());
    token.addPattern(pattern, Keyword.ADD_ATTR);
    Parser.tokenizers.put(Keyword.ADD_ATTR, token);

    token = new Tokenizer();
    pattern = String.format("[A-Za-z0-9]*\\.%s( )*[A-Za-z0-9]*;", Keyword.REM_ATTR.toString());
    token.addPattern(pattern, Keyword.REM_ATTR);
    Parser.tokenizers.put(Keyword.REM_ATTR, token);

    token = new Tokenizer();
    pattern = String.format("[A-Za-z0-9]*\\.[A-Za-z0-9]*( )*%s( )*[A-Za-z0-9\\.]*;", Keyword.ASSIGN.toString());
    token.addPattern(pattern, Keyword.ASSIGN);
    Parser.tokenizers.put(Keyword.ASSIGN, token);

    token = new Tokenizer();
    pattern = "[A-Za-z0-9]*\\.[A-Za-z0-9]*;";
    token.addPattern(pattern, Keyword.EXECUTE);
    Parser.tokenizers.put(Keyword.EXECUTE, token);

    token = new Tokenizer();
    pattern = String.format("[A-Za-z0-9]*\\.%s( )*[A-Za-z0-9]*;", Keyword.REM_OP.toString());
    token.addPattern(pattern, Keyword.REM_OP);
    Parser.tokenizers.put(Keyword.REM_OP, token);

    // token = new Tokenizer();
    // pattern = String
    // .format(
    // "[A-Za-z0-9]*\\.%s( )*[A-Za-z0-9]( )*def( )*\\w*( )*(\\w*[+\\*-/])*\\w*;",
    // Keyword.ADD_OP.toString() );
    // // token
    // // .addPattern(
    // //
    // "[A-Za-z0-9]*\\.%s( )*[A-Za-z0-9]( )*def( )*\\w*( )*(\\w*[+\\*-/])*\\w*;",
    // // Keyword.ADD_OP );
    // token.addPattern( pattern, Keyword.ADD_OP );
    // tokenizers.put( Keyword.ADD_OP, token );
  };

  public static final HashMap<String, Instance> instances = new HashMap<String, Instance>();

  static public Tokenizer getTokenizer(final Keyword keyword) {
    return Parser.tokenizers.get(keyword);
  }

  private static final HashMap<String, Class<?>> types = new HashMap<String, Class<?>>();
  static {
    Parser.types.put("str", String.class);
    Parser.types.put("int", Integer.class);
    Parser.types.put("double", Double.class);
  }

  public void parse(final String text) throws DuplicateEntityException, DuplicateAttributeName, DuplicateOperation,
      EntityNotFound, BadCommand, InvalidArgumentType, NoSuchAttributeException, UnrecognizedSymbol, InvalidValueType,
      Exception {

    Collection<Pair<Keyword, String>> tokens = null;

    boolean matched = false;

    for (final Map.Entry<Keyword, Tokenizer> entry : Parser.tokenizers.entrySet()) {
      final Keyword typeOfInput = entry.getKey();
      final Tokenizer tokenizer = Parser.tokenizers.get(typeOfInput);
      tokens = tokenizer.tokenize(text);

      if (tokens != null) {
        typeOfInput.validate(tokens);
        matched = true;
        return;
      }
    }
    if (!matched) {
      throw new BadCommand(text);
    }

  }

  private static Expression createExpression(final ArrayList<String> tokens) {
    final HashMap<String, Integer> weight = new HashMap<String, Integer>();
    weight.put("+", 2);
    weight.put("-", 2);
    weight.put("*", 3);
    weight.put("4", 3);

    final ArrayList<Expression> expr = new ArrayList<Expression>();

    for (final String token : tokens) {
      expr.add(Parser.expressionFactory(token));
    }

    while (tokens.size() > 1) {
      int max = 0;
      int indexOfMax = 0;

      // we found the highest operator;
      for (int i = 0; i < tokens.size(); i++) {

        final Integer temp = weight.get(tokens.get(i));
        if ((null != temp) && (max < temp)) {
          max = temp;
          indexOfMax = i;
        }
      }
      tokens.remove(indexOfMax);
      tokens.remove(indexOfMax - 1);
      final Expression center = expr.remove(indexOfMax);
      final Expression left = expr.remove(indexOfMax - 1);
      final Expression right = expr.remove(indexOfMax - 1);
      center.bind(left, right);
      expr.add(indexOfMax - 1, center);
    }
    return expr.get(0);
  }

  private static ArrayList<String> tokenizeOpDefinition(String text) throws UnrecognizedSymbol {
    final ArrayList<String> coll = new ArrayList<String>();
    text = text.replace("def ", "");
    text = text.replace(";", "");
    final String name = text.substring(0, text.indexOf(' '));
    text = text.replace(name + " ", "");
    coll.add(name);

    final String[] temp = text.split("[\\*/\\-+]");

    // // TODO JUSt ADDED
    // Instance instance = instances.get( name );
    // Entity entity = instance.getEntity();
    //
    // if ( instance == null )
    // throw new UnrecognizedSymbol( name );
    //
    // for (String var : temp ) {
    // try {
    // // we use this as a dummy, if it fails it means that 'var' is a
    // variable
    // // not a constant
    // new Double( var );
    // }
    // catch (NumberFormatException e) {
    // if ( !entity.containsAttribute( var ) ) {
    // throw new UnrecognizedSymbol( var );
    // }
    // }
    // }
    final String[] operators = text.split("\\w*");
    final String[] operator = new String[temp.length - 1];
    int i = 0;
    for (final String it : operators) {
      if (!it.equals("")) {
        operator[i++] = it;
      }
    }
    for (i = 0; i < temp.length; i++) {
      coll.add(temp[i]);
      if (i < operator.length) {
        coll.add(operator[i]);
      }
    }

    return coll;
  }

  private static Expression expressionFactory(final String expr) {
    Double temp = null;
    Expression ex = null;

    try {
      temp = new Double(expr);
      ex = new NumericExpression();
      ex.bind(temp);
      return ex;
    } catch (final Exception e) {
    }

    if (expr.equals("+")) {
      return new ExprAdd();
    }

    if (expr.equals("-")) {
      return new ExprSubtract();
    }

    if (expr.equals("/")) {
      return new ExprDivide();
    }

    if (expr.equals("*")) {
      return new ExprMultiply();
    }

    ex = new VariableExpression();
    ex.bind(expr);
    return ex;
  }

  public static enum Keyword implements Validator {
    ENTITY {
      @Override
      public String toString() {
        return "entity";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        final List<AttributeType> attributes = new ArrayList<AttributeType>();

        String entityName = "";
        boolean entityWasCreated = false;

        for (final Pair<Keyword, String> pair : tokens) {
          switch (pair.getKey()) {
          case ENTITY:
            try {
              entityName = pair.getValue();
              entityName = entityName.substring(entityName.indexOf(' ') + 1);
              ObjectModel.newEntity(entityName);
              entityWasCreated = true;
            } catch (final DuplicateEntityException e) {
              throw e;
            }
            break;
          case ATTRIBUTE:
            final String lala = pair.getValue();
            final String[] attrStuff = lala.split(" ");
            final String type = attrStuff[0];
            final String name = attrStuff[1];
            attributes.add(new AttributeType(name, Parser.types.get(type)));
            break;
          case DEF:

            break;
          }
          ;// END SWITCH

        }
        if (entityWasCreated) {
          try {
            final Entity ent = ObjectModel.getEntity(entityName);

            // we add all the atributes;
            for (final AttributeType a : attributes) {
              try {
                ent.addAttribute(a);
              } catch (final DuplicateAttributeName e) {
                throw e;
              }
            }

            // NOW we create the operations. We did this so we could
            // see wether or not the fields used by the operations
            // actually
            // exists or not
            for (final Pair<Keyword, String> pair : tokens) {
              if (pair.getKey() == DEF) {

                final ArrayList<String> operators = new ArrayList<String>();
                operators.add("+");
                operators.add("-");
                operators.add("/");
                operators.add("*");

                final String text = pair.getValue();
                final ArrayList<String> tempTokens = Parser.tokenizeOpDefinition(text);
                final String operationName = tempTokens.remove(0);
                @SuppressWarnings("unchecked")
                final ArrayList<String> validationTokens = (ArrayList<String>) tempTokens.clone();
                validationTokens.removeAll(operators);
                for (final String token : validationTokens) {
                  try {
                    new Double(token);
                  } catch (final NumberFormatException e) {
                    if (!ent.containsAttribute(token)) {
                      throw new UnrecognizedSymbol(token);
                    }
                  }
                }

                final Expression opEx = Parser.createExpression(tempTokens);
                final Operation newOp = new Operation(operationName, opEx);
                ent.addOperation(newOp);
              }
            }
          } catch (final EntityNotFound e) {
            return;
          }
        }

      }
    },
    EXTENDS {
      @Override
      public String toString() {
        return "extends";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        final List<AttributeType> attributes = new ArrayList<AttributeType>();

        String entityName = "";

        boolean entityWasCreated = false;

        for (final Pair<Keyword, String> pair : tokens) {
          switch (pair.getKey()) {
          case EXTENDS:
            try {
              final String asdasd = pair.getValue();
              final String[] entityStuff = asdasd.split(" ");
              entityName = entityStuff[1];
              final String superName = entityStuff[3];
              ObjectModel.extendEntity(entityName, superName);
              entityWasCreated = true;
            } catch (final DuplicateEntityException e) {
              throw e;
            }
            break;
          case ATTRIBUTE:
            final String lala = pair.getValue();
            final String[] attrStuff = lala.split(" ");
            final String type = attrStuff[0];
            final String name = attrStuff[1];
            attributes.add(new AttributeType(name, Parser.types.get(type)));
            break;
          }
        }
        if (entityWasCreated) {
          try {
            final Entity ent = ObjectModel.getEntity(entityName);
            for (final AttributeType a : attributes) {
              try {
                ent.addAttribute(a);
              } catch (final DuplicateAttributeName e) {
                throw e;
              }
            }

            // NOW we create the operations. We did this so we could
            // see wether or not the fields used by the operations
            // actually
            // exists or not
            for (final Pair<Keyword, String> pair : tokens) {
              if (pair.getKey() == DEF) {

                final ArrayList<String> operators = new ArrayList<String>();
                operators.add("+");
                operators.add("-");
                operators.add("/");
                operators.add("*");

                final String text = pair.getValue();
                final ArrayList<String> tempTokens = Parser.tokenizeOpDefinition(text);
                final String operationName = tempTokens.remove(0);
                @SuppressWarnings("unchecked")
                final ArrayList<String> validationTokens = (ArrayList<String>) tempTokens.clone();
                validationTokens.removeAll(operators);
                for (final String token : validationTokens) {
                  try {
                    new Double(token);
                  } catch (final NumberFormatException e) {
                    if (!ent.containsAttribute(token)) {
                      throw new UnrecognizedSymbol(token);
                    }
                  }
                }

                final Expression opEx = Parser.createExpression(tempTokens);
                final Operation newOp = new Operation(operationName, opEx);
                ent.addOperation(newOp);
              }
            }
          } catch (final EntityNotFound e) {
            return;
          }

        }

      }
    },
    DEF {
      @Override
      public String toString() {
        return "def";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        // TODO Auto-generated method stub

      }
    },
    ATTRIBUTE {
      @Override
      public String toString() {
        return "attribute";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {

      }
    },
    ADD_ATTR {
      @Override
      public String toString() {
        return "addAt";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String entityName = "";
        String attributeName = "";
        String attributeType = "";
        final String command = tokens.iterator().next().getValue();
        String[] temp = command.split("\\.");
        entityName = temp[0];
        temp = temp[1].split(" ");
        attributeType = temp[1];
        attributeName = temp[2];

        final Entity entity = ObjectModel.getEntity(entityName);

        final Class<?> type = Parser.types.get(attributeType);
        if (type == null) {
          throw new InvalidArgumentType(attributeType);
        }

        entity.addAttribute(new AttributeType(attributeName, type));

      }

    },
    REM_ATTR {
      @Override
      public String toString() {
        return "remAt";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String entityName = "";
        String attributeName = "";
        final String command = tokens.iterator().next().getValue();
        String[] temp = command.split("\\.");
        entityName = temp[0];
        temp = temp[1].split(" ");
        attributeName = temp[1].trim();

        final Entity entity = ObjectModel.getEntity(entityName);
        entity.removeAttribute(attributeName);
      }
    },
    ASSIGN {
      @Override
      public String toString() {
        return "=";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String instanceName = "";
        String attributeName = "";
        String stringValue = "";
        final String command = tokens.iterator().next().getValue();
        String[] temp = command.split("\\.");

        instanceName = temp[0];
        temp = command.substring(command.indexOf(".") + 1).split("=");
        attributeName = temp[0].trim();
        stringValue = temp[1].trim();

        final Instance instance = Parser.instances.get(instanceName);
        if (null == instance) {
          throw new UnrecognizedSymbol(instanceName);
        }
        final AttributeType type = instance.getAttributeType(attributeName);
        if (type.getType().equals(String.class)) {
          instance.setAttribute(attributeName, stringValue);
        } else if (type.getType().equals(Double.class)) {
          instance.setAttribute(attributeName, new Double(stringValue));
        } else if (type.getType().equals(Integer.class)) {
          instance.setAttribute(attributeName, new Integer(stringValue));
        }

      }
    },
    NEW {
      @Override
      public String toString() {
        return "new";
      }

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String entityName = "";
        String instanceName = "";
        final String command = tokens.iterator().next().getValue();

        instanceName = command.substring(0, command.indexOf('='));
        final String[] tok = command.split(" ");
        entityName = tok[tok.length - 1];

        if (Parser.instances.containsKey(instanceName)) {
          throw new DuplicateVariable(instanceName);
        }

        final Instance intance = ObjectModel.newInstance(entityName);
        Parser.instances.put(instanceName, intance);

      }
    },
    EXECUTE {

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String instanceName = "";
        String opName = "";
        final String command = tokens.iterator().next().getValue();

        final String[] temp = command.split("\\.");
        instanceName = temp[0];
        opName = temp[1];

        final Instance intance = Parser.instances.get(instanceName);
        Parser.opResult = (Double) intance.executeOperation(opName);

      }

    },
    REM_OP {

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
        if (tokens.size() > 1) {
          throw new BadCommand(tokens.toString());
        }

        String entityName = "";
        String opName = "";
        final String command = tokens.iterator().next().getValue();
        String[] temp = command.split("\\.");
        entityName = temp[0];
        temp = temp[1].split(" ");
        opName = temp[1].trim();

        final Entity entity = ObjectModel.getEntity(entityName);
        entity.removeOperation(opName);

      }

      @Override
      public String toString() {
        return "remOp";
      }
    },
    ADD_OP {

      @Override
      public void validate(final Collection<Pair<Keyword, String>> tokens) throws Exception {
      }

      @Override
      public String toString() {
        return "addOp";
      }
    }
  };

}

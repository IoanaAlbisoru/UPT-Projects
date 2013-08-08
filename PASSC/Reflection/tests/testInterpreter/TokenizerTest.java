package testInterpreter;

import interpreter.Parser;
import interpreter.Parser.Keyword;
import interpreter.Tokenizer;
import interpreter.Tokenizer.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import myReflection.expressions.ExprAdd;
import myReflection.expressions.ExprDivide;
import myReflection.expressions.ExprMultiply;
import myReflection.expressions.ExprSubtract;
import myReflection.expressions.Expression;
import myReflection.expressions.ExpressionContext;
import myReflection.expressions.NumericExpression;
import myReflection.expressions.VariableExpression;

public class TokenizerTest extends TestCase {

  public void testEntityTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.ENTITY);

    final String text = "entity name\nstr asf;\n  double asd;\nint asc;\n  def name asd+asc+asf;";

    final ArrayList<Pair<Keyword, String>> result = new ArrayList<Pair<Keyword, String>>();
    result.add(new Pair<Keyword, String>(Keyword.ENTITY, "entity name"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "str asf"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "double asd"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "int asc"));
    result.add(new Pair<Keyword, String>(Keyword.DEF, "def name asd+asc+asf"));

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    Assert.assertTrue(temp.containsAll(result));
  }

  public void testExtendsTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.EXTENDS);

    final String text = "entity name extends super\nstr asf;\n  double asd;\nint asc;\n  def name asd+asc+asf;";

    final ArrayList<Pair<Keyword, String>> result = new ArrayList<Pair<Keyword, String>>();
    result.add(new Pair<Keyword, String>(Keyword.EXTENDS, "entity name extends super"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "str asf"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "double asd"));
    result.add(new Pair<Keyword, String>(Keyword.ATTRIBUTE, "int asc"));
    result.add(new Pair<Keyword, String>(Keyword.DEF, "def name asd+asc+asf"));

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    Assert.assertTrue(temp.containsAll(result));
  }

  public void testNewInstanceTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.NEW);
    final String text = "lala   =  new ASS;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("lala   =  new ASS"));
  }

  public void testAddAttributeTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.ADD_ATTR);
    final String text = "entity.addAt name type;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("entity.addAt name type"));
  }

  public void testRemoveAttributeTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.REM_ATTR);
    final String text = "entity.remAt name;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("entity.remAt name"));
  }

  public void testAssignTokenizer() {
    final Tokenizer token = Parser.getTokenizer(Keyword.ASSIGN);
    final String text = "instance.attr  =  value;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("instance.attr  =  value"));
  }

  public void testWithoutMapping() {
    String text = "def name asd+name+lala+erea*sfd;";

    final ArrayList<String> coll = new ArrayList<String>();
    text = text.replace("def ", "");
    text = text.replace(";", "");
    final String name = text.substring(0, text.indexOf(' '));
    text = text.replace(name + " ", "");
    coll.add(name);

    final String[] temp = text.split("[\\*/\\-+]");
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
    this.createExpr(coll);
    //
  }

  public void testExecute() {
    final Tokenizer token = Parser.getTokenizer(Keyword.EXECUTE);
    final String text = "entity.operation;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("entity.operation"));
  }

  public void testAddOP() {
    final Tokenizer token = Parser.getTokenizer(Keyword.ADD_OP);
    final String text = "entity.addOp def name a+b;";

    final List<Pair<Keyword, String>> temp = token.tokenize(text);
    final Pair<Keyword, String> a = temp.iterator().next();
    final String wtf = a.getValue();
    Assert.assertTrue(wtf.equals("entity.addAt name type"));
  }

  private Expression expressionFactory(final String expr) {
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

  private Expression createExpr(final ArrayList<String> tokens) {

    final HashMap<String, Integer> weight = new HashMap<String, Integer>();
    weight.put("+", 2);
    weight.put("-", 2);
    weight.put("*", 3);
    weight.put("4", 3);

    final ArrayList<Expression> expr = new ArrayList<Expression>();

    for (final String token : tokens) {
      expr.add(this.expressionFactory(token));
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

    // def name asd+name+lala+erea*sfd"
    final ExpressionContext context = new ExpressionContext();
    context.bind("asd", new Double(2));
    context.bind("name", new Double(2));
    context.bind("lala", new Double(2));
    context.bind("erea", new Double(5));
    context.bind("sfd", new Double(5));
    final Expression expression = expr.get(0);
    System.out.println(expression.evaluate(context));
    return null;
  }
}

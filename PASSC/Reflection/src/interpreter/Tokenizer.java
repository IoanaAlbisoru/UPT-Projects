package interpreter;

import interpreter.Parser.Keyword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
  private final HashMap<Keyword, Pattern> mappedPatterns = new HashMap<Keyword, Pattern>();
  private final HashMap<String, Pattern> patterns = new HashMap<String, Pattern>();

  public boolean addPattern(final String pattern, final Keyword keyword) {
    if ((pattern == null) || (pattern.length() == 0)) {
      return false;
    }

    if (this.mappedPatterns.containsKey(keyword)) {
      return false;
    }
    try {
      final Pattern temp = Pattern.compile(pattern);
      this.mappedPatterns.put(keyword, temp);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  public boolean addPattern(final String pattern) {
    if ((pattern == null) || (pattern.length() == 0)) {
      return false;
    }

    if (this.mappedPatterns.containsKey(pattern)) {
      return false;
    }
    try {
      final Pattern temp = Pattern.compile(pattern);
      this.patterns.put(pattern, temp);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  public List<Pair<Keyword, String>> tokenize(final String text) {
    final List<Pair<Keyword, String>> list = new ArrayList<Pair<Keyword, String>>();

    String copy = text.replace('\n', ' ');
    copy = copy.replace('\t', ' ');

    while (copy.length() > 0) {
      final int previousLength = copy.length();
      for (final Map.Entry<Keyword, Pattern> entry : this.mappedPatterns.entrySet()) {
        final Matcher matcher = entry.getValue().matcher(copy);
        while (matcher.find()) {
          final String test = matcher.group();
          list.add(new Pair<Keyword, String>(entry.getKey(), test.replace(";", "")));
          copy = copy.replace(test, "").trim();
        }
      }
      if (copy.length() == previousLength) {
        return null;
      }
    }
    return list;
  }

  public List<String> tokenizeWithOutMapping(final String text) {

    final ArrayList<String> tokens = new ArrayList<String>();

    for (int i = 0; i < text.length(); i++) {
      boolean matched = false;
      for (int j = text.length() - i; (j > 0) && !matched; j--) {
        for (final java.util.Map.Entry<String, Pattern> pair : this.patterns.entrySet()) {

          final Matcher match = pair.getValue().matcher(text.substring(i, i + j));
          if (match.find()) {
            matched = true;
            tokens.add(match.group());
            i += j - 1;
            break;
          }
        }
      }

      if (!matched) {
        return null;
      }
    }

    return tokens;
  }

  public static class Pair<K, V> {

    private final K left;
    private final V right;

    public Pair(final K left, final V right) {
      this.left = left;
      this.right = right;
    }

    public K getKey() {
      return this.left;
    }

    public V getValue() {
      return this.right;
    }

    @Override
    public int hashCode() {
      return this.left.hashCode() ^ this.right.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
      if (o == null) {
        return false;
      }
      if (!(o instanceof Pair)) {
        return false;
      }
      final Pair<?, ?> pairo = (Pair<?, ?>) o;
      return this.left.equals(pairo.getKey()) && this.right.equals(pairo.getValue());
    }

    @Override
    public String toString() {
      return this.left.toString() + "->" + this.right.toString();
    }

  }
}

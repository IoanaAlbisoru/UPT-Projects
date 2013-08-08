package interpreter;

import interpreter.Parser.Keyword;
import interpreter.Tokenizer.Pair;

import java.util.Collection;

public interface Validator {
  void validate(Collection<Pair<Keyword, String>> tokens) throws Exception;
}

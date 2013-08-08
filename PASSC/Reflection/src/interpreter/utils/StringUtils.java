package interpreter.utils;

//----------------------------------------------------------------------------------------
//	Copyright © 2007 - 2011 Tangible Software Solutions Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class is used to simulate some .NET string functions in Java.
//----------------------------------------------------------------------------------------
public final class StringUtils {
  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'IsNullOrEmpty'.
  // ------------------------------------------------------------------------------------
  public static boolean isNullOrEmpty(final String string) {
    return (string == null) || string.equals("");
  }

  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'Join' (2 parameter
  // version).
  // ------------------------------------------------------------------------------------
  public static String join(final String separator, final String[] stringarray) {
    if (stringarray == null) {
      return null;
    } else {
      return StringUtils.join(separator, stringarray, 0, stringarray.length);
    }
  }

  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'Join' (4 parameter
  // version).
  // ------------------------------------------------------------------------------------
  public static String join(final String separator, final String[] stringarray, final int startindex, final int count) {
    String result = "";

    if (stringarray == null) {
      return null;
    }

    for (int index = startindex; (index < stringarray.length) && ((index - startindex) < count); index++) {
      if ((separator != null) && (index > startindex)) {
        result += separator;
      }

      if (stringarray[index] != null) {
        result += stringarray[index];
      }
    }

    return result;
  }

  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'TrimEnd'.
  // ------------------------------------------------------------------------------------
  public static String trimEnd(final String string, final Character... charsToTrim) {
    if ((string == null) || (charsToTrim == null)) {
      return string;
    }

    int lengthToKeep = string.length();
    for (int index = string.length() - 1; index >= 0; index--) {
      boolean removeChar = false;
      if (charsToTrim.length == 0) {
        if (Character.isWhitespace(string.charAt(index))) {
          lengthToKeep = index;
          removeChar = true;
        }
      } else {
        for (final Character element : charsToTrim) {
          if (string.charAt(index) == element) {
            lengthToKeep = index;
            removeChar = true;
            break;
          }
        }
      }
      if (!removeChar) {
        break;
      }
    }
    return string.substring(0, lengthToKeep);
  }

  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'TrimStart'.
  // ------------------------------------------------------------------------------------
  public static String trimStart(final String string, final Character... charsToTrim) {
    if ((string == null) || (charsToTrim == null)) {
      return string;
    }

    int startingIndex = 0;
    for (int index = 0; index < string.length(); index++) {
      boolean removeChar = false;
      if (charsToTrim.length == 0) {
        if (Character.isWhitespace(string.charAt(index))) {
          startingIndex = index + 1;
          removeChar = true;
        }
      } else {
        for (final Character element : charsToTrim) {
          if (string.charAt(index) == element) {
            startingIndex = index + 1;
            removeChar = true;
            break;
          }
        }
      }
      if (!removeChar) {
        break;
      }
    }
    return string.substring(startingIndex);
  }

  // ------------------------------------------------------------------------------------
  // This method replaces the .NET static string method 'Trim' when arguments
  // are used.
  // ------------------------------------------------------------------------------------
  public static String trim(final String string, final Character... charsToTrim) {
    return StringUtils.trimEnd(StringUtils.trimStart(string, charsToTrim), charsToTrim);
  }

  // ------------------------------------------------------------------------------------
  // This method is used for string equality comparisons when the option
  // 'Use helper 'stringsEqual' method to handle null strings' is selected
  // (The Java String 'equals' method can't be called on a null instance).
  // ------------------------------------------------------------------------------------
  public static boolean stringsEqual(final String s1, final String s2) {
    if ((s1 == null) && (s2 == null)) {
      return true;
    } else {
      return (s1 != null) && s1.equals(s2);
    }
  }

}
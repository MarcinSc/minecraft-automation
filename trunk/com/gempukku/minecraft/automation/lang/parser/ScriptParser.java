package com.gempukku.minecraft.automation.lang.parser;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.*;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScriptParser {
  public ScriptExecutable parseScript(Reader reader) throws IllegalSyntaxException, IOException {
    BufferedReader bufferedReader = new BufferedReader(reader);

    ScriptExecutable result = new ScriptExecutable();
    List<Term> terms = parseToTerms(bufferedReader);

//        for (Term term : terms)
//            System.out.println(term.getType() + "(" + term.getLine() + "):" + term.getValue());

    TermBlock termBlockStructure = constructBlocks(terms);

//        System.out.println("Printing program structure");
//        printTerms(0, termBlockStructure);

    List<ExecutableStatement> statements = seekStatementsInBlock(termBlockStructure);
    result.setStatement(new BlockStatement(statements, false, true));

    return result;
  }

  private List<ExecutableStatement> seekStatementsInBlock(TermBlock termBlock) throws IllegalSyntaxException {
    if (termBlock.isTerm()) {
      throw new IllegalStateException("Should not get here");
    } else {
      List<ExecutableStatement> result = new LinkedList<ExecutableStatement>();
      List<TermBlock> blocks = termBlock.getTermBlocks();
      PeekingIterator<TermBlock> termBlockIter = Iterators.peekingIterator(blocks.iterator());
      while (termBlockIter.hasNext()) {
        final ExecutableStatement resultStatement = produceStatementFromIterator(termBlockIter);
        result.add(resultStatement);
        if (resultStatement.requiresSemicolon())
          consumeSemicolon(termBlockIter);
      }
      return result;
    }
  }

  private ExecutableStatement produceStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    TermBlock firstTermBlock = termIterator.peek();
    if (firstTermBlock.isTerm()) {
      Term firstTerm = firstTermBlock.getTerm();
      if (firstTerm.getType() == Term.Type.STRING) {
        // We do not allow at the moment any statements starting with constant
        throw new IllegalSyntaxException("Illegal start of statement");
      } else {
        // It's a program term
        String literal = getFirstLiteral(firstTerm.getValue());
        if (literal.equals("return")) {
          return produceReturnStatement(termIterator);
        } else if (literal.equals("var")) {
          return produceVarStatement(termIterator);
        } else if (literal.equals("function")) {
          return produceDefineFunctionStatement(termIterator);
        } else if (literal.equals("if")) {
          return produceIfStatement(termIterator);
        } else if (literal.equals("for")) {
          return produceForStatement(termIterator);
        } else if (literal.equals("while")) {
          return produceWhileStatement(termIterator);
        } else if (literal.equals("break")) {
          return produceBreakStatement(termIterator);
        } else {
          return produceExpressionFromIterator(termIterator);
        }
      }
    } else {
      return new BlockStatement(seekStatementsInBlock(firstTermBlock), true, false);
    }
  }

  private ExecutableStatement produceBreakStatement(PeekingIterator<TermBlock> termIterator) {
    consumeCharactersFromTerm(termIterator, 5);
    return new BreakStatement();
  }

  private ExecutableStatement produceWhileStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 5);

    if (!isNextTermStartingWith(termIterator, "("))
      throw new IllegalSyntaxException("( expected");
    consumeCharactersFromTerm(termIterator, 1);

    ExecutableStatement condition = produceExpressionFromIterator(termIterator);

    if (!isNextTermStartingWith(termIterator, ")"))
      throw new IllegalSyntaxException(") expected");
    consumeCharactersFromTerm(termIterator, 1);

    ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator);

    return new WhileStatement(condition, statementInLoop);
  }

  private ExecutableStatement produceForStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 3);

    if (!isNextTermStartingWith(termIterator, "("))
      throw new IllegalSyntaxException("( expected");
    consumeCharactersFromTerm(termIterator, 1);

    ExecutableStatement firstStatement = null;
    if (!isNextTermStartingWith(termIterator, ";"))
      firstStatement = produceStatementFromIterator(termIterator);
    consumeSemicolon(termIterator);

    final ExecutableStatement terminationCondition = produceExpressionFromIterator(termIterator);
    consumeSemicolon(termIterator);

    ExecutableStatement statementExecutedAfterEachLoop = null;
    if (!isNextTermStartingWith(termIterator, ")"))
      statementExecutedAfterEachLoop = produceStatementFromIterator(termIterator);

    if (!isNextTermStartingWith(termIterator, ")"))
      throw new IllegalSyntaxException(") expected");
    consumeCharactersFromTerm(termIterator, 1);

    final ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator);

    return new ForStatement(firstStatement, terminationCondition, statementExecutedAfterEachLoop, statementInLoop);
  }

  private ExecutableStatement produceIfStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 2);

    ExecutableStatement condition = produceConditionInBrackets(termIterator);

    ExecutableStatement statement = produceStatementFromGroupOrTerm(termIterator);
    IfStatement ifStatement = new IfStatement(condition, statement);

    boolean hasElse = false;

    while (!hasElse && isNextLiteral(termIterator, "else")) {
      consumeCharactersFromTerm(termIterator, 4);
      if (isNextLiteral(termIterator, "if")) {
        consumeCharactersFromTerm(termIterator, 2);
        ExecutableStatement elseIfCondition = produceConditionInBrackets(termIterator);
        ifStatement.addElseIf(elseIfCondition, produceStatementFromGroupOrTerm(termIterator));
      } else {
        ifStatement.addElse(produceStatementFromGroupOrTerm(termIterator));
        hasElse = true;
      }
    }

    return ifStatement;
  }

  private ExecutableStatement produceStatementFromGroupOrTerm(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    ExecutableStatement statement;
    final TermBlock ifExecute = peekNextTermBlockSafely(termIterator);
    if (ifExecute.isTerm()) {
      if (isNextTermStartingWithSemicolon(termIterator)) {
        consumeSemicolon(termIterator);
        return null;
      }
      statement = produceStatementFromIterator(termIterator);
      consumeSemicolon(termIterator);
    } else {
      termIterator.next();
      final List<ExecutableStatement> statements = seekStatementsInBlock(ifExecute);
      statement = new BlockStatement(statements, false, false);
    }
    return statement;
  }

  private ExecutableStatement produceConditionInBrackets(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    if (!isNextTermStartingWith(termIterator, "("))
      throw new IllegalSyntaxException("( expected");
    consumeCharactersFromTerm(termIterator, 1);

    ExecutableStatement condition = produceExpressionFromIterator(termIterator);

    if (!isNextTermStartingWith(termIterator, ")"))
      throw new IllegalSyntaxException(") expected");
    consumeCharactersFromTerm(termIterator, 1);
    return condition;
  }

  private boolean isNextLiteral(PeekingIterator<TermBlock> termIterator, String literal) throws IllegalSyntaxException {
    if (isNextTermStartingWith(termIterator, literal)) {
      final Term term = peekNextProgramTermSafely(termIterator);
      if (getFirstLiteral(term.getValue()).equals(literal))
        return true;
    }
    return false;
  }

  private ExecutableStatement produceDefineFunctionStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 8);
    Term functionDefTerm = peekNextProgramTermSafely(termIterator);

    String functionName = getFirstLiteral(functionDefTerm.getValue());
    consumeCharactersFromTerm(termIterator, functionName.length());

    if (!isNextTermStartingWith(termIterator, "("))
      throw new IllegalSyntaxException("( expected");
    consumeCharactersFromTerm(termIterator, 1);

    List<String> parameterNames = new ArrayList<String>();
    while (!isNextTermStartingWith(termIterator, ")")) {
      String parameterName = getFirstLiteral(functionDefTerm.getValue());
      consumeCharactersFromTerm(termIterator, parameterName.length());
      parameterNames.add(parameterName);
      if (isNextTermStartingWith(termIterator, ","))
        consumeCharactersFromTerm(termIterator, 1);
    }
    consumeCharactersFromTerm(termIterator, 1);

    if (!termIterator.hasNext())
      throw new IllegalSyntaxException("{ expected");
    final TermBlock functionBodyBlock = termIterator.next();
    if (functionBodyBlock.isTerm())
      throw new IllegalSyntaxException("{ expected");

    final List<ExecutableStatement> functionBody = seekStatementsInBlock(functionBodyBlock);
    return new DefineFunctionStatement(functionName, parameterNames, functionBody);
  }

  private ExecutableStatement produceVarStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 3);
    final Term variableTerm = peekNextProgramTermSafely(termIterator);
    String variableName = getFirstLiteral(variableTerm.getValue());
    consumeCharactersFromTerm(termIterator, variableName.length());

    if (isNextTermStartingWithSemicolon(termIterator))
      return new DefineStatement(variableName);

    if (!isNextTermStartingWith(termIterator, "="))
      throw new IllegalSyntaxException("Expected =");

    consumeCharactersFromTerm(termIterator, 1);

    final ExecutableStatement value = produceExpressionFromIterator(termIterator);
    return new DefineAndAssignStatement(variableName, value);
  }

  private ExecutableStatement produceReturnStatement(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    consumeCharactersFromTerm(termIterator, 6);
    if (isNextTermStartingWithSemicolon(termIterator))
      return new ReturnStatement(new ConstantStatement(new Variable(null)));
    return new ReturnStatement(produceExpressionFromIterator(termIterator));
  }

  private void consumeCharactersFromTerm(PeekingIterator<TermBlock> termIterator, int charCount) {
    final Term term = termIterator.peek().getTerm();
    String termText = term.getValue();
    String termRemainder = termText.substring(charCount).trim();
    if (termRemainder.length() > 0)
      term.setValue(termRemainder);
    else
      termIterator.next();
  }

  private void consumeSemicolon(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    Term term = peekNextProgramTermSafely(termIterator);
    String value = term.getValue();
    if (!value.startsWith(";"))
      throw new IllegalSyntaxException("; expected");
    consumeCharactersFromTerm(termIterator, 1);
  }

  private ExecutableStatement produceExpressionFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    return parseExpression(termIterator, parseNextOperationToken(termIterator), Integer.MAX_VALUE);
  }

  private ExecutableStatement parseExpression(PeekingIterator<TermBlock> termIterator, ExecutableStatement left, int maxPriority) throws IllegalSyntaxException {
    // Based on algorithm from http://en.wikipedia.org/wiki/Operator-precedence_parser on March 28, 2013
    Operator operator;
    while ((operator = peekNextOperator(termIterator)) != null &&
            operator.getPriority() <= maxPriority) {
      if (operator.isBinary()) {
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());

        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters())
          parameters = parseParameters(termIterator, operator.getParametersClosing());

        ExecutableStatement right = parseNextOperationToken(termIterator);
        Operator nextOperator;
        while ((nextOperator = peekNextOperator(termIterator)) != null &&
                (nextOperator.getPriority() < operator.getPriority() ||
                        (nextOperator.getPriority() == operator.getPriority() && !nextOperator.isLeftAssociative()))) {
          if (operator.isBinary())
            right = parseExpression(termIterator, right, nextOperator.getPriority());
          else {
            consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
            right = produceOperation(right, nextOperator, null, parseParameters(termIterator, nextOperator.getParametersClosing()));
          }
        }

        left = produceOperation(left, operator, right, parameters);
      } else {
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters())
          parameters = parseParameters(termIterator, operator.getParametersClosing());

        if (operator.isLeftAssociative())
          left = produceOperation(left, operator, null, parameters);
        else {
          ExecutableStatement operatorExpression = parseExpression(termIterator, parseNextOperationToken(termIterator), operator.getPriority());
          left = produceOperation(operatorExpression, operator, null, parameters);
        }
      }
    }

    return left;
  }

  private List<ExecutableStatement> parseParameters(PeekingIterator<TermBlock> termIterator, String parametersClosing) throws IllegalSyntaxException {
    boolean first = true;
    List<ExecutableStatement> parameters;
    parameters = new ArrayList<ExecutableStatement>();
    while (!isNextTermStartingWith(termIterator, parametersClosing)) {
      if (!first) {
        if (!isNextTermStartingWith(termIterator, ","))
          throw new IllegalSyntaxException(", expected");
        consumeCharactersFromTerm(termIterator, 1);
      }

      parameters.add(produceExpressionFromIterator(termIterator));
      first = false;
    }
    consumeCharactersFromTerm(termIterator, parametersClosing.length());
    return parameters;
  }

  private Operator peekNextOperator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    if (!termIterator.hasNext())
      return null;
    final Term term = peekNextProgramTermSafely(termIterator);
    String termValue = term.getValue();
    Operator operator = null;
    if (termValue.startsWith("=="))
      operator = Operator.EQUALS;
    else if (termValue.startsWith("!="))
      operator = Operator.NOT_EQUALS;
    else if (termValue.startsWith("="))
      operator = Operator.ASSIGNMENT;
    else if (termValue.startsWith("("))
      operator = Operator.FUNCTION_CALL;
    else if (termValue.startsWith("+"))
      operator = Operator.ADD;
    else if (termValue.startsWith("-"))
      operator = Operator.SUBTRACT;
    else if (termValue.startsWith("*"))
      operator = Operator.MULTIPLY;
    else if (termValue.startsWith("/"))
      operator = Operator.DIVIDE;
    else if (termValue.startsWith("%"))
      operator = Operator.MOD;
    else if (termValue.startsWith(">="))
      operator = Operator.GREATER_OR_EQUAL;
    else if (termValue.startsWith(">"))
      operator = Operator.GREATER;
    else if (termValue.startsWith("<="))
      operator = Operator.LESS_OR_EQUAL;
    else if (termValue.startsWith("<"))
      operator = Operator.LESS;
    else if (termValue.startsWith("."))
      operator = Operator.MEMBER_ACCESS;
    else if (termValue.startsWith("&&"))
      operator = Operator.AND;
    else if (termValue.startsWith("||"))
      operator = Operator.OR;
    else if (termValue.startsWith("!"))
      operator = Operator.NOT;
    else if (termValue.startsWith("["))
      operator = Operator.MAPPED_ACCESS;

    return operator;
  }

  private ExecutableStatement produceOperation(ExecutableStatement left, Operator operator, ExecutableStatement right, List<ExecutableStatement> parameters) throws IllegalSyntaxException {
    if (operator == Operator.ASSIGNMENT)
      return new AssignStatement(left, right);
    else if (operator == Operator.FUNCTION_CALL)
      return new FunctionCallStatement(left, parameters);
    else if (operator == Operator.ADD)
      return new AddStatement(left, right);
    else if (operator == Operator.EQUALS || operator == Operator.NOT_EQUALS)
      return new ComparisonStatement(left, operator, right);
    else if (operator == Operator.MEMBER_ACCESS)
      return new MemberAccessStatement(left, ((VariableStatement) right).getName());
    else if (operator == Operator.AND || operator == Operator.OR)
      return new LogicalOperatorStatement(left, operator, right);
    else if (operator == Operator.NOT)
      return new NegateStatement(left);
    else if (operator == Operator.MAPPED_ACCESS) {
      if (parameters.size() != 1)
        throw new IllegalSyntaxException("Expected one expression");
      return new MapAccessStatement(left, parameters.get(0));
    } else
      return new MathStatement(left, operator, right);
  }

  private ExecutableStatement parseNextOperationToken(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    ExecutableStatement result;
    TermBlock termBlock = peekNextTermBlockSafely(termIterator);
    if (termBlock.isTerm()) {
      Term term = termBlock.getTerm();
      if (term.getType() == Term.Type.STRING) {
        String value = term.getValue();
        result = new ConstantStatement(new Variable(value));
        // Consume the String
        termIterator.next();
      } else {
        // PROGRAM term
        String termValue = term.getValue();
        if (termValue.charAt(0) == '(') {
          consumeCharactersFromTerm(termIterator, 1);
          result = produceExpressionFromIterator(termIterator);
          if (!isNextTermStartingWith(termIterator, ")"))
            throw new IllegalSyntaxException(") expected");
          consumeCharactersFromTerm(termIterator, 1);
        } else if (Character.isDigit(termValue.charAt(0)) || termValue.charAt(0) == '-') {
          String numberInStr = getNumber(termValue);
          consumeCharactersFromTerm(termIterator, numberInStr.length());
          result = new ConstantStatement(new Variable(Float.parseFloat(numberInStr)));
        } else {
          if (Character.isLetter(termValue.charAt(0))) {
            String literal = getFirstLiteral(termValue);

            consumeCharactersFromTerm(termIterator, literal.length());

            if (literal.equals("true"))
              result = new ConstantStatement(new Variable(true));
            else if (literal.equals("false"))
              result = new ConstantStatement(new Variable(false));
            else if (literal.equals("null"))
              result = new ConstantStatement(new Variable(null));
            else
              result = new VariableStatement(literal);
          } else {
            // It might be operator
            result = null;
          }
        }
      }
    } else {
      // It's a map (in {})
      result = produceMapDefinitionFromBlock(termBlock);
      // Consume the block
      termIterator.next();
    }
    return result;
  }

  private ExecutableStatement produceMapDefinitionFromBlock(TermBlock termBlock) throws IllegalSyntaxException {
    MapDefineStatement mapStatement = new MapDefineStatement();
    final PeekingIterator<TermBlock> iterator = Iterators.peekingIterator(termBlock.getTermBlocks().iterator());
    boolean first = true;
    while (iterator.hasNext()) {
      if (!first) {
        if (!isNextTermStartingWith(iterator, ","))
          throw new IllegalSyntaxException(", expected");
        consumeCharactersFromTerm(iterator, 1);
      }
      final TermBlock property = iterator.peek();
      if (!property.isTerm())
        throw new IllegalSyntaxException("Property name expected");
      String propertyName;
      if (property.getTerm().getType() == Term.Type.STRING) {
        propertyName = property.getTerm().getValue();
        // Consume the string
        iterator.next();
      } else {
        propertyName = getFirstLiteral(property.getTerm().getValue());
        consumeCharactersFromTerm(iterator, propertyName.length());
      }

      if (!isNextTermStartingWith(iterator, ":"))
        throw new IllegalSyntaxException(": expected");
      consumeCharactersFromTerm(iterator, 1);

      mapStatement.addProperty(propertyName, produceExpressionFromIterator(iterator));

      first = false;
    }
    return mapStatement;
  }

  private String getNumber(String termValue) {
    StringBuilder result = new StringBuilder();
    boolean hasDot = false;
    final char[] chars = termValue.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (i == 0 && chars[i] == '-')
        result.append(chars[i]);
      else if (Character.isDigit(chars[i]))
        result.append(chars[i]);
      else if (chars[i] == '.' && !hasDot) {
        hasDot = true;
        result.append('.');
      } else
        return result.toString();
    }
    return result.toString();
  }

  private String getFirstLiteral(String text) throws IllegalSyntaxException {
    StringBuilder sb = new StringBuilder();
    char[] chars = text.toCharArray();
    if (!Character.isLetter(chars[0]))
      throw new IllegalSyntaxException("Expected expression");
    for (char c : chars) {
      if (Character.isLetterOrDigit(c))
        sb.append(c);
      else
        return sb.toString();
    }
    return sb.toString();
  }

  private Term peekNextProgramTermSafely(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    if (termIterator.hasNext()) {
      TermBlock termBlock = termIterator.peek();
      if (!termBlock.isTerm())
        throw new IllegalSyntaxException("Expression expected");

      Term term = termBlock.getTerm();
      if (term.getType() != Term.Type.PROGRAM)
        throw new IllegalSyntaxException("Expression expected");
      return term;
    } else
      throw new IllegalSyntaxException("Expression expected");
  }

  private TermBlock peekNextTermBlockSafely(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
    if (termIterator.hasNext()) {
      return termIterator.peek();
    } else
      throw new IllegalSyntaxException("Expression expected");
  }

  private boolean isNextTermStartingWithSemicolon(PeekingIterator<TermBlock> termIterator) {
    return isNextTermStartingWith(termIterator, ";");
  }

  private boolean isNextTermStartingWith(PeekingIterator<TermBlock> termIterator, String text) {
    if (termIterator.hasNext()) {
      final TermBlock termBlock = termIterator.peek();
      if (termBlock.isTerm()) {
        final Term term = termBlock.getTerm();
        if (term.getType() == Term.Type.PROGRAM) {
          return term.getValue().startsWith(text);
        }
      }
    }
    return false;
  }

  private void printTerms(int indent, TermBlock block) {
    if (block.isTerm()) {
      for (int i = 0; i < indent; i++)
        System.out.print("  ");
      Term term = block.getTerm();
      System.out.println(term.getType() + "(" + term.getLine() + "):" + term.getValue());
    } else {
      List<TermBlock> childBlocks = block.getTermBlocks();
      for (TermBlock childBlock : childBlocks) {
        printTerms(indent + 1, childBlock);
      }
    }
  }

  private TermBlock constructBlocks(List<Term> terms) throws IllegalSyntaxException {
    LinkedList<TermBlock> termBlocksStack = new LinkedList<TermBlock>();

    TermBlock result = new TermBlock();
    TermBlock currentBlock = result;

    for (Term term : terms) {
      if (term.getType() == Term.Type.PROGRAM) {
        String value = term.getValue().trim();

        while (value.length() > 0) {
          int open = value.indexOf('{');
          int close = value.indexOf('}');
          if (open > -1 && (close < 0 || open < close)) {
            String before = value.substring(0, open);
            String after = value.substring(open + 1);
            if (before.length() > 0)
              appendProgramTerm(currentBlock, before.trim(), term.getLine());
            termBlocksStack.add(currentBlock);
            TermBlock childBlock = new TermBlock();
            currentBlock.addTermBlock(childBlock);
            currentBlock = childBlock;
            value = after;
          } else if (close > -1 && (open < 0 || close < open)) {
            String before = value.substring(0, close);
            String after = value.substring(close + 1);
            if (before.length() > 0)
              appendProgramTerm(currentBlock, before.trim(), term.getLine());
            if (termBlocksStack.size() == 0)
              throw new IllegalSyntaxException("Found closing bracket for no block");
            currentBlock = termBlocksStack.removeLast();
            value = after;
          } else {
            appendProgramTerm(currentBlock, value.trim(), term.getLine());
            value = "";
          }
        }
      } else if (term.getType() == Term.Type.STRING) {
        currentBlock.addTermBlock(term);
      }
    }

    if (termBlocksStack.size() > 0)
      throw new IllegalStateException("Unclosed bracket - }");

    return result;
  }

  private void appendProgramTerm(TermBlock currentBlock, String text, int line) {
    currentBlock.addTermBlock(new Term(Term.Type.PROGRAM, text, line));
  }

  private List<Term> parseToTerms(BufferedReader bufferedReader) throws IOException, IllegalSyntaxException {
    int lineNumber = 1;
    List<Term> terms = new ArrayList<Term>();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      termAndValidateLine(line, lineNumber, terms);
      lineNumber++;
    }
    return terms;
  }

  private void termAndValidateLine(String line, int lineNumber, List<Term> resultTerms) throws IllegalSyntaxException {
    // Remove all not needed white-space characters
    line = line.trim();
    Term.Type type = Term.Type.PROGRAM;
    StringBuilder valueSoFar = new StringBuilder();
    char[] lineChars = line.toCharArray();
    for (int i = 0; i < lineChars.length; i++) {
      if (type == Term.Type.PROGRAM) {
        if (lineChars[i] == '\"') {
          if (valueSoFar.length() > 0)
            resultTerms.add(new Term(type, valueSoFar.toString().trim(), lineNumber));
          type = Term.Type.STRING;
          valueSoFar = new StringBuilder();
        } else if (lineChars[i] == '/' && i + 1 < lineChars.length && lineChars[i + 1] == '/') {
          if (valueSoFar.length() > 0)
            resultTerms.add(new Term(type, valueSoFar.toString().trim(), lineNumber));
          type = Term.Type.COMMENT;
          valueSoFar = new StringBuilder();
        } else {
          valueSoFar.append(lineChars[i]);
        }
      } else if (type == Term.Type.STRING) {
        if (lineChars[i] == '\"') {
          resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber));
          type = Term.Type.PROGRAM;
          valueSoFar = new StringBuilder();
        } else if (lineChars[i] == '\\') {
          i++;
          if (i < lineChars.length) {
            if (lineChars[i] == '\"' || lineChars[i] == '\\')
              valueSoFar.append(lineChars[i]);
            else
              throw new IllegalSyntaxException("Illegal escape sequence in String \\" + lineChars[i]);
          } else {
            throw new IllegalSyntaxException("Unfinished escape sequence in String");
          }
        } else {
          valueSoFar.append(lineChars[i]);
        }
      } else {
        valueSoFar.append(lineChars[i]);
      }
    }

    if (valueSoFar.length() > 0)
      resultTerms.add(new Term(type, (type == Term.Type.PROGRAM) ? valueSoFar.toString().trim() : valueSoFar.toString(), lineNumber));
  }
}

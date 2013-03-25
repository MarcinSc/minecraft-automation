package com.gempukku.minecraft.automation.lang.parser;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.ScriptExecutable;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.lang.statement.*;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
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

        System.out.println("Printing program structure");
        printTerms(0, termBlockStructure);

        List<ExecutableStatement> statements = seekStatementsInBlock(termBlockStructure);
        result.setStatement(new BlockStatement(statements));

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
                result.add(produceStatementFromIterator(termBlockIter));
                consumeSemicolonIfThere(termBlockIter);
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
                String termText = firstTerm.getValue();
                String literal = getFirstLiteral(termText);
                if (literal.equals("function")) {
                    appendFunctionFromIterator(termIterator);
                    return null;
                } else if (literal.equals("for")) {
                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 3);
                    return produceForStatementFromIterator(termIterator);
                } else if (literal.equals("if")) {
                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 2);
                    return produceIfStatementFromIterator(termIterator);
                } else if (literal.equals("while")) {
                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 5);
                    return produceWhileStatementFromIterator(termIterator);
//                } else if (literal.equals("do")) {
//                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 2);
//                    return produceDoStatementFromIterator(termIterator);
                } else if (literal.equals("return")) {
                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 6);
                    return produceReturnStatementFromIterator(termIterator);
                } else if (literal.equals("var")) {
                    consumeCharactersFromTerm(termIterator, firstTerm, termText, 3);
                    return produceAssignmentStatementFromIterator(termIterator);
                } else
                    return produceOtherStatementFromIterator(termIterator);
            }
        } else {
            return new BlockStatement(seekStatementsInBlock(firstTermBlock));
        }
    }

    private void consumeCharactersFromTerm(PeekingIterator<TermBlock> termIterator, Term term, String termText, int charCount) {
        String termRemainder = termText.substring(charCount).trim();
        if (termRemainder.length() > 0)
            term.setValue(termRemainder);
        else
            termIterator.next();
    }

    private ExecutableStatement produceAssignmentStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        String variableName = consumeLiteral(termIterator);
        Term term = peekNextProgramTermSafely(termIterator);
        String value = term.getValue();
        if (value.startsWith(";")) {
            return new DefineStatement(variableName);
        }
        if (!value.startsWith("="))
            throw new IllegalSyntaxException("Invalid assignment");

        consumeCharactersFromTerm(termIterator, term, value, 1);

        ExecutableStatement executableStatement = produceValueReturningStatementFromIterator(termIterator);

        return new AssignStatement(true, variableName, executableStatement);
    }

    private void consumeSemicolonIfThere(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (termIterator.hasNext()) {
            TermBlock nextTerm = termIterator.peek();
            if (nextTerm.isTerm()) {
                Term term = nextTerm.getTerm();
                if (term.getType() == Term.Type.PROGRAM) {
                    consumeSemicolon(termIterator);
                }
            }
        }
    }

    private void consumeSemicolon(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term term = peekNextProgramTermSafely(termIterator);
        String value = term.getValue();
        if (!value.startsWith(";"))
            throw new IllegalSyntaxException("; expected");
        consumeCharactersFromTerm(termIterator, term, value, 1);
    }

    private ExecutableStatement produceValueReturningStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        TermBlock termBlock = peekNextTermBlockSafely(termIterator);
        if (termBlock.isTerm()) {
            Term term = termBlock.getTerm();
            if (term.getType() == Term.Type.STRING) {
                String value = term.getValue();
                ExecutableStatement stringStatement = new ConstantStatement(new Variable(value));
                // Consume the String
                termIterator.next();

                return wrapInPossibleMethodCalls(stringStatement, termIterator);
            } else {
                // PROGRAM term
                ExecutableStatement result = null;
                String value = term.getValue();
                if (value.startsWith(";"))
                    throw new IllegalSyntaxException("Statement expected");

                if (value.startsWith("[")) {
                    consumeCharactersFromTerm(termIterator, term, value, 1);
                    result = produceArrayStatementFromIterator(termIterator);
                } else {
                    String functionOrVariableName = getFirstLiteral(value);
                    consumeCharactersFromTerm(termIterator, term, value, functionOrVariableName.length());
                    if (functionOrVariableName.equals("true"))
                        return new ConstantStatement(new Variable(true));
                    else if (functionOrVariableName.equals("false"))
                        return new ConstantStatement(new Variable(false));
                    else if (functionOrVariableName.equals("null"))
                        return new ConstantStatement(new Variable(null));
                    Term nextTerm = peekNextProgramTermSafely(termIterator);
                    boolean first = true;
                    String nextTermValue = nextTerm.getValue();
                    if (nextTermValue.startsWith("=")) {
                        consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);
                        return new AssignStatement(false, functionOrVariableName, produceValueReturningStatementFromIterator(termIterator));
                    }
                    while (true) {
                        if (first)
                            result = new VariableStatement(functionOrVariableName);
                        if (nextTermValue.startsWith("(")) {
                            if (!first)
                                throw new IllegalArgumentException("Function call not expected");
                            consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);
                            result = produceFunctionCallFromIterator(functionOrVariableName, termIterator);
                        } else if (nextTermValue.startsWith(";")) {
                            return result;
                        } else {
                            if (nextTermValue.startsWith(".")) {
                                consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);
                                result = produceMethodCallFromIterator(result, termIterator);
                            } else {
                                return result;
                            }
                        }
                        nextTerm = peekNextProgramTermSafely(termIterator);
                        nextTermValue = nextTerm.getValue();
                        first = false;
                    }
                }
                return result;
            }
        } else {
            return produceMapStatementFromBlock(termBlock);
        }

    }

    private ExecutableStatement produceFunctionCallFromIterator(String functionName, PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        List<ExecutableStatement> parameters = produceParametersFromIterator(termIterator);
        return new CallFunctionStatement(functionName, parameters);
    }

    private ExecutableStatement produceArrayStatementFromIterator(PeekingIterator<TermBlock> termIterator) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private ExecutableStatement wrapInPossibleMethodCalls(ExecutableStatement statement, PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term nextTerm = peekNextProgramTermSafely(termIterator);
        String nextTermValue = nextTerm.getValue();
        if (nextTermValue.startsWith(";"))
            return statement;
        else if (nextTermValue.startsWith(".")) {
            ExecutableStatement current = statement;
            String followingTermValue;
            do {
                consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);

                current = produceMethodCallFromIterator(current, termIterator);
                Term followingTerm = peekNextProgramTermSafely(termIterator);
                followingTermValue = followingTerm.getValue();
            } while (followingTermValue.startsWith("."));
            return current;
        } else
            throw new IllegalSyntaxException("Statement expected");
    }

    private ExecutableStatement produceMethodCallFromIterator(ExecutableStatement object, PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        String methodName = consumeLiteral(termIterator);
        Term term = peekNextProgramTermSafely(termIterator);
        String termValue = term.getValue();
        if (!termValue.startsWith("("))
            throw new IllegalSyntaxException("Method parameters expected");
        consumeCharactersFromTerm(termIterator, term, termValue, 1);

        List<ExecutableStatement> parameters = produceParametersFromIterator(termIterator);
        return new MethodCallStatement(object, methodName, parameters);
    }

    private List<ExecutableStatement> produceParametersFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        List<ExecutableStatement> parameters = new ArrayList<ExecutableStatement>();
        while (true) {
            parameters.add(produceValueReturningStatementFromIterator(termIterator));
            Term nextTerm = peekNextProgramTermSafely(termIterator);
            String nextTermValue = nextTerm.getValue();

            if (nextTermValue.startsWith(",")) {
                consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);
            } else if (nextTermValue.startsWith(")")) {
                consumeCharactersFromTerm(termIterator, nextTerm, nextTermValue, 1);
                break;
            } else {
                throw new IllegalSyntaxException(", or ) expected in parameters");
            }
        }
        return parameters;
    }

    private String consumeLiteral(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term term = peekNextProgramTermSafely(termIterator);
        String value = term.getValue();
        String literal = getFirstLiteral(value);
        consumeCharactersFromTerm(termIterator, term, value, literal.length());
        return literal;
    }

    private ExecutableStatement produceForStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term term = peekNextProgramTermSafely(termIterator);
        String termValue = term.getValue();
        if (!termValue.startsWith("("))
            throw new IllegalSyntaxException("Invalid for statement syntax");

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private ExecutableStatement produceMapStatementFromBlock(TermBlock termBlock) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private ExecutableStatement produceOtherStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        return produceValueReturningStatementFromIterator(termIterator);
    }

    private ExecutableStatement produceReturnStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (!termIterator.hasNext())
            throw new IllegalSyntaxException("Expected expression");
        final TermBlock term = termIterator.peek();
        if (term.isTerm() && term.getTerm().getType() == Term.Type.PROGRAM && term.getTerm().getValue().startsWith(";"))
            return new ReturnStatement(new ConstantStatement(new Variable(null)));

        return new ReturnStatement(produceValueReturningStatementFromIterator(termIterator));
    }

    private void appendFunctionFromIterator(PeekingIterator<TermBlock> termIterator) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private ExecutableStatement produceWhileStatementFromIterator(PeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        final Term term = peekNextProgramTermSafely(termIterator);
        final String termValue = term.getValue();
        if (!termValue.startsWith("("))
            throw new IllegalSyntaxException("( expected");

        consumeCharactersFromTerm(termIterator, term, termValue, 1);
        ExecutableStatement condition = produceValueReturningStatementFromIterator(termIterator);

        final Term afterCondition = peekNextProgramTermSafely(termIterator);
        String afterConditionValue = afterCondition.getValue();
        if (!afterConditionValue.startsWith(")"))
            throw new IllegalSyntaxException(") expected");

        consumeCharactersFromTerm(termIterator, afterCondition, afterConditionValue, 1);

        if (!termIterator.hasNext())
            throw new IllegalSyntaxException("Expression expected");
        final TermBlock statementsTerm = termIterator.peek();

        if (!statementsTerm.isTerm()) {
            final List<ExecutableStatement> executableStatements = seekStatementsInBlock(statementsTerm);
            // Consume the block
            termIterator.next();
            return new WhileStatement(condition, executableStatements);
        } else {
            final ExecutableStatement executableStatement = produceStatementFromIterator(termIterator);
            return new WhileStatement(condition, Collections.singletonList(executableStatement));
        }
    }

    private ExecutableStatement produceIfStatementFromIterator(PeekingIterator<TermBlock> termIterator) {
        return null;  //To change body of created methods use File | Settings | File Templates.
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
                        appendProgramTerm(currentBlock, term.getValue().trim(), term.getLine());
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

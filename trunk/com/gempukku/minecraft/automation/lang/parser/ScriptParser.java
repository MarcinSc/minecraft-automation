package com.gempukku.minecraft.automation.lang.parser;

import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.ScriptExecutable;

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
        List<Statement> statements = seekStatementsInBlock(termBlockStructure);

        System.out.println("Printing program structure");
        printTerms(0, termBlockStructure);

        return result;
    }

    private List<Statement> seekStatementsInBlock(TermBlock termBlock) {
        if (termBlock.isTerm()) {
            throw new IllegalStateException("Should not get here");
        } else {
            List<Statement> result = new LinkedList<Statement>();
            List<TermBlock> blocks = termBlock.getTermBlocks();
            for (TermBlock block : blocks) {
                if (block.isTerm()) {

                } else {
                    
                }
            }
            return result;
        }
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
                        String before = term.getValue().substring(0, open);
                        String after = term.getValue().substring(open + 1);
                        if (before.length() > 0)
                            appendProgramTerm(currentBlock, before.trim(), term.getLine());
                        termBlocksStack.add(currentBlock);
                        TermBlock childBlock = new TermBlock();
                        currentBlock.addTermBlock(childBlock);
                        currentBlock = childBlock;
                        value = after;
                    } else if (close > -1 && (open < 0 || close < open)) {
                        String before = term.getValue().substring(0, close);
                        String after = term.getValue().substring(close + 1);
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
            } else {
                currentBlock.addTermBlock(term);
            }
        }

        if (termBlocksStack.size() > 0)
            throw new IllegalStateException("Unclosed bracket - }");

        return result;
    }

    private void appendProgramTerm(TermBlock currentBlock, String text, int line) {
        while (text.length() > 0) {
            int semicolon = text.indexOf(';');
            if (semicolon > -1) {
                String before = text.substring(0, semicolon + 1);
                currentBlock.addTermBlock(new Term(Term.Type.PROGRAM, before, line));
                text = text.substring(semicolon + 1).trim();
            } else {
                currentBlock.addTermBlock(new Term(Term.Type.PROGRAM, text, line));
                text = "";
            }
        }
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

    private void seekStatement(List<Term> remainingTerms, BufferedReader bufferedReader, ScriptExecutable result) throws IllegalSyntaxException, IOException {

    }
}

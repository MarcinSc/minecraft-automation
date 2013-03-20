package com.gempukku.minecraft.automation.lang.parser;

import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.ScriptExecutable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {
    public ScriptExecutable parseScript(Reader reader) throws IllegalSyntaxException, IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);

        ScriptExecutable result = new ScriptExecutable();
        int lineNumber = 1;
        List<Term> terms = new ArrayList<Term>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            termAndValidateLine(line, lineNumber, terms);
            lineNumber++;
        }

        for (Term term : terms)
            System.out.println(term.getType() + "(" + term.getLine() + "):" + term.getValue());

        return result;
    }

    private void termAndValidateLine(String line, int lineNumber, List<Term> resultTerms) throws IllegalSyntaxException {
        // Remove all not needed white-space characters
        line = line.trim();
        Term.Type type = Term.Type.PROGRAM;
        StringBuilder valueSoFar = new StringBuilder();
        char[] lineChars = line.toCharArray();
        for (int i = 0; i < lineChars.length; i++) {
            if (type == Term.Type.PROGRAM) {
                if (lineChars[i] == '\"' ) {
                    if (valueSoFar.length() > 0)
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber));
                    type = Term.Type.STRING;
                    valueSoFar = new StringBuilder();
                } else if (lineChars[i] == '/' && i + 1 < lineChars.length && lineChars[i + 1] == '/' ) {
                    if (valueSoFar.length() > 0)
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber));
                    type = Term.Type.COMMENT;
                    valueSoFar = new StringBuilder();
                } else {
                    valueSoFar.append(lineChars[i]);
                }
            } else if (type == Term.Type.STRING) {
                if (lineChars[i] == '\"' ) {
                    resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber));
                    type = Term.Type.PROGRAM;
                    valueSoFar = new StringBuilder();
                } else if (lineChars[i] == '\\' ) {
                    i++;
                    if (i < lineChars.length) {
                        if (lineChars[i] == '\"' || lineChars[i] == '\\' )
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
            resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber));
    }

    private void seekStatement(List<Term> remainingTerms, BufferedReader bufferedReader, ScriptExecutable result) throws IllegalSyntaxException, IOException {

    }
}

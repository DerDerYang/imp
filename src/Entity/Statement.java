package Entity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statement {
    /*
    Sequence = 0
    If = 1
    While = 2
    Wait = 3
     */
    private int type;
    private String seqBody;
    private String condition;
    private ArrayList<Statement> ifBody;
    private ArrayList<Statement> elseBody;
    private ArrayList<Statement> whileBody;


    public Statement() {
        this.type = 0;
        this.seqBody = "";
        this.condition = "";
        this.ifBody = new ArrayList<>();
        this.elseBody = new ArrayList<>();
        this.whileBody = new ArrayList<>();
    }

    public ArrayList<Statement> parseStatement(final String input, ArrayList<Statement> statements) {
        int s = 0;
        int t = 0;
        while (s < input.length()) {
            Pattern pattern = Pattern.compile("if|while");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                t = input.length();
                ArrayList<Statement> p = parseSequence(input.substring(s, t - s));
                statements.addAll(p);
                s = t;
            } else {
                int position = matcher.start();
                if (position > s)
                    statements.addAll(parseSequence(input.substring(s, position - s)));
                s = position;
                if (input.charAt(position) == 'i') {
                    t = input.indexOf("endif");
                    t += 6;
                    statements.add(parseIf(input.substring(s, t - s)));
                } else {
                    t = input.indexOf("endwhile");
                    t += 9;
                    statements.add(parseWhile(input.substring(s, t - s)));
                }
                s = t;
            }
        }
        return statements;
    }

    public ArrayList<Statement> parseSequence(final String input) {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        String inputTrimmed = input.trim();
        String[] strings = inputTrimmed.split(";");
        for (int i = 0; i < strings.length; i++) {
            Statement s = new Statement();
            strings[i] = strings[i].trim();
            if (strings[i].contains("wait")) {
                s = parseWait(strings[i]);
            } else {
                s.type = 0;
                s.seqBody = strings[i];
            }
            statements.add(s);
        }
        return statements;
    }

    public Statement parseIf(final String input) {
        Statement sm = new Statement();
        String inputNew = input;
        inputNew = inputNew.replace("\n", "");
        String condition, ifBody, elseBody;
        if (input.contains("else")) {
            Pattern pattern = Pattern.compile("if(.*)then(.*)else(.*)endif;");
            Matcher matcher = pattern.matcher(inputNew);
            matcher.find();
            condition = matcher.group(1).trim();
            ifBody = matcher.group(2).trim();
            elseBody = matcher.group(3).trim();
            parseStatement(ifBody, sm.ifBody);
            parseStatement(elseBody, sm.elseBody);
        } else {
            Pattern pattern = Pattern.compile("if(.*)then(.*)endif;");
            Matcher matcher = pattern.matcher(inputNew);
            matcher.find();
            condition = matcher.group(1).trim();
            ifBody = matcher.group(2).trim();
            parseStatement(ifBody, sm.ifBody);
        }
        sm.type = 1;
        sm.condition = condition;
        return sm;
    }


    public Statement parseWhile(final String input) {
        String inputNew = input;
        inputNew =  inputNew.replace("\n","");
        Statement sm = new Statement();
        String condition,body;
        Pattern pattern = Pattern.compile("while(.*)do(.*)end");
        Matcher matcher = pattern.matcher(inputNew);
        matcher.find();
        condition = matcher.group(1).trim();
        body = matcher.group(2).trim();
        sm.type = 2;
        sm.condition = condition;
        parseStatement(body,sm.whileBody);
        return sm;
    }

    public Statement parseWait(final String input) {
        return new Statement();
    }
}

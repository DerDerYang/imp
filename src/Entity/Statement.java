package Entity;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private String label;
    private ArrayList<Statement> ifBody;
    private ArrayList<Statement> elseBody;
    private ArrayList<Statement> whileBody;
    ArrayList<Variable> vars;

    public Statement() {
        this.type = 0;
        this.seqBody = "";
        this.condition = "";
        this.label = "";
        this.ifBody = new ArrayList<>();
        this.elseBody = new ArrayList<>();
        this.whileBody = new ArrayList<>();
        this.vars = new ArrayList<Variable>();
    }

    public void reversedCondition() {
        if (condition.contains("not")) {
            condition = condition.replace("not", "");
            condition = condition.trim();
        } else {
            condition = "not " + condition;
        }
    }

    void labelStatements(final char prefix, Integer index, ArrayList<Statement> sms) {
        if (sms.isEmpty()) {
            return;
        }
        for (Statement s : sms) {
            s.label = prefix + String.valueOf(index);
            if (s.type == 1) {
                labelStatements(prefix, index, s.ifBody);
                labelStatements(prefix, index, s.elseBody);
            } else if (s.type == 2) {
                labelStatements(prefix, index, s.whileBody);
            }
        }
    }

    public void labelStatements(ArrayList<ArrayList<Statement>> smss) {
        char prefix = 'A';
        for (ArrayList<Statement> sms : smss) {
            int index = 0;
            labelStatements(prefix, index, sms);
            prefix++;
        }
    }

    public void outputLabel(ArrayList<ArrayList<Statement>> statements) {
        System.out.println("Labeled function:\n");
        for (ArrayList<Statement> s : statements) {
            ArrayList<String> list = new ArrayList<>();
            statementToList(s, list);
            //添加一个结束标签
            String prefix = list.get(0).substring(0, 1);
            list.add(prefix + "E:");
            System.out.println(String.join("\n", list));
            System.out.println("\n\n");
        }
    }

    void statementToList(ArrayList<Statement> sms, ArrayList<String> list) {
        statementToList(sms, list, "");
    }

    void statementToList(ArrayList<Statement> sms, ArrayList<String> list, String space) {
        if (sms.isEmpty())
            return;
        String SpaceNew = space + "    ";
        ArrayList<Statement> ls;
        for (Statement s : sms) {
            if (s.type == 0) {
                list.add(s.label + ": " + space + s.seqBody + ';');
            } else if (s.type == 3) {
                list.add(s.label + ": " + space + "wait (" + s.condition + ");");
            } else if (s.type == 1) {
                list.add(s.label + ": " + space + "if " + s.condition + " then");
                statementToList(s.ifBody, list, SpaceNew);
                list.add(space + "   else");
                statementToList(s.elseBody, list, SpaceNew);
                list.add(space + "   endif");
            } else if (s.type == 2) {
                list.add(s.label + ": " + space + "while " + s.condition + " do");
                statementToList(s.whileBody, list, SpaceNew);
                list.add(space + "   endwhile;");
            }
        }
    }

    public void parseStatement(final String input, ArrayList<Statement> statements) {
        int s = 0;
        int t = 0;
        while (s < input.length()) {
            Pattern pattern = Pattern.compile("if|while");
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                t = input.length();
                ArrayList<Statement> p = parseSequence(input.substring(s, t));
                statements.addAll(p);
                s = t;
            } else {
                int position = matcher.start();
                if (position > s)
                    statements.addAll(parseSequence(input.substring(s, position)));
                s = position;
                if (input.charAt(position) == 'i') {
                    t = input.indexOf("endif");
                    t += 6;
                    String subs = input.substring(s, t);
                    statements.add(parseIf(subs));
                } else {
                    t = input.indexOf("endwhile");
                    t += 9;
                    statements.add(parseWhile(input.substring(s, t)));
                }
                s = t;
            }
        }
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
        inputNew = inputNew.replace("\n", "");
        Statement sm = new Statement();
        String condition, body;
        Pattern pattern = Pattern.compile("while(.*)do(.*)end");
        Matcher matcher = pattern.matcher(inputNew);
        matcher.find();
        condition = matcher.group(1).trim();
        body = matcher.group(2).trim();
        sm.type = 2;
        sm.condition = condition;
        parseStatement(body, sm.whileBody);
        return sm;
    }

    public Statement parseWait(final String input) {
        Statement sm = new Statement();
        String condition;
        Pattern pattern = Pattern.compile("wait\\((.*)\\)");
        Matcher matcher = pattern.matcher(input);
        matcher.find();
        condition = matcher.group(1).trim();
        sm.type = 3;
        sm.condition = condition;
        return sm;
    }

    public ArrayList<String> parseCoProcesses(final String text) {
        ArrayList<String> processes = new ArrayList<String>();
        ArrayList<String> processTags = new ArrayList<String>();  //代码段标签
        Pattern pattern = Pattern.compile("cobegin(.*)coend");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String processTmp = matcher.group(1).trim();
            String[] temp = processTmp.split("\\|{2}");
            Collections.addAll(processTags, temp);
        }

        //如果没有并行程序，则整个输入就是一个单线程执行的程序
        if (processTags.isEmpty()) {
            processes.add(text);
            return processes;
        }

        //如果有并行程序，则解析出各个并行程序段
        for (String s : processTags) {
            pattern = Pattern.compile(s + "::([^:]+)");         //从::开始匹配，去除所有:
            matcher = pattern.matcher(text);
            if (matcher.find()) {
                String split = matcher.group(1).trim();
                split = split.substring(0, split.lastIndexOf(';') + 1);
                split = split.trim();
                processes.add(split);
            }
        }
        return processes;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSeqBody() {
        return seqBody;
    }

    public void setSeqBody(String seqBody) {
        this.seqBody = seqBody;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Statement> getIfBody() {
        return ifBody;
    }

    public void setIfBody(ArrayList<Statement> ifBody) {
        this.ifBody = ifBody;
    }

    public ArrayList<Statement> getElseBody() {
        return elseBody;
    }

    public void setElseBody(ArrayList<Statement> elseBody) {
        this.elseBody = elseBody;
    }

    public ArrayList<Statement> getWhileBody() {
        return whileBody;
    }

    public void setWhileBody(ArrayList<Statement> whileBody) {
        this.whileBody = whileBody;
    }

    public ArrayList<Variable> getVars() {
        return vars;
    }

    public void setVars(ArrayList<Variable> vars) {
        this.vars = vars;
    }
}

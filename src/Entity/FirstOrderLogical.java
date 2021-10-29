package Entity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstOrderLogical {
    private String preLabel;
    private String postLabel;
    private String condition;
    private String opr;
    private ArrayList<Variable> vars;
    public Integer ModValue = 3;

    public FirstOrderLogical() {
        this.preLabel = "";
        this.postLabel = "";
        this.condition = "";
        this.opr = "";
        this.vars = new ArrayList<>();
    }

    public FirstOrderLogical(String preLabel, String postLabel, String condition, String opr, ArrayList<Variable> vars) {
        this.preLabel = preLabel;
        this.postLabel = postLabel;
        this.condition = condition;
        this.opr = opr;
        this.vars = vars;
    }

    public static String findAssignVariable(String statement) {
        Pattern pattern = Pattern.compile("(\\w*)\\s*=\\s*");
        Matcher matcher = pattern.matcher(statement);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public String toString() {
        if (!condition.isEmpty()) {
            return "pc=" + preLabel + " and pc'=" + postLabel + " and (" + condition + ") and SAME(V)";
        } else {
            String tmp = "pc=" + preLabel + " and pc'=" + postLabel + " and (" + opr + ")";
            String var = findAssignVariable(opr);
            if (var.isEmpty()) {
                tmp += " and SAME(V)";
            } else {
                tmp += " and SAME(V\\{" + var + "})";
            }
            return tmp;
        }
    }

    public String valueToString() {
        StringBuilder tmp = new StringBuilder();
        for (int v = 0; v <= vars.size(); v++) {
            if (tmp.length() > 0) {
                tmp.append(",");
            }
            tmp.append(vars.get(v).getName()).append("=").append(vars.get(v).getValue());
        }
        return tmp.toString();
    }

    public Tuple<Character, Integer> assign() {
        if (opr.isEmpty() || !opr.contains("=")) {
            return new Tuple<>();
        }
        Pattern pattern = Pattern.compile("(\\w)\\s*=\\s*(\\w)\\s*([\\*+-])\\s*(\\w)");
        Matcher matcher = pattern.matcher(opr);
        if (matcher.find()) {
            Character ret = matcher.group(1).charAt(0);
            Character left = matcher.group(2).charAt(0);
            char midOpr = matcher.group(3).charAt(0);
            Character right = matcher.group(4).charAt(0);

            Integer nLeft = getVarValue(left);
            Integer nRight = getVarValue(right);
            int nRet = 0;
            if (midOpr == '*') {
                nRet = nLeft * nRight % ModValue;
            } else if (midOpr == '+') {
                nRet = (nLeft + nRight) % ModValue;
            } else if (midOpr == '-') {
                nRet = (nLeft - nRight + 3) % ModValue;
            }
            return new Tuple<>(ret, nRet);

        }
        Pattern has_pattern = Pattern.compile("(\\w*)\\s*=\\s*(\\d*)");
        Matcher has_matcher = has_pattern.matcher(opr);
        if (matcher.find()) {
            return new Tuple<>(has_matcher.group(1).charAt(0), Integer.valueOf(has_matcher.group(2)));
        }
        return new Tuple<>();
    }

    public boolean hasAssign() {
        if (!condition.isEmpty() || opr.isEmpty()) {
            return false;
        }
        String var = findAssignVariable(opr);
        return !var.isEmpty();
    }

    public int getVarValue(Character var) {
        String tmp = String.valueOf(var);
        if (Character.isDigit(var)) {
            return Integer.parseInt(tmp);
        }
        for (int v = 0; v <= vars.size(); v++) {
            if (vars.get(v).getName().equals(tmp)) {
                return vars.get(v).getValue();
            }
        }
        return 0;
    }

    public boolean isConditionOk() {
        if (condition.isEmpty())
            return true;
        if (condition.compareToIgnoreCase("true") == 0)
            return true;
        if (condition.compareToIgnoreCase("false") == 0)
            return false;
        boolean hasNot = false;
        String conditionNew = condition;
        if (conditionNew.contains("not")) {
            hasNot = true;
            conditionNew = conditionNew.replace("not", "");
            conditionNew = conditionNew.trim();
        }
        //未改正则表达式
        Pattern pattern = Pattern.compile("(\\w)\\s*([><=andotr]*)\\s*(\\w)");
        Matcher matcher = pattern.matcher(conditionNew);
        matcher.find();
        Character varLeft = matcher.group(1).charAt(0);
        String midOpr = matcher.group(2);
        Character varRight = matcher.group(3).charAt(0);

        int left = getVarValue(varLeft);
        int right = getVarValue(varRight);

        boolean res = true;
        switch (midOpr) {
            case ">=":
                res = left >= right;
                break;
            case "==":
                res = left == right;
                break;
            case "<=":
                res = left <= right;
                break;
            case ">":
                res = left > right;
                break;
            case "<":
                res = left < right;
                break;
            case "and":
                res = left != 0 && right != 0;
                break;
            case "not":
                res = right == 0;
                break;
            default:
                assert (false);
                break;
        }
        return hasNot != res;


    }

    public void outputFOL(ArrayList<Statement> statements) {
        ArrayList<ArrayList<FirstOrderLogical>> lgss = new ArrayList<>();
        boolean hasPc = statements.size() > 1;
        for (int i = 0; i < statements.size(); ++i) {
            ArrayList<FirstOrderLogical> formulas = new ArrayList<>(toFormula(statements, new Statement()));       //修改statements.get(i)->statements
            lgss.add(formulas);
            for (FirstOrderLogical v : formulas) {
                if (hasPc) {
                    String pc = "pc" + i;
                    String formulaNew = v.toString();
                    formulaNew = formulaNew.replace("pc", pc);
                    System.out.println("pc=" + pc + " and " + formulaNew);
                } else
                    System.out.println(v.toString());
            }
        }
    }

    //重载函数，传入smss则用每个sms遍历
    public void outputFOLs(ArrayList<ArrayList<Statement>> smss) {
        for (ArrayList<Statement> sms : smss) {
            outputFOL(sms);
        }
    }


    //将关联语句转换为逻辑公式
    public FirstOrderLogical toFormula(final Statement pre, final Statement post) {
        return new FirstOrderLogical(pre.getLabel(), post.getLabel(), pre.getCondition(), pre.getSeqBody(), pre.getVars());
    }

    public ArrayList<FirstOrderLogical> toFormula(final ArrayList<Statement> statements) {
        return toFormula(statements, new Statement());
    }

    //将所有语句转换为逻辑公式
    public ArrayList<FirstOrderLogical> toFormula(final ArrayList<Statement> statements, Statement out) {
        if (statements.isEmpty())
            return new ArrayList<>();
        if (out.getLabel().isEmpty()) {
            String temp = statements.get(0).getLabel();
            String prefix = temp.substring(0, 1);
            out.setLabel(prefix + "E");
        }
        ArrayList<FirstOrderLogical> list = new ArrayList<>();
        for (int i = 0; i < statements.size(); ++i) {
            Statement postSm = out;
            Statement sm = statements.get(i);
            if (i + 1 < statements.size()) {
                postSm = statements.get(i + 1);
            }

            if (sm.getType() == 1) {
                if (!sm.getIfBody().isEmpty()) {
                    list.add(toFormula(sm, sm.getIfBody().get(0)));     //first()?
                    list.addAll(toFormula(sm.getIfBody(), postSm));
                }
                if (!sm.getElseBody().isEmpty()) {
                    sm.reversedCondition();
                    list.add(toFormula(sm, sm.getElseBody().get(0)));
                    list.addAll(toFormula(sm.getElseBody(), postSm));
                }
            } else if (sm.getType() == 2) {
                if (!sm.getWhileBody().isEmpty()) {
                    list.add(toFormula(sm, sm.getWhileBody().get(0)));
                    list.addAll(toFormula(sm.getWhileBody(), sm));
                }
                sm.reversedCondition();
                list.add(toFormula(sm, postSm));
            } else if (sm.getType() == 3) {
                sm.reversedCondition();
                list.add(toFormula(sm, sm));
                sm.reversedCondition();
                list.add(toFormula(sm, postSm));
            } else {
                list.add(toFormula(sm, postSm));
            }
        }
        return list;
    }
}

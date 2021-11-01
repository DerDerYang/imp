package Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class KripkeStructure {
    private String preLabel;
    private String postLabel;
    private ArrayList<Variable> preVars;
    private ArrayList<Variable> postVars;
    private String opr;

    public KripkeStructure(String preLabel, String postLabel, ArrayList<Variable> preVars, ArrayList<Variable> postVars, String opr) {
        this.preLabel = preLabel;
        this.postLabel = postLabel;
        this.preVars = preVars;
        this.postVars = postVars;
        this.opr = opr;
    }

    public KripkeStructure() {
        this.preLabel = "";
        this.postLabel = "";
        this.preVars = new ArrayList<>();
        this.postVars = new ArrayList<>();
        this.opr = "";
    }

    public String toString() {
        if (opr.isEmpty() && preLabel.isEmpty() && postLabel.isEmpty())
            return "";

        ArrayList<String> unknownVars = new ArrayList<>();
        for (Variable v : postVars) {
            boolean contains = false;
            for (Variable vPre : preVars) {
                if (vPre.getName() == v.getName()) {
                    contains = true;
                    break;
                }
            }
            if (!contains)
                unknownVars.add(String.valueOf(v.getName()));
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append("(pc=");
        tmp.append(preLabel.isEmpty() ? "U" : preLabel);

        for (Variable v : preVars) {
            tmp.append(",");
            tmp.append(v.toString());
        }

        //加进入未定义的变量
        for (String v : unknownVars) {
            tmp.append(",");
            tmp.append(v).append("=u");
        }

        tmp.append(") -> (pc'=");
        tmp.append(postLabel.isEmpty() ? "U" : postLabel);

        int n = 0;
        for (Variable v : postVars) {
            tmp.append(",");
            tmp.append(v.toString(true));
        }

        tmp.append(")");

        //注释掉变换条件
        //tmp += "and (";
        //tmp += opr;
        //tmp += ")";
        return tmp.toString();
    }

    public static ArrayList<HashMap<String, String>> outputKS(ArrayList<ArrayList<FirstOrderLogical>> lgss){
        ArrayList<String> pcs = new ArrayList<>();
        ArrayList<HashMap<String, String>> relations = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<FirstOrderLogical> lastLgs = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        ArrayList<KripkeStructure> Rs = new ArrayList<>();
        ArrayList<Variable> vars = new ArrayList<>();
        for (ArrayList<FirstOrderLogical> v : lgss) {
            if (lgss.size() > 1) {
                pcs.add("U");
            } else {
                pcs.add("");
            }
            lastLgs.add(new FirstOrderLogical());
        }

        //转化KS结构
        KripkeStructure.createKsLabels(lgss, pcs, relations, labels, lastLgs, vars, states, Rs);

        //输出所有S状态
        System.out.println("\n\nAll States:\n");
        int index = 0;
        for (String v : labels) {
            System.out.println("S"+(index++)+":("+v+")");
        }
        System.out.println();
        index = 0;
        for (KripkeStructure v : Rs) {
            if( !v.toString().isEmpty() )
                System.out.println("R"+(index++)+":= "+v.toString());
        }
        return relations;
    }


    public static void createKsLabels(ArrayList<ArrayList<FirstOrderLogical>> lgss, ArrayList<String> pcs,
                               ArrayList<HashMap<String, String>> relations, ArrayList<String> labels,
                               ArrayList<FirstOrderLogical> lastLgs, ArrayList<Variable> vars,
                               ArrayList<String> states, ArrayList<KripkeStructure> Rs) {
        createKsLabels(lgss, pcs, relations, labels, lastLgs, vars, states, Rs, 0);
    }


    public static void createKsLabels(ArrayList<ArrayList<FirstOrderLogical>> lgss, ArrayList<String> pcs,
                               ArrayList<HashMap<String, String>> relations, ArrayList<String> labels,
                               ArrayList<FirstOrderLogical> lastLgs, final ArrayList<Variable> vars,
                               ArrayList<String> states, ArrayList<KripkeStructure> Rs, int deep) {
        ++deep;
        ArrayList<String> temp = pcs;
        ArrayList<FirstOrderLogical> lastLgsTmp = lastLgs;
        //如果一阶逻辑有n段，则pcs有n个'U'组成，如果只有一段，则pcs=""

        for (int i = 0; i < pcs.size(); ++i) {
            //用户执行完之后恢复
            KripkeStructure oneRs = new KripkeStructure();
            String oldLabel = String.join(" ", temp);
            oneRs.setPreLabel(oldLabel);

            //只包含空格，则认为是空
            String oldTmp = oldLabel;
            oldTmp = oldTmp.replace(" ", "");
            if (oldTmp.isEmpty())
                oldLabel = "";

            //执行完要恢复到上一步的状态
            lastLgsTmp.get(i).setVars(vars);
            FirstOrderLogical lastLg = lastLgsTmp.get(i);
            String lastArgsStr = lastLgsTmp.get(i).valueToString();
            oneRs.setPreVars(lastLgsTmp.get(i).getVars());
            if (!oldLabel.isEmpty() && !lastArgsStr.isEmpty())
                oldLabel += ',' + lastArgsStr;

//            if (oldLabel.contains("A1") && !oldLabel.contains("B")) {
//                int a = 10;
//            }

            lastLgsTmp.set(i, FirstOrderLogical.nextStep(lgss.get(i), lastLgsTmp.get(i)));

            temp.set(i, lastLgsTmp.get(i).getPostLabel());
            ArrayList<Variable> newVars = lastLgsTmp.get(i).getVars();
            String newLabel = String.join(" ", temp);

            //收集R变换
            oneRs.setPostLabel(newLabel);
            oneRs.setPostVars(lastLgsTmp.get(i).getVars());
            oneRs.setOpr(lastLgsTmp.get(i).getOpr());
            Rs.add(oneRs);

            //收集状态S
            String oneState = lastLgsTmp.get(i).valueToString();
            if (!oneState.isEmpty() && !states.contains(oneState)) {
                states.add(oneState);
            }

            if (!newLabel.isEmpty() && !lastLgsTmp.get(i).valueToString().isEmpty())
                newLabel += ',' + lastLgsTmp.get(i).valueToString();

            HashMap<String, String> r = new HashMap<>();
            r.put(oldLabel, newLabel);
            if (relations.contains(r)) {
                temp.set(i, pcs.get(i));
                lastLgsTmp.set(i, lastLg);
                continue;
            }
            if (!oldLabel.isEmpty() && !newLabel.isEmpty()) {
                HashMap<String, String> temp1 = new HashMap<>();
                temp1.put(oldLabel, newLabel);
                relations.add(temp1);
            }
            if (!labels.contains(oldLabel) && !oldLabel.isEmpty()) {
                labels.add(oldLabel);
            }
            if (!labels.contains(newLabel) && !newLabel.isEmpty()) {
                labels.add(newLabel);
            }
            createKsLabels(lgss, temp, relations, labels, lastLgsTmp, newVars, states, Rs, deep);
            temp.set(i, pcs.get(i));
            lastLgsTmp.set(i, lastLg);
        }
    }

    public String getPreLabel() {
        return preLabel;
    }

    public void setPreLabel(String preLabel) {
        this.preLabel = preLabel;
    }

    public String getPostLabel() {
        return postLabel;
    }

    public void setPostLabel(String postLabel) {
        this.postLabel = postLabel;
    }

    public ArrayList<Variable> getPreVars() {
        return preVars;
    }

    public void setPreVars(ArrayList<Variable> preVars) {
        this.preVars = preVars;
    }

    public ArrayList<Variable> getPostVars() {
        return postVars;
    }

    public void setPostVars(ArrayList<Variable> postVars) {
        this.postVars = postVars;
    }

    public String getOpr() {
        return opr;
    }

    public void setOpr(String opr) {
        this.opr = opr;
    }
}

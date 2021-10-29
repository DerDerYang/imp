package Entity;

import java.util.ArrayList;
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

    public static void outputKS(ArrayList<ArrayList<FirstOrderLogical>> lgss){
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
    }


    public static void createKsLabels(ArrayList<ArrayList<FirstOrderLogical>> lgss, ArrayList<String> pcs,
                               ArrayList<HashMap<String, String>> relations, ArrayList<String> labels,
                               ArrayList<FirstOrderLogical> lastLgs, ArrayList<Variable> vars,
                               ArrayList<String> states, ArrayList<KripkeStructure> Rs) {
        createKsLabels(lgss, pcs, relations, labels, lastLgs, vars, states, Rs, 0);
    }


    public static void createKsLabels(ArrayList<ArrayList<FirstOrderLogical>> lgss, final ArrayList<String> pcs,
                               ArrayList<HashMap<String, String>> relations, ArrayList<String> labels,
                               ArrayList<FirstOrderLogical> lastLgs, final ArrayList<Variable> vars,
                               ArrayList<String> states, ArrayList<KripkeStructure> Rs, int deep) {
        ++deep;
        for (int i = 0; i < pcs.size(); ++i) {
            //用户执行完之后恢复
            KripkeStructure oneRs = new KripkeStructure();
            String oldLabel = String.join(" ", pcs);
            oneRs.setPreLabel(oldLabel);

            //只包含空格，则认为是空
            String oldTmp = oldLabel;
            oldTmp = oldTmp.replace(" ", "");
            if (oldTmp.isEmpty())
                oldLabel = "";

            //执行完要恢复到上一步的状态
            lastLgs.get(i).setVars(vars);
            FirstOrderLogical lastLg = lastLgs.get(i);
            String lastArgsStr = lastLgs.get(i).valueToString();
            oneRs.preVars = lastLgs.get(i).getVars();
            if (!oldLabel.isEmpty() && !lastArgsStr.isEmpty())
                oldLabel += ',' + lastArgsStr;

//            if (oldLabel.contains("A1") && !oldLabel.contains("B")) {
//                int a = 10;
//            }

            lastLgs.set(i, FirstOrderLogical.nextStep(lgss.get(i), lastLgs.get(i)));

            pcs.set(i, lastLgs.get(i).getPostLabel());
            ArrayList<Variable> newVars = lastLgs.get(i).getVars();
            String newLabel = String.join(" ", pcs);

            //收集R变换
            oneRs.postLabel = newLabel;
            oneRs.postVars = lastLgs.get(i).getVars();
            oneRs.opr = lastLgs.get(i).getOpr();
            Rs.add(oneRs);

            //收集状态S
            String oneState = lastLgs.get(i).valueToString();
            if (!oneState.isEmpty() && !states.contains(oneState)) {
                states.add(oneState);
            }

            if (!newLabel.isEmpty() && !lastLgs.get(i).valueToString().isEmpty())
                newLabel += ',' + lastLgs.get(i).valueToString();

            HashMap<String, String> r = new HashMap<>();
            r.put(oldLabel, newLabel);
            if (relations.contains(r)) {
                pcs.set(i, pcs.get(i));
                lastLgs.set(i, lastLg);
                continue;
            }

            if (!oldLabel.isEmpty() && !newLabel.isEmpty()) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put(oldLabel, newLabel);
                relations.add(temp);
            }
            if (!labels.contains(oldLabel) && !oldLabel.isEmpty()) {
                labels.add(oldLabel);
            }

            if (!labels.contains(newLabel) && !newLabel.isEmpty()) {
                labels.add(newLabel);
            }
            createKsLabels(lgss, pcs, relations, labels, lastLgs, newVars, states, Rs, deep);
            pcs.set(i, pcs.get(i));
            lastLgs.set(i, lastLg);
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

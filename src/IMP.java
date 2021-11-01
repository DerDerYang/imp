import Entity.FirstOrderLogical;
import Entity.KripkeStructure;
import Entity.Statement;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class IMP {

    public static void pre(ArrayList<HashMap<String, String>> relations) {
        ArrayList<String> strings = new ArrayList<>();
        HashMap<String, String> nodes = new HashMap<>();
        for (HashMap<String, String> relation : relations) {
            Set<String> set = relation.keySet();
            for (String str : set) {
                String preNode = str.substring(0, 2);
                String value = relation.get(str);
                String postNode = value.substring(0, 2);
                String preVar, postVar;
                if (str.length() <= 3)
                    preVar = "";
                else
                    preVar = str.substring(3).replace(",", " ");
                if (value.length() <= 3)
                    postVar = "";
                else
                    postVar = value.substring(3).replace(",", " ");
                if (!nodes.containsKey(preNode))
                    nodes.put(preNode, preVar);
                if (!nodes.containsKey(postNode))
                    nodes.put(postNode, postVar);
                strings.add(preNode + "->" + postNode);
                Set<String> nodeSet = relation.keySet();
            }
        }
        StringBuilder diagram = new StringBuilder("digraph G {\n");
        for (String key : nodes.keySet()) {
            diagram.append("\t").append(key);
            if(!nodes.get(key).equals(""))
                diagram.append("[label=\"").append(key).append(" ").append(nodes.get(key)).append("\"];\n");
            else
                diagram.append(";\n");
        }
        for (String s : strings){
            diagram.append("\t").append(s).append("\n");
        }
        diagram.append("}");
        IMP.write(diagram.toString());
    }

    public static void write(String s){
        FileWriter writer = null;
        try {
            writer = new FileWriter("D:\\graduate\\研究生课程\\系统分析与验证\\imp\\img\\diagram.gv");
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String input = "cobegin P0||P1 coend;P0:: t=0; while true do wait(t==0); t=1; endwhile; P1:: while true do wait(t==1); t=0; endwhile;";
        String input1 = "x=0;y=0;z=0;x=y+1;z=z+2;while y<2 do \nif x<y then \nx=x+1;else \ny=y+1;endif;endwhile;";
        String input2 = "x=0;y=0;x=y+1";
        Statement statement = new Statement();
        ArrayList<String> processes = statement.parseCoProcesses(input);
        ArrayList<ArrayList<Statement>> smss = new ArrayList<>();
        for (String p : processes) {
            ArrayList<Statement> tmp = new ArrayList<Statement>();
            statement.parseStatement(p, tmp);
            smss.add(tmp);
        }

        //打标签
        statement.labelStatements(smss);
        statement.outputLabel(smss);

        //一阶逻辑
        FirstOrderLogical f = new FirstOrderLogical();
        System.out.println("First order logical formula:");
        ArrayList<ArrayList<FirstOrderLogical>> lgss = f.outputFOLs(smss);

        //转化KS
        ArrayList<HashMap<String, String>> relations = KripkeStructure.outputKS(lgss);
        IMP.pre(relations);
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("dot -Tpng D:\\graduate\\研究生课程\\系统分析与验证\\imp\\img\\diagram.gv -o D:\\graduate\\研究生课程\\系统分析与验证\\imp\\img\\out.png ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

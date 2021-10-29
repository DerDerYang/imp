import Entity.FirstOrderLogical;
import Entity.Statement;

import java.util.ArrayList;

public class IMP {


    public static void main(String[] args) {
        String input = "cobegin P0||P1 coend;P0:: t=0; while true do wait(t==0); t=1; endwhile; P1:: while true do wait(t==1); t=0; endwhile;";
        String input1 = "x=0;y=0;z=0;x=y+1;z=z+2;while y<2 do \nif x<y then \nx=x+1;else \ny=y+1;endif;endwhile;";
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
        f.outputFOLs(smss);

    }
}

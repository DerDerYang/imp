import Entity.Statement;

import java.util.ArrayList;
import java.util.List;

public class IMP {




    public static void main(String[] args) {
        String test1 = "if b then\np0;\nelse p1;\n endif;";
//while a<b do a=a+1;endwhile;a=2;b=0;
        ArrayList<Statement> statements = new ArrayList<>();
        Statement statement = new Statement();
//        statements.add(statement);
        statements = statement.parseStatement(test1,statements);
        System.out.println(statement);

    }
}

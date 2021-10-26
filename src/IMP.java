import Entity.Statement;

import java.util.ArrayList;
import java.util.List;

public class IMP {




    public static void main(String[] args) {
        String test1 = "x=0;y=2;if x<y then\nwhile x<2 do\nx=x+1;endwhile;else\ny=y+1;endif;";

        ArrayList<Statement> statements = new ArrayList<>();
        Statement statement = new Statement();
        statements = statement.parseStatement(test1,statements);
        System.out.println(statements);
        for(int i = 0 ; i<statements.size();i++){
            System.out.print(statements.get(i));
        }
    }
}

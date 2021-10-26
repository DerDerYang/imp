package GraphVizTest;

import java.io.IOException;

public class GraphVizTest {
    public static void main(String[] args) {
        GraphViz gViz=new GraphViz("D:\\graduate\\研究生课程\\系统分析与验证\\imp\\img", "D:\\graduate\\研究生课程\\系统分析与验证\\Graphviz\\bin\\dot.exe");
        gViz.start_graph();
        gViz.addln("cjn->zjn;");
        gViz.addln("njh->zjn;");
        gViz.addln("zjn->lyy;");
        gViz.end_graph();
        try {
            gViz.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.component;


import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableUndirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;


/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs.
 * Applet based on JGraphAdapterDemo.
 *
 * @since July 9, 2013
 */
public class JGraphXAdapterDemo extends JApplet
{


    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);



    private JGraphXAdapter<String, DefaultEdge> jgxAdapter;



    /**
     * An alternative starting point for this demo, to also allow running this
     * applet as an application.
     *
     * @param args ignored.
     */
    public static void main(String [] args)
    {
        JGraphXAdapterDemo applet = new JGraphXAdapterDemo();
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("JGraphT Adapter to JGraph Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        // create a JGraphT graph
        ListenableUndirectedGraph<String, DefaultEdge> g =
                new ListenableUndirectedGraph<String, DefaultEdge>(
                        DefaultEdge.class);

        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<String, DefaultEdge>(g);


        getContentPane().add(new mxGraphComponent(jgxAdapter));
        resize(DEFAULT_SIZE);

        mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);

        mxGraph graph =(mxGraph)graphComponent.getGraph();

//        graph.getModel().beginUpdate();
//        try
//        {
//            Object v1 = graph.insertVertex(this ,null, "Hello,", 20, 20, 80, 30);
//            Object v2 = graph.insertVertex(this, null, "World!", 200, 150, 80, 30);
//            Object e1 = graph.insertEdge(this, null, "yes", v1, v2);
//        }
//        finally
//        {
//            // Updates the display
//            graph.getModel().endUpdate();
//        }


        mxGraphModel graphModel  = (mxGraphModel)graphComponent.getGraph().getModel();



        Collection<Object> cells =  graphModel.getCells().values();
        mxUtils.setCellStyles(graphComponent.getGraph().getModel(),
                cells.toArray(), mxConstants.NONE, mxConstants.NONE);


//instead of getContentPane().add(new mxGraphComponent(jgxAdapter));
        getContentPane().add(graphComponent);




        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add some sample data (graph manipulated via JGraphX)
        g.addVertex(v1);



        g.addVertex(v3);
        g.addVertex(v4);



        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v1);
        g.addEdge(v4, v3);





        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());


        // that's all there is to it!...

    }
}
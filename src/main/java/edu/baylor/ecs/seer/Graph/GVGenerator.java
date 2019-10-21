package edu.baylor.ecs.seer.Graph;

import edu.baylor.ecs.seer.context.RadResponseContext;
import edu.baylor.ecs.seer.model.RestEntity;
import edu.baylor.ecs.seer.model.RestFlow;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GVGenerator {
    /*public static void generate(RadResponseContext radResponseContext) throws IOException {
        Graph g = graph("rad").directed();

        for (RestFlow restFlow : radResponseContext.getRestFlowContext().getRestFlows()) {
            RestEntity server = restFlow.getServers().get(0);
            Node nodeFrom = node(restFlow.getClassName() + "." + restFlow.getMethodName());
            Node nodeTo = node(server.getClassName() + "." + server.getMethodName());
            g = g.with(nodeFrom.link(nodeTo));
        }

        Graphviz.fromGraph(g).render(Format.SVG).toFile(new File("C:\\Users\\das\\Desktop\\rad.svg"));
    }*/

    public static void generate(RadResponseContext radResponseContext) {
        StringBuilder graph = new StringBuilder();
        graph.append("digraph cil_rad {").append("\n");
        graph.append("rankdir = LR;").append("\n");
        graph.append("node [shape=box];").append("\n");

        int clusterIndex = 0;

        Map<String, Set<String>> clusters = getClusters(radResponseContext);

        for (String key : clusters.keySet()) {
            StringBuilder cluster = new StringBuilder();

            cluster.append("subgraph cluster_").append(clusterIndex++).append(" {").append("\n")
                    .append("label = ").append(key).append(";").append("\n")
                    .append("color=blue;").append("\n")
                    .append("rank = same;");

            Set<String> entities = clusters.get(key);

            for (String entity : entities) {
                cluster.append(" ").append(entity).append(";");
            }

            cluster.append("\n").append("}").append("\n");
            graph.append(cluster);
        }

        for (RestFlow restFlow : radResponseContext.getRestFlowContext().getRestFlows()) {
            String nodeFrom = getFullMethodName(restFlow);

            for (RestEntity server : restFlow.getServers()) {
                String nodeTo = getFullMethodName(server);
                String label = getLinkLabel(server);

                String link = String.format("%s  -> %s [ label = %s ];", nodeFrom, nodeTo, label);
                graph.append(link).append("\n");
            }
        }

        graph.append("}");

        try (PrintWriter out = new PrintWriter("C:\\Users\\das\\Desktop\\rad.gv")) {
            out.println(graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Set<String>> getClusters(RadResponseContext radResponseContext) {
        Map<String, Set<String>> clusters = new HashMap<>();

        for (RestFlow restFlow : radResponseContext.getRestFlowContext().getRestFlows()) {
            String nodeFrom = getFullMethodName(restFlow);
            addToMap(clusters, addDoubleQuotations(restFlow.getResourcePath()), nodeFrom);

            for (RestEntity server : restFlow.getServers()) {
                String nodeTo = getFullMethodName(server);
                addToMap(clusters, addDoubleQuotations(server.getResourcePath()), nodeTo);
            }
        }

        return clusters;
    }

    private static String addDoubleQuotations(String str) {
        return "\"" + str + "\"";
    }

    private static void addToMap(Map<String, Set<String>> m, String key, String value) {
        if (!m.containsKey(key)) {
            m.put(key, new HashSet<>());
        }
        m.get(key).add(value);
    }

    private static String getLinkLabel(RestEntity restEntity) {
        return addDoubleQuotations(restEntity.getHttpMethod() + " " + restEntity.getUrl());
    }

    private static String getFullMethodName(RestEntity restEntity) {
        return addDoubleQuotations(restEntity.getClassName() + "." + restEntity.getMethodName());
    }

    private static String getFullMethodName(RestFlow restFlow) {
        return addDoubleQuotations(restFlow.getClassName() + "." + restFlow.getMethodName());
    }
}
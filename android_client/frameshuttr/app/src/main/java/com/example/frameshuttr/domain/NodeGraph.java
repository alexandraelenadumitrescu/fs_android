package com.example.frameshuttr.domain;

import com.example.frameshuttr.domain.nodes.Node;
import com.example.frameshuttr.domain.nodes.NodeType;

import java.util.ArrayList;
import java.util.List;

public class NodeGraph {//TIN DATELE LEGATE DE NODURI SI CONEXIUNI
    private List<Node> nodes=new ArrayList<>();


    public void addNode(Node node) {
        nodes.add(node);

    }
    public void removeNode(Node node) {
        // deconecteaza nodurile legate de nod
        for (Node n : nodes) {
            if (n.outputNode == node) {
                n.outputNode = null;
            }
            n.inputs.remove(node);
        }
        nodes.remove(node);
    }
    public boolean connectNodes(Node from, Node to) {
        // impiedica cicluri
        if (wouldCreateCycle(from, to)) {
            return false;
        }

        from.outputNode = to;
        to.inputs.add(from);
        return true;
    }

    public void disconnectNodes(Node from, Node to) {
        if (from.outputNode == to) {
            from.outputNode = null;
        }
        to.inputs.remove(from);
    }

    private boolean wouldCreateCycle(Node from, Node to) {
        Node current = to;
        while (current != null) {
            if (current == from) return true;
            current = current.outputNode;
        }
        return false;
    }
    public boolean isValid() {
        // doar un insgur source node
        int sourceCount = 0;
        for (Node node : nodes) {
            if (node.type == NodeType.SOURCE) {
                sourceCount++;
            }
        }
        return sourceCount == 1;
    }

    public Node findSourceNode() {
        for (Node node : nodes) {
            if (node.type == NodeType.SOURCE) {
                return node;
            }
        }
        return null;
    }

    public Node findNodeAt(float x, float y) {
        //cautam de la final la inceput
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
            if (node.position.contains(x, y)) {
                return node;
            }
        }
        return null;
    }
    public List<Node> getAllNodes() {
        return new ArrayList<>(nodes);
    }

    public int getNodeCount() {
        return nodes.size();
    }



}

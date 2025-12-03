package com.example.frameshuttr.domain;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.frameshuttr.domain.nodes.Node;

public class Pipeline {//clasa pentru executia pipeline ului
    private static final String TAG = "PipelineManager";

    private NodeGraph graph;
    private PipelineListener listener;
    public interface PipelineListener {
        void onNodeProcessing(Node node);
        void onNodeCompleted(Node node, Bitmap result);
        void onPipelineCompleted(Bitmap finalResult);
        void onPipelineError(String error);
    }

    public Pipeline(NodeGraph graph) {
        this.graph = graph;
    }
    public void setListener(PipelineListener listener) {
        this.listener = listener;
    }
    public Bitmap execute(Bitmap inputImage) {
        if (!graph.isValid()) {
            notifyError("Pipeline invalid: missing SOURCE node");
            return null;
        }

        Node startNode = graph.findSourceNode();
        if (startNode == null) {
            notifyError("No SOURCE node found");
            return null;
        }

        Log.d(TAG, "Starting pipeline execution...");
        return processNodeRecursively(startNode, inputImage);
    }
    private Bitmap processNodeRecursively(Node currentNode, Bitmap image) {
        if (currentNode == null) {
            notifyCompleted(image);
            return image;
        }

        if (image == null) {
            notifyError("Null image at node: " + currentNode.title);
            return null;
        }

        // Notifică UI că procesăm acest nod
        notifyProcessing(currentNode);

        try {
            // Procesează imaginea
            Bitmap processedImage = currentNode.process(image);

            // Notifică UI că am terminat cu acest nod
            notifyNodeCompleted(currentNode, processedImage);

            // Continuă cu următorul nod
            return processNodeRecursively(currentNode.outputNode, processedImage);

        } catch (Exception e) {
            Log.e(TAG, "Error processing node: " + currentNode.title, e);
            notifyError("Error at " + currentNode.title + ": " + e.getMessage());
            return null;
        }
    }

    // ==================== NOTIFICĂRI CĂTRE UI ====================

    private void notifyProcessing(Node node) {
        if (listener != null) {
            listener.onNodeProcessing(node);
        }
    }

    private void notifyNodeCompleted(Node node, Bitmap result) {
        if (listener != null) {
            listener.onNodeCompleted(node, result);
        }
    }

    private void notifyCompleted(Bitmap result) {
        if (listener != null) {
            listener.onPipelineCompleted(result);
        }
    }

    private void notifyError(String error) {
        Log.e(TAG, error);
        if (listener != null) {
            listener.onPipelineError(error);
        }
    }

    // ==================== UTILITĂȚI ====================

    public void reset() {
        // Resetează stările nodurilor dacă este cazul
        for (Node node : graph.getAllNodes()) {
            // node.state = NodeState.IDLE; (dacă implementezi enum-ul NodeState)
        }
    }

}

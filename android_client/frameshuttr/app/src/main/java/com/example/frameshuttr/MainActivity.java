//package com.example.frameshuttr;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.frameshuttr.domain.nodes.CullingNode;
//import com.example.frameshuttr.domain.NodeEditorView;
//import com.example.frameshuttr.domain.nodes.SourceNode;
//import com.example.frameshuttr.domain.nodes.StyleTransferNode;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//public class MainActivity extends AppCompatActivity {
//
//    private NodeEditorView editorView;
//    private ImageView previewImage;
//    private FloatingActionButton btnProcess;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // todo mutat intr un init sau intr o alta activitate daca e predefinit
//        editorView = findViewById(R.id.nodeEditor);
//        previewImage = findViewById(R.id.resultImage);
//        btnProcess = findViewById(R.id.btnProcess);
//
//
//        SourceNode source = new SourceNode("1", 100, 100);
//        CullingNode culling = new CullingNode("2", 500, 200);
//        StyleTransferNode style = new StyleTransferNode("3", 900, 100);
//
//        //todo de adaugat din interfata nu doar din cod
//        editorView.addNode(source);
//        editorView.addNode(culling);
//        editorView.addNode(style);
//
//
//        editorView.connectNodes(source, culling);
//        editorView.connectNodes(culling, style);
//
//        // procesare
//        btnProcess.setOnClickListener(v -> {
//            // incarc o imagine de test din res drawable
//            Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.img);
//
//            // execut pe thread secundar
//            new Thread(() -> {
//                Bitmap result = editorView.executePipeline(original);
//
//                runOnUiThread(() -> {
//                    if (result != null) {
//                        previewImage.setImageBitmap(result);
//                    }
//                });
//            }).start();
//        });
//    }
//}
package com.example.frameshuttr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frameshuttr.domain.NodeEditorView;
import com.example.frameshuttr.domain.NodeGraph;
import com.example.frameshuttr.domain.Pipeline;

import com.example.frameshuttr.domain.nodes.CullingNode;
import com.example.frameshuttr.domain.nodes.Node;
import com.example.frameshuttr.domain.nodes.SourceNode;
import com.example.frameshuttr.domain.nodes.StyleTransferNode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // ==================== COMPONENTE SEPARATE ====================

    private NodeEditorView editorView;      // UI
    private NodeGraph graph;                 // Data
    private Pipeline pipelineManager; // Logic

    private ImageView previewImage;
    private FloatingActionButton btnProcess;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ==================== SETUP COMPONENTE ====================

        // 1. Setup thread pool
        executorService = Executors.newSingleThreadExecutor();

        // 2. Setup UI
        editorView = findViewById(R.id.nodeEditor);
        previewImage = findViewById(R.id.resultImage);
        btnProcess = findViewById(R.id.btnProcess);

        // 3. Obține graph-ul din UI (sau creează unul nou)
        graph = editorView.getGraph();

        // 4. Creează pipeline manager
        pipelineManager = new Pipeline(graph);

        // 5. Setup callback-uri pentru procesare
        setupPipelineCallbacks();

        // ==================== CONSTRUIEȘTE GRAPH-UL ====================

        buildDemoGraph();

        // ==================== SETUP BUTON PROCESARE ====================

        btnProcess.setOnClickListener(v -> executeProcessing());
    }

    private void buildDemoGraph() {
        // Creăm nodurile
        SourceNode source = new SourceNode("1", 100, 100);
        CullingNode culling = new CullingNode("2", 500, 200);
        StyleTransferNode style = new StyleTransferNode("3", 900, 100);

        // Le adăugăm în view (care le adaugă în graph)
        editorView.addNode(source);
        editorView.addNode(culling);
        editorView.addNode(style);

        // Creăm conexiunile
        editorView.connectNodes(source, culling);
        editorView.connectNodes(culling, style);
    }

    private void setupPipelineCallbacks() {
        pipelineManager.setListener(new Pipeline.PipelineListener() {
            @Override
            public void onNodeProcessing(Node node) {
                runOnUiThread(() -> {
                    // Poți actualiza UI-ul: schimbă culoarea nodului, etc.
                    Toast.makeText(MainActivity.this,
                            "Processing: " + node.title,
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onNodeCompleted(Node node, Bitmap result) {
                runOnUiThread(() -> {
                    // Poți afișa preview-uri intermediare
                });
            }

            @Override
            public void onPipelineCompleted(Bitmap finalResult) {
                runOnUiThread(() -> {
                    if (finalResult != null) {
                        previewImage.setImageBitmap(finalResult);
                        Toast.makeText(MainActivity.this,
                                "Processing complete!",
                                Toast.LENGTH_SHORT).show();
                    }
                    btnProcess.setEnabled(true);
                });
            }

            @Override
            public void onPipelineError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Error: " + error,
                            Toast.LENGTH_LONG).show();
                    btnProcess.setEnabled(true);
                });
            }
        });
    }

    private void executeProcessing() {
        // Validare
        if (!graph.isValid()) {
            Toast.makeText(this, "Pipeline is invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Încarcă imaginea
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        if (original == null) {
            Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dezactivează butonul
        btnProcess.setEnabled(false);

        // Execută pe thread secundar
        executorService.execute(() -> {
            pipelineManager.execute(original);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
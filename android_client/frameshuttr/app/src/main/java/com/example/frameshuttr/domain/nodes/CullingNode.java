package com.example.frameshuttr.domain.nodes;

import android.graphics.Bitmap;
import android.util.Log;

public class CullingNode extends Node {
    public CullingNode(String id, float x, float y) {
        super(id, "Culling", NodeType.AI_CULLING, x, y);
    }

    @Override
    public Bitmap process(Bitmap inputImage) {
        if (inputImage == null){ return null;}
        Log.d("procesare","s a procesat nodul de culling");
        return inputImage;

    }
}

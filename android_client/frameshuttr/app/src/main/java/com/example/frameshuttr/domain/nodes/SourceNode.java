package com.example.frameshuttr.domain.nodes;


import android.graphics.Bitmap;

public class SourceNode extends Node {

    public SourceNode(String id, float x, float y) {
        super(id, "Input Photo", NodeType.SOURCE, x, y);
    }

    @Override
    public Bitmap process(Bitmap inputImage) {
        return inputImage;
    }
}

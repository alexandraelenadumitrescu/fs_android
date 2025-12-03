package com.example.frameshuttr.domain.nodes;

import android.graphics.Bitmap;

public class StyleTransferNode extends Node {
    public StyleTransferNode(String id, float x, float y) {
        super(id, "Style Transfer", NodeType.AI_STYLE_TRANSFER, x, y);
    }

    @Override
    public Bitmap process(Bitmap inputImage) {
        // TODO: 11/19/2025
        System.out.println("transfer");
        return inputImage;

    }
}

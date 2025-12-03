package com.example.frameshuttr.domain.network;

public class VectorRequest {
    public float[] embeddings;

    public VectorRequest(float[] embeddings) {
        this.embeddings = embeddings;
    }
}

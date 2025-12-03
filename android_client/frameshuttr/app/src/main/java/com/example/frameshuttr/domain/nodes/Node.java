package com.example.frameshuttr.domain.nodes;
//definire elemente vizuale si minim functionale pentru un nod

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    public String id;
    public String title;
    public NodeType type;
    public List<Node> inputs=new ArrayList<>();
    public Node outputNode=null;
    //vreau sa il modelez ca hit area in ui si sa pot sa evit coliziunile todo
    public RectF position;//(x,y,width,height)
    //metoda abstracta
    public abstract Bitmap process(Bitmap inputImage);

    public Node(String id, String title, NodeType type, float x, float y) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.position = new RectF(x, y, x + 300, y + 150); // fix 300x150
    }
}

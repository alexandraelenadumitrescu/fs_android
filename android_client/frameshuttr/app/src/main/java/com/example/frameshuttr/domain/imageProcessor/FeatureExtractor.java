package com.example.frameshuttr.domain.imageProcessor;
import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class FeatureExtractor {
    private Interpreter interpreter;
    private final int INPUT_SIZE = 224; // MobileNet vrea poze de 224x224
    // MobileNet V2 standard scoate 1001 valori (probabilitățile claselor ImageNet)
    // Acesta va fi "vectorul" nostru de trăsături.
    private final int OUTPUT_SIZE = 1001;

    public FeatureExtractor(Context context) {
        try {
            // 1. Încărcăm modelul din Assets
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, "1.tflite");
            // 2. Inițializăm interpretorul (Creierul)
            Interpreter.Options options = new Interpreter.Options();
            interpreter = new Interpreter(tfliteModel, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float[] extractFeatures(Bitmap bitmap) {
        if (interpreter == null) return new float[0];

        // 1. Pre-procesare Imagine
        // Convertim Bitmap-ul în formatul cerut de TFLite (Tensor)
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        //TensorImage tensorImage = new TensorImage(DataType.UINT8);
        tensorImage.load(bitmap);

        // Procesorul face Resize automat și Normalizează pixelii
        // (Valori între 0 și 1 sau -1 și 1, depinde de model. MobileNet Float vrea de obicei normalizare)
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(127.5f, 127.5f)) // Transformă pixelii [0,255] în [-1,1]
                .build();

        tensorImage = imageProcessor.process(tensorImage);

        // 2. Pregătim containerul pentru rezultat
        // Ieșirea modelului este un array de [1][1001]
        float[][] outputBuffer = new float[1][OUTPUT_SIZE];

        // 3. RULĂM MODELUL (Inferența)
        interpreter.run(tensorImage.getBuffer(), outputBuffer);

        // 4. Returnăm vectorul (prima linie)
        return outputBuffer[0];
    }

    // Este politicos să închidem creierul când nu-l folosim
    public void close() {
        if (interpreter != null) {
            interpreter.close();
        }
    }
}

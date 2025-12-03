from flask import Flask, request, jsonify
import os
import numpy as np

app = Flask(__name__)

# Configurare folder salvare (opțional, ca să vezi că a ajuns)
UPLOAD_FOLDER = 'uploads'
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)





@app.route('/analyze_vector', methods=['POST'])
def analyze_vector():
    # 1. Primim JSON-ul
    data = request.json
    
    if not data or 'embeddings' not in data:
        return jsonify({"error": "No vector data"}), 400

    # 2. Extragem vectorul (lista de float-uri)
    vector_list = data['embeddings']
    
    # Convertim în numpy array pentru matematică
    vector = np.array(vector_list)

    print(f"Am primit un vector de dimensiunea: {vector.shape}")
    print(f"Primele 5 valori: {vector[:5]}")

    # 3. SIMULARE ANALIZĂ AI
    # Aici ai face: "Cât de aproape e vectorul ăsta de pozele cu pisici?"
    # Pentru test, calculăm doar "puterea" vectorului (Norma)
    score = np.linalg.norm(vector)

    return jsonify({
        "status": "success",
        "message": "Vector analizat securizat!",
        "feature_score": float(score) # Trimitem un scor înapoi
    })    

@app.route('/upload', methods=['POST'])
def upload_file():
    if 'image' not in request.files:
        return jsonify({"error": "No image part"}), 400
    
    file = request.files['image']
    
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        # 1. Salvăm fișierul (sau îl procesăm direct cu OpenCV)
        filepath = os.path.join(UPLOAD_FOLDER, file.filename)
        file.save(filepath)
        print(f"Imagine primită și salvată: {filepath}")

        # AICI vei chema funcția ta de AI/OpenCV în viitor
        # result = my_ai_function(filepath)

        # 2. Răspundem telefonului
        return jsonify({
            "status": "success",
            "message": "Imagine procesată cu succes!",
            "ai_result": "Blur Detected: False" # Exemplu
        })

if __name__ == '__main__':
    # IMPORTANT: host='0.0.0.0' permite accesul din rețea (de pe telefon)
    app.run(host='0.0.0.0', port=5000, debug=True)
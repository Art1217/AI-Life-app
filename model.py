# train_and_export_model.py

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
import tensorflow as tf
import joblib
import os

# --- 1) Carga y preprocesado de datos ---
df = pd.read_csv("synthetic_diagnosis_data.csv")

# Features y etiqueta
X = df[["age", "temperature", "heart_rate", "spo2"]].to_numpy()
y_raw = df["diagnosis"].to_numpy()

# Codificar etiquetas a 0…6
le = LabelEncoder()
y = le.fit_transform(y_raw)

# Split estratificado 80/20
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, stratify=y, random_state=42
)

# Escalado de features
scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test  = scaler.transform(X_test)

# Guarda scaler y encoder para producción (Cloud Function o app)
joblib.dump(scaler,         "scaler.joblib")
joblib.dump(le,             "label_encoder.joblib")

# --- 2) Definición y entrenamiento de la red neuronal ---
n_classes = len(le.classes_)

model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(4,)),
    tf.keras.layers.Dense(32, activation="relu"),
    tf.keras.layers.Dense(32, activation="relu"),
    tf.keras.layers.Dense(n_classes, activation="softmax")
])

model.compile(
    optimizer="adam",
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"]
)

model.fit(
    X_train, y_train,
    validation_data=(X_test, y_test),
    epochs=10,
    batch_size=32
)

# Evalúa en test
loss, acc = model.evaluate(X_test, y_test, verbose=0)
print(f"\nTest accuracy: {acc:.4f}, loss: {loss:.4f}")

# --- 3) Exportar SavedModel para TFLite ---
saved_model_dir = "saved_model/diagnosis_mlp"
if os.path.exists(saved_model_dir):
    # elimina modelo previo para evitar conflictos
    import shutil
    shutil.rmtree(saved_model_dir)

# Keras 3: use model.export() to SavedModel
model.export(saved_model_dir)
print(f"SavedModel exportado en: {saved_model_dir}")

# --- 4) Convertir a TFLite optimizado ---
converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()

with open("diagnosis_mlp.tflite", "wb") as f:
    f.write(tflite_model)
print("Modelo TFLite generado: diagnosis_mlp.tflite")

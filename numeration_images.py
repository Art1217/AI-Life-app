# balanced_synthetic_data.py

from faker import Faker
import numpy as np
import pandas as pd
from datetime import datetime
import random

fake = Faker()
DIAGNOSES = [
    "Normal",
    "Fiebre aislada",
    "Taquicardia sin fiebre",
    "Hipoxia",
    "Fiebre con taquicardia",
    "Fiebre con hipoxia",
    "Emergencia médica"
]
def calculate_age_from_dob(dob):
    today = datetime.today()
    age = today.year - dob.year - ((today.month, today.day) < (dob.month, dob.day))
    return age

def sample_age():
    return random.randint(0, 90)

def hr_range_by_age(age):
    if age < 1:   return 120, 170
    if age < 3:   return 120, 160
    if age < 6:   return 110, 130
    if age < 11:  return 100, 120
    if age < 15:  return 60, 105
    return 60, 100

def sample_vitals_for_label(label, age):
    hr_min, hr_max = hr_range_by_age(age)
    t_norm = np.random.uniform(36.5, 37.5)
    hr_norm = np.random.randint(hr_min, hr_max + 1)
    spo2_norm = np.random.randint(96, 101)

    if label == "Normal":
        return (
            np.random.uniform(36.5, 37.3), 
            np.random.randint(hr_min, hr_max + 1),
            np.random.randint(96, 101)
        )
    if label == "Fiebre aislada":
        return (
            np.random.uniform(38.0, 39.0),  
            np.random.randint(hr_min, hr_max + 1),  
            np.random.randint(96, 101) 
        )
    if label == "Taquicardia sin fiebre":
        return (
            np.random.uniform(36.5, 37.3),  
            np.random.randint(hr_max + 1, int(hr_max * 1.3)), 
            np.random.randint(96, 101)
        )
    if label == "Hipoxia":
        return (
            np.random.uniform(36.5, 37.3),
            np.random.randint(hr_min, hr_max + 1),
            np.random.randint(85, 90) 
        )
    if label == "Fiebre con taquicardia":
        return (
            np.random.uniform(38.0, 39.5),
            np.random.randint(hr_max + 1, int(hr_max * 1.5)),
            np.random.randint(96, 101)
        )
    if label == "Fiebre con hipoxia":
        return (
            np.random.uniform(38.0, 39.5),
            np.random.randint(hr_min, hr_max + 1),
            np.random.randint(85, 90)
        )
    if label == "Emergencia médica":
        temp = np.random.choice([
            np.random.uniform(40.1, 42.0),
            np.random.uniform(36.5, 37.3)
        ])
        hr = np.random.choice([
            np.random.randint(int(hr_max * 1.8), int(hr_max * 2.5)),  
            np.random.randint(1, int(hr_min * 0.5))                  
        ])
        spo2 = np.random.choice([
            np.random.randint(50, 85), 
            np.random.randint(96, 101)
        ])
        return temp, hr, spo2

    return t_norm, hr_norm, spo2_norm

def generate_balanced_dataset(n_per_class=5000):
    rows = []
    for label in DIAGNOSES:
        for _ in range(n_per_class):
            age = sample_age()
            temp, hr, spo2 = sample_vitals_for_label(label, age)
            rows.append({
                "age": age,
                "temperature": round(temp,1),
                "heart_rate": hr,
                "spo2": spo2,
                "diagnosis": label
            })
    df = pd.DataFrame(rows)
    df["temperature"] = df["temperature"].clip(34.0, 42.0)
    df["heart_rate"]   = df["heart_rate"].clip(1, 250)
    df["spo2"]         = df["spo2"].clip(1, 100)
    return df

if __name__ == "__main__":
    df = generate_balanced_dataset(n_per_class=5000)
    print(df["diagnosis"].value_counts())
    df.to_csv("balanced_diagnosis.csv", index=False)

    print("Generados balanced_diagnosis.csv y balanced_diagnosis.parquet")

#!/usr/bin/env python3
"""
Bubakan Green - Idempotent Learning Content Seeder (Quiz Bank + Plant Voices)
Populates 135 botanical quiz questions and plant voice metadata into Firestore
without overwriting or duplicating existing data.
"""

import argparse
import json
import os
import sys

def seed_learning_content(service_account_path: str):
    try:
        import firebase_admin
        from firebase_admin import credentials, firestore
    except ImportError:
        print("ERROR: firebase-admin package is required. Install via: pip install firebase-admin")
        sys.exit(1)

    if not os.path.exists(service_account_path):
        print(f"ERROR: Service account key not found at: {service_account_path}")
        sys.exit(1)

    cred = credentials.Certificate(service_account_path)
    if not firebase_admin._apps:
        firebase_admin.initialize_app(cred)
    db = firestore.client()

    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    questions_path = os.path.join(base_dir, "docs", "data", "DEFAULT_QUIZ_QUESTIONS.json")
    voices_path = os.path.join(base_dir, "docs", "data", "DEFAULT_PLANT_VOICES.json")

    print(f"Loading questions from: {questions_path}")
    with open(questions_path, "r", encoding="utf-8") as f:
        questions = json.load(f)

    print(f"Loading plant voices from: {voices_path}")
    with open(voices_path, "r", encoding="utf-8") as f:
        voices = json.load(f)

    # 1. Validation check against canonical master_plants
    master_col = db.collection("master_plants")
    unique_plant_ids = set(q["plantId"] for q in questions)
    print(f"Verifying {len(unique_plant_ids)} unique plant IDs against /master_plants...")

    missing_plants = []
    for pid in unique_plant_ids:
        snap = master_col.document(pid).get()
        if not snap.exists:
            missing_plants.append(pid)

    if missing_plants:
        print(f"FAIL-FAST: The following plant IDs do not exist in /master_plants: {missing_plants}")
        print("Aborting seed process to prevent dangling relations.")
        sys.exit(1)

    print("All plant IDs verified against /master_plants.")

    # 2. Seeding Plant Voices
    print("\n--- SEEDING PLANT VOICES ---")
    voices_col = db.collection("plant_voices")
    created_voices = 0
    preserved_voices = 0

    for voice in voices:
        doc_id = voice["plantId"]
        doc_ref = voices_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"[PRESERVED] PlantVoice '{doc_id}' already exists.")
            preserved_voices += 1
        else:
            doc_ref.set(voice)
            print(f"[CREATED]   PlantVoice '{doc_id}' inserted.")
            created_voices += 1

    print(f"Plant voices summary: {created_voices} created, {preserved_voices} preserved.")

    # 3. Seeding Quiz Questions
    print("\n--- SEEDING QUIZ QUESTIONS ---")
    questions_col = db.collection("plant_quiz_questions")
    created_questions = 0
    preserved_questions = 0

    for q in questions:
        doc_id = q["questionId"]
        doc_ref = questions_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            preserved_questions += 1
        else:
            doc_ref.set(q)
            created_questions += 1

    print(f"Quiz questions summary: {created_questions} created, {preserved_questions} preserved.")
    print("Idempotent seeding completed successfully.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Seed learning content (quizzes and voices) into Firestore")
    parser.add_argument("--key", required=True, help="Path to Firebase Service Account JSON key")
    args = parser.parse_args()

    seed_learning_content(args.key)

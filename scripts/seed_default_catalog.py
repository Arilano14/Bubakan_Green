#!/usr/bin/env python3
"""
Bubakan Green - Idempotent Firestore Catalog Seeder
Populates default plant catalog and location relationships into Firestore
without overwriting existing verified data.
"""

import argparse
import json
import os
import sys

def seed_firestore(service_account_path: str):
    try:
        import firebase_admin
        from firebase_admin import credentials, firestore
    except ImportError:
        print("ERROR: firebase-admin package is required. Install via: pip install firebase-admin")
        sys.exit(1)

    if not os.path.exists(service_account_path):
        print(f"ERROR: Service account key not found at: {service_account_path}")
        sys.exit(1)

    # Initialize Firebase Admin
    cred = credentials.Certificate(service_account_path)
    if not firebase_admin._apps:
        firebase_admin.initialize_app(cred)
    db = firestore.client()

    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    catalog_path = os.path.join(base_dir, "docs", "data", "DEFAULT_PLANT_CATALOG.json")
    relations_path = os.path.join(base_dir, "docs", "data", "DEFAULT_LOCATION_PLANTS.json")

    print(f"Loading catalog from: {catalog_path}")
    with open(catalog_path, "r", encoding="utf-8") as f:
        plants = json.load(f)

    print(f"Loading location relationships from: {relations_path}")
    with open(relations_path, "r", encoding="utf-8") as f:
        relations = json.load(f)

    print("\n--- SEEDING MASTER PLANTS ---")
    plants_col = db.collection("plants")
    created_plants = 0
    skipped_plants = 0

    for plant in plants:
        doc_id = plant["id"]
        doc_ref = plants_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"[PRESERVED] MasterPlant '{doc_id}' already exists in Firestore.")
            skipped_plants += 1
        else:
            doc_ref.set(plant)
            print(f"[CREATED]   MasterPlant '{doc_id}' ({plant.get('nameLatin')}) inserted.")
            created_plants += 1

    print(f"\nMaster plants summary: {created_plants} created, {skipped_plants} preserved/skipped.")

    print("\n--- SEEDING LOCATION PLANTS ---")
    loc_plants_col = db.collection("location_plants")
    created_rels = 0
    skipped_rels = 0

    for rel in relations:
        doc_id = rel["id"]
        doc_ref = loc_plants_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"[PRESERVED] LocationPlant '{doc_id}' already exists in Firestore.")
            skipped_rels += 1
        else:
            doc_ref.set(rel)
            print(f"[CREATED]   LocationPlant '{doc_id}' -> {rel.get('locationName')} - {rel.get('masterPlantName')}")
            created_rels += 1

    print(f"\nLocation relationships summary: {created_rels} created, {skipped_rels} preserved/skipped.")
    print("\nSeeding finished successfully.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Seed default Bubakan Green catalog into Firestore")
    parser.add_argument("--key", required=True, help="Path to Firebase Service Account JSON key")
    args = parser.parse_args()

    seed_firestore(args.key)

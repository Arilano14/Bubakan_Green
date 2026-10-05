#!/usr/bin/env python3
"""
Bubakan Green - Idempotent Firestore Catalog & Location Seeder
Populates default plant catalog (9 MasterPlants), approved featured locations (2 Locations),
and garden relationships (10 LocationPlants) into Cloud Firestore without overwriting verified data.
Adheres strictly to Sections 6 - 18 of Bubakan Green architecture guidelines.
"""

import argparse
import json
import os
import sys

def seed_firestore(service_account_path: str = None, oauth_token: str = None):
    # Resolve credentials
    token = oauth_token or os.environ.get("GOOGLE_OAUTH_TOKEN")
    cred_path = service_account_path or os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")

    if token:
        try:
            import google.oauth2.credentials
            from google.cloud import firestore
        except ImportError:
            print("ERROR: google-cloud-firestore package required.")
            sys.exit(1)
        creds = google.oauth2.credentials.Credentials(token)
        db = firestore.Client(project="bubakan-green", credentials=creds)
        key_project_id = db.project
    elif cred_path and os.path.exists(cred_path):
        try:
            import firebase_admin
            from firebase_admin import credentials, firestore
        except ImportError:
            print("ERROR: firebase-admin package is required. Install via: python -m pip install firebase-admin")
            sys.exit(1)

        # Section 12: Verify Service Account belongs to bubakan-green
        with open(cred_path, "r", encoding="utf-8") as f:
            key_data = json.load(f)
            key_project_id = key_data.get("project_id")

        if key_project_id != "bubakan-green":
            print(f"ABORTED: Service account belongs to project '{key_project_id}', but expected 'bubakan-green'.")
            sys.exit(1)

        cred = credentials.Certificate(cred_path)
        if not firebase_admin._apps:
            firebase_admin.initialize_app(cred)
        db = firestore.client()
    else:
        print("ERROR: Credentials not found.")
        print("Provide via --token, --key argument, or set $env:GOOGLE_APPLICATION_CREDENTIALS / $env:GOOGLE_OAUTH_TOKEN")
        sys.exit(1)

    if key_project_id != "bubakan-green":
        print(f"ABORTED: Target project is '{key_project_id}', but expected 'bubakan-green'.")
        sys.exit(1)


    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    catalog_path = os.path.join(base_dir, "docs", "data", "DEFAULT_PLANT_CATALOG.json")
    locations_path = os.path.join(base_dir, "docs", "data", "DEFAULT_LOCATIONS.json")
    relations_path = os.path.join(base_dir, "docs", "data", "DEFAULT_LOCATION_PLANTS.json")

    print("==================================================")
    print("BUBAKAN GREEN — FIRESTORE CANONICAL SEEDER")
    print("==================================================")
    print(f"Project:    {key_project_id}")
    print("Database:   (default)")
    print("Collections: master_plants, locations, location_plants")
    print("==================================================")

    # 1. SEED MASTER PLANTS (Section 14)
    print("\n--- 1. SEEDING MASTER PLANTS (/master_plants) ---")
    with open(catalog_path, "r", encoding="utf-8") as f:
        plants = json.load(f)

    plants_col = db.collection("master_plants")
    created_plants = 0
    skipped_plants = 0

    for plant in plants:
        doc_id = plant["id"]
        doc_ref = plants_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"  [PRESERVED] MasterPlant '{doc_id}' already exists.")
            skipped_plants += 1
        else:
            doc_ref.set(plant)
            print(f"  [CREATED]   MasterPlant '{doc_id}' ({plant.get('nameLatin')}) inserted.")
            created_plants += 1

    print(f"Master plants summary: {created_plants} created, {skipped_plants} preserved (Total: {len(plants)}).")

    # 2. SEED LOCATIONS (Section 15)
    print("\n--- 2. SEEDING LOCATIONS (/locations) ---")
    with open(locations_path, "r", encoding="utf-8") as f:
        locations = json.load(f)

    locs_col = db.collection("locations")
    created_locs = 0
    skipped_locs = 0

    for loc in locations:
        doc_id = loc["id"]
        doc_ref = locs_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"  [PRESERVED] Location '{doc_id}' already exists.")
            skipped_locs += 1
        else:
            doc_ref.set(loc)
            print(f"  [CREATED]   Location '{doc_id}' ({loc.get('name')}) inserted.")
            created_locs += 1

    print(f"Locations summary: {created_locs} created, {skipped_locs} preserved (Total: {len(locations)}).")

    # 3. SEED LOCATION PLANTS (Section 16)
    print("\n--- 3. SEEDING LOCATION PLANTS (/location_plants) ---")
    with open(relations_path, "r", encoding="utf-8") as f:
        relations = json.load(f)

    loc_plants_col = db.collection("location_plants")
    created_rels = 0
    skipped_rels = 0

    for rel in relations:
        doc_id = rel["id"]
        doc_ref = loc_plants_col.document(doc_id)
        doc_snap = doc_ref.get()

        if doc_snap.exists:
            print(f"  [PRESERVED] LocationPlant '{doc_id}' already exists.")
            skipped_rels += 1
        else:
            doc_ref.set(rel)
            print(f"  [CREATED]   LocationPlant '{doc_id}' -> {rel.get('locationName')} - {rel.get('masterPlantName')}")
            created_rels += 1

    print(f"Location relationships summary: {created_rels} created, {skipped_rels} preserved (Total: {len(relations)}).")

    # 4. READ-BACK VERIFICATION (Section 18)
    print("\n--- 4. READ-BACK VERIFICATION ---")
    verified_plants = len(list(plants_col.stream()))
    verified_locs = len(list(locs_col.stream()))
    verified_rels = len(list(loc_plants_col.stream()))

    print(f"Live Firestore Collection Counts:")
    print(f"  /master_plants:    {verified_plants} documents (Expected: >= {len(plants)})")
    print(f"  /locations:        {verified_locs} documents (Expected: >= {len(locations)})")
    print(f"  /location_plants:  {verified_rels} documents (Expected: >= {len(relations)})")

    if verified_plants >= len(plants) and verified_locs >= len(locations) and verified_rels >= len(relations):
        print("\nSUCCESS: All canonical collections verified in Cloud Firestore.")
    else:
        print("\nWARNING: Verification counts do not match expected totals.")

def validate_data_sources():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    catalog_path = os.path.join(base_dir, "docs", "data", "DEFAULT_PLANT_CATALOG.json")
    locations_path = os.path.join(base_dir, "docs", "data", "DEFAULT_LOCATIONS.json")
    relations_path = os.path.join(base_dir, "docs", "data", "DEFAULT_LOCATION_PLANTS.json")

    with open(catalog_path, "r", encoding="utf-8") as f:
        plants = json.load(f)
    with open(locations_path, "r", encoding="utf-8") as f:
        locations = json.load(f)
    with open(relations_path, "r", encoding="utf-8") as f:
        relations = json.load(f)

    plant_ids = {p["id"] for p in plants}
    loc_ids = {l["id"] for l in locations}

    print("Pre-flight data validation:")
    print(f"  Master Plants:   {len(plants)} items (IDs: {', '.join(sorted(plant_ids))})")
    print(f"  Locations:       {len(locations)} items (IDs: {', '.join(sorted(loc_ids))})")
    print(f"  Location-Plants: {len(relations)} relationships")

    # Validate relationships integrity
    orphan_plants = [r["masterPlantId"] for r in relations if r["masterPlantId"] not in plant_ids]
    orphan_locs = [r["locationId"] for r in relations if r["locationId"] not in loc_ids]

    if orphan_plants:
        print(f"  [ERROR] Orphan masterPlantId in relations: {orphan_plants}")
        sys.exit(1)
    if orphan_locs:
        print(f"  [ERROR] Orphan locationId in relations: {orphan_locs}")
        sys.exit(1)

    print("  [OK] Data integrity check PASSED: No orphan relations, valid canonical collections.")
    return len(plants), len(locations), len(relations)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Seed canonical Bubakan Green data into Cloud Firestore")
    parser.add_argument("--key", required=False, help="Path to Firebase Service Account JSON key")
    parser.add_argument("--token", required=False, help="Google OAuth2 access token")
    parser.add_argument("--dry-run", action="store_true", help="Validate data sources and relationships without connecting to Firestore")
    args = parser.parse_args()

    if args.dry_run:
        print("=== DRY-RUN PRE-FLIGHT VALIDATION ===")
        validate_data_sources()
        print("=== DRY-RUN COMPLETE (No cloud writes performed) ===")
    else:
        seed_firestore(args.key, args.token)


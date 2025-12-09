import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import csv

def upload_csv_to_firestore(
    csv_path, 
    collection_name, 
    unique_key_col='name', 
    list_cols_to_split=[], 
    row_modifier_func=None
):
    batch = db.batch()
    counter = 0
    total_new = 0
    total_updated = 0
    
    print(f"--- [START] Processing {collection_name} ---")
    print("Fetching existing records to prevent duplicates...")
    existing_map_data_stored = {} 
    
    docs = db.collection(collection_name).stream()
    for doc in docs:
        data = doc.to_dict()
        firestore_key = data.get(unique_key_col, '').strip().lower()
        if firestore_key:
            existing_map_data_stored[firestore_key] = doc.id
            
    print(f"Found {len(existing_map_data_stored)} existing docs.")

    with open(csv_path, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        
        for row in reader:
            for col in list_cols_to_split:
                if col in row:
                    if row[col] and row[col].strip():
                        row[col] = [x.strip() for x in row[col].split(';')]
                    else:
                        row[col] = []

            if row_modifier_func:
                row = row_modifier_func(row)

            identifier = row.get(unique_key_col, '').strip().lower()
            if identifier in existing_map_data_stored:
                doc_id = existing_map_data_stored[identifier]
                doc_ref = db.collection(collection_name).document(doc_id)
                total_updated += 1
            else:
                doc_ref = db.collection(collection_name).document()
                total_new += 1

            batch.set(doc_ref, row, merge=False)
            counter += 1

            if counter == 400:
                batch.commit()
                print(f"Committed batch...")
                batch = db.batch()
                counter = 0

        if counter > 0:
            batch.commit()
            
    print(f"--- [DONE] {collection_name}. New: {total_new}, Updated: {total_updated} ---\n")


if __name__ == "__main__":

    # initialize Firebase Admin
    cred = credentials.Certificate('./serviceAccountKey.json')
    firebase_admin.initialize_app(cred)
    db = firestore.client()

    # ---- BRANDS ----
    brand_lists_col_to_split = ['categories', 'values']
    upload_csv_to_firestore(
        csv_path='brands.csv', 
        collection_name='brands',
        list_cols_to_split=brand_lists_col_to_split
    )
    # ---- BRANDS ----


    # ---- PRODUCTS ----
    brand_lookup = {}
    print("Building Brand Lookup Map...")
    for brand_doc in db.collection('brands').stream():
        brand_data = brand_doc.to_dict()
        if 'name' in brand_data:
            brand_lookup[brand_data['name']] = brand_doc.id

    def process_product_row(row):
        if 'price' in row and row['price']:
            try:
                row['price'] = float(row['price'])
            except:
                row['price'] = 0.0
        
        if 'isFeatured' in row and row['isFeatured']:
            try:
                row['isFeatured'] = bool(row['isFeatured'])
            except:
                row['isFeatured'] = False

        brand_name = row.get('brandName', '').strip()
        if brand_name in brand_lookup:
            row['brandId'] = brand_lookup[brand_name]
            row['brandName'] = brand_name
        else:
            print(f"Brand '{brand_name}' not found for product {row.get('brandName')}")
            row['brandId'] = ""
            
        return row

    upload_csv_to_firestore(
        csv_path='products.csv',
        collection_name='products',
        list_cols_to_split=['categories'],
        row_modifier_func=process_product_row
    )
    # ---- PRODUCTS ----
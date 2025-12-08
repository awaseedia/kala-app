import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import csv

# initialize Firebase Admin
cred = credentials.Certificate('./serviceAccountKey.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

csv_file_path = 'brands.csv'
collection_name = 'brands'
batch_size = 400

def upload_data():
    batch = db.batch()
    counter = 0
    total_uploaded = 0

    print(f"--------Reading {csv_file_path}...--------")
    
    col_to_split = ['values', 'categories', 'savedProductIds']

    with open(csv_file_path, mode='r', encoding='utf-8') as csv_file:
        csv_reader = csv.DictReader(csv_file)
        
        for row in csv_reader:
            # OPTIONAL: Data cleanup e.g., type conversion
            for col_name in col_to_split:
                if col_name in row and row[col_name]:
                    row[col_name] = [item.strip() for item in row[col_name].split(';')]
                elif col_name in row:
                    row[col_name] = []

            # Create a reference for a new document (auto-generated ID)
            doc_ref = db.collection(collection_name).document()

            batch.set(doc_ref, row)
            counter += 1
            total_uploaded += 1

            if counter == batch_size:
                batch.commit()
                print(f"Committed a batch of {counter} records.")
                batch = db.batch()
                counter = 0

        if counter > 0:
            batch.commit()
            print(f"Committed final batch of {counter} records.")

    print(f"--------Upload Complete! Total documents: {total_uploaded}--------")

if __name__ == "__main__":
    upload_data()
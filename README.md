# KĀLA – Mobile App Specification
*By Awa Cisse and Mai Tran*

## Project Overview
**KĀLA** is a mobile discovery platform that helps women interested in modest fashion easily find clothing and accessories from global ethical brands. The MVP version of the app focuses on providing an elegant, minimal browsing experience where users can explore brands, view their collections, save favorites, and be redirected to the brand’s official store for purchase.
### Project Scope (Feasible for 4 Weeks)
This simplified MVP will include 4 key screens and basic interactive functionality, developed using Android Studio Jetpack Compose.

**Core Screens**
1. Home / Discover Screen
* Displays a curated grid or list of modest fashion brands and their featured products (5+ hard-coded items).
* Each product card includes:
  * Product image
  * Brand name
  * Category (e.g., Abayas, Activewear)
* A search bar at the top allows users to search by keyword or category.
2. Brand Detail Screen
* Displays a brief brand profile (name, short bio, location, values such as “Sustainable” or “Luxury”).
* Lists product thumbnails (with images and prices).
* “Visit Website” button, which opens an external link in the user’s browser.
3. Product Detail Screen
* Enlarged product image, description, and brand tag.
* “Save” button to save to the user’s Favorites.
* “Visit Website” button redirects to the product’s external link.
4. Saved Items Screen
Displays all products that the user has favorited.
Allows users to remove items from their saved list.
Persistent local storage (e.g., SQLite) to keep data saved between sessions.

### KĀLA – Backend Documentation
**Update CSV files**
* Write onto the spreadsheet, install Chrome extension Instant Data Scraper to manually input
  * brands.csv
      1.*brandId* auto-generated
      2. name
      3. location
      4. bio
      5. websiteUrl
      6. logoUrl
      7. imageUrl
      8. categories
      9. values
  * products.csv
      1. brandId
      2. brandName
          * brandName connected to brandId
      3. name
      4. bio
      5. price
      6. currency
      7. categories
      8. imageUrl
      9. productUrl
      10. isFeatured
     
**Run python3 command** `$ python3 upload_csv.py`
* Update collections on Firebase > Cloud Firestore > Database
  * `serviceAccountKey` SHOULD ONLY BE SHARED INTERNALLY
For live updates of CSV files (methods in theory)
* Try Rowy
  * Set up the project on the Firebase console
    * Project settings > Service accounts > All service accounts
      * rowy-service@kala-52dcc.iam.gserviceaccount.com
        * For IAM permissions
  * Set up a billing plan
* Try Firebase Functions
  * Install Firebase CLI (command-line interface)
    * Install Node.js
    * Install Firebase CLI `$ npm install -g firebase-tools`
  * Init Firebase Functions
    * `$ firebase login`
    * `$ firebase init functions`
      * Choose the Firebase project.
      * Select JavaScript or TypeScript for Cloud Functions
      * Install dependencies when prompted
  * Set up Cloud Function
    * `$ cd functions`
    * `$ npm install csv-parser`
  * Write Cloud Function
    * Open index.js or index.ts (for more info, check out Cloud Functions)
  * This will use Firebase Storage → Upgrade project plan is needed
 

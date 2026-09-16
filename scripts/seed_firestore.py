import sys
import os
import requests

PROJECT_ID = "civora-app-433214"
DATABASE_URL = f"https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents"

def to_firestore_value(val):
    if val is None:
        return {"nullValue": None}
    if isinstance(val, bool):
        return {"booleanValue": val}
    if isinstance(val, int):
        return {"integerValue": str(val)}
    if isinstance(val, float):
        return {"doubleValue": val}
    if isinstance(val, str):
        return {"stringValue": val}
    if isinstance(val, list):
        return {"arrayValue": {"values": [to_firestore_value(x) for x in val]}}
    if isinstance(val, dict):
        return {"mapValue": {"fields": {k: to_firestore_value(v) for k, v in val.items()}}}
    return {"stringValue": str(val)}

def write_document(session, token, collection_id, doc_id, data):
    url = f"{DATABASE_URL}/{collection_id}/{doc_id}"
    fields = {k: to_firestore_value(v) for k, v in data.items()}
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json",
        "X-Goog-User-Project": PROJECT_ID
    }
    resp = session.patch(url, headers=headers, json={"fields": fields}, timeout=15)
    if resp.status_code == 200:
        print(f"[OK] Seeded {collection_id}/{doc_id}", flush=True)
    else:
        print(f"[ERR] Failed {collection_id}/{doc_id}: {resp.status_code} - {resp.text}", flush=True)

def main():
    if len(sys.argv) < 2:
        print("Usage: python seed_firestore.py <access_token>", flush=True)
        sys.exit(1)
    token = sys.argv[1].strip()
    session = requests.Session()
    print(f"Starting Firestore seeding for {PROJECT_ID}...", flush=True)

    # 1. User
    user_data = {
        "id": "usr_992140",
        "nationalId": "2495685261",
        "fullNameEn": "MD ABDUL HALIM MEIA",
        "fullNameAr": "مد عبد ال حليم مياه",
        "dateOfBirth": "1988/02/03",
        "dateOfBirthAr": "١٩٨٨/٠٢/٠٣",
        "dateOfBirthHijri": "1408/10/18",
        "nationality": "Bangladesh",
        "nationalityAr": "بنجلاديش",
        "placeOfBirthEn": "Bangladesh",
        "placeOfBirthAr": "بنجلاديش",
        "religionEn": "Islam",
        "religionAr": "الاسلام",
        "professionEn": "Laundry Worker",
        "professionAr": "عامل غسيل ملابس",
        "sponsorId": "7034884309",
        "sponsorNameEn": "Durrat Najah Laundry",
        "sponsorName": "مؤسسة درر نجاح للملابس",
        "issuePlaceEn": "Elm Information Security",
        "issuePlace": "شركة العلم لامن المعلومات",
        "workPlaceAr": "منطقة الرياض",
        "expiryDateEn": "2026/10/08",
        "expiryDateAr": "٢٠٢٦/١٠/٠٨",
        "versionNumber": "٢",
        "expiryDateDigits": "081026",
        "issueDateDigits": "070926",
        "verificationLevel": "TIER_3_VERIFIED",
        "digitalIdActive": True,
        "totalDocuments": 4,
        "activeRequestsCount": 2,
        "unreadNotificationsCount": 3
    }
    write_document(session, token, "users", user_data["id"], user_data)

    # 2. Documents
    documents = [
        {
            "id": "doc_nat_id",
            "type": "NATIONAL_ID",
            "title": "Digital National ID",
            "subtitle": "Ministry of Interior - Civil Affairs",
            "documentNumber": "1098442190",
            "issueDate": "1441/06/10 H",
            "expiryDate": "1451/06/10 H",
            "status": "ACTIVE",
            "issuer": "General Directorate of Civil Affairs",
            "details": {
                "Full Name": "Tariq Abdulaziz Al-Mansoor",
                "Arabic Name": "طارق عبدالعزيز المنصور",
                "Place of Birth": "Riyadh",
                "Blood Type": "O+"
            },
            "qrCodePayload": "CIVORA:NATID:1098442190:TIER3:VALID"
        },
        {
            "id": "doc_driving_lic",
            "type": "DRIVING_LICENSE",
            "title": "Private Driving License",
            "subtitle": "General Directorate of Traffic",
            "documentNumber": "DL-4819002-K",
            "issueDate": "1443/02/15 H",
            "expiryDate": "1448/02/15 H",
            "status": "ACTIVE",
            "issuer": "Ministry of Interior - Traffic Dept",
            "details": {
                "License Class": "Private Vehicle (Automatic & Manual)",
                "Restrictions": "Corrective Lenses Required",
                "Endorsement": "Safe Driver Score 96/100"
            },
            "qrCodePayload": "CIVORA:DL:4819002:VALID:CLASS_PRIVATE"
        },
        {
            "id": "doc_veh_reg",
            "type": "VEHICLE_REGISTRATION",
            "title": "Vehicle Registration (Istimara)",
            "subtitle": "Toyota Land Cruiser 2023 - [7742 KSA]",
            "documentNumber": "VR-9920194",
            "issueDate": "1444/08/01 H",
            "expiryDate": "1447/08/01 H",
            "status": "EXPIRING_SOON",
            "issuer": "Traffic Department - Vehicle Licensing",
            "details": {
                "Plate Number": "7742 KSA",
                "VIN": "JTMHY7AJ9P4019283",
                "Color": "Pearl White",
                "Insurance Status": "Comprehensive - Valid"
            },
            "qrCodePayload": "CIVORA:ISTIMARA:7742KSA:EXPIRING_SOON"
        },
        {
            "id": "doc_passport",
            "type": "PASSPORT",
            "title": "Citizen E-Passport",
            "subtitle": "General Directorate of Passports (Jawazat)",
            "documentNumber": "P0841920",
            "issueDate": "1442/11/04 H",
            "expiryDate": "1452/11/04 H",
            "status": "ACTIVE",
            "issuer": "General Directorate of Passports",
            "details": {
                "Biometric Chip": "Encoded & Signed (ICAO 9303)",
                "Pages": "48 Pages",
                "Authorized Border Gates": "All International Terminals"
            },
            "qrCodePayload": "CIVORA:PASSPORT:P0841920:BIOMETRIC_VERIFIED"
        }
    ]
    for doc in documents:
        write_document(session, token, "documents", doc["id"], doc)

    # 3. Services
    services = [
        {
            "id": "srv_renew_id",
            "title": "Renew National ID",
            "description": "Submit digital request to renew national identification with updated photo and biometric verification.",
            "category": "CIVIL_AFFAIRS",
            "processingTime": "2-3 business days",
            "fee": "Free",
            "isPopular": True,
            "isQuickAction": True,
            "requiredDocuments": ["Digital portrait photo", "Current National ID"]
        },
        {
            "id": "srv_renew_license",
            "title": "Renew Driving License",
            "description": "Renew your driver license online with linked digital medical checkup reports.",
            "category": "TRAFFIC_VEHICLES",
            "processingTime": "Instant (Digital) / 2 days (Card delivery)",
            "fee": "SAR 200 (5 Years)",
            "isPopular": True,
            "isQuickAction": True,
            "requiredDocuments": ["Valid medical examination", "Cleared traffic violations"]
        },
        {
            "id": "srv_vehicle_transfer",
            "title": "Vehicle Ownership Transfer",
            "description": "Direct private transfer of vehicle registration between citizens with escrow buyer protection.",
            "category": "TRAFFIC_VEHICLES",
            "processingTime": "Instant upon payment confirmation",
            "fee": "SAR 230 fee + tax",
            "isPopular": True,
            "isQuickAction": False,
            "requiredDocuments": ["Valid Istimara", "Valid periodic vehicle inspection (MVPI)"]
        },
        {
            "id": "srv_issue_passport",
            "title": "Renew Citizen Passport",
            "description": "Fast digital issuance or renewal of national biometric passport with home delivery.",
            "category": "PASSPORTS_TRAVEL",
            "processingTime": "3 business days",
            "fee": "SAR 300 (5 Years) / SAR 600 (10 Years)",
            "isPopular": True,
            "isQuickAction": True,
            "requiredDocuments": ["National ID", "Digital photo with white background"]
        },
        {
            "id": "srv_family_registry",
            "title": "Digital Family Record Issuance",
            "description": "Issue and view authenticated family book certifying dependents and marital records.",
            "category": "CIVIL_AFFAIRS",
            "processingTime": "Instant Digital",
            "fee": "Free",
            "isPopular": False,
            "isQuickAction": False,
            "requiredDocuments": ["Civil registry record"]
        },
        {
            "id": "srv_traffic_violations",
            "title": "Traffic Violations & Objection",
            "description": "Inquire into automated photo radar violations and file official dispute statements.",
            "category": "TRAFFIC_VEHICLES",
            "processingTime": "Instant inquiry / 7 days objection review",
            "fee": "Free inquiry",
            "isPopular": True,
            "isQuickAction": True,
            "requiredDocuments": ["National ID number"]
        },
        {
            "id": "srv_travel_permit",
            "title": "Issue Travel Authorizations",
            "description": "Manage dependents travel clearances and official international departure authorizations.",
            "category": "PASSPORTS_TRAVEL",
            "processingTime": "Instant Digital",
            "fee": "Free",
            "isPopular": False,
            "isQuickAction": False,
            "requiredDocuments": ["Dependent ID"]
        },
        {
            "id": "srv_security_clearance",
            "title": "Police Clearance Certificate",
            "description": "Official criminal history certification for employment, embassy, or commercial licensure.",
            "category": "PERMITS_SECURITY",
            "processingTime": "24 hours",
            "fee": "Free",
            "isPopular": False,
            "isQuickAction": False,
            "requiredDocuments": ["Active biometrics on record"]
        }
    ]
    for srv in services:
        write_document(session, token, "services", srv["id"], srv)

    # 4. Service Requests
    requests_list = [
        {
            "id": "req_101",
            "referenceNumber": "CIV-2026-99120",
            "serviceTitle": "Renew Citizen Passport (10 Years)",
            "category": "PASSPORTS_TRAVEL",
            "status": "PROCESSING",
            "submissionDate": "2026-09-02",
            "expectedCompletion": "2026-09-08",
            "currentStepIndex": 2,
            "timeline": [
                {"title": "Application Submitted & Authenticated", "timestamp": "Sep 02, 10:15 AM", "completed": True},
                {"title": "Security & Document Verification", "timestamp": "Sep 03, 02:40 PM", "completed": True},
                {"title": "Passport Printing & Chip Encoding", "timestamp": "In Progress", "completed": False},
                {"title": "Handover to National Postal Express", "timestamp": "Pending", "completed": False}
            ]
        },
        {
            "id": "req_102",
            "referenceNumber": "CIV-2026-88412",
            "serviceTitle": "Renew Vehicle Istimara (7742 KSA)",
            "category": "TRAFFIC_VEHICLES",
            "status": "ACTION_REQUIRED",
            "submissionDate": "2026-09-04",
            "expectedCompletion": "2026-09-06",
            "currentStepIndex": 1,
            "timeline": [
                {"title": "Renewal Draft Initiated", "timestamp": "Sep 04, 09:12 AM", "completed": True},
                {"title": "Periodic Vehicle Inspection (MVPI) Check", "timestamp": "Action Required: Expired MVPI", "completed": False},
                {"title": "Digital License Issuance", "timestamp": "Pending", "completed": False}
            ]
        },
        {
            "id": "req_100",
            "referenceNumber": "CIV-2026-77310",
            "serviceTitle": "Digital National ID Biometric Sync",
            "category": "CIVIL_AFFAIRS",
            "status": "COMPLETED",
            "submissionDate": "2026-08-20",
            "expectedCompletion": "2026-08-21",
            "currentStepIndex": 3,
            "timeline": [
                {"title": "Biometric Sync Scheduled", "timestamp": "Aug 20, 11:00 AM", "completed": True},
                {"title": "Live Verification Confirmed", "timestamp": "Aug 20, 11:30 AM", "completed": True},
                {"title": "Digital Wallet Credentials Updated", "timestamp": "Aug 20, 11:45 AM", "completed": True}
            ]
        }
    ]
    for req in requests_list:
        write_document(session, token, "requests", req["id"], req)

    # 5. Notifications
    notifications = [
        {
            "id": "notif_01",
            "title": "Action Required: Vehicle Inspection Expired",
            "message": "Your vehicle (7742 KSA) requires a fresh MVPI test to complete your registration renewal.",
            "timestamp": "2 hours ago",
            "priority": "WARNING",
            "isRead": False,
            "actionDeepLink": "requests/req_102"
        },
        {
            "id": "notif_02",
            "title": "Passport Status Update",
            "message": "Your 10-year e-Passport printing process has begun at the central issuance facility.",
            "timestamp": "1 day ago",
            "priority": "INFO",
            "isRead": False,
            "actionDeepLink": "requests/req_101"
        },
        {
            "id": "notif_03",
            "title": "Security Alert: New Sign-in Verified",
            "message": "A recognized sign-in was confirmed on Pixel 9 Pro XL via Biometric Passkey.",
            "timestamp": "3 days ago",
            "priority": "INFO",
            "isRead": True
        }
    ]
    for notif in notifications:
        write_document(session, token, "notifications", notif["id"], notif)

    print("\nAll 17 Civora entities seeded successfully in Firestore!", flush=True)

if __name__ == "__main__":
    main()

import requests
import subprocess

token = subprocess.run("gcloud auth print-access-token", shell=True, capture_output=True, text=True).stdout.strip()
headers = {
    "Authorization": f"Bearer {token}",
    "X-Goog-User-Project": "civora-app-433214",
    "Content-Type": "application/json"
}

rules_content = """rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
"""

rules_body = {
    "source": {
        "files": [{
            "name": "firestore.rules",
            "content": rules_content
        }]
    }
}

r1 = requests.post("https://firebaserules.googleapis.com/v1/projects/civora-app-433214/rulesets", headers=headers, json=rules_body)
print("Ruleset status:", r1.status_code, r1.json())
ruleset_name = r1.json().get("name")

if ruleset_name:
    release_body = {
        "name": "projects/civora-app-433214/releases/cloud.firestore",
        "rulesetName": ruleset_name
    }
    r2 = requests.post("https://firebaserules.googleapis.com/v1/projects/civora-app-433214/releases", headers=headers, json=release_body)
    print("Release status:", r2.status_code, r2.text)

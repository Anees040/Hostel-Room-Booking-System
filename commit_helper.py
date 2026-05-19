import os
import subprocess

BASE = r'c:\Users\Anees\Desktop\OOP_ Project'

# Let's get the list of modified and untracked files
# We can run git status --porcelain to find them easily
result = subprocess.run(['git', 'status', '--porcelain'], cwd=BASE, capture_output=True, text=True, check=True)

lines = result.stdout.strip().split('\n')
files_to_commit = []

for line in lines:
    if not line:
        continue
    status = line[:2]
    filepath = line[2:].strip().strip('"')
    # If it's a directory (like utils/ or screenshots/), let's list files inside it
    full_path = os.path.join(BASE, filepath)
    if os.path.isdir(full_path):
        for root, dirs, files in os.walk(full_path):
            for file in files:
                rel_file = os.path.relpath(os.path.join(root, file), BASE)
                # Avoid files inside out/ or data/ (already in .gitignore, but just in case)
                if not rel_file.startswith('out') and not rel_file.startswith('data'):
                    files_to_commit.append(rel_file)
    else:
        # Avoid files in out/ or data/
        if not filepath.startswith('out') and not filepath.startswith('data'):
            files_to_commit.append(filepath)

# De-duplicate
files_to_commit = sorted(list(set(files_to_commit)))

print(f"Found {len(files_to_commit)} files to commit:")
for f in files_to_commit:
    print(f" - {f}")

print("\nStarting individual commits...")
for i, f in enumerate(files_to_commit, 1):
    # Stage the single file
    subprocess.run(['git', 'add', f], cwd=BASE, check=True)
    
    # Create commit message
    basename = os.path.basename(f)
    if f.endswith('.java'):
        msg = f"Refactor and refine {basename} for Hostel Room Booking System"
    elif f.endswith('.png'):
        msg = f"Add system screenshot: {basename}"
    elif f.endswith('.md'):
        msg = f"Update project documentation: {basename}"
    elif f.endswith('.docx'):
        msg = f"Add final project report document: {basename}"
    else:
        msg = f"Add or update {basename}"
        
    print(f"[{i}/{len(files_to_commit)}] Committing: {f}")
    subprocess.run(['git', 'commit', '-m', msg], cwd=BASE, check=True)

print("\nAll files committed individually! Now push to remote.")

"""
Auto-screenshot tool for Hostel Room Booking System.
Uses pyautogui + PIL to capture screens after robot actions.
"""
import subprocess, time, os, sys

try:
    import pyautogui
    from PIL import ImageGrab
except ImportError:
    subprocess.check_call([sys.executable, '-m', 'pip', 'install', 'pyautogui', 'pillow', '-q'])
    import pyautogui
    from PIL import ImageGrab

pyautogui.PAUSE = 0.5
pyautogui.FAILSAFE = False

BASE       = r'c:\Users\Anees\Desktop\OOP_ Project'
SHOTS_DIR  = os.path.join(BASE, 'screenshots')
os.makedirs(SHOTS_DIR, exist_ok=True)

def shot(name, delay=2.0):
    time.sleep(delay)
    img = ImageGrab.grab()
    path = os.path.join(SHOTS_DIR, name)
    img.save(path)
    print(f'  Saved: {name}')

def click(x, y, delay=0.8):
    pyautogui.click(x, y)
    time.sleep(delay)

def type_text(text, delay=0.4):
    pyautogui.typewrite(text, interval=0.05)
    time.sleep(delay)

# ── Launch app ────────────────────────────────────────────────────────────────
print('Launching app...')
proc = subprocess.Popen(
    ['java', '-cp', 'out', 'Main'],
    cwd=BASE,
    stdout=subprocess.DEVNULL,
    stderr=subprocess.DEVNULL
)

# 1. Splash screen
print('Capturing splash...')
time.sleep(3)
shot('01_splash.png', delay=0)

# 2. Login screen (splash auto-closes after ~2.5s)
time.sleep(3)
shot('02_login.png', delay=0)

# 3. Click Register link to show registration
print('Capturing register screen...')
# Registration button is near bottom-center of the right card
# Move to approx center of screen then locate the button
pyautogui.hotkey('alt', 'tab')
time.sleep(0.5)
pyautogui.hotkey('alt', 'tab')
time.sleep(0.5)
# Click "Create Student Account" button — approx position
w, h = pyautogui.size()
cx, cy = w // 2, h // 2
# The login card is on the right half; register button near bottom
pyautogui.click(cx + 80, cy + 130)
shot('03_register.png', delay=1.5)

# Go back to login — click Back button
pyautogui.click(cx - 20, cy + 195)
time.sleep(1)

# 4. Admin Login
print('Logging in as admin...')
# Clear and fill ID field (approx y = center - 60)
pyautogui.click(cx + 80, cy - 60)
time.sleep(0.3)
pyautogui.hotkey('ctrl', 'a')
type_text('admin')
# Password field
pyautogui.click(cx + 80, cy - 5)
pyautogui.hotkey('ctrl', 'a')
type_text('admin123')
# Role combo — keep as Admin (default or select)
# Click Sign In
pyautogui.click(cx + 80, cy + 50)
time.sleep(3)

# 5. Admin Rooms tab (default)
shot('04_admin_rooms.png', delay=1)

# 6. Click All Bookings tab
pyautogui.click(cx - 200, cy - 270)  # rough tab position
time.sleep(1)
shot('05_admin_bookings.png', delay=0)

# 7. Students tab
pyautogui.press('tab')
time.sleep(1)
shot('06_admin_students.png', delay=0)

# 8. Maintenance tab
pyautogui.press('tab')
time.sleep(1)
shot('07_admin_maintenance.png', delay=0)

# 9. Reports tab (last tab)
# Click multiple times to reach reports
for _ in range(3):
    pyautogui.press('tab')
    time.sleep(0.5)
shot('08_admin_reports.png', delay=1)

# Close admin and re-login as student
pyautogui.hotkey('alt', 'F4')
time.sleep(1.5)

# 10. Re-launch for student login
proc2 = subprocess.Popen(
    ['java', '-cp', 'out', 'Main'],
    cwd=BASE,
    stdout=subprocess.DEVNULL,
    stderr=subprocess.DEVNULL
)
time.sleep(5)  # wait for splash + login

# Select Student role in combo
pyautogui.click(cx + 80, cy - 85)  # role combo
time.sleep(0.3)
# Role is already Student by default

# Fill credentials
pyautogui.click(cx + 80, cy - 60)
pyautogui.hotkey('ctrl', 'a')
type_text('SP23-BSE-030')

pyautogui.click(cx + 80, cy - 5)
pyautogui.hotkey('ctrl', 'a')
type_text('ali12345')

pyautogui.click(cx + 80, cy + 50)
time.sleep(3)

# Browse Rooms
shot('09_student_browse.png', delay=1)

# Click Book Now on first room to open dialog
pyautogui.click(cx + 80, cy - 50)  # click first row in table
time.sleep(0.5)
# Book Now button
pyautogui.click(cx + 200, cy + 200)
time.sleep(1.5)
shot('10_booking_dialog.png', delay=0)
pyautogui.press('escape')
time.sleep(0.5)

# My Bookings tab
pyautogui.press('tab')
time.sleep(1)
shot('11_student_bookings.png', delay=0)

# Maintenance tab
pyautogui.press('tab')
time.sleep(1)
shot('12_student_maintenance.png', delay=0)

# Reviews tab
pyautogui.press('tab')
time.sleep(1)
shot('13_student_reviews.png', delay=0)

# Notifications tab
pyautogui.press('tab')
time.sleep(1)
shot('14_notifications.png', delay=0)

proc.kill()
proc2.kill()
print('\nAll screenshots saved to:', SHOTS_DIR)
print('Now run: python generate_report.py')

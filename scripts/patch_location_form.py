import re

path = r'app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Replace "Lepas dari Lahan" with "Lepas"
content = content.replace(
    'Text("Lepas dari Lahan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)',
    'Text("Lepas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)'
)

# 2. Replace "+ Tambah Tanaman Baru" with "Tambah Tanaman Baru"
content = content.replace(
    'Text("+ Tambah Tanaman Baru", fontWeight = FontWeight.Bold, color = PrimaryForest, fontSize = 12.sp)',
    'Text("Tambah Tanaman Baru", fontWeight = FontWeight.Bold, color = PrimaryForest, fontSize = 12.sp)'
)

# 3. In assigned plant card, update Card container color and elevation
content = content.replace(
    'colors = CardDefaults.cardColors(containerColor = SurfaceCard),\n                                        border = BorderStroke(1.dp, BorderCard),',
    'colors = CardDefaults.cardColors(containerColor = SurfaceWhite),\n                                        border = BorderStroke(1.dp, BorderCard),\n                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),'
)

# 4. In assigned plant card, TextPrimary -> OnSurfaceDark, TextSecondary -> OnSurfaceVariant
content = content.replace(
    'color = TextPrimary\n                                                    )',
    'color = OnSurfaceDark\n                                                    )'
)
content = content.replace(
    'color = TextSecondary\n                                                        )',
    'color = OnSurfaceVariant\n                                                        )'
)

# 5. In picker sheet, TextPrimary -> OnSurfaceDark
content = content.replace(
    'color = if (isAssigned) OnSurfaceVariant else TextPrimary',
    'color = if (isAssigned) OnSurfaceVariant else OnSurfaceDark'
)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("SUCCESS: LocationFormScreen.kt patched successfully")

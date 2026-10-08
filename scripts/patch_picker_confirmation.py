import re

path = r'app/src/main/java/id/bubakangreen/app/ui/pic/LocationFormScreen.kt'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports
if 'import androidx.compose.foundation.layout.fillMaxHeight' not in content:
    content = content.replace(
        'import androidx.compose.foundation.layout.fillMaxSize',
        'import androidx.compose.foundation.layout.fillMaxHeight\nimport androidx.compose.foundation.layout.fillMaxSize'
    )

if 'import androidx.compose.material.icons.filled.Check\n' not in content:
    content = content.replace(
        'import androidx.compose.material.icons.filled.CheckCircle',
        'import androidx.compose.material.icons.filled.Check\nimport androidx.compose.material.icons.filled.CheckCircle'
    )

# Replace the bottom sheet body
old_sheet_start = '''    // Bottom Sheet: Plant Picker (Phase 4)
    if (showPlantPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlantPickerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tambah Tanaman ke Lahan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                    IconButton(onClick = { showPlantPickerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }'''

new_sheet_start = '''    // Bottom Sheet: Plant Picker (Phase 4)
    if (showPlantPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlantPickerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tambah Tanaman ke Lahan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        Text(
                            text = if (selectedPlantIds.isEmpty()) "Centang tanaman untuk menambahkan" else "${selectedPlantIds.size} tanaman dipilih",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (selectedPlantIds.isNotEmpty()) PrimaryForest else OnSurfaceVariant,
                                fontWeight = if (selectedPlantIds.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    if (selectedPlantIds.isNotEmpty()) {
                        Button(
                            onClick = {
                                viewModel.addSelectedPlantsToLahan(selectedPlantIds.toSet(), picUid)
                                showPlantPickerSheet = false
                            },
                            enabled = !state.isSavingInventory,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Simpan (${selectedPlantIds.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(onClick = { showPlantPickerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }'''

assert old_sheet_start in content, "old_sheet_start not found"
content = content.replace(old_sheet_start, new_sheet_start)

# Add horizontal padding to search field and "Tanaman belum terdaftar" row
old_search_block = '''                // Search field
                OutlinedTextField(
                    value = plantSearchQuery,
                    onValueChange = { plantSearchQuery = it },
                    placeholder = { Text("Cari nama tanaman (Indonesia / Latin)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (plantSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { plantSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Bersihkan")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryForest,
                        unfocusedBorderColor = OutlineGrey,
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "Tanaman belum terdaftar?" -> "+ Tambah Tanaman Baru"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {'''

new_search_block = '''                // Search field
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = plantSearchQuery,
                        onValueChange = { plantSearchQuery = it },
                        placeholder = { Text("Cari nama tanaman (Indonesia / Latin)...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (plantSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { plantSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Bersihkan")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = OutlineGrey,
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // "Tanaman belum terdaftar?" -> "+ Tambah Tanaman Baru"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {'''

assert old_search_block in content, "old_search_block not found"
content = content.replace(old_search_block, new_search_block)

# Replace LazyColumn modifier to use weight(1f) and padding(horizontal = 16.dp)
old_lazy_column = '''                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 460.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {'''

new_lazy_column = '''                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {'''

assert old_lazy_column in content, "old_lazy_column not found"
content = content.replace(old_lazy_column, new_lazy_column)

# Replace the sticky footer
old_footer = '''                Spacer(modifier = Modifier.height(12.dp))

                // Sticky footer: "X tanaman dipilih" + "Tambahkan X Tanaman" button
                Surface(
                    color = SurfaceWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedPlantIds.size} tanaman dipilih",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.addSelectedPlantsToLahan(selectedPlantIds.toSet(), picUid)
                                showPlantPickerSheet = false
                            },
                            enabled = selectedPlantIds.isNotEmpty() && !state.isSavingInventory,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (selectedPlantIds.isNotEmpty()) "Tambahkan ${selectedPlantIds.size} Tanaman" else "Pilih Tanaman",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }'''

new_footer = '''                // Prominent sticky footer: Guaranteed visible, full width above navigation bar
                Surface(
                    color = SurfaceWhite,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, BorderCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.addSelectedPlantsToLahan(selectedPlantIds.toSet(), picUid)
                                showPlantPickerSheet = false
                            },
                            enabled = selectedPlantIds.isNotEmpty() && !state.isSavingInventory,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryForest,
                                disabledContainerColor = OutlineGrey.copy(alpha = 0.5f),
                                contentColor = OnPrimaryWhite,
                                disabledContentColor = OnSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedPlantIds.isNotEmpty()) {
                                    "Konfirmasi Tambah (${selectedPlantIds.size} Tanaman)"
                                } else {
                                    "Pilih Tanaman untuk Ditambahkan"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }'''

assert old_footer in content, "old_footer not found"
content = content.replace(old_footer, new_footer)

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)

print("SUCCESS: LocationFormScreen.kt patched with confirmed sticky button!")

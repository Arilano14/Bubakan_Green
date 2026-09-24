package id.bubakangreen.app.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.bubakangreen.app.domain.model.Location
import id.bubakangreen.app.ui.theme.OnSurfaceDark
import id.bubakangreen.app.ui.theme.OnSurfaceVariant
import id.bubakangreen.app.ui.theme.PrimaryForest

data class PicOfficerOption(
    val uid: String,
    val name: String,
    val rw: String
)

val defaultPicRoster = listOf(
    PicOfficerOption("pic_rw01", "Petugas Lapangan RW 01", "01"),
    PicOfficerOption("pic_rw02", "Petugas Lapangan RW 02", "02"),
    PicOfficerOption("pic_rw03", "Petugas Lapangan RW 03", "03"),
    PicOfficerOption("pic_rw05", "Petugas Lapangan RW 05", "05")
)

@Composable
fun PicAssignmentDialog(
    location: Location,
    onDismiss: () -> Unit,
    onAssignPic: (newPicUid: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedUid by remember { mutableStateOf(location.picUid) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Penugasan Pengelola Kebun",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Pilih petugas lapangan (PIC) yang bertanggung jawab atas pemeliharaan dan pencatatan kebun ${location.name} (RW ${location.rw}).",
                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(14.dp))

                defaultPicRoster.forEach { officer ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedUid = officer.uid }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedUid == officer.uid,
                                onClick = { selectedUid = officer.uid },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryForest)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = officer.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = OnSurfaceDark
                                    )
                                )
                                Text(
                                    text = "Wilayah Tugas: RW ${officer.rw}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAssignPic(selectedUid) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
            ) {
                Text("Tugaskan Petugas")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

package id.bubakangreen.app.ui.scanner

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import id.bubakangreen.app.core.util.ParsedQrResult
import id.bubakangreen.app.core.util.QrUrlBuilder

/**
 * CodeScannerHandler: Manages Google Code Scanner execution.
 *
 * Enforces Phase 5 specifications:
 * - Uses Google Play Services Code Scanner (no CAMERA permission needed).
 * - Constrained strictly to Barcode.FORMAT_QR_CODE.
 * - Parses and validates scanned payload against canonical URL contracts.
 */
object CodeScannerHandler {

    fun startScan(
        context: Context,
        onSuccess: (ParsedQrResult) -> Unit,
        onCanceled: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        try {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build()

            val scanner = GmsBarcodeScanning.getClient(context, options)

            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue = barcode.rawValue ?: barcode.displayValue ?: ""
                    val parsed = QrUrlBuilder.parseCanonicalUrl(rawValue)
                    onSuccess(parsed)
                }
                .addOnCanceledListener {
                    onCanceled()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}

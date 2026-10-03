package id.bubakangreen.app.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import android.view.ViewGroup
import id.bubakangreen.app.domain.model.Location
import org.json.JSONArray
import org.json.JSONObject

/**
 * Android JS Bridge interface for communication between Leaflet map and Jetpack Compose.
 */
class BubakanMapBridge(
    private val onMarkerSelected: (Location) -> Unit,
    private val onMapDeselected: () -> Unit,
    private val onReady: () -> Unit,
    private val locationsProvider: () -> List<Location>
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onMarkerClicked(
        id: String,
        name: String,
        type: String,
        rw: String,
        address: String,
        lat: Double,
        lng: Double
    ) {
        mainHandler.post {
            val matchingLoc = locationsProvider().find { it.id == id }
            if (matchingLoc != null) {
                onMarkerSelected(matchingLoc)
            }
        }
    }

    @JavascriptInterface
    fun onMapClicked() {
        mainHandler.post {
            onMapDeselected()
        }
    }

    @JavascriptInterface
    fun onMapReady() {
        mainHandler.post {
            onReady()
        }
    }
}

/**
 * Reusable Leaflet.js-powered Map View for Bubakan Green.
 * Used in both full map (LocationsScreen) and discovery mini-map (HomeScreen).
 *
 * Strict Compliance:
 * - Zero Google Maps API cost (Rp0).
 * - Bundled local Leaflet assets in assets/map/.
 * - Single source of truth from Firestore -> ViewModel -> MapState.
 * - Zero GPS location permission needed for public viewing.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BubakanMapView(
    locations: List<Location>,
    selectedLocationId: String? = null,
    onLocationSelect: (Location) -> Unit = {},
    onMapClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enableZoomControls: Boolean = true
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapReady by remember { mutableStateOf(false) }

    // Filter valid GPS coordinates (-90..90, -180..180, non-zero)
    val validLocations = remember(locations) {
        locations.filter { loc ->
            loc.isPublished &&
                loc.latitude in -90.0..90.0 &&
                loc.longitude in -180.0..180.0 &&
                !(loc.latitude == 0.0 && loc.longitude == 0.0)
        }
    }

    // Push markers to WebView whenever data changes or map becomes ready
    LaunchedEffect(validLocations, isMapReady) {
        val webView = webViewRef
        if (webView != null && isMapReady) {
            val jsonArray = JSONArray()
            for (loc in validLocations) {
                val obj = JSONObject().apply {
                    put("id", loc.id)
                    put("name", loc.name)
                    put("type", loc.type.name)
                    put("rw", loc.rw)
                    put("address", loc.address)
                    put("latitude", loc.latitude)
                    put("longitude", loc.longitude)
                }
                jsonArray.put(obj)
            }
            val escapedJson = JSONObject.quote(jsonArray.toString())
            webView.evaluateJavascript("window.setMarkers(JSON.parse($escapedJson));", null)

            // Re-apply selection if exists
            if (!selectedLocationId.isNullOrEmpty()) {
                webView.evaluateJavascript("window.selectMarker('${selectedLocationId}');", null)
            }
        }
    }

    // Highlight marker when selectedLocationId changes
    LaunchedEffect(selectedLocationId, isMapReady) {
        val webView = webViewRef
        if (webView != null && isMapReady && !selectedLocationId.isNullOrEmpty()) {
            webView.evaluateJavascript("window.selectMarker('${selectedLocationId}');", null)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F4))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                createConfiguredWebView(
                    context = context,
                    locationsProvider = { validLocations },
                    onMarkerSelected = onLocationSelect,
                    onMapDeselected = onMapClick,
                    onReady = { isMapReady = true }
                ).also { webViewRef = it }
            },
            update = {
                // View updates handled reactively via LaunchedEffects
            }
        )
    }

    // Cleanup WebView when leaving composition to prevent memory leaks (Section 18)
    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.let { wv ->
                wv.stopLoading()
                wv.removeJavascriptInterface("AndroidBridge")
                wv.destroy()
            }
            webViewRef = null
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun createConfiguredWebView(
    context: Context,
    locationsProvider: () -> List<Location>,
    onMarkerSelected: (Location) -> Unit,
    onMapDeselected: () -> Unit,
    onReady: () -> Unit
): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
            allowUniversalAccessFromFileURLs = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                android.util.Log.d("BubakanMap", "${consoleMessage?.message()} -- line ${consoleMessage?.lineNumber()}")
                return true
            }
        }

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onReady()
            }
        }

        val bridge = BubakanMapBridge(
            onMarkerSelected = onMarkerSelected,
            onMapDeselected = onMapDeselected,
            onReady = onReady,
            locationsProvider = locationsProvider
        )
        addJavascriptInterface(bridge, "AndroidBridge")

        try {
            val html = context.assets.open("map/map_template.html").bufferedReader().use { it.readText() }
            loadDataWithBaseURL("file:///android_asset/map/", html, "text/html", "UTF-8", null)
        } catch (_: Exception) {
            loadUrl("file:///android_asset/map/map_template.html")
        }
    }
}

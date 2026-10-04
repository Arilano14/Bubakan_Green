import json

with open('app/src/main/assets/map/bubakan_boundary.geojson', 'r', encoding='utf-8') as f:
    geojson_str = f.read().strip()

html_content = '''<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
  <title>Peta Sebaran Kebun Bubakan</title>
  <link rel="stylesheet" href="leaflet.css" onerror="this.onerror=null;this.href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';" />
  <style>
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
    }
    html, body {
      width: 100vw;
      height: 100vh;
      overflow: hidden;
      background: #F4F6F4;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    }
    #map {
      width: 100vw;
      height: 100vh;
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: #EAF0E8;
    }
    /* Custom Pin Styles */
    .custom-marker {
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
    }
    .custom-marker:active {
      transform: scale(0.92);
    }
    .custom-marker.selected .pin-body {
      transform: scale(1.18);
      box-shadow: 0 0 0 4px rgba(46, 125, 50, 0.4), 0 8px 16px rgba(0,0,0,0.25);
    }
    .custom-marker.selected.toga .pin-body {
      box-shadow: 0 0 0 4px rgba(217, 119, 6, 0.4), 0 8px 16px rgba(0,0,0,0.25);
    }
    .pin-body {
      width: 38px;
      height: 38px;
      border-radius: 50% 50% 50% 0;
      transform: rotate(-45deg);
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
      border: 2px solid #FFFFFF;
      transition: all 0.2s ease;
    }
    .pin-body.urban-farming {
      background: linear-gradient(135deg, #2E7D32 0%, #1B5E20 100%);
    }
    .pin-body.taman-toga {
      background: linear-gradient(135deg, #D97706 0%, #B45309 100%);
    }
    .pin-icon {
      transform: rotate(45deg);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #FFFFFF;
      width: 20px;
      height: 20px;
    }
    .pin-icon svg {
      width: 18px;
      height: 18px;
      fill: currentColor;
    }
    /* Attribution styling */
    .leaflet-control-attribution {
      font-size: 9px !important;
      background: rgba(255, 255, 255, 0.85) !important;
      padding: 2px 6px !important;
      border-radius: 4px 0 0 0 !important;
    }
    .leaflet-control-attribution a {
      color: #2E7D32 !important;
      text-decoration: none;
    }
  </style>
</head>
<body>
  <div id="map"></div>

  <script src="leaflet.js"></script>
  <script>
    if (typeof L === 'undefined') {
      var s = document.createElement('script');
      s.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      document.head.appendChild(s);
    }
  </script>
  <script>
    // Official Kelurahan Bubakan boundary GeoJSON (Semarang City Geoportal)
    var BUBAKAN_BOUNDARY = ''' + geojson_str + ''';

    var BUBAKAN_CENTER_LAT = -7.09237;
    var BUBAKAN_CENTER_LNG = 110.32036;

    var map = null;
    var bubakanGeoLayer = null;
    var bubakanBounds = null;
    var currentMarkers = {};
    var selectedLocationId = null;
    var pendingMarkers = null;
    var lastMarkerClickTime = 0;

    var SEEDLING_SVG = '<svg viewBox="0 0 24 24"><path d="M12 3C7.58 3 4 6.58 4 11c0 3.23 1.91 6.01 4.67 7.28.2.09.43-.02.49-.23l.54-1.9c.07-.25-.06-.51-.3-.62C7.38 14.59 6 12.95 6 11c0-3.31 2.69-6 6-6s6 2.69 6 6c0 1.95-1.38 3.59-3.4 4.53-.24.11-.37.37-.3.62l.54 1.9c.06.21.29.32.49.23C18.09 17.01 20 14.23 20 11c0-4.42-3.58-8-8-8zM12 7c-2.21 0-4 1.79-4 4 0 1.3.63 2.45 1.61 3.19.2.15.48.11.63-.09l1.45-1.93c.15-.2.11-.48-.09-.63C11.16 11.2 11 10.63 11 10c0-.55.45-1 1-1s1 .45 1 1c0 .63-.16 1.2-.6 1.54-.2.15-.24.43-.09.63l1.45 1.93c.15.2.43.24.63.09C15.37 13.45 16 12.3 16 11c0-2.21-1.79-4-4-4z"/></svg>';
    var LEAF_SVG = '<svg viewBox="0 0 24 24"><path d="M17 8C8 10 5.9 16.17 3.82 21.34L5.71 22l1-2.3A4.49 4.49 0 0 0 8 20C19 20 22 3 22 3c-1 2-8 2.25-13 3.25S2 11.5 2 13.5s1.75 3.75 1.75 3.75C7 8 17 8 17 8z"/></svg>';

    function createMarkerIcon(type, isToga) {
      var bodyClass = isToga ? 'pin-body taman-toga' : 'pin-body urban-farming';
      var extraClass = isToga ? 'toga' : '';
      var svgIcon = isToga ? LEAF_SVG : SEEDLING_SVG;

      return L.divIcon({
        className: 'custom-marker ' + extraClass,
        html: '<div class="' + bodyClass + '"><div class="pin-icon">' + svgIcon + '</div></div>',
        iconSize: [38, 38],
        iconAnchor: [19, 38],
        popupAnchor: [0, -38]
      });
    }

    // Point-in-polygon verification in JS (Section 11)
    function isInsideBubakanBoundary(lng, lat) {
      if (!BUBAKAN_BOUNDARY || !BUBAKAN_BOUNDARY.features) return true;
      var poly = BUBAKAN_BOUNDARY.features[0].geometry.coordinates[0];
      var inside = false;
      var n = poly.length;
      var j = n - 1;
      for (var i = 0; i < n; i++) {
        var xi = poly[i][0], yi = poly[i][1];
        var xj = poly[j][0], yj = poly[j][1];
        var intersect = ((yi > lat) !== (yj > lat)) &&
            (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi);
        if (intersect) inside = !inside;
        j = i;
      }
      return inside;
    }

    function initMap() {
      if (typeof L === 'undefined') {
        setTimeout(initMap, 50);
        return;
      }

      try {
        map = L.map('map', {
          zoomControl: false,
          attributionControl: true,
          minZoom: 13,
          maxZoom: 19
        });

        L.control.zoom({ position: 'topleft' }).addTo(map);

        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 19,
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> | Batas: Pemkot Semarang'
        }).addTo(map);

        // Section 7: Render subtle official administrative boundary outline
        bubakanGeoLayer = L.geoJSON(BUBAKAN_BOUNDARY, {
          style: {
            color: '#1B5E20',       // Dark emerald border
            weight: 2,              // Clear boundary stroke
            opacity: 0.85,
            dashArray: '5, 5',      // Administrative boundary dashed style
            fillColor: '#2E7D32',   // Emerald green tint
            fillOpacity: 0.05       // 5% subtle transparent fill
          },
          interactive: false
        }).addTo(map);

        bubakanBounds = bubakanGeoLayer.getBounds();

        // Section 6: Camera View Lock
        // Initial view MUST fit the Bubakan administrative boundary
        map.fitBounds(bubakanBounds, { padding: [15, 15] });

        // Movement constraint: maxBounds with 20% visual padding to allow road context
        map.setMaxBounds(bubakanBounds.pad(0.20));

        map.on('click', function(e) {
          if (Date.now() - lastMarkerClickTime < 400) return;
          if (selectedLocationId && currentMarkers[selectedLocationId]) {
            var el = currentMarkers[selectedLocationId].getElement();
            if (el) el.classList.remove('selected');
            selectedLocationId = null;
          }
          if (window.AndroidBridge && window.AndroidBridge.onMapClicked) {
            window.AndroidBridge.onMapClicked();
          }
        });

        // Trigger size invalidation to ensure map fills container properly
        setTimeout(function() { if (map) map.invalidateSize(); }, 150);
        setTimeout(function() { if (map) map.invalidateSize(); }, 500);

        if (pendingMarkers) {
          window.setMarkers(pendingMarkers);
          pendingMarkers = null;
        }

        if (window.AndroidBridge && window.AndroidBridge.onMapReady) {
          window.AndroidBridge.onMapReady();
        }
      } catch (e) {
        console.error('Map init error:', e);
      }
    }

    window.setMarkers = function(markersJson) {
      if (!map) {
        pendingMarkers = markersJson;
        return;
      }

      try {
        var locations = typeof markersJson === 'string' ? JSON.parse(markersJson) : markersJson;

        for (var id in currentMarkers) {
          map.removeLayer(currentMarkers[id]);
        }
        currentMarkers = {};

        if (!locations || locations.length === 0) {
          if (bubakanBounds) {
            map.fitBounds(bubakanBounds, { padding: [15, 15] });
          } else {
            map.setView([BUBAKAN_CENTER_LAT, BUBAKAN_CENTER_LNG], 15);
          }
          map.invalidateSize();
          return;
        }

        var bounds = [];

        locations.forEach(function(loc) {
          if (loc.latitude === undefined || loc.longitude === undefined) return;
          var lat = parseFloat(loc.latitude);
          var lng = parseFloat(loc.longitude);
          if (isNaN(lat) || isNaN(lng) || (lat === 0 && lng === 0)) return;

          // Section 10 & 11: Point-in-polygon enforcement
          if (!isInsideBubakanBoundary(lng, lat)) {
            console.warn('Excluding marker outside Bubakan administrative boundary:', loc.name, lat, lng);
            return;
          }

          var isToga = loc.type === 'TAMAN_TOGA';
          var icon = createMarkerIcon(loc.type, isToga);

          var marker = L.marker([lat, lng], { icon: icon, title: loc.name }).addTo(map);

          marker.on('click', function(e) {
            lastMarkerClickTime = Date.now();
            if (e.originalEvent) {
              L.DomEvent.stop(e.originalEvent);
            }
            window.selectMarker(loc.id);
            if (window.AndroidBridge && window.AndroidBridge.onMarkerClicked) {
              window.AndroidBridge.onMarkerClicked(
                loc.id,
                loc.name || '',
                loc.type || 'URBAN_FARMING',
                loc.rw || '',
                loc.address || '',
                lat,
                lng
              );
            }
          });

          currentMarkers[loc.id] = marker;
          bounds.push([lat, lng]);
        });

        // Always keep bounds framed within or centered on Bubakan
        if (bounds.length > 0) {
          map.fitBounds(bubakanBounds, { padding: [15, 15] });
        } else {
          map.fitBounds(bubakanBounds, { padding: [15, 15] });
        }

        map.invalidateSize();
      } catch (err) {
        console.error('Error in setMarkers:', err);
      }
    };

    window.selectMarker = function(locId) {
      if (!map) return;

      if (selectedLocationId && currentMarkers[selectedLocationId]) {
        var prevEl = currentMarkers[selectedLocationId].getElement();
        if (prevEl) prevEl.classList.remove('selected');
      }

      selectedLocationId = locId;

      if (locId && currentMarkers[locId]) {
        var marker = currentMarkers[locId];
        var el = marker.getElement();
        if (el) el.classList.add('selected');
        map.panTo(marker.getLatLng(), { animate: true, duration: 0.5 });
      }
    };

    window.addEventListener('resize', function() {
      if (map) map.invalidateSize();
    });

    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', initMap);
    } else {
      initMap();
    }
  </script>
</body>
</html>
'''

with open('app/src/main/assets/map/map_template.html', 'w', encoding='utf-8') as f:
    f.write(html_content)

print('Updated map_template.html successfully!')

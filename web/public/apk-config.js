/**
 * Bubakan Green - Shared APK Distribution Configuration
 * 
 * Single source of truth for the Android application direct download link.
 * Used across the website to ensure consistent, immediate downloads without
 * Dropbox intermediate preview pages.
 */
(function(window) {
  'use strict';

  var BUBAKAN_APK_CONFIG = {
    appName: 'Bubakan Green',
    fileName: 'Bubakan-Green.apk',
    directDownloadUrl: 'https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=1',
    originalShareUrl: 'https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=0',
    fileSizeBytes: 36588219,
    fileSizeLabel: '~36.6 MB',
    versionLabel: 'v1.0.0 (Release Build)',

    /**
     * Synchronize all APK download buttons in the DOM to the authoritative URL.
     * Removes the cross-origin 'download' attribute (which modern browsers ignore)
     * and ensures reliable direct download behavior on desktop & mobile Android browsers.
     */
    syncButtons: function() {
      var targets = [
        '#nav-btn-download',
        '#hero-btn-download',
        '#btn-download-apk',
        '[data-apk-download="true"]'
      ];
      var elements = document.querySelectorAll(targets.join(','));
      for (var i = 0; i < elements.length; i++) {
        var el = elements[i];
        el.setAttribute('href', BUBAKAN_APK_CONFIG.directDownloadUrl);
        el.removeAttribute('download');
        el.setAttribute('rel', 'noopener noreferrer');
        el.setAttribute('data-apk-download', 'true');
      }
    }
  };

  window.BUBAKAN_APK_CONFIG = BUBAKAN_APK_CONFIG;

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', BUBAKAN_APK_CONFIG.syncButtons);
  } else {
    BUBAKAN_APK_CONFIG.syncButtons();
  }
})(window);

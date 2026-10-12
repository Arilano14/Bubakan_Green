/**
 * BUBAKAN GREEN — Reusable Side Mascot Component System
 * 
 * Manages secondary companion mascots across the web platform without
 * altering or displacing the primary mascot ("Si Buba").
 *
 * Responsibilities:
 * 1. Asset Mapping: Contextual mapping of features/states to WebP assets.
 * 2. Presentation: Responsive sizing, positioning, speech bubbles, and fallbacks.
 * 3. Feature Integration: Contextual hooks for user journeys.
 */

(function (global) {
  'use strict';

  var SIDE_MASCOT_REGISTRY = {
    GREETING: {
      id: 'greeting',
      name: 'Si Hijau Menyapa',
      src: '/assets/mascot/green_hello_maskot.webp',
      width: 2582,
      height: 3106,
      aspectRatio: 2582 / 3106,
      defaultAlt: 'Maskot pendamping hijau menyapa ramah',
      role: 'Sambutan, onboarding, pengenalan antarmuka'
    },
    SEEDS_GUIDE: {
      id: 'seeds_guide',
      name: 'Si Benih Botani',
      src: '/assets/mascot/seeds_mascot.webp',
      width: 1952,
      height: 2534,
      aspectRatio: 1952 / 2534,
      defaultAlt: 'Maskot benih botani memandu koleksi tanaman',
      role: 'Panduan benih, katalog botani, wawasan budidaya'
    },
    SEEDS_CHEER: {
      id: 'seeds_cheer',
      name: 'Si Benih Semangat',
      src: '/assets/mascot/seeds_cheer.webp',
      width: 4416,
      height: 4332,
      aspectRatio: 4416 / 4332,
      defaultAlt: 'Maskot benih bergembira memberi semangat menanam',
      role: 'Dorongan motivasi aktivitas budidaya mandiri'
    },
    PLANT_SUCCESS: {
      id: 'plant_success',
      name: 'Tanaman Mekar',
      src: '/assets/mascot/goodjob_plant.webp',
      width: 2986,
      height: 4786,
      aspectRatio: 2986 / 4786,
      defaultAlt: 'Ilustrasi bunga mekar merayakan keberhasilan',
      role: 'Konfirmasi keberhasilan eksplorasi atau membaca detail tanaman'
    },
    LOVE_APPRECIATION: {
      id: 'love_appreciation',
      name: 'Si Hijau Apresiasi',
      src: '/assets/mascot/green_love_mackot.webp',
      width: 4416,
      height: 4494,
      aspectRatio: 4416 / 4494,
      defaultAlt: 'Maskot hijau memeluk hati menyampaikan apresiasi hangat',
      role: 'Apresiasi warga, unduhan aplikasi, partisipasi komunitas'
    },
    ROBO_GUIDE: {
      id: 'robo_guide',
      name: 'Robo-Hijau Panduan',
      src: '/assets/mascot/green_robo.webp',
      width: 2102,
      height: 2754,
      aspectRatio: 2102 / 2754,
      defaultAlt: 'Maskot robot asisten memandu peta dan data kebun',
      role: 'Pemandu fitur cerdas, peta kebun, dan navigasi GPS'
    },
    ROBO_SUCCESS: {
      id: 'robo_success',
      name: 'Robo-Hijau Sukses',
      src: '/assets/mascot/green_robo_goodjob.webp',
      width: 4416,
      height: 5296,
      aspectRatio: 4416 / 5296,
      defaultAlt: 'Maskot robot asisten mengacungkan jempol sukses',
      role: 'Validasi sukses, verifikasi koordinat, dan penyelesaian tugas'
    },
    ROBO_SUPPORT: {
      id: 'robo_support',
      name: 'Robo-Hijau Peduli',
      src: '/assets/mascot/green_robo_hug.webp',
      width: 4416,
      height: 4332,
      aspectRatio: 4416 / 4332,
      defaultAlt: 'Maskot robot memeluk daun menjaga kelestarian alam',
      role: 'Dukungan pelestarian lingkungan dan perawatan tanaman'
    }
  };

  var SIZE_PRESETS = {
    xs: { width: 36, height: 44 },
    sm: { width: 48, height: 58 },
    md: { width: 68, height: 82 },
    lg: { width: 92, height: 110 },
    xl: { width: 120, height: 144 }
  };

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  var SideMascot = {
    REGISTRY: SIDE_MASCOT_REGISTRY,

    /**
     * Resolves an asset definition from key or object.
     */
    resolveAsset: function (assetKey) {
      if (!assetKey) return SIDE_MASCOT_REGISTRY.GREETING;
      if (typeof assetKey === 'object' && assetKey.src) return assetKey;
      var key = String(assetKey).toUpperCase();
      return SIDE_MASCOT_REGISTRY[key] || SIDE_MASCOT_REGISTRY.GREETING;
    },

    /**
     * Generates HTML markup for a Side Mascot presentation component.
     *
     * @param {Object} options
     * @param {string|Object} options.asset - Key from REGISTRY or asset object
     * @param {string} [options.size='md'] - 'xs' | 'sm' | 'md' | 'lg' | 'xl'
     * @param {string} [options.variant='callout'] - 'callout' | 'badge' | 'inline' | 'tile' | 'card'
     * @param {string} [options.theme='mint'] - 'mint' | 'warm' | 'teal' | 'neutral'
     * @param {string} [options.speech] - Optional speech bubble / companion caption text
     * @param {string} [options.speechTitle] - Optional heading in speech bubble
     * @param {string} [options.alt] - Alt text; empty string for decorative
     * @param {string} [options.className] - Additional CSS class
     * @param {string} [options.id] - Optional element ID
     * @param {boolean} [options.animate=true] - Gentle breathing animation
     * @param {string} [options.fallback] - Fallback URL on load error
     * @returns {string} HTML markup
     */
    renderHTML: function (options) {
      options = options || {};
      var asset = this.resolveAsset(options.asset);
      var sizeKey = options.size || 'md';
      var sizePreset = SIZE_PRESETS[sizeKey] || SIZE_PRESETS.md;
      var variant = options.variant || (options.speech ? 'callout' : 'badge');
      var theme = options.theme || 'mint';
      var isDecorative = options.alt === '' || (!options.alt && !options.speech);
      var altText = isDecorative ? '' : (options.alt || asset.defaultAlt);
      var ariaAttr = isDecorative ? ' aria-hidden="true"' : '';
      var fallbackUrl = options.fallback || '/assets/brand/new-logo.png';
      var animClass = options.animate !== false ? ' side-mascot-breathe' : '';
      var customClass = options.className ? ' ' + options.className : '';
      var idAttr = options.id ? ' id="' + escapeHtml(options.id) + '"' : '';

      // Computed dimensions keeping aspect ratio
      var targetWidth = sizePreset.width;
      var targetHeight = Math.round(targetWidth / asset.aspectRatio);

      var imgTag = [
        '<img src="' + escapeHtml(asset.src) + '"',
        ' alt="' + escapeHtml(altText) + '"',
        ariaAttr,
        ' class="side-mascot-img' + animClass + '"',
        ' width="' + targetWidth + '"',
        ' height="' + targetHeight + '"',
        ' loading="lazy"',
        ' onerror="this.onerror=null; this.src=\'' + escapeHtml(fallbackUrl) + '\';"' + '>'
      ].join('');

      if (!options.speech) {
        return [
          '<div' + idAttr + ' class="side-mascot-badge side-mascot-theme-' + theme + ' side-mascot-size-' + sizeKey + customClass + '">',
          imgTag,
          '</div>'
        ].join('');
      }

      var speechTitleHtml = options.speechTitle
        ? '<div class="side-mascot-speech-title">' + escapeHtml(options.speechTitle) + '</div>'
        : '';

      return [
        '<aside' + idAttr + ' class="side-mascot-callout side-mascot-variant-' + variant + ' side-mascot-theme-' + theme + ' side-mascot-size-' + sizeKey + customClass + '" role="region" aria-label="Catatan Pendamping">',
        '  <div class="side-mascot-avatar">' + imgTag + '</div>',
        '  <div class="side-mascot-speech">',
        '    ' + speechTitleHtml,
        '    <div class="side-mascot-speech-text">' + escapeHtml(options.speech) + '</div>',
        '  </div>',
        '</aside>'
      ].join('\n');
    },

    /**
     * Mounts a Side Mascot directly into a DOM element.
     */
    mount: function (target, options) {
      var container = typeof target === 'string' ? document.querySelector(target) : target;
      if (!container) return null;
      var html = this.renderHTML(options);
      container.insertAdjacentHTML('beforeend', html);
      return container.lastElementChild;
    }
  };

  // Expose globally
  global.SideMascot = SideMascot;

})(typeof window !== 'undefined' ? window : this);

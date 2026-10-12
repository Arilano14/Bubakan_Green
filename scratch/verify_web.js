const fs = require('fs');
const path = require('path');

const htmlPath = path.join(__dirname, '..', 'web', 'public', 'index.html');
const configPath = path.join(__dirname, '..', 'web', 'public', 'apk-config.js');
const firebaseJsonPath = path.join(__dirname, '..', 'web', 'firebase.json');

const EXPECTED_URL = 'https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=1';

// 1. Verify apk-config.js
console.log('--- 1. Testing apk-config.js ---');
const configContent = fs.readFileSync(configPath, 'utf8');
if (!configContent.includes(EXPECTED_URL)) {
  throw new Error('apk-config.js does not contain expected direct download URL');
}
console.log('OK: apk-config.js contains direct download URL.');

// 2. Verify index.html
console.log('--- 2. Testing index.html ---');
const htmlContent = fs.readFileSync(htmlPath, 'utf8');

if (!htmlContent.includes('/apk-config.js')) {
  throw new Error('index.html does not load apk-config.js');
}
console.log('OK: index.html loads apk-config.js in head.');

const buttonIds = ['nav-btn-download', 'hero-btn-download', 'btn-download-apk'];
for (const id of buttonIds) {
  const marker = `id="${id}"`;
  const idx = htmlContent.indexOf(marker);
  if (idx === -1) {
    throw new Error(`Missing button with id: ${id}`);
  }
  const tagStart = htmlContent.lastIndexOf('<a', idx);
  const tagEnd = htmlContent.indexOf('>', idx);
  const tagSnippet = htmlContent.substring(tagStart, tagEnd + 1);
  console.log(`Found [${id}]:\n  ${tagSnippet}`);
  if (!tagSnippet.includes(EXPECTED_URL)) {
    throw new Error(`Button ${id} does not have href pointing to expected URL`);
  }
  if (!tagSnippet.includes('data-apk-download="true"')) {
    throw new Error(`Button ${id} missing data-apk-download attribute`);
  }
}
console.log('OK: All 3 buttons properly configured in HTML.');

// 3. Verify firebase.json
console.log('--- 3. Testing firebase.json ---');
const fbJson = JSON.parse(fs.readFileSync(firebaseJsonPath, 'utf8'));
if (!fbJson.hosting || !Array.isArray(fbJson.hosting.redirects)) {
  throw new Error('firebase.json is missing hosting.redirects');
}
const hasRedirect = fbJson.hosting.redirects.some(r => 
  r.source === '/downloads/bubakan-green.apk' && r.destination === EXPECTED_URL && r.type === 302
);
if (!hasRedirect) {
  throw new Error('firebase.json missing redirect for /downloads/bubakan-green.apk');
}
console.log('OK: firebase.json has 302 redirect for legacy download URL.');

console.log('ALL VERIFICATION CHECKS PASSED!');

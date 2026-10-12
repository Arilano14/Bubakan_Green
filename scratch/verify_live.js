const https = require('https');

const EXPECTED = 'https://www.dropbox.com/scl/fi/5f0307as5awimpj0h8iar/Bubakan-Green.apk?rlkey=bc2j9f4g51b0sq70o8u0n7lrj&st=48c5cbaq&dl=1';

function fetchUrl(url) {
  return new Promise((resolve, reject) => {
    https.get(url, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => resolve({ statusCode: res.statusCode, headers: res.headers, body: data }));
    }).on('error', reject);
  });
}

async function verifyLive() {
  console.log('--- 1. Testing live https://bubakan-green.web.app ---');
  const home = await fetchUrl('https://bubakan-green.web.app');
  console.log('Homepage status:', home.statusCode);

  const targets = ['id="nav-btn-download"', 'id="hero-btn-download"', 'id="btn-download-apk"'];
  for (const t of targets) {
    const idx = home.body.indexOf(t);
    if (idx === -1) {
      throw new Error(`FAIL: Target not found in live HTML: ${t}`);
    }
    const tagStart = home.body.lastIndexOf('<a', idx);
    const tagEnd = home.body.indexOf('>', idx);
    const tag = home.body.substring(tagStart, tagEnd + 1);
    console.log(`Live Button found:\n  ${tag}`);
    if (!tag.includes(EXPECTED)) {
      throw new Error(`FAIL: Target ${t} does not have EXPECTED URL in live HTML!`);
    }
  }

  console.log('--- 2. Testing live /apk-config.js ---');
  const config = await fetchUrl('https://bubakan-green.web.app/apk-config.js');
  console.log('apk-config.js status:', config.statusCode);
  if (!config.body.includes(EXPECTED)) {
    throw new Error('FAIL: apk-config.js does not contain EXPECTED URL');
  }

  console.log('--- 3. Testing live /downloads/bubakan-green.apk redirect ---');
  const redirect = await fetchUrl('https://bubakan-green.web.app/downloads/bubakan-green.apk');
  console.log('downloads redirect status:', redirect.statusCode);
  console.log('downloads Location header:', redirect.headers.location);
  if (redirect.statusCode !== 302 || redirect.headers.location !== EXPECTED) {
    throw new Error('FAIL: /downloads/bubakan-green.apk does not 302 redirect to EXPECTED URL');
  }

  console.log('ALL LIVE VERIFICATIONS PASSED SUCCESSFULLY!');
}

verifyLive().catch(err => {
  console.error(err);
  process.exit(1);
});
